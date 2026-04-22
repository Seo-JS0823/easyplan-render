package com.easyplan._01_web.in.ledger.request;

import java.time.LocalDate;

import com.easyplan._02_app.command.JournalCommand;
import com.easyplan._02_app.command.JournalCommand.JournalUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JournalUpdateRequest {
	@JsonProperty("journal")
	private Long journalId;
	
	private LocalDate transactionDate;
	
	private Long creditAccountId;
	
	private Long debitAccountId;
	
	private String memo;
	
	private Long totalAmount;
	
	public JournalCommand.JournalUpdate toCommand() {
		return new JournalUpdate(journalId, transactionDate, creditAccountId, debitAccountId, memo, totalAmount);
	}
}
