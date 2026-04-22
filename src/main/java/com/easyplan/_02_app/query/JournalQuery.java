package com.easyplan._02_app.query;

import java.time.LocalDate;

import com.easyplan._03_domain.ledger.readModel.SearchOptions;
import com.easyplan._03_domain.ledger.readModel.SortedOptions;

public class JournalQuery {
	public record JournalTransactionList(
			Long ledger,
			LocalDate startDate,
			LocalDate endDate,
			int offset,
			SearchOptions searchOption,
			SortedOptions sortedOptions,
			String keyword
	) {}
	
	
}
