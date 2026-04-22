package com.easyplan._03_domain.ledger.model.journal;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Journal {
	private Long id;
	
	private Long ledgerId;
	
	private Long createdOwnerId;
	
	private LocalDate transactionDate;
	
	private String memo;
	
	private Long totalAmount;
	
	private List<EntryLine> entryLines;
	
	private Instant createdAt;
	
	private Instant updatedAt;
	
	private TransactionType transactionType;
	
	public static Journal create(Long ledgerId, Long createdOwnerId, LocalDate transactionDate, String memo, Instant now, TransactionType transactionType) {
		return Journal.builder()
				.id(null)
				.ledgerId(ledgerId)
				.createdOwnerId(createdOwnerId)
				.transactionDate(transactionDate)
				.memo(memo)
				.createdAt(now)
				.updatedAt(now)
				.transactionType(transactionType)
				.build();
	}
	
	public void changeTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public void changeMemo(String memo) {
		this.memo = memo;
	}
	
	public void changeEntryLine(Long debitAccountId, Long creditAccountId, Long amount, Instant now) {
		for(EntryLine line : this.entryLines) {
			if(line.getType() == EntryType.DEBIT) {
				line.updateEntryLine(debitAccountId, amount, now);
			} else {
				line.updateEntryLine(creditAccountId, amount, now);
			}
		}
		
		this.totalAmount = amount;
	}
	
	public void setEntryLines(List<EntryLine> entryLines) {
		if(entryLines == null) {
			// TODO: throw
		}
		
		this.entryLines = List.copyOf(entryLines);
		Long totalAmount = journalValidate(entryLines);
		this.totalAmount = totalAmount;
		
		for(EntryLine line : this.entryLines) {
			line.setJournalId(id);
		}
	}
	
	private Long journalValidate(List<EntryLine> entryLines) {
		if(entryLines == null || entryLines.size() < 2) {
			// TODO: throw 최소 2개 이상의 항목이 필요.
		}
		
		long debitSum = 0;
		long creditSum = 0;
		int debitCount = 0;
		int creditCount = 0;
		
		for(EntryLine line : entryLines) {
			if(line.getAmount() <= 0) {
				// TODO: throw 거래할 금액은 0보다 커야함.
			}
			
			if(line.getType().equals(EntryType.DEBIT)) {
				debitSum += line.getAmount();
				debitCount++;
			} else if(line.getType().equals(EntryType.CREDIT)) {
				creditSum += line.getAmount();
				creditCount++;
			}
		}
		
		if(debitCount == 0 || creditCount == 0) {
			// TODO: throw 차변과 대변 내역은 각각 하나 이상씩 있어야 함.
		}
		
		if(debitSum != creditSum) {
			// TODO: throw 차변합계 debitSum 대변합계 creditSum 이 일치하지 않음.
		}
		
		return debitSum;
	}
	
	public void onUpdate(Instant now) {
		this.updatedAt = now;
	}
}