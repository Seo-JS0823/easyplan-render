package com.easyplan.shared.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
	private final GlobalError error;
	
	public GlobalException(GlobalError error) {
		super(error.getMessage());
		this.error = error;
	}
}
