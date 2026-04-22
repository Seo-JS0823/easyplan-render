package com.easyplan._03_domain.ledger.readModel;

import java.util.List;

import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.Category;
import com.easyplan._03_domain.ledger.model.ledger.Ledger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LedgerGroupDTO {
	private Ledger ledger;
	
	private List<Category> categories;
	
	private List<Account> accountList;
}
