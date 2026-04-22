package com.easyplan._04_infra.jpa.ledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.ledger.entity.AccountEntity;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
	List<AccountEntity> findByLedgerId(Long id);

	List<AccountEntity> findByCategoryId(Long id);

	List<AccountEntity> findByCategoryIdIn(List<Long> categoryIds);
}
