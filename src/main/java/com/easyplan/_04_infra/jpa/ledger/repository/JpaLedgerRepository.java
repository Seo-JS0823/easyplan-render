package com.easyplan._04_infra.jpa.ledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.ledger.entity.LedgerEntity;

public interface JpaLedgerRepository extends JpaRepository<LedgerEntity, Long> {
	Optional<LedgerEntity> findByPublicId(String publicId);
	
	List<LedgerEntity> findByOwnerId(Long ownerId);
}
