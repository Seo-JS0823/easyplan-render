package com.easyplan._03_domain.ledger.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategoryOption {
	private Long id;
	
	private String optionCode;
	
	private AccountType accountType;
	
	private String optionName;
	
	public static CategoryOption create(String optionCode, AccountType accountType, String optionName) {
		return CategoryOption.builder()
				.optionCode(optionCode)
				.accountType(accountType)
				.optionName(optionName)
				.build();
	}
}
