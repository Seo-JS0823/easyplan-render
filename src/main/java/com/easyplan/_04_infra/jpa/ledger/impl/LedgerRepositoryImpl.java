package com.easyplan._04_infra.jpa.ledger.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.Category;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.ledger.Ledger;
import com.easyplan._03_domain.ledger.repository.LedgerRepository;
import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._04_infra.jpa.ledger.entity.AccountEntity;
import com.easyplan._04_infra.jpa.ledger.entity.CategoryEntity;
import com.easyplan._04_infra.jpa.ledger.entity.CategoryOptionEntity;
import com.easyplan._04_infra.jpa.ledger.entity.LedgerEntity;
import com.easyplan._04_infra.jpa.ledger.repository.JpaAccountRepository;
import com.easyplan._04_infra.jpa.ledger.repository.JpaCategoryOptionRepository;
import com.easyplan._04_infra.jpa.ledger.repository.JpaCategoryRepository;
import com.easyplan._04_infra.jpa.ledger.repository.JpaLedgerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LedgerRepositoryImpl implements LedgerRepository {
	private final JpaLedgerRepository ledgerRepo;
	
	private final JpaCategoryRepository categoryRepo;
	
	private final JpaAccountRepository accountRepo;
	
	private final JpaCategoryOptionRepository categoryOptionRepo;

	@Override
	public Ledger createLedger(Ledger ledger) {
		LedgerEntity entity = LedgerEntity.from(ledger);
		LedgerEntity saved = ledgerRepo.save(entity);
		return saved.toDomain();
	}

	@Override
	public Optional<Ledger> findByPublicId(PublicId publicId) {
		return ledgerRepo.findByPublicId(publicId.getValue())
				.map(LedgerEntity :: toDomain);
	}

	@Override
	public Category createCategory(Category category) {
		LedgerEntity ledgerEntity = ledgerRepo.getReferenceById(category.getLedgerId());
		CategoryEntity entity = CategoryEntity.from(category, ledgerEntity);
		CategoryEntity saved = categoryRepo.save(entity);
		return saved.toDomain();
	}

	@Override
	public List<Category> createDefaultCategory(List<Category> category) {
		List<CategoryEntity> entities = category.stream()
				.map(item -> {
					LedgerEntity ledgerEntity = ledgerRepo.getReferenceById(item.getLedgerId());
					CategoryEntity entity = CategoryEntity.from(item, ledgerEntity);
					return entity;
				})
				.toList();
		
		List<CategoryEntity> saveList = categoryRepo.saveAll(entities);
		return saveList.stream()
				.map(CategoryEntity :: toDomain)
				.toList();
	}

	@Override
	public List<Category> categoryFindByLedger(Ledger ledger) {
		List<CategoryEntity> entities = categoryRepo.findByLedgerId(ledger.getId());
		return entities.stream()
				.map(CategoryEntity :: toDomain)
				.toList();
	}

	@Override
	public Account createAccount(Account account) {
		LedgerEntity ledgerEntity = ledgerRepo.getReferenceById(account.getLedgerId());
		CategoryEntity categoryEntity = categoryRepo.getReferenceById(account.getCategoryId());
		CategoryOptionEntity categoryOptionEntity = categoryOptionRepo.getReferenceById(account.getCategoryOptionId());
		AccountEntity entity = AccountEntity.from(account, ledgerEntity, categoryEntity, categoryOptionEntity);
		AccountEntity saved = accountRepo.save(entity);
		return saved.toDomain();
	}

	@Override
	public List<Account> createUserSelectedCategories(List<Account> accountList) {
		Set<String> codes = accountList.stream()
        .map(Account::getCategoryOptionCode)
        .collect(Collectors.toSet());
		
		Map<String, CategoryOptionEntity> optionMap = categoryOptionRepo.findAllByOptionCodeIn(codes)
        .stream()
        .collect(Collectors.toMap(CategoryOptionEntity::getOptionCode, o -> o));
		
		List<AccountEntity> entities = accountList.stream()
				.map(item -> {
					LedgerEntity ledgerEntity = ledgerRepo.getReferenceById(item.getLedgerId());
					CategoryEntity categoryEntity = categoryRepo.getReferenceById(item.getCategoryId());
					CategoryOptionEntity categoryOptionEntity = optionMap.get(item.getCategoryOptionCode());
					
					return AccountEntity.from(item, ledgerEntity, categoryEntity, categoryOptionEntity);
				})
				.toList();
		
		List<AccountEntity> saveList = accountRepo.saveAll(entities);
		return saveList.stream()
				.map(AccountEntity :: toDomain)
				.toList();
	}

	@Override
	public List<Account> accountFindByLedger(Ledger ledger) {
		List<AccountEntity> entities = accountRepo.findByLedgerId(ledger.getId());
		return entities.stream()
				.map(AccountEntity :: toDomain)
				.toList();
	}

	@Override
	public List<Account> accountFindByCategory(Category category) {
		List<AccountEntity> entities = accountRepo.findByCategoryId(category.getId());
		return entities.stream()
				.map(AccountEntity :: toDomain)
				.toList();
	}

	@Override
	public List<Ledger> findByOwnerId(Long ownerId) {
		List<LedgerEntity> entities = ledgerRepo.findByOwnerId(ownerId);
		return entities.stream()
				.map(LedgerEntity :: toDomain)
				.toList();
	}

	@Override
	public List<Category> categoryFindByLedgerIn(List<Long> ledgerIds) {
		List<CategoryEntity> entities = categoryRepo.findByLedgerIdIn(ledgerIds);
		return entities.stream()
				.map(CategoryEntity :: toDomain)
				.toList();
	}

	@Override
	public List<Account> accountFindByCategoryIdIn(List<Long> categoryIds) {
		List<AccountEntity> entities = accountRepo.findByCategoryIdIn(categoryIds);
		return entities.stream()
				.map(AccountEntity :: toDomain)
				.toList();
	}

	@Override
	public Optional<Ledger> ledgerFindById(Long ledgerId) {
		return ledgerRepo.findById(ledgerId)
				.map(LedgerEntity :: toDomain);
	}

	@Override
	public Optional<Account> accountFindById(Long accountId) {
		return accountRepo.findById(accountId)
				.map(AccountEntity :: toDomain);
	}

	@Override
	public Account accountCategoryOptionUpdate(Account account) {
		AccountEntity accountEntity = accountRepo.findById(account.getId())
				.orElseThrow();
		
		accountEntity.categoryOptionUpdate(categoryOptionRepo.getReferenceById(account.getCategoryOptionId()));
		
		accountRepo.save(accountEntity);
		
		return accountEntity.toDomain();
	}

	@Override
	public CategoryOption categoryOptionFindById(Long optionId) {
		CategoryOptionEntity entity =  categoryOptionRepo.findById(optionId).orElseThrow();
		return entity.toDomain();
	}

	@Override
	public List<CategoryOption> getCategoryOptionAll() {
		List<CategoryOptionEntity> entities = categoryOptionRepo.findAll();
		
		return entities.stream()
				.map(CategoryOptionEntity :: toDomain)
				.toList();
	}
	
}
