package com.easyplan._03_domain.ledger.model.ledger;

import java.time.Instant;

import com.easyplan._03_domain.shared.PublicId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Aggregate Root
 * 
 * 가계부 하나를 의미함.
 * 
 * 개인: Personal , 공유: Group
 * 
 * 회계시작일 존재 , Default: 1월 1일 , 이후 Update 가능
 */
@Getter
@AllArgsConstructor
@Builder
public class Ledger {
	private final Long id;
	
	private final Long ownerId;
	
	private final PublicId publicId;
	
	private LedgerType type;
	
	private LedgerName name;
	
	private String description;
	
	private LedgerStatus status;
	
	private FiscalDate fiscalDate;
	
	private final Instant createdAt;
	
	private Instant updatedAt;
	
	private Instant deletedAt;
	
	public static Ledger create(Long ownerId, LedgerType type, LedgerName name, String description, Instant now) {
		return Ledger.builder()
				.id(null)
				.ownerId(ownerId)
				.publicId(PublicId.create())
				.type(type)
				.name(name)
				.description(description)
				.status(LedgerStatus.ACTIVE)
				.fiscalDate(FiscalDate.createDefault())
				.createdAt(now)
				.updatedAt(null)
				.deletedAt(null)
				.build();
	}
	
	public void updateName(LedgerName newName, Instant now) {
		ensureActive();
		this.name = newName;
		onUpdate(now);
	}
	
	public void updateDescription(String newDescription, Instant now) {
		ensureActive();
		this.description = newDescription;
		onUpdate(now);
	}
	
	public void updateFiscalDate(int month, int day, Instant now) {
		ensureActive();
		this.fiscalDate = new FiscalDate(month, day);		
		onUpdate(now);
	}
	
	public void updateType(LedgerType newType, Instant now) {
		this.type = newType;
		onUpdate(now);
	}
	
	
	private void onUpdate(Instant now) {
		this.updatedAt = now;
	}
	
	private void ensureActive() {
		if(this.status == LedgerStatus.DELETED || this.status == LedgerStatus.ARCHIVED) {
			// TODO EXCEPTION
		}
	}
}
