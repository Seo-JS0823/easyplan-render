package com.easyplan._01_web.in.ledger.request;

import com.easyplan._02_app.command.LedgerCommand;
import com.easyplan._02_app.command.LedgerCommand.AccountCreate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountCreateRequest {
	private Long ledgerId;
	
	private Long optionId;
	
	private Long categoryId;
	
	private String accountName;
	
	private String memo;
	
	public LedgerCommand.AccountCreate toCommand() {
		return new AccountCreate(ledgerId, optionId, categoryId, accountName, memo);
	}
}
