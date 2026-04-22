package com.easyplan._02_app;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._02_app.command.JournalCommand;
import com.easyplan._02_app.query.JournalQuery;
import com.easyplan._03_domain.ledger.model.journal.EntryLine;
import com.easyplan._03_domain.ledger.model.journal.Journal;
import com.easyplan._03_domain.ledger.readModel.JournalThumbTransactionScroll;
import com.easyplan._03_domain.ledger.service.JournalQueryService;
import com.easyplan._03_domain.ledger.service.JournalService;
import com.easyplan.shared.time.Clock;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalApplication {
	private final JournalService journalService;
	
	private final JournalQueryService journalQS;
	
	private final Clock clock;

	/*
	 * 거래 입력
	 * 
	 * 
	 */
	@Transactional
	public Journal postJournal(JournalCommand.PostJournal postJournal) {
		Long createdOwnerId = postJournal.createdOwnerId();
		Long ledgderId = postJournal.ledgerId();
		Long debitAccountId = postJournal.debitAccountId();
		Long creditAccountId = postJournal.creditAccountId();
		Long amount = postJournal.amount();
		String memo = postJournal.memo();
		LocalDate date = postJournal.transactionDate();
		Instant now = clock.now();
		
		Journal journal = journalService.transactionCreate(createdOwnerId, ledgderId, debitAccountId, creditAccountId, amount, memo, date, now, postJournal.transactionType());
		
		return journal;
	}
	
	@Transactional
	public Journal journalUpdate(JournalCommand.JournalUpdate journalUpdate) {
		Long journalId = journalUpdate.journalId();
		Long creditAccountId = journalUpdate.creditAccountId();
		Long debitAccountId = journalUpdate.debitAccountId();
		LocalDate transactionDate = journalUpdate.transactionDate();
		Long totalAmount = journalUpdate.totalAmount();
		String memo = journalUpdate.memo();
		
		Journal journal = journalService.getJournalById(journalId);
		
		return journalService.updateJournal(journal, creditAccountId, debitAccountId, transactionDate, memo, totalAmount, clock.now());
	}
	
	@Transactional(readOnly = true)
	public JournalThumbTransactionScroll getTransactionThumbHistory(JournalQuery.JournalTransactionList journal) {
		return journalQS.getTransactionThumbHistory(
				journal.ledger(),
				journal.startDate(),
				journal.endDate(),
				journal.offset(),
				journal.searchOption(),
				journal.sortedOptions(),
				journal.keyword());
	}
	
	@Transactional(readOnly = true)
	public Journal getJournalUpdateBeginData(Long journalId) {
		return journalService.getJournalById(journalId);
	}
	
}
