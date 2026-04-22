package com.easyplan._04_infra.jpa.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.easyplan._04_infra.jpa.ledger.entity.JournalEntity;

public interface JpaJournalRepository extends JpaRepository<JournalEntity, Long> {
	
	@Query("""
			select j
			from JournalEntity j
			left join fetch j.entryLines
			where j.id = :journalId
	""")
	JournalEntity getJournalDetail(Long journalId);
}
