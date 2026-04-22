package com.easyplan._03_domain.ledger.readModel;

import java.util.List;

import com.easyplan._03_domain.ledger.model.account.CategoryOption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryMetadata {
	private final List<LedgerCategorisWithAccounts> accounts;
	
	private final List<CategoryOption> options;
}
