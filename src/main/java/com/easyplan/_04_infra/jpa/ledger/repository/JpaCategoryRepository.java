package com.easyplan._04_infra.jpa.ledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.ledger.entity.CategoryEntity;
import com.easyplan._04_infra.jpa.ledger.entity.LedgerEntity;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {
	List<CategoryEntity> findByLedger(LedgerEntity ledgerEntity);
	
	List<CategoryEntity> findByLedgerId(Long ledgerId);
	
	List<CategoryEntity> findByLedgerIdIn(List<Long> ledgerIds);
}
