package com.easyplan._01_web.in.ledger.request;

import com.easyplan._02_app.command.LedgerCommand;
import com.easyplan._02_app.command.LedgerCommand.CategoryOptionUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryOptionUpdateRequest {
	private Long ledgerId;
	
	private Long accountId;
	
	private Long optionId;
	
	public LedgerCommand.CategoryOptionUpdate toCommand() {
		return new CategoryOptionUpdate(ledgerId, accountId, optionId);
	}
}
