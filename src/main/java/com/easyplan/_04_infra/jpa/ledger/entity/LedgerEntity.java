package com.easyplan._04_infra.jpa.ledger.entity;

import java.time.Instant;

import com.easyplan._03_domain.ledger.model.ledger.FiscalDate;
import com.easyplan._03_domain.ledger.model.ledger.Ledger;
import com.easyplan._03_domain.ledger.model.ledger.LedgerName;
import com.easyplan._03_domain.ledger.model.ledger.LedgerType;
import com.easyplan._03_domain.shared.PublicId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ledger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class LedgerEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "owner_id", nullable = false)
	private Long ownerId;
	
	@Column(name = "ledger_public_id", nullable = false, unique= true, updatable = false)
	private String publicId;
	
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private LedgerType type;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "fiscal_month")
	private Integer fiscalMonth;
	
	@Column(name = "fiscal_day")
	private Integer fiscalDay;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	@Column(name = "deleted_at")
	private Instant deletedAt;
	
	public static LedgerEntity from(Ledger ledger) {
		return LedgerEntity.builder()
				.id(ledger.getId())
				.ownerId(ledger.getOwnerId())
				.publicId(ledger.getPublicId().getValue())
				.type(ledger.getType())
				.name(ledger.getName().getValue())
				.description(ledger.getDescription())
				.fiscalMonth(ledger.getFiscalDate().getMonth())
				.fiscalDay(ledger.getFiscalDate().getDay())
				.createdAt(ledger.getCreatedAt())
				.updatedAt(ledger.getUpdatedAt())
				.deletedAt(ledger.getDeletedAt())
				.build();
	}
	
	public Ledger toDomain() {
		return Ledger.builder()
				.id(id)
				.ownerId(ownerId)
				.publicId(PublicId.of(publicId))
				.type(type)
				.name(LedgerName.of(name))
				.description(description)
				.fiscalDate(new FiscalDate(fiscalMonth, fiscalDay))
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.deletedAt(deletedAt)
				.build();
	}
}
