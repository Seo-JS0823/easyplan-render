package com.easyplan._01_web.in.ledger.request;

import java.time.LocalDate;

import com.easyplan._02_app.command.JournalCommand;
import com.easyplan._02_app.command.JournalCommand.PostJournal;
import com.easyplan._03_domain.ledger.model.journal.TransactionType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JournalCreateRequest {
	private Long ledgerId;
	
	private Long debitAccountId;
	
	private Long creditAccountId;
	
	private Long amount;
	
	private String memo;
	
	private String date;
	
	private TransactionType transactionType;
	
	public JournalCommand.PostJournal toCommand(Long createdOwnerId) {
		return new PostJournal(
				createdOwnerId,
				ledgerId,
				debitAccountId,
				creditAccountId,
				amount,
				memo,
				LocalDate.parse(date),
				transactionType);
	}
}