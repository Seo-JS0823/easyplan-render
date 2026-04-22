package com.easyplan._03_domain.ledger.repository;

import java.util.List;
import java.util.Optional;

import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.Category;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.ledger.Ledger;
import com.easyplan._03_domain.shared.PublicId;

public interface LedgerRepository {
	
	// ===== Ledger Entity Function =====
	Ledger createLedger(Ledger ledger);
	
	Optional<Ledger> findByPublicId(PublicId publicId);

	Optional<Ledger> ledgerFindById(Long ledgerId);
	
	// ===== Category Entity Function =====
	Category createCategory(Category category);
	
	List<Category> createDefaultCategory(List<Category> category);
	
	List<Category> categoryFindByLedger(Ledger ledger);
	
	List<Category> categoryFindByLedgerIn(List<Long> ledgerIds);
	
	// ===== Account Entity Function =====
	Account createAccount(Account account);
	
	Optional<Account> accountFindById(Long accountId);
	
	Account accountCategoryOptionUpdate(Account account);
	
	List<Account> createUserSelectedCategories(List<Account> accountList);
	
	List<Account> accountFindByLedger(Ledger ledger);
	
	List<Account> accountFindByCategory(Category category);
	
	List<Account> accountFindByCategoryIdIn(List<Long> categoryIds);

	List<Ledger> findByOwnerId(Long ownerId);
	
	// ===== CategoryOption Entity Function =====
	CategoryOption categoryOptionFindById(Long optionId);
	
	List<CategoryOption> getCategoryOptionAll();
}