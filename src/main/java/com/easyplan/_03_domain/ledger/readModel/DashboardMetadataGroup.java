package com.easyplan._03_domain.ledger.readModel;

import java.util.List;

import com.easyplan._04_infra.dsl.readModel.CurrentMonthRankExpenseDTO;
import com.easyplan._04_infra.dsl.readModel.NetWorthDTO;
import com.easyplan._04_infra.dsl.readModel.RecentTransaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardMetadataGroup {
	private List<LedgerCategorisWithAccounts> ledgerMeta;
	
	private NetWorthDTO netWorth;
	
	private CurrentMonthRankExpenseDTO monthRank;
	
	private List<RecentTransaction> recentExpense;
	
	private List<RecentTransaction> recentIncome;
}
