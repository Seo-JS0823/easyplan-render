package com.easyplan._03_domain.ledger.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
	ALL(AccountSide.NOT_SIDE, "ALL"),
	ASSET(AccountSide.DEBIT, "자산"),
	LIABILITIES(AccountSide.CREDIT, "부채"),
	EQUITY(AccountSide.CREDIT, "기초 자산"),
	INCOME(AccountSide.CREDIT, "수입"),
	EXPENSE(AccountSide.DEBIT, "지출")
	;
	private final AccountSide accountSide;
	
	private final String categoryName;
	
}
