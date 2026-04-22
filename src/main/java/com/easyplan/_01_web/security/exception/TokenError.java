package com.easyplan._01_web.security.exception;

import com.easyplan.shared.exception.GlobalError;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenError implements GlobalError {

	;
	private final int status;
	
	private final String message;
}
