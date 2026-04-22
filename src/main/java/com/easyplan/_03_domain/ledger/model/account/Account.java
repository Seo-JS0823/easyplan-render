package com.easyplan._03_domain.ledger.model.account;

import com.easyplan._03_domain.ledger.exception.LedgerError;
import com.easyplan._03_domain.ledger.exception.LedgerException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Account {
	private static final int MAX_NAME_LENGTH = 30;
	
	private Long id;
	
	private Long ledgerId;
	
	private Long categoryId;
	
	private Long categoryOptionId;
	
	@Builder.Default
	private AccountStatus status = AccountStatus.ACTIVE;
	
	private String accountName;
	
	private String description;
	
	private boolean payment;
	
	private String categoryOptionCode;
	
	public static Account create(Long ledgerId, Long categoryId, String accountName, String description, boolean payment, String categoryOptionCode) {
		if(accountName == null || accountName.length() > MAX_NAME_LENGTH) {
			throw new LedgerException(LedgerError.ACCOUNT_NAME_OVER_LENGTH);
		}
		
		return Account.builder()
				.id(null)
				.ledgerId(ledgerId)
				.categoryId(categoryId)
				.accountName(accountName)
				.description(description)
				.payment(payment)
				.categoryOptionCode(categoryOptionCode)
				.build();
	}
	
	public void changeCategoryOption(Long categoryOptionId) {
		this.categoryOptionId = categoryOptionId;
	}
	
	public void setCreateOfCategoryOption(Long categoryOptionId) {
		this.categoryOptionId = categoryOptionId;		
	}
	
}
