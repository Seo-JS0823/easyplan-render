package com.easyplan._03_domain.ledger.readModel;

import java.util.List;

import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.ledger.LedgerType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class LedgerCategorisWithAccounts {
	private final Long ledgerId;
	
	private final LedgerType type;
	
	private final String ledgerName;
	
	private final List<CategoriesWithAccount> categories;
	
	@Getter
	@AllArgsConstructor
	public static final class CategoriesWithAccount {
		private final Long categoryId;
		
		private final String categoryName;
		
		private final List<AccountQuery> account; 
	}
	
	@Getter
	@AllArgsConstructor
	public static final class AccountQuery {
		private final Long accountId;
		
		private final String accountName;
		
		private final Long optionId;
		
		private final List<CategoryOption> options;
		
		private final boolean payment;
	}
}
