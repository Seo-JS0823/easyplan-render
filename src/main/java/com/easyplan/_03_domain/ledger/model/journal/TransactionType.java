package com.easyplan._03_domain.ledger.model.journal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
	EXPENSE("지출", true),
	INCOME("수입", true),
	TRANSFER("이체", false)
	;
	
	private final String description;
	
	private final boolean isStatisticsTarget;
}
