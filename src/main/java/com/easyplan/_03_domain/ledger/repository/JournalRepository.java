package com.easyplan._03_domain.ledger.repository;

import java.util.List;

import com.easyplan._03_domain.ledger.model.journal.EntryLine;
import com.easyplan._03_domain.ledger.model.journal.Journal;

public interface JournalRepository {
	Journal journalCreate(Journal journal, List<EntryLine> entryLine);
	
	Journal getJournal(Long journalId);
	
	Journal getJournalAndEntry(Long journalId);
	
	EntryLine getEntryLine(Long entryLineId);

	Journal journalUpdate(Journal journal);
}
