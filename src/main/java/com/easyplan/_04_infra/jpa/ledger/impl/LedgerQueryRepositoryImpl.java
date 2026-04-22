package com.easyplan._04_infra.jpa.ledger.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.journal.EntryType;
import com.easyplan._03_domain.ledger.readModel.LedgerCategorisWithAccounts;
import com.easyplan._03_domain.ledger.repository.LedgerQueryRepository;
import com.easyplan._03_domain.ledger.service.LedgerQueryService.RecentType;
import com.easyplan._04_infra.dsl.readModel.CategoryUsageDTO;
import com.easyplan._04_infra.dsl.readModel.CurrentMonthRankExpenseDTO;
import com.easyplan._04_infra.dsl.readModel.NetWorthDTO;
import com.easyplan._04_infra.dsl.readModel.RecentTransaction;
import com.easyplan._04_infra.jpa.ledger.entity.AccountEntity;
import com.easyplan._04_infra.jpa.ledger.entity.CategoryEntity;
import com.easyplan._04_infra.jpa.ledger.entity.CategoryOptionEntity;
import com.easyplan._04_infra.jpa.ledger.entity.LedgerEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QAccountEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QCategoryEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QCategoryOptionEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QEntryLineEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QJournalEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QLedgerEntity;
import com.easyplan._04_infra.jpa.user.entity.QUserEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LedgerQueryRepositoryImpl implements LedgerQueryRepository {
	private final JPAQueryFactory qf;
	
	@Override
	public List<LedgerCategorisWithAccounts> dashboardMetadata(Long ownerId) {
		QLedgerEntity ledgerEntity = QLedgerEntity.ledgerEntity;
		QAccountEntity accountEntity = QAccountEntity.accountEntity;
		QCategoryEntity categoryEntity = QCategoryEntity.categoryEntity;
		QCategoryOptionEntity categoryOptionEntity = QCategoryOptionEntity.categoryOptionEntity;
		
		List<CategoryOptionEntity> options = qf
				.selectFrom(categoryOptionEntity)
				.fetch();
		
		Map<AccountType, List<CategoryOption>> optionsByGroup = options.stream()
        .collect(Collectors.groupingBy(
            CategoryOptionEntity::getAccountType,
            Collectors.mapping(ent -> new CategoryOption(ent.getId(), ent.getOptionName(), ent.getAccountType(), ent.getOptionName()), Collectors.toList())
        ));
		
		// [STEP 1] Ledger 조회 (이건 동일)
    List<LedgerEntity> ledgers = qf
            .selectFrom(ledgerEntity)
            .where(ledgerEntity.ownerId.eq(ownerId))
            .fetch();

    if (ledgers.isEmpty()) return List.of();

    // [STEP 2] Category 조회 (엔티티 객체 자체로 비교!)
    // categoryEntity.ledgerId.in(ids) 대신 아래처럼 객체 참조를 씁니다.
    List<CategoryEntity> allCategories = qf
            .selectFrom(categoryEntity)
            .where(categoryEntity.ledger.in(ledgers)) // 엔티티 리스트를 그대로 때려박음!
            .fetch();

    // [STEP 3] Account 조회 (이것도 엔티티 참조로!)
    List<AccountEntity> allAccounts = qf
            .selectFrom(accountEntity)
            .where(accountEntity.category.in(allCategories)) // CategoryEntity 참조 사용
            .fetch();

    // [STEP 4] 메모리 조립 (엔티티에서 ID를 뽑아서 그룹핑)
    Map<Long, List<LedgerCategorisWithAccounts.AccountQuery>> accountsByCategoryId = allAccounts.stream()
        .collect(Collectors.groupingBy(
            a -> a.getCategory().getId(),
            Collectors.mapping(a -> {
                // 현재 계정의 카테고리 타입 (ASSET, LIABILITY, INCOME, EXPENSE, BASE_ASSET 등)
                AccountType currentType = a.getCategory().getType();
                
                List<CategoryOption> mergedOptions = new ArrayList<>();
                
                // 1. 현재 타입에 딱 맞는 옵션들은 무조건 추가
                mergedOptions.addAll(optionsByGroup.getOrDefault(currentType, List.of()));
                
                // 2. 자산(ASSET)이나 부채(LIABILITY)인 경우에만 ALL 옵션을 추가로 합침
                if (currentType == AccountType.ASSET || currentType == AccountType.LIABILITIES || currentType == AccountType.EQUITY) {
                    mergedOptions.addAll(optionsByGroup.getOrDefault(AccountType.ALL, List.of()));
                }

                return new LedgerCategorisWithAccounts.AccountQuery(
                    a.getId(), 
                    a.getAccountName(),
                    a.getCategoryOption().getId(),
                    mergedOptions, 
                    a.isPayment()
                );
            }, Collectors.toList())
        ));

    return ledgers.stream().map(l -> {
        List<LedgerCategorisWithAccounts.CategoriesWithAccount> categories = allCategories.stream()
            .filter(c -> c.getLedger().getId().equals(l.getId())) // 참조 중인 가계부 ID 비교
            .filter(c -> c.getType() != AccountType.ALL)
            .map(c -> new LedgerCategorisWithAccounts.CategoriesWithAccount(
                c.getId(),
                c.getCategoryName(),
                accountsByCategoryId.getOrDefault(c.getId(), List.of())
            ))
            .toList();

        return new LedgerCategorisWithAccounts(
            l.getId(),
            l.getType(),
            l.getName(),
            categories
        );
    }).toList();
	}

	@Override
	public NetWorthDTO dashboardNetWorth(Long ledgerId) {
	    QEntryLineEntity entryLine = QEntryLineEntity.entryLineEntity;
	    QAccountEntity account = QAccountEntity.accountEntity;

	    // [Step 1] 해당 장부(ledger)의 모든 계정 ID와 타입을 메모리에 먼저 올림 (고작 20~30개)
	    // 이 쿼리는 1ms 걸림
	    Map<Long, AccountType> accountTypeMap = qf
	        .select(account.id, account.category.type)
	        .from(account)
	        .where(account.ledger.id.eq(ledgerId))
	        .fetch()
	        .stream()
	        .collect(Collectors.toMap(
	            tuple -> tuple.get(account.id),
	            tuple -> tuple.get(account.category.type)
	        ));

	    // [Step 2] EntryLine에서 계정별 합계만 집계 (조인 없음!)
	    // 이 쿼리는 인덱스만 타면 20만 건이라도 수십 ms 내에 끝남
	    List<com.querydsl.core.Tuple> amountStats = qf
	        .select(
	            entryLine.account.id,
	            entryLine.type,
	            entryLine.amount.sum()
	        )
	        .from(entryLine)
	        .where(entryLine.account.id.in(accountTypeMap.keySet()))
	        .groupBy(entryLine.account.id, entryLine.type)
	        .fetch();
	    
	    long assetSum = 0;
	    long liabilitySum = 0;

	    for (var row : amountStats) {
	        Long accId = row.get(entryLine.account.id);
	        EntryType eType = row.get(entryLine.type);
	        Long sum = row.get(2, Long.class);
	        AccountType aType = accountTypeMap.get(accId);

	        if (aType == AccountType.ASSET) {
	            assetSum += (eType == EntryType.DEBIT) ? sum : -sum;
	        } else if (aType == AccountType.LIABILITIES) {
	            liabilitySum += (eType == EntryType.CREDIT) ? sum : -sum;
	        }
	    }

	    return new NetWorthDTO(assetSum, liabilitySum, assetSum - liabilitySum);
	}

	@Override
	public CurrentMonthRankExpenseDTO dashboardCurrentMonthRankExpense(Long ledgerId, LocalDate startDate, LocalDate endDate) {
		QCategoryEntity categoryEntity = QCategoryEntity.categoryEntity;
		QEntryLineEntity entryLineEntity = QEntryLineEntity.entryLineEntity;
		QAccountEntity accountEntity = QAccountEntity.accountEntity;
		QJournalEntity journalEntity = QJournalEntity.journalEntity;
		
		Tuple totals = qf
				.select(Expressions.cases()
						.when(categoryEntity.type.eq(AccountType.INCOME))
						.then(entryLineEntity.amount).otherwise(0L).sum().coalesce(0L),
						Expressions.cases()
						.when(categoryEntity.type.eq(AccountType.EXPENSE))
						.then(entryLineEntity.amount).otherwise(0L).sum().coalesce(0L)
				)
				.from(entryLineEntity)
				.join(entryLineEntity.journal, journalEntity)
				.join(entryLineEntity.account, accountEntity)
				.join(accountEntity.category, categoryEntity)
				.where(
						journalEntity.ledger.id.eq(ledgerId),
						journalEntity.transactionDate.between(startDate, endDate)
				)
				.fetchOne();
		
		long totalIncome = (totals != null) ? totals.get(0, Long.class) : 0L;
    long totalExpense = (totals != null) ? totals.get(1, Long.class) : 0L;
    
    List<CategoryUsageDTO> top3Raw = getMonthExpenseTop3Categoires(ledgerId, startDate, endDate);
    
    List<CategoryUsageDTO> finalTop3 = top3Raw.stream().map(dto -> {
    	double percent = (totalExpense > 0)
    			? (dto.amount().doubleValue() / totalExpense) * 100
    			: 0.0;
    	
    	double roundedPercent = Math.round(percent * 10.0) / 10.0;
    	
    	return new CategoryUsageDTO(dto.categoryName(), dto.amount(), roundedPercent);
    }).toList();
		
    return new CurrentMonthRankExpenseDTO(totalIncome, totalExpense, finalTop3);
	}
	
	private List<CategoryUsageDTO> getMonthExpenseTop3Categoires(Long ledgerId, LocalDate startDate, LocalDate endDate) {
		QCategoryEntity categoryEntity = QCategoryEntity.categoryEntity;
		QEntryLineEntity entryLineEntity = QEntryLineEntity.entryLineEntity;
		QAccountEntity accountEntity = QAccountEntity.accountEntity;
		QJournalEntity journalEntity = QJournalEntity.journalEntity;
		
		List<CategoryUsageDTO> top3 = qf
				.select(Projections.constructor(CategoryUsageDTO.class,
						accountEntity.accountName,
						entryLineEntity.amount.sum(),
						Expressions.asNumber(0.0).as("percentage")
				))
				.from(entryLineEntity)
				.join(entryLineEntity.journal, journalEntity)
				.join(entryLineEntity.account, accountEntity)
				.join(accountEntity.category, categoryEntity)
				.where(
						journalEntity.ledger.id.eq(ledgerId),
						categoryEntity.type.eq(AccountType.EXPENSE),
						journalEntity.transactionDate.between(startDate, endDate)
				)
				.groupBy(accountEntity.id)
				.orderBy(entryLineEntity.amount.sum().desc())
				.limit(3)
				.fetch();
		
		return top3;
	}

	@Override
	public List<RecentTransaction> recentTransactionList(Long ledgerId, RecentType recentType) {
		/*
		SELECT
		    u.nickname,
		    a2.account_name,
		    j.transaction_date,
		    j.total_amount
		FROM journal_entries j
		JOIN users u
		    ON u.id = j.created_owner_id
		JOIN entry_line el
		    ON el.journal_id = j.id
		JOIN account a2
		    ON a2.id = el.account_id
		JOIN account_category c2
		    ON c2.id = a2.category_id
		WHERE
		    j.ledger_id = 1
		    AND c2.account_type = 'EXPENSE'
		    AND EXISTS (
		        SELECT 1
		        FROM entry_line el2
		        JOIN account a1 ON a1.id = el2.account_id
		        JOIN account_category c1 ON c1.id = a1.category_id
		        WHERE el2.journal_id = j.id
		          AND c1.account_type IN ('ASSET', 'LIABILITIES')
		    )
		ORDER BY j.transaction_date DESC, j.id DESC
		LIMIT 5;
		*/
		
		QUserEntity userEntity = QUserEntity.userEntity;
    QJournalEntity journalEntity = QJournalEntity.journalEntity;
    QEntryLineEntity entryLineEntity = QEntryLineEntity.entryLineEntity;
    QAccountEntity accountEntity = QAccountEntity.accountEntity;
    QCategoryEntity categoryEntity = QCategoryEntity.categoryEntity;

    // Query 1: 최근 거래 내역 Top5 형태로 JournalId만 추출
    long journalIdsTime = System.currentTimeMillis();
    List<Long> journalIds = qf
    		.select(journalEntity.id)
    		.from(journalEntity)
    		.where(
    				journalEntity.ledger.id.eq(ledgerId),
    				journalEntity.transactionType.eq(recentType.getTransactionType())
    		)
    		.orderBy(journalEntity.transactionDate.desc(), journalEntity.id.desc())
    		.limit(5)
    		.fetch();
    
    if (journalIds.isEmpty()) {
    	System.out.println("IS EMPTY");
      return List.of();
    }
    log.info("LedgerQueryRepository journalIdsTime={}ms", System.currentTimeMillis() - journalIdsTime);
    
    // Query 2: 추출한 journalId로 기본 정보 조회
    long infoRowsTime = System.currentTimeMillis();
    List<Tuple> infoRows = qf
    		.select(
    				journalEntity.id,
    				userEntity.nickname,
    				accountEntity.accountName,
    				journalEntity.transactionDate,
    				journalEntity.totalAmount
    		)
    		.from(entryLineEntity)
    		.join(entryLineEntity.journal, journalEntity)
        .join(entryLineEntity.account, accountEntity)
        .join(accountEntity.category, categoryEntity)
        .join(userEntity).on(userEntity.id.eq(journalEntity.createdOwnerId))
        .where(
                journalEntity.id.in(journalIds),
                categoryEntity.type.eq(recentType.getDebit())
        )
        .fetch();
    log.info("LedgerQueryRepository infoRows={}ms", System.currentTimeMillis() - infoRowsTime);
    
    QEntryLineEntity assetLine = new QEntryLineEntity("assetLine");
    QAccountEntity assetAccount = new QAccountEntity("assetAccount");
    QCategoryEntity assetCategory = new QCategoryEntity("assetCategory");
    QJournalEntity assetJournal = new QJournalEntity("assetJournal");
    
    // Query 3: 대변 추가 정보 조회
    long counterRowsTime = System.currentTimeMillis();
    List<Tuple> counterRows = qf
    		.select(
    				assetJournal.id,
    				assetAccount.accountName,
    				assetCategory.type
    		)
    		.from(assetLine)
    		.join(assetLine.journal, assetJournal)
        .join(assetLine.account, assetAccount)
        .join(assetAccount.category, assetCategory)
        .where(
                assetJournal.id.in(journalIds),
                assetCategory.type.in(recentType.getCredit())
        )
        .fetch();
    log.info("LedgerQueryRepository counterRowsTime={}ms", System.currentTimeMillis() - counterRowsTime);
    
    // 조립 1단계 Map
    long infoMapTime = System.currentTimeMillis();
    Map<Long, Tuple> infoMap = infoRows.stream()
            .collect(Collectors.toMap(
                    row -> row.get(journalEntity.id),
                    row -> row,
                    (a, b) -> a
            ));
    log.info("LedgerQueryRepository infoMapTime={}ms", System.currentTimeMillis() - infoMapTime);

    long counterMapTime = System.currentTimeMillis();
    Map<Long, Tuple> counterMap = counterRows.stream()
            .collect(Collectors.toMap(
                    row -> row.get(assetJournal.id),
                    row -> row,
                    (a, b) -> a
            ));
    log.info("LedgerQueryRepository counterMapTime={}ms", System.currentTimeMillis() - counterMapTime);
    
    // 조립 2단계 Return
    return journalIds.stream()
        .map(journalId -> {
            Tuple info = infoMap.get(journalId);
            Tuple counter = counterMap.get(journalId);

            if (info == null) {
                return null;
            }

            return new RecentTransaction(
                    info.get(userEntity.nickname),
                    info.get(accountEntity.accountName),
                    info.get(journalEntity.transactionDate),
                    info.get(journalEntity.totalAmount),
                    counter != null ? counter.get(assetAccount.accountName) : null,
                    counter != null ? counter.get(assetCategory.type) : null
            );
        })
        .filter(Objects::nonNull)
        .toList();
    
    /*
    QEntryLineEntity assetLine = new QEntryLineEntity("assetLine");
    QAccountEntity assetAccount = new QAccountEntity("assetAccount");
    QCategoryEntity assetCategory = new QCategoryEntity("assetCategory");
    QJournalEntity assetJournal = new QJournalEntity("assetJournal");

    long counterRowsTime = System.currentTimeMillis();
    List<Tuple> counterRows = qf
            .select(
                    assetJournal.id,
                    assetAccount.accountName,
                    assetCategory.type
            )
            .from(assetLine)
            .join(assetLine.journal, assetJournal)
            .join(assetLine.account, assetAccount)
            .join(assetAccount.category, assetCategory)
            .where(
                    assetJournal.ledger.id.in(journalIds),
                    assetCategory.type.in(recentType.getCredit())
            )
            .limit(5)
            .fetch();
    log.info("LedgerQueryRepository counterRows={}ms", System.currentTimeMillis() - counterRowsTime);

    long counterMapTime = System.currentTimeMillis();
    Map<Long, Tuple> counterMap = counterRows.stream()
            .collect(Collectors.toMap(
                    row -> row.get(assetJournal.id),
                    row -> row,
                    (a, b) -> a
            ));
    log.info("LedgerQueryRepository counterMap={}ms", System.currentTimeMillis() - counterMapTime);

    return baseRows.stream()
            .map(row -> {
                Long journalId = row.get(journalEntity.id);
                Tuple counter = counterMap.get(journalId);

                return new RecentTransaction(
                        row.get(userEntity.nickname),
                        row.get(accountEntity.accountName),
                        row.get(journalEntity.transactionDate),
                        row.get(journalEntity.totalAmount),
                        counter != null ? counter.get(assetAccount.accountName) : null,
                        counter != null ? counter.get(assetCategory.type) : null
                );
            })
            .toList();
     */
    
		/*
		QUserEntity userEntity = QUserEntity.userEntity;
    QEntryLineEntity entryLineEntity = QEntryLineEntity.entryLineEntity; 
    QEntryLineEntity assetLine = new QEntryLineEntity("assetLine");     
    QAccountEntity accountEntity = QAccountEntity.accountEntity;       
    QAccountEntity assetAccount = new QAccountEntity("assetAccount");   
    QJournalEntity journalEntity = QJournalEntity.journalEntity;
    QCategoryEntity categoryEntity = QCategoryEntity.categoryEntity;
    QCategoryEntity assetCategory = new QCategoryEntity("assetCategory"); 

    return qf
		    .select(new QRecentTransaction(
		            userEntity.nickname,
		            accountEntity.accountName,     
		            journalEntity.transactionDate,
		            journalEntity.totalAmount,
		            assetAccount.accountName,
		            assetCategory.type
		    ))
		    .from(entryLineEntity)
		    .join(entryLineEntity.journal, journalEntity)
		    .join(entryLineEntity.account, accountEntity)
		    .join(accountEntity.category, categoryEntity)
		    .join(journalEntity.entryLines, assetLine) 
		    .join(assetLine.account, assetAccount)
		    // 자산 계정의 카테고리를 조인에 추가
		    .join(assetAccount.category, assetCategory) 
		    .join(userEntity).on(userEntity.id.eq(journalEntity.createdOwnerId))
		    .where(
		            journalEntity.ledger.id.eq(ledgerId),
		            categoryEntity.type.eq(recentType.getDebit()),
		            assetCategory.type.in(recentType.getCredit())
		    )
		    .orderBy(journalEntity.transactionDate.desc())
		    .limit(5)
		    .fetch();
		*/
	}
}
