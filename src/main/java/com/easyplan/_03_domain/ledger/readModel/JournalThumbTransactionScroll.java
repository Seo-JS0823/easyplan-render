package com.easyplan._03_domain.ledger.readModel;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;

@Getter
public class JournalThumbTransactionScroll {
	private final List<JournalThumbTransaction> journal;
	
	private final boolean next;
	
	private String nextURL = "";
	
	public JournalThumbTransactionScroll(List<JournalThumbTransaction> journal, boolean next, int nextOffset) {
		this.journal = journal;
		this.next = next;
	}
	
	public String setNextRequestUrlHint(Long ledgerId, LocalDate startDate, LocalDate endDate, int nextOffset, SearchOptions searchOption, SortedOptions sortedOption, String keyword) {
		if(this.next) {
			int offset = nextOffset + 30;
			String nextPath = "/api/journal/search?ledger=" + ledgerId + "&startDate=" + startDate + "&endDate=" + endDate + "&offset=" + offset + "&search-option=" + searchOption.name() + "&sorted-option=" + sortedOption.name() + "&keyword=" + keyword;
			this.nextURL = nextPath;
			return nextPath;
		}
		return "";
	}
}
