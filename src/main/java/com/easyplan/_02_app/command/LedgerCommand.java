package com.easyplan._02_app.command;

import java.util.List;

import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.DefaultCategories;
import com.easyplan._03_domain.ledger.model.ledger.LedgerType;

public class LedgerCommand {
	public record LedgerCreate(String publicId, String name, String description, LedgerType type, List<DefaultCategories> categories) {
		
	}
	
	public record CategoryOptionUpdate(Long ledgerId, Long accountId, Long optionId) {
		
	}
	
	public record AccountCreate(Long ledgerId, Long optionId, Long categoryId, String accountName, String memo) {
		public Account account() {
			Account account =  Account.create(ledgerId, categoryId, accountName, memo, true, null);
			account.setCreateOfCategoryOption(optionId);
			return account;
		}
	}
}
