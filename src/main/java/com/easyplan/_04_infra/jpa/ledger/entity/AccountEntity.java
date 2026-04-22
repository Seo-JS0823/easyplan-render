package com.easyplan._04_infra.jpa.ledger.entity;

import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.AccountStatus;

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
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "ledger_id",
			nullable = false,
			updatable = false,
			foreignKey = @ForeignKey(name = "FK_ACCOUNT_LEDGER_ID")
	)
	private LedgerEntity ledger;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "category_id",
			nullable = false,
			updatable = false,
			foreignKey = @ForeignKey(name = "FK_ACCOUNT_CATEGORY_ID")
	)
	private CategoryEntity category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_id", nullable = false)
	private CategoryOptionEntity categoryOption;
	
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	
	@Column(name = "account_name", nullable = false, length = 30)
	private String accountName;
	
	@Column(name = "description", length = 500)
	private String description;
	
	@Column(name = "payment", nullable = false)
	private boolean payment;
	
	public static AccountEntity from(Account account, LedgerEntity ledgerEntity, CategoryEntity categoryEntity, CategoryOptionEntity categoryOption) {
		return AccountEntity.builder()
				.id(account.getId())
				.ledger(ledgerEntity)
				.category(categoryEntity)
				.status(account.getStatus())
				.categoryOption(categoryOption)
				.accountName(account.getAccountName())
				.description(account.getDescription())
				.payment(account.isPayment())
				.build();
	}
	
	public Account toDomain() {
		return Account.builder()
				.id(id)
				.ledgerId(ledger.getId())
				.categoryId(category.getId())
				.categoryOptionId(categoryOption.getId())
				.status(status)
				.accountName(accountName)
				.description(description)
				.payment(payment)
				.build();
	}
	
	public void categoryOptionUpdate(CategoryOptionEntity categoryOption) {
		this.categoryOption = categoryOption;
	}
}
