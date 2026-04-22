package com.easyplan._03_domain.auth.exception;

import com.easyplan.shared.exception.GlobalError;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthError implements GlobalError {

	NOT_FOUND_RT(401, ""),
	
	EXIST_AUTH(401, "인증정보가 존재하지 않습니다."),
	;
	private final int status;
	
	private final String message;
}
