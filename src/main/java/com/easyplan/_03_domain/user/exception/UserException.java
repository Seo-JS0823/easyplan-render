package com.easyplan._03_domain.user.exception;

import com.easyplan.shared.exception.GlobalError;
import com.easyplan.shared.exception.GlobalException;

public class UserException extends GlobalException {

	public UserException(GlobalError error) {
		super(error);
	}
	
}
