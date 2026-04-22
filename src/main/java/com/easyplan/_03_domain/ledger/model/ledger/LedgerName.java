package com.easyplan._03_domain.ledger.model.ledger;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LedgerName {

	private static final int MAX_NAME_LENGTH = 20;
	
	private final String value;
	
	private LedgerName(String value) {
		this.value = value;
	}
	
	public static LedgerName of(String value) {
		if(value == null || value.isBlank() || value.length() > MAX_NAME_LENGTH) {
			
		}
		return new LedgerName(value);
	}
}
