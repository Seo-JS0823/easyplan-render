package com.easyplan._04_infra.jpa.ledger.entity;

import com.easyplan._03_domain.ledger.model.account.AccountSide;
import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.account.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CategoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "ledger_id",
			nullable = false,
			updatable = false,
			foreignKey = @ForeignKey(name = "FK_CATEGORY_LEDGER_ID")
	)
	private LedgerEntity ledger;
	
	@Column(name = "category_name", nullable = false)
	private String categoryName;
	
	@Column(name = "account_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountType type;
	
	@Column(name = "account_side", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountSide side;
	
	public static CategoryEntity from(Category category, LedgerEntity ledgerEntity) {
		return CategoryEntity.builder()
				.id(category.getId())
				.ledger(ledgerEntity)
				.categoryName(category.getCategoryName())
				.type(category.getType())
				.side(category.getAccountSide())
				.build();
	}
	
	public Category toDomain() {
		return Category.builder()
				.id(id)
				.ledgerId(ledger.getId())
				.categoryName(categoryName)
				.type(type)
				.accountSide(side)
				.build();
	}
}
