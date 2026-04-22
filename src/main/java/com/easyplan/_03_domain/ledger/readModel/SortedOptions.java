package com.easyplan._03_domain.ledger.readModel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortedOptions {
	LATEST("최신순"),
	OLDEST("오래된 순"),
	HIGH_PRICE("금액 높은순"),
	LOW_PRICE("금액 낮은순"),
	;
	private final String optionName;
}
