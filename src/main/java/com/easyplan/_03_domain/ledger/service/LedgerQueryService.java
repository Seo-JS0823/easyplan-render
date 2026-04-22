package com.easyplan._03_domain.ledger.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.journal.TransactionType;
import com.easyplan._03_domain.ledger.readModel.DashboardMetadataGroup;
import com.easyplan._03_domain.ledger.readModel.LedgerCategorisWithAccounts;
import com.easyplan._03_domain.ledger.repository.LedgerQueryRepository;
import com.easyplan._03_domain.ledger.repository.LedgerRepository;
import com.easyplan._04_infra.dsl.readModel.CurrentMonthRankExpenseDTO;
import com.easyplan._04_infra.dsl.readModel.NetWorthDTO;
import com.easyplan._04_infra.dsl.readModel.RecentTransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LedgerQueryService {
	private final LedgerRepository ledgerRepo;
	
	private final LedgerQueryRepository ledgerQueryRepo;
	
	private final Executor dashboardExecutor;
	
	public DashboardMetadataGroup dashboardMetadateGroup(Long owenerId, Long ledgerId, LocalDate startDate, LocalDate endDate) {
		
		/*
		DashboardMetadataGroup metadata = new DashboardMetadataGroup();
		
		long s1 = System.currentTimeMillis();
		metadata.setNetWorth(dashboardQueryGroupNetWorth(ledgerId));
		log.info("dashboardQueryGroupNetWorth={}ms", System.currentTimeMillis() - s1);
		
		long s2 = System.currentTimeMillis();
		metadata.setMonthRank(dashboardQueryGroupCurrentMonthRank(ledgerId, startDate, endDate));
		log.info("dashboardQueryGroupCurrentMonthRank={}ms", System.currentTimeMillis() - s2);
		
		long s3 = System.currentTimeMillis();
		metadata.setRecentExpense(recentExpenseTop5(ledgerId));
		log.info("recentExpenseTop5={}ms", System.currentTimeMillis() - s3);
		
		long s4 = System.currentTimeMillis();
		metadata.setRecentIncome(recentIncomeTop5(ledgerId));
		log.info("recentIncomeTop5={}ms", System.currentTimeMillis() - s4);		
		
		return metadata;
		*/
		
		/*
		 * Version 2 --> 병렬처리 CompletableFuture 사용
		 * 
		 * 처음쓰니까 주석
		 * Version 1 로직이 전부다 I/O 작업이기 때문에 병렬처리했을 때 효과가 있음.
		 * 각자 독립적인 쿼리이기 때문에 병렬처리하기 최적인 상황
		 * 호출쪽에서 System.currentTimeMillis() 로 계산해본 결과 약 800ms 에서 300ms 정도로 줄음
		 * 
		 * 지금 구조는 요청 1개 -> 쿼리 4개 -> 병렬 실행
		 * 즉 요청 1개당 DB 커넥션 4개를 동시에 사용하는 구조임.
		 * 
		 * CompletableFuture.supplyAsync(...) 이 메서드가 기본적으로
		 * 공용 ForkJoinPool을 사용함
		 * 
		 * 이 ForkJoinPool의 문제점은
		 * 1. 다른 async 작업과 스레드 공유
		 * 2. CPU 작업이 끼어들면 DB 작업이 밀림
		 * 3. 예측 불가능한 latency(지연시간) 가 발생할 수 있음
		 * 그리고 이 작업은 호출자쪽에 Transactional(readOnly = true)가 걸려있어야 한다.
		 * @Transactional 안에서 호출하면 병렬 쓰레드는 트랜잭션이 공유되지 않음.
		 */
		
		CompletableFuture<NetWorthDTO> netWorthFuture = CompletableFuture.supplyAsync(() ->
					dashboardQueryGroupNetWorth(ledgerId), dashboardExecutor);
		
		CompletableFuture<CurrentMonthRankExpenseDTO> currentMonthRankFuture = CompletableFuture.supplyAsync(() ->
					dashboardQueryGroupCurrentMonthRank(ledgerId, startDate, endDate), dashboardExecutor);
		
		CompletableFuture<List<RecentTransaction>> recentExpenseTop5Future = CompletableFuture.supplyAsync(() ->
					recentExpenseTop5(ledgerId), dashboardExecutor);
		
		CompletableFuture<List<RecentTransaction>> recentIncomeTop5Future = CompletableFuture.supplyAsync(() ->
					recentIncomeTop5(ledgerId), dashboardExecutor);
		
		DashboardMetadataGroup metadata = new DashboardMetadataGroup();
		try {
			CompletableFuture.allOf(
					netWorthFuture,
					currentMonthRankFuture,
					recentExpenseTop5Future,
					recentIncomeTop5Future
			).join();
			
			metadata.setNetWorth(netWorthFuture.join());
			metadata.setMonthRank(currentMonthRankFuture.join());
			metadata.setRecentExpense(recentExpenseTop5Future.join());
			metadata.setRecentIncome(recentIncomeTop5Future.join());
		} catch (CompletionException e) {
			log.error("대시보드 메인 데이터 조회 오류 발생", e.getCause());
		} catch (Exception e) {
			log.error("대시보드 메인 데이터 조회 오류 발생", e);
		}
		
		return metadata;
	}
	
	public List<CategoryOption> findOptionsAll() {
		return ledgerRepo.getCategoryOptionAll();
	}
	
	public List<LedgerCategorisWithAccounts> dashboardMetadata(Long owenrId) {
		return ledgerQueryRepo.dashboardMetadata(owenrId);
	}
	
	private NetWorthDTO dashboardQueryGroupNetWorth(Long ledgerId) {
		return ledgerQueryRepo.dashboardNetWorth(ledgerId);
	}
	
	private CurrentMonthRankExpenseDTO dashboardQueryGroupCurrentMonthRank(Long ledgerId, LocalDate startDate, LocalDate endDate) {
		return ledgerQueryRepo.dashboardCurrentMonthRankExpense(ledgerId, startDate, endDate);
	}
	
	private List<RecentTransaction> recentExpenseTop5(Long ledgerId) {
		return ledgerQueryRepo.recentTransactionList(ledgerId, RecentType.EXPENSE);
	}
	
	private List<RecentTransaction> recentIncomeTop5(Long ledgerId) {
		return ledgerQueryRepo.recentTransactionList(ledgerId, RecentType.INCOME);
	}
	
	public static enum RecentType {
		EXPENSE(TransactionType.EXPENSE, AccountType.EXPENSE, List.of(AccountType.ASSET, AccountType.LIABILITIES)),
		
		INCOME(TransactionType.INCOME, AccountType.ASSET, List.of(AccountType.INCOME))
		
		;
		private final TransactionType transactionType;
		
		private final AccountType debit;
		
		private final List<AccountType> credit;
		
		RecentType(TransactionType transactionType, AccountType debit, List<AccountType> credit) {
			this.transactionType = transactionType;
			this.debit = debit;
			this.credit = credit;
		}

		public TransactionType getTransactionType() {
			return transactionType;
		}
		
		public AccountType getDebit() {
			return debit;
		}
		
		public List<AccountType> getCredit() {
			return credit;
		}
	}
}
