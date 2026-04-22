package com.easyplan._03_domain.ledger.exception;

import com.easyplan.shared.exception.GlobalError;
import com.easyplan.shared.exception.GlobalException;

public class LedgerException extends GlobalException {

	public LedgerException(GlobalError error) {
		super(error);
	}
	
}
