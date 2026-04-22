package com.easyplan._04_infra.jpa.ledger.entity;

import java.time.Instant;

import com.easyplan._03_domain.ledger.model.journal.EntryLine;
import com.easyplan._03_domain.ledger.model.journal.EntryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entry_line", indexes = {
		@Index(name = "idx_journal_account",
				   columnList = "journal_id, account_id"),
		@Index(name = "idx_acc_type_amt",
		       columnList = "account_id, entry_Type, amount"),
		@Index(name = "idx_el_lookup_summary",
		       columnList = "journal_id, account_id, entry_Type, amount")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class EntryLineEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journal_id", nullable = false)
	private JournalEntity journal;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private AccountEntity account;
	
	@Column(name = "entry_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private EntryType type;
	
	@Column(name = "amount", nullable = false)
	private Long amount;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	public static EntryLineEntity from(EntryLine entryLine, JournalEntity journal, AccountEntity account) {
		return EntryLineEntity.builder()
				.id(entryLine.getId())
				.journal(journal)
				.account(account)
				.type(entryLine.getType())
				.amount(entryLine.getAmount())
				.createdAt(entryLine.getCreatedAt())
				.updatedAt(entryLine.getUpdatedAt())
				.build();
	}
	
	public EntryLine toDomain() {
		return EntryLine.builder()
				.id(id)
				.journalId(journal.getId())
				.accountId(account.getId())
				.type(type)
				.amount(amount)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}
	
	public void changeAccount(AccountEntity account) {
		this.account = account;
	}
	
	public void changeAmount(Long amount, Instant now) {
		this.amount = amount;
		onUpdate(now);
	}
	
	public void setJournal(JournalEntity journal) {
		this.journal = journal;
	}
	
	public void onUpdate(Instant now) {
		this.updatedAt = now;
	}
}
