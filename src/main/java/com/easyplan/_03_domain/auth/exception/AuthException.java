package com.easyplan._03_domain.auth.exception;

import com.easyplan.shared.exception.GlobalError;
import com.easyplan.shared.exception.GlobalException;

public class AuthException extends GlobalException {

	public AuthException(GlobalError error) {
		super(error);
	}

}
