package com.easyplan._03_domain.ledger.repository;

import java.time.LocalDate;

import com.easyplan._03_domain.ledger.readModel.JournalThumbTransactionScroll;
import com.easyplan._03_domain.ledger.readModel.SearchOptions;
import com.easyplan._03_domain.ledger.readModel.SortedOptions;

public interface JournalQueryRepository {

	JournalThumbTransactionScroll findJournalThumbTransactions
		(Long ledgerId, LocalDate startDate, LocalDate endDate, int offset, SearchOptions searchOption, SortedOptions sortedOption, String keyword);
}
