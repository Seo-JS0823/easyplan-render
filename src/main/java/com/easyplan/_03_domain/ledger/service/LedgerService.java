package com.easyplan._03_domain.ledger.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.easyplan._03_domain.ledger.model.account.Category;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.account.DefaultCategories;
import com.easyplan._03_domain.ledger.model.ledger.Ledger;
import com.easyplan._03_domain.ledger.model.ledger.LedgerName;
import com.easyplan._03_domain.ledger.model.ledger.LedgerType;
import com.easyplan._03_domain.ledger.repository.LedgerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LedgerService {
	private final LedgerRepository ledgerRepo;
	
	public Ledger getLedger(Long ledgerId) {
		Ledger ledger = ledgerRepo.ledgerFindById(ledgerId)
				.orElseThrow();
		
		return ledger;
	}
	
	public Account getAccount(Long accountId) {
		Account account = ledgerRepo.accountFindById(accountId)
				.orElseThrow();
		
		return account;
	}
	
	public Account createAccount(Account account) {
		CategoryOption option = ledgerRepo.categoryOptionFindById(account.getCategoryOptionId());
		
		Account savedAccount = ledgerRepo.createAccount(account);
		
		return savedAccount;
	}
	
	public void categoryOptionUpdate(Account account) {
		ledgerRepo.accountCategoryOptionUpdate(account);
	}
	
	public Ledger ledgerCreate(Long ownerId, LedgerName name, String description, LedgerType type, Instant now) {
		Ledger ledger = Ledger.create(ownerId, type, name, description, now);
		Ledger saved = ledgerRepo.createLedger(ledger);
		return saved;
	}
	
	public List<Category> categoryDefaultCreate(Long ledgerId) {
		List<Category> categories = Category.createDefault(ledgerId);
		List<Category> saveList = ledgerRepo.createDefaultCategory(categories);
		return saveList;
	}
	
	public List<Account> accountUserSelectedCreate(List<Category> categories, List<DefaultCategories> defaultCategories) {
		List<Account> accountList = new ArrayList<>();
		
		Map<AccountType, List<DefaultCategories>> selectedMap = defaultCategories.stream()
		    .collect(Collectors.groupingBy(DefaultCategories::getType));

		for(Category item : categories) {
		    List<DefaultCategories> matches = selectedMap.get(item.getType());
		    if (matches != null) {
		        for(DefaultCategories prop : matches) {
		            accountList.add(Account.create(item.getLedgerId(), item.getId(), prop.getAccountName(), null, prop.isPayment(), prop.getCategoryOptionCode().name()));
		        }
		    }
		}
		
		List<Account> saveList = ledgerRepo.createUserSelectedCategories(accountList);
		return saveList;
	}
}
