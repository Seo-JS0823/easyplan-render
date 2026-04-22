package com.easyplan._02_app.command;

import java.time.LocalDate;

import com.easyplan._03_domain.ledger.model.journal.TransactionType;

public class JournalCommand {
	public record PostJournal(Long createdOwnerId, Long ledgerId, Long debitAccountId, Long creditAccountId, Long amount, String memo, LocalDate transactionDate, TransactionType transactionType) {
		
	}
	
	public record JournalUpdate(Long journalId, LocalDate transactionDate, Long creditAccountId, Long debitAccountId, String memo, Long totalAmount) {
		
	}
}
