package com.easyplan._03_domain.ledger.model.journal;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EntryLine {
	private Long id;
	
	private Long journalId;
	
	private Long accountId;
	
	private EntryType type;
	
	private Long amount;
	
	private Instant createdAt;
	
	private Instant updatedAt;
	
	public static EntryLine create(Long accountId, EntryType type, Long amount, Instant now) {
		return EntryLine.builder()
				.accountId(accountId)
				.type(type)
				.amount(amount)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
	
	public void updateEntryLine(Long accountId, Long amount, Instant now) {
		this.accountId = accountId;
		this.amount = amount;
		onUpdate(now);
	}
	
	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}
	
	private void onUpdate(Instant now) {
		this.updatedAt = now;
	}
	
}
