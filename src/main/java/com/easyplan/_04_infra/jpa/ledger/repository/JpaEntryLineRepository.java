package com.easyplan._04_infra.jpa.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.ledger.entity.EntryLineEntity;

public interface JpaEntryLineRepository extends JpaRepository<EntryLineEntity, Long> {

}
