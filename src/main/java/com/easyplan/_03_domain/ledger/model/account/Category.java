package com.easyplan._03_domain.ledger.model.account;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Category {
	private Long id;
	
	private Long ledgerId;
	
	private String categoryName;
	
	private AccountType type;
	
	private AccountSide accountSide;
	
	public static Category create(Long ledgerId, AccountType type) {
		return Category.builder()
				.id(null)
				.ledgerId(ledgerId)
				.type(type)
				.categoryName(type.getCategoryName())
				.accountSide(type.getAccountSide())
				.build();
	}
	
	public static List<Category> createDefault(Long ledgerId) {
		AccountType[] types = AccountType.values();
		
		List<Category> categories = new ArrayList<>();
		for(AccountType type : types) {
			categories.add(Category.create(ledgerId, type));
		}
		
		return categories;
	}
}
