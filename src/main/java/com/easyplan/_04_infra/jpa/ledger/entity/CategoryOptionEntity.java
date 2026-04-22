package com.easyplan._04_infra.jpa.ledger.entity;

import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;

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
@Table(name = "category_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CategoryOptionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "option_code", unique = true,  nullable = false, updatable = false)
	private String optionCode;
	
	@Column(name = "account_type", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	@Column(name = "option_name", nullable = false, length = 20, updatable = false)
	private String optionName;
	
	public static CategoryOptionEntity from(CategoryOption categoryOption) {
		return CategoryOptionEntity.builder()
				.id(categoryOption.getId())
				.optionCode(categoryOption.getOptionCode())
				.accountType(categoryOption.getAccountType())
				.optionName(categoryOption.getOptionName())
				.build();
	}
	
	public CategoryOption toDomain() {
		return CategoryOption.builder()
				.id(id)
				.optionCode(optionCode)
				.accountType(accountType)
				.optionName(optionName)
				.build();
	}
}
