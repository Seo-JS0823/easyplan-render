package com.easyplan._03_domain.ledger.repository;

import java.time.LocalDate;
import java.util.List;

import com.easyplan._03_domain.ledger.readModel.LedgerCategorisWithAccounts;
import com.easyplan._03_domain.ledger.service.LedgerQueryService.RecentType;
import com.easyplan._04_infra.dsl.readModel.CurrentMonthRankExpenseDTO;
import com.easyplan._04_infra.dsl.readModel.NetWorthDTO;
import com.easyplan._04_infra.dsl.readModel.RecentTransaction;

public interface LedgerQueryRepository {
	List<LedgerCategorisWithAccounts> dashboardMetadata(Long ownerId);
	
	NetWorthDTO dashboardNetWorth(Long ledgerId);
	
	CurrentMonthRankExpenseDTO dashboardCurrentMonthRankExpense(Long ledgerId, LocalDate startDate, LocalDate endDate);
	
	List<RecentTransaction> recentTransactionList(Long ledgerId, RecentType recentType);
}
