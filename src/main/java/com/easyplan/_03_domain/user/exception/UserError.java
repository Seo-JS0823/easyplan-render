package com.easyplan._03_domain.user.exception;

import com.easyplan.shared.exception.GlobalError;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserError implements GlobalError {
	IN_EMAIL_ERROR(400, "올바른 이메일 형식으로 입력하세요."),
	IN_PASSWORD_ERROR(400, "영문 소문자, 숫자, 특수문자를 포함한 9자 이상이어야 합니다."),
	IN_NICKNAME_ERROR(400, "2~10자 사이의 한글, 영문, 숫자여야 합니다."),
	IN_PUBLIC_ID_ERROR(400, "식별할 수 없는 회원식별자 입니다."),
	IN_PASSWORD_HASH_ERROR(400, "지원하지 않는 비밀번호 암호화 형식입니다."),
	IN_BIRTHDAY_ERROR(400, "존재하지 않는 날짜입니다."),
	
	DUPLICATE_EMAIL(400, "이미 사용중인 이메일입니다."),
	DUPLICATE_NICKNAME(400, "이미 사용중인 닉네임입니다."),
	
	USER_NOT_FOUND(400, "존재하지 않는 사용자입니다."),
	NOT_UPDATED_LIST(400, "변경 목록에 존재하지 않는 코드"),
	LOGIN_NOT_MATCH(400, "아이디와 비밀번호가 일치하지 않습니다."),
	
	CANNOT_MODIFY_DELETED_USER(400, "해당 계정은 삭제 대기중인 계정입니다."),
	;
	private final int status;
	
	private final String message;
}
