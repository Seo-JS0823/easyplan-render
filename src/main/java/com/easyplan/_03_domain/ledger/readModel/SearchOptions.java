package com.easyplan._03_domain.ledger.readModel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchOptions {
	// journal transaction search option
	ALL("전체 조회"),
	TRANSACTION_INCOME("수입 거래"),
	TRANSACTION_EXPENSE("지출 거래"),
	TRANSACTION_TRANSFER("이체 거래"),
	ASSET("자산 항목"),
	INCOME("수입 항목"),
	EQUITY("기초 자산 항목"),
	LIABILITIES("부채 항목"),
	EXPENSE("지출 항목"),
	FIXED("고정거래 조회"),
	;
	private final String optionName;
}
