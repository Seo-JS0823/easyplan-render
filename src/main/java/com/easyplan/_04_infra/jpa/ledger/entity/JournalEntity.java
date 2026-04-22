package com.easyplan._04_infra.jpa.ledger.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.easyplan._03_domain.ledger.model.journal.Journal;
import com.easyplan._03_domain.ledger.model.journal.TransactionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "journal_entries", indexes= {
		@Index(name = "idx_ledger_tDate_id",
				   columnList = "ledger_id, transaction_date, id"),
		@Index(name = "idx_ledger_type_date_id",
		       columnList = "ledger_id, transaction_type, transaction_date, id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class JournalEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_JOURNAL_LEDGER_ID"))
	private LedgerEntity ledger;
	
	@Column(name = "created_owner_id")
	private Long createdOwnerId;
	
	@Column(name = "transaction_date", nullable = false)
	private LocalDate transactionDate;
	
	@Column(name = "memo")
	private String memo;
	
	@Column(name = "total_amount")
	private Long totalAmount;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	
	@Column(name = "transaction_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@OneToMany(mappedBy = "journal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<EntryLineEntity> entryLines = new ArrayList<>();
	
	public void addEntryLines(EntryLineEntity entryLine) {
		this.entryLines.add(entryLine);
		entryLine.setJournal(this);
	}
	
	public void changeMemo(String memo) {
		this.memo = memo;
	}
	
	public void changeTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public void changeUpdatedAt(Instant now) {
		this.updatedAt = now;
	}
	
	public void changeTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public static JournalEntity from(Journal journal, LedgerEntity ledger) {
		return JournalEntity.builder()
				.id(journal.getId())
				.ledger(ledger)
				.createdOwnerId(journal.getCreatedOwnerId())
				.transactionDate(journal.getTransactionDate())
				.memo(journal.getMemo())
				.totalAmount(journal.getTotalAmount())
				.createdAt(journal.getCreatedAt())
				.updatedAt(journal.getUpdatedAt())
				.transactionType(journal.getTransactionType())
				.build();
	}
	
	public Journal toDomain() {
		return Journal.builder()
				.id(id)
				.ledgerId(ledger.getId())
				.createdOwnerId(createdOwnerId)
				.transactionDate(transactionDate)
				.memo(memo)
				.totalAmount(totalAmount)
				.entryLines(this.entryLines.stream().map(EntryLineEntity :: toDomain).toList())
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.transactionType(transactionType)
				.build();
	}
}
