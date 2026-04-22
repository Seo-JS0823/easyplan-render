package com.easyplan._03_domain.ledger.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.easyplan._03_domain.ledger.model.journal.EntryLine;
import com.easyplan._03_domain.ledger.model.journal.EntryType;
import com.easyplan._03_domain.ledger.model.journal.Journal;
import com.easyplan._03_domain.ledger.model.journal.TransactionType;
import com.easyplan._03_domain.ledger.repository.JournalRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalService {
	private final JournalRepository journalRepo;
	
	public Journal transactionCreate(
			Long createdOwnerId,
			Long ledgerId,
			Long debitAccountId,
			Long creditAccountId,
			Long amount,
			String memo,
			LocalDate transactionDate,
			Instant now,
			TransactionType transactionType) {
		Journal postJournal = Journal.create(ledgerId, createdOwnerId, transactionDate, memo, now, transactionType);
		
		List<EntryLine> entryLines = List.of(
				EntryLine.create(debitAccountId, EntryType.DEBIT, amount, now),
				EntryLine.create(creditAccountId, EntryType.CREDIT, amount, now)
		);
		
		postJournal.setEntryLines(entryLines);
		
		Journal savedJournal = journalRepo.journalCreate(postJournal, entryLines);
		
		return savedJournal;
	}
	
	public Journal getJournalById(Long journalId) {
		return journalRepo.getJournalAndEntry(journalId);
	}
	
	public EntryLine getEntryLine(Long entryLineId) {
		return journalRepo.getEntryLine(entryLineId);
	}
	
	public Journal updateJournal(
			Journal journal,
			Long creditAccountId,
			Long debitAccountId,
			LocalDate transactionDate,
			String memo,
			Long totalAmount,
			Instant now) {
		
		journal.changeEntryLine(debitAccountId, creditAccountId, totalAmount, now);
		journal.changeMemo(memo);
		journal.changeTransactionDate(transactionDate);
		journal.onUpdate(now);
		
		return journalRepo.journalUpdate(journal);
	}
	
}
