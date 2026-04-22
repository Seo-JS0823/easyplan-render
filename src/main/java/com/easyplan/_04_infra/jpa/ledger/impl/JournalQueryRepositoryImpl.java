package com.easyplan._04_infra.jpa.ledger.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.journal.TransactionType;
import com.easyplan._03_domain.ledger.readModel.JournalThumbTransaction;
import com.easyplan._03_domain.ledger.readModel.JournalThumbTransactionScroll;
import com.easyplan._03_domain.ledger.readModel.SearchOptions;
import com.easyplan._03_domain.ledger.readModel.SortedOptions;
import com.easyplan._03_domain.ledger.repository.JournalQueryRepository;
import com.easyplan._04_infra.jpa.ledger.entity.JournalEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QAccountEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QCategoryEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QCategoryOptionEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QEntryLineEntity;
import com.easyplan._04_infra.jpa.ledger.entity.QJournalEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JournalQueryRepositoryImpl implements JournalQueryRepository {
	private final JPAQueryFactory qf;
	
	/*
	 * 현재 쿼리의 상태
	 * 
	 * Index -> LedgerId / TransactionType / TransactionDate / id 순의 복합 인덱스와
	 *       -> LedgerId / TransactionDate / id 순의 복합 인덱스가 있음
	 *       
	 *       가장 많이 사용하는 필터 옵션을 수입거래, 지출거래, 이체거래, 전체조회라고 가정하고 만들었는데
	 *       전체 조회에서는 LedgerId / TransactionDate / Id 순의 인덱스를 타는게 가장 빠름
	 *       위에서 해당하는 필터옵션이 지정되면 LedgerId / TransactionType / TransactionDate / Id 순의 인덱스가 가장 빠름
	 *       
	 *       그러나 List<Long> ids 를 뽑는 쿼리에서 30개의 ids를 가져올 때 전체적으로 빠르지만
	 *       전체 조회일 때 높은 오프셋에서 많이 느려짐 MySQL의 EXPLAIN 을 찍어보았을 때
	 *       Index Skip Scan이 뜨면서 LedgerId / TransactionTyep / TransactionDate / id 순의 인덱스를 사용하게 됨.
	 *       
	 *       하지만 OFFSET 500 정도부터 OFFSET 15만까지 올라가더라도 SQL쿼리 속도는 비슷하다는 걸 봄
	 *       로컬 환경에서 테스트한 것이기 때문에 운영환경에 올라간다고 가정하면 몇 배 느려질 테니 이 문제를 해결할 필요가 있다고 봄
	 *       
	 *       Cursor 기반 페이징이 간단한 해결책이 될 수 있는데 해당 방식은 다음 프로젝트에서 사용해보도록 하고
	 *       이 문제를 인덱스만으로 해결해보자.
	 *       
	 *       LedgerId / TransactionType / TransactionDate / id 의 복합인덱스가 전체 조회에서 쓰이면
	 *       높은 오프셋에서 느려진다는 단점이 있음. -> MySQL 옵티마이저가 자꾸 이 인덱스를 사용하기 때문에 강제로 지정을 해줘야함
	 *       그러나 QueryDSL에서는 그런 기능이 없고 Native Query를 사용해야하는데 이러면 QueryDSL의 큰 장점을 버리는 것이라 생각되니
	 *       LedgerId / TransactionType / TransactionDate / id 의 복합인덱스를 제거하는 것이 현재로서 좋은 해결책이 될 수 있음.
	 *       
	 *       이때 사용자의 수입과 소비 패턴에 따라 TransactionType에 대한 데이터 분포가 나뉠텐데
	 *       보통 사용자는 수입대비 지출이 압도적으로 많을 거라 판단 수입 10 : 지출 90으로 가정해서
	 *       
	 *       
	 */
	@Override
	public JournalThumbTransactionScroll findJournalThumbTransactions(
			Long ledgerId,
			LocalDate startDate,
			LocalDate endDate,
			int offset,
			SearchOptions searchOption,
			SortedOptions sortedOption,
			String keyword) {
		
			final int pageSize = 30;

	    QJournalEntity journal = QJournalEntity.journalEntity;
	    QEntryLineEntity entryLine = QEntryLineEntity.entryLineEntity;
	    QAccountEntity account = QAccountEntity.accountEntity;
	    QCategoryEntity category = QCategoryEntity.categoryEntity;
	    
	    /*
	     * EXPLAIN SELECT
	     *   J.id
	     * FROM
	     *   Journal_entries J
	     * WHERE
	     *   J.ledger_id = 1 AND
	     *   J.transaction_type = 'INCOME' AND
	     *   J.transaction_date BETWEEN date_format('', '') AND date_format('', '')
	     * ORDER BY
	     *   J.transaction_date desc, J.id desc
	     * LIMIT 30 OFFSET 0
	     * 
	     * TYPE    = RANGE
	     * KEY     = idx_ledger_tDate_id
	     * KEY_LEN = 11
	     * EXTRA   = Using Index Condition; Using Where; Backward Index Scan
	     */
	    BooleanBuilder builder = new BooleanBuilder();
	    builder.and(journal.ledger.id.eq(ledgerId));
	    builder.and(journal.transactionDate.between(startDate, endDate));
	    
	    builder.and(buildSearchPredicate(searchOption, journal));
	    
	    builder.and(containsKeyword(keyword, journal));
	    List<Long> ids = qf
	            .select(journal.id)
	            .from(journal)
	            .where(
	            		builder
	            		/*
	            		 * Journal.ledger_id = ledgerId And
	            		 * Journal.transactionDate between [startDate] And [endDate] And
	            		 * buildSearchPredicate 메서드의 switch case 문 중 SearchOption에 따라 동적으로 한 개 추가 And
	            		 * containsKeyword 메서드에 keyword 조건에 포함되는 것 중 동적으로 한 개 추가
	            		 */
	            )
	            .orderBy(getOrderOption(sortedOption, journal))
	            .offset(offset)
	            .limit(pageSize + 1L)
	            .fetch();

	    if (ids.isEmpty()) {
	        return new JournalThumbTransactionScroll(Collections.emptyList(), false, offset);
	    }

	    boolean next = ids.size() > pageSize;
	    List<Long> fetchIds = next ? ids.subList(0, pageSize) : ids;

	    List<JournalEntity> fetched = qf
	            .selectFrom(journal)
	            .leftJoin(journal.entryLines, entryLine).fetchJoin()
	            .leftJoin(entryLine.account, account).fetchJoin()
	            .leftJoin(account.category, category).fetchJoin()
	            .where(journal.id.in(fetchIds))
	            .orderBy(getOrderOption(sortedOption, journal))
	            .fetch();

	    Map<Long, JournalEntity> uniqueJournalMap = new LinkedHashMap<>();
	    for (JournalEntity jo : fetched) {
	        uniqueJournalMap.putIfAbsent(jo.getId(), jo);
	    }

	    List<JournalThumbTransaction> journalTransactionList = new ArrayList<>(uniqueJournalMap.size());

	    for (JournalEntity jo : uniqueJournalMap.values()) {
	        List<JournalThumbTransaction.AccountSideInfo> sideInfos = jo.getEntryLines().stream()
	                .map(line -> new JournalThumbTransaction.AccountSideInfo(
	                        line.getAccount().getAccountName(),
	                        line.getAccount().getCategory().getType(),
	                        line.getType()
	                ))
	                .toList();

	        journalTransactionList.add(new JournalThumbTransaction(
	                jo.getId(),
	                jo.getTransactionDate(),
	                jo.getTotalAmount(),
	                jo.getMemo(),
	                sideInfos
	        ));
	    }

	    return new JournalThumbTransactionScroll(
	            journalTransactionList,
	            next,
	            offset + pageSize
	    );
	}

	private OrderSpecifier<?>[] getOrderOption(SortedOptions option, QJournalEntity journal) {
	    if (option == null) {
	        return new OrderSpecifier[]{
	                journal.transactionDate.desc(),
	                journal.id.desc()
	        };
	    }

	    return switch (option) {
	        case OLDEST -> new OrderSpecifier[]{
	                journal.transactionDate.asc(),
	                journal.id.asc()
	        };
	        case HIGH_PRICE -> new OrderSpecifier[]{
	                journal.totalAmount.desc(),
	                journal.id.desc()
	        };
	        case LOW_PRICE -> new OrderSpecifier[]{
	                journal.totalAmount.asc(),
	                journal.id.asc()
	        };
	        default -> new OrderSpecifier[]{
	                journal.transactionDate.desc(),
	                journal.id.desc()
	        };
	    };
	}

	private BooleanExpression buildSearchPredicate(
	        SearchOptions option,
	        QJournalEntity journal
	) {
	    if (option == null || option == SearchOptions.ALL) {
	        return null;
	    }

	    return switch (option) {
	        case TRANSACTION_INCOME -> journal.transactionType.eq(TransactionType.INCOME);
	        case TRANSACTION_EXPENSE -> journal.transactionType.eq(TransactionType.EXPENSE);
	        case TRANSACTION_TRANSFER -> journal.transactionType.eq(TransactionType.TRANSFER);

	        case ASSET -> existsCategoryType(journal, AccountType.ASSET);
	        case LIABILITIES -> existsCategoryType(journal, AccountType.LIABILITIES);
	        case EQUITY -> existsCategoryType(journal, AccountType.EQUITY);
	        case INCOME -> existsCategoryType(journal, AccountType.INCOME);
	        case EXPENSE -> existsCategoryType(journal, AccountType.EXPENSE);

	        case FIXED -> existsFixedCategoryOption(journal);

	        default -> null;
	    };
	}

	private BooleanExpression existsCategoryType(QJournalEntity journal, AccountType accountType) {
	    QEntryLineEntity subEntryLine = new QEntryLineEntity("subEntryLine");
	    QAccountEntity subAccount = new QAccountEntity("subAccount");
	    QCategoryEntity subCategory = new QCategoryEntity("subCategory");

	    return JPAExpressions
	            .selectOne()
	            .from(subEntryLine)
	            .join(subEntryLine.account, subAccount)
	            .join(subAccount.category, subCategory)
	            .where(
	                    subEntryLine.journal.eq(journal),
	                    subCategory.type.eq(accountType)
	            )
	            .exists();
	}

	private BooleanExpression existsFixedCategoryOption(QJournalEntity journal) {
	    QEntryLineEntity subEntryLine = new QEntryLineEntity("subEntryLine");
	    QAccountEntity subAccount = new QAccountEntity("subAccount");
	    QCategoryOptionEntity subCategoryOption = new QCategoryOptionEntity("subCategoryOption");

	    return JPAExpressions
	            .selectOne()
	            .from(subEntryLine)
	            .join(subEntryLine.account, subAccount)
	            .join(subAccount.categoryOption, subCategoryOption)
	            .where(
	                    subEntryLine.journal.eq(journal),
	                    subCategoryOption.optionCode.in("FIXED_INCOME", "FIXED_EXPENSE")
	            )
	            .exists();
	}
	
	private BooleanExpression containsKeyword(String keyword, QJournalEntity journalEntity) {
		if(keyword == null || keyword.trim().isEmpty()) {
			return null;
		}
		
		BooleanExpression memoContains = journalEntity.memo.contains(keyword);
		
		QEntryLineEntity subEntryLine = new QEntryLineEntity("subEntryLineKeyword");
		QAccountEntity subAccount = new QAccountEntity("subAccountKeyword");
		
		BooleanExpression accountNameContains = JPAExpressions
				.selectOne()
				.from(subEntryLine)
				.join(subEntryLine.account, subAccount)
				.where(
						subEntryLine.journal.eq(journalEntity),
						subAccount.accountName.contains(keyword)
				)
				.exists();
		
		return memoContains.or(accountNameContains);
	}
	
	/*
	조회 옵션에 따른 조인이 필요한지 조건여부에 쓰이기 위한 필드
	private static final Set<SearchOptions> JOIN_NEED_LIST = Collections.unmodifiableSet(
			EnumSet.of(
					SearchOptions.ASSET,
					SearchOptions.LIABILITIES,
					SearchOptions.EQUITY,
					SearchOptions.INCOME,
					SearchOptions.EXPENSE,
					SearchOptions.FIXED
			)
	);
	
	@Override
	public JournalThumbTransactionScroll findJournalThumbTransactionsNoFilter
		(Long ledgerId, LocalDate startDate, LocalDate endDate, int offset, SearchOptions searchOption, SortedOptions sortedOption) {
		
		TODO: 나중에 프론트에서 사용자가 pageSize를 직접 선택할 수 있을 때 파라미터로 전환
		 
		int pageSize = 30;
		
		QJournalEntity journal = QJournalEntity.journalEntity;
		QEntryLineEntity entryLine = QEntryLineEntity.entryLineEntity;
    QAccountEntity account = QAccountEntity.accountEntity;
    QCategoryEntity category = QCategoryEntity.categoryEntity;
    QCategoryOptionEntity categoryOption = QCategoryOptionEntity.categoryOptionEntity;
    
    var idQuery = qf.select(journal.id, journal.transactionDate).from(journal);
    
    JOIN_NEED_LIST 에서 FIXED 제외한 모든 조회 옵션은
    journal.entryLines -> EntryLineEntity
    entryLine.account  -> AccountEntity
    account.category   -> CategoryEntity
    의 Join을 필요로 함
    
    FIXED (고정거래) 옵션의 경우에만
    CategoryOptionEntity가 필요하므로 조건문에서 설정함.
    
    if(isSearchOptionJoinNeed(searchOption)) {
    	idQuery.leftJoin(journal.entryLines, entryLine)
    	       .leftJoin(entryLine.account, account)
    	       .leftJoin(account.category, category);
    	       
    	if(searchOption == SearchOptions.FIXED) {
    		idQuery.leftJoin(account.categoryOption, categoryOption);    		
    	}
    }
    
    limit에 +1의 이유는 프론트쪽 페이징 방식이 더보기(LoadMore) 방식이라서
    다음 데이터가 존재하는지 여부만 알면되기 때문에 + 1 연산으로 동작
    
    fetch(); 까지는 List<Tuple>을 반환함 -> select 절에서 id, transactionDate로 했기 때문에
    List<Long>으로 받기 위해서는 stream 호출해서 transactionDate는 버리고 id로만 다시 List로 생성함
    
    List<Long> ids = idQuery
    		.where(
    				journal.ledger.id.eq(ledgerId),
    				journal.transactionDate.between(startDate, endDate),
    				eqSearchOption(searchOption, journal, category, categoryOption)
    		)
    		.distinct()
    		.orderBy(getOrderOption(sortedOption, journal))
    		.offset(offset)
    		.limit(pageSize + 1)
    		.fetch().stream()
    			.map(tp -> tp.get(journal.id))
    			.toList();
    		
		
    if(ids.isEmpty()) {
    	return new JournalThumbTransactionScroll(Collections.emptyList(), false, offset);
    }
    
    boolean next = ids.size() > pageSize;
    List<Long> fetchIds = next ? ids.subList(0, pageSize) : ids;
    
    List<JournalEntity> journalList = qf
    		.selectFrom(journal)
    		.leftJoin(journal.entryLines, entryLine).fetchJoin()
    		.leftJoin(entryLine.account, account).fetchJoin()
    		.leftJoin(account.category, category).fetchJoin()
    		.where(journal.id.in(fetchIds))
    		.orderBy(getOrderOption(sortedOption, journal))
    		.fetch();
    
    List<JournalThumbTransaction> journalTransactionList = journalList.stream()
    		.distinct()
    		.map(jo -> new JournalThumbTransaction(
    				jo.getId(),
    				jo.getTransactionDate(),
    				jo.getTotalAmount(),
    				jo.getMemo(),
    				jo.getEntryLines().stream()
    						.map(line -> new JournalThumbTransaction.AccountSideInfo(
    								line.getAccount().getAccountName(),
    								line.getAccount().getCategory().getType(),
    								line.getType()
    				))
    				.toList()
    		))
    		.toList();
    
    return new JournalThumbTransactionScroll(journalTransactionList, next, offset + pageSize);
	}
	
	private OrderSpecifier<?>[] getOrderOption(SortedOptions option, QJournalEntity journal) {
    if (option == null) return new OrderSpecifier[]{journal.transactionDate.desc(), journal.id.desc()};

    return switch (option) {
        case OLDEST -> new OrderSpecifier[]{journal.transactionDate.asc(), journal.id.asc()};
        case HIGH_PRICE -> new OrderSpecifier[]{journal.totalAmount.desc(), journal.id.desc()};
        case LOW_PRICE -> new OrderSpecifier[]{journal.totalAmount.asc(), journal.id.asc()};
        default -> new OrderSpecifier[]{journal.transactionDate.desc(), journal.id.desc()};
    };
}

	private BooleanExpression eqSearchOption(
			SearchOptions option, 
	    QJournalEntity journal, 
	    QCategoryEntity category, 
	    QCategoryOptionEntity categoryOption
	) {
		
    if (option == null || option == SearchOptions.ALL) return null;

    return switch (option) {
        case TRANSACTION_INCOME -> journal.transactionType.eq(TransactionType.INCOME);
        case TRANSACTION_EXPENSE -> journal.transactionType.eq(TransactionType.EXPENSE);
        case TRANSACTION_TRANSFER -> journal.transactionType.eq(TransactionType.TRANSFER);

        case ASSET -> category.type.eq(AccountType.ASSET);
        case LIABILITIES -> category.type.eq(AccountType.LIABILITIES);
        case EQUITY -> category.type.eq(AccountType.EQUITY);
        case INCOME -> category.type.eq(AccountType.INCOME);
        case EXPENSE -> category.type.eq(AccountType.EXPENSE);
        
        case FIXED -> categoryOption.optionCode.in("FIXED_INCOME", "FIXED_EXPENSE");
        
        default -> null;
    };
	}

	private boolean isSearchOptionJoinNeed(SearchOptions option) {
		if(option == null) return false;
		return JOIN_NEED_LIST.contains(option);
	}
	*/
	
	/*
	 * 거래 내역 조회 초기 버전 (SearchOption, SortedOption 적용 X)
	 * 
	@Override
	public JournalThumbTransactionScroll findJournalThumbTransactionsNoFilter(
			Long ledgerId,
			LocalDate startDate,
			LocalDate endDate,
			int offset,
			SearchOptions searchOption,
			SortedOptions sortedOption
	) {
		int pageSize = 30;
		
		QJournalEntity journal = QJournalEntity.journalEntity;
		QEntryLineEntity entryLine = QEntryLineEntity.entryLineEntity;
    QAccountEntity account = QAccountEntity.accountEntity;
    QCategoryEntity category = QCategoryEntity.categoryEntity;

    // [Step 1] 페이징 대상 ID 조회 (기존과 동일)
    List<Long> ids = qf
        .select(journal.id)
        .from(journal)
        .where(
        		journal.ledger.id.eq(ledgerId),
            journal.transactionDate.between(startDate, endDate)
        )
        .orderBy(journal.transactionDate.desc(), journal.id.desc())
        .offset(offset)
        .limit(pageSize + 1)
        .fetch();

    if (ids.isEmpty()) return new JournalThumbTransactionScroll(Collections.emptyList(), false, 0);

    boolean next = ids.size() > pageSize;
    
    List<Long> fetchIds = next ? ids.subList(0, pageSize) : ids;
    
    // [Step 2] Fetch Join으로 데이터 가져오기 
    // transform 대신 일반 fetch()를 사용해 Tuple 리스트로 받습니다.
    List<JournalEntity> journals = qf
        .selectFrom(journal)
        .leftJoin(journal.entryLines, entryLine).fetchJoin()
        .leftJoin(entryLine.account, account).fetchJoin()
        .leftJoin(account.category, category).fetchJoin()
        .where(journal.id.in(fetchIds))
        .orderBy(journal.transactionDate.desc(), journal.id.desc())
        .fetch(); // transform을 쓰지 않아 Hibernate 에러가 발생하지 않음

    // [Step 3] 메모리에서 DTO로 조립 (Java Stream 활용)
    // fetchJoin 덕분에 entryLines에 접근해도 N+1이 발생하지 않습니다.
    List<JournalThumbTransaction> journalList = journals.stream()
        .distinct() // Join으로 인한 중복 엔티티 제거
        .map(j -> new JournalThumbTransaction(
            j.getId(),
            j.getTransactionDate(),
            j.getTotalAmount(),
            j.getMemo(),
            j.getEntryLines().stream()
                .map(line -> new JournalThumbTransaction.AccountSideInfo(
                    line.getAccount().getAccountName(),
                    line.getAccount().getCategory().getType(),
                    line.getType()
                ))
                .toList()
        ))
        .toList();
    return new JournalThumbTransactionScroll(journalList, next, offset + 30);
	}
	*/
}
