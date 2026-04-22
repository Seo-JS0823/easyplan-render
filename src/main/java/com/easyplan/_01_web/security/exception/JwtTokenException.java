package com.easyplan._01_web.security.exception;

import com.easyplan.shared.exception.GlobalError;
import com.easyplan.shared.exception.GlobalException;

public class JwtTokenException extends GlobalException {

	public JwtTokenException(GlobalError error) {
		super(error);
	}

}
