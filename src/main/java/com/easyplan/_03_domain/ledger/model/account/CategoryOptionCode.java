package com.easyplan._03_domain.ledger.model.account;

import java.util.List;

import io.jsonwebtoken.lang.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryOptionCode {
	GENERAL("일반", AccountType.ALL),
	
	LIQUID("유동성 자산", AccountType.ASSET),
	INVESTMENT("투자 자산", AccountType.ASSET),
	
	CREDIT_CARD("신용카드", AccountType.LIABILITIES),
	LOAN("대출금", AccountType.LIABILITIES),
	OVERDRAFT("마이너스 통장", AccountType.LIABILITIES),
	
	VARIABLE_INCOME("유동 수익", AccountType.INCOME),
	FIXED_INCOME("고정 수익", AccountType.INCOME),
	
	VARIABLE_EXPENSE("유동 비용", AccountType.EXPENSE),
	FIXED_EXPENSE("고정 비용", AccountType.EXPENSE),
	
	;
	private final String optionName;
	
	private final AccountType accountType;
	
	public static List<CategoryOption> defaultOptions() {
		List<CategoryOption> defaultOptionList = Arrays.asList(CategoryOptionCode.values()).stream()
				.map(option -> {
					CategoryOption categoryOption = CategoryOption.create(option.name(), option.getAccountType(), option.getOptionName());
					
					return categoryOption;
				})
				.toList();
		
		return defaultOptionList;
	}
}
