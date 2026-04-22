package com.easyplan._03_domain.ledger.service;

import java.time.LocalDate;

import com.easyplan._03_domain.ledger.readModel.JournalThumbTransactionScroll;
import com.easyplan._03_domain.ledger.readModel.SearchOptions;
import com.easyplan._03_domain.ledger.readModel.SortedOptions;
import com.easyplan._03_domain.ledger.repository.JournalQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JournalQueryService {
	private final JournalQueryRepository journalQR;
	
	public JournalThumbTransactionScroll getTransactionThumbHistory(
			Long ledgerId,
			LocalDate startDate,
			LocalDate endDate,
			int offset,
			SearchOptions searchOption,
			SortedOptions sortedOption,
			String keyword
	) {
		return journalQR.findJournalThumbTransactions(ledgerId, startDate, endDate, offset, searchOption, sortedOption, keyword);
	}
}
