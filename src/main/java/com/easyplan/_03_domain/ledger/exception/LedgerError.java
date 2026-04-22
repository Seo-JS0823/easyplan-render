package com.easyplan._03_domain.ledger.exception;

import com.easyplan.shared.exception.GlobalError;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LedgerError implements GlobalError {
	ACCOUNT_NAME_OVER_LENGTH(400, "계정 항목 이름은 30자 이내여야 합니다."),
	
	NOT_FOUND_LEDGER(400, "존재하지 않는 가계부입니다."),
	
	
	;
	private final int status;
	
	private final String message;
}
