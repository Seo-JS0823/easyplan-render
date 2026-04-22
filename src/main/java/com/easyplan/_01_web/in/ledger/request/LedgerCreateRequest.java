package com.easyplan._01_web.in.ledger.request;

import java.util.List;

import com.easyplan._02_app.command.LedgerCommand;
import com.easyplan._02_app.command.LedgerCommand.LedgerCreate;
import com.easyplan._03_domain.ledger.model.account.DefaultCategories;
import com.easyplan._03_domain.ledger.model.ledger.LedgerType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LedgerCreateRequest {
	private String ledgerName;
	
	private String ledgerDescription;
	
	private LedgerType ledgerType;
	
	private List<DefaultCategories> categories;
	
	public LedgerCommand.LedgerCreate toCommand(String publicId) {
		return new LedgerCreate(publicId, ledgerName, ledgerDescription, ledgerType, categories);
	}
}
