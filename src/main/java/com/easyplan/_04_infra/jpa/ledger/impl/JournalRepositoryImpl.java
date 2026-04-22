package com.easyplan._04_infra.jpa.ledger.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.easyplan._03_domain.ledger.model.journal.EntryLine;
import com.easyplan._03_domain.ledger.model.journal.Journal;
import com.easyplan._03_domain.ledger.repository.JournalRepository;
import com.easyplan._04_infra.jpa.ledger.entity.AccountEntity;
import com.easyplan._04_infra.jpa.ledger.entity.EntryLineEntity;
import com.easyplan._04_infra.jpa.ledger.entity.JournalEntity;
import com.easyplan._04_infra.jpa.ledger.entity.LedgerEntity;
import com.easyplan._04_infra.jpa.ledger.repository.JpaAccountRepository;
import com.easyplan._04_infra.jpa.ledger.repository.JpaEntryLineRepository;
import com.easyplan._04_infra.jpa.ledger.repository.JpaJournalRepository;
import com.easyplan._04_infra.jpa.ledger.repository.JpaLedgerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JournalRepositoryImpl implements JournalRepository {
	private final JpaLedgerRepository ledgerRepo;
	
	private final JpaJournalRepository journalRepo;
	
	private final JpaAccountRepository accountRepo;
	
	private final JpaEntryLineRepository entryRepo;

	@Override
	public Journal journalCreate(Journal journal, List<EntryLine> entryLine) {
		LedgerEntity ledgerEntity = ledgerRepo.getReferenceById(journal.getLedgerId());
		JournalEntity entity = JournalEntity.from(journal, ledgerEntity);
		
		List<EntryLineEntity> entryLineEntities = entryLine.stream()
				.map(line -> {
					AccountEntity account = accountRepo.getReferenceById(line.getAccountId());
					
					return EntryLineEntity.from(line, entity, account);
				}).toList();
		
		for(EntryLineEntity line : entryLineEntities) {
			entity.addEntryLines(line);
		}
		
		JournalEntity saved = journalRepo.save(entity);
		return saved.toDomain();
	}

	@Override
	public Journal getJournal(Long journalId) {
		JournalEntity entity = journalRepo.findById(journalId)
				.orElseThrow();
		
		return entity.toDomain();
	}

	@Override
	public Journal getJournalAndEntry(Long journalId) {
		JournalEntity entity = journalRepo.getJournalDetail(journalId);
		
		return entity.toDomain();
	}

	@Override
	public EntryLine getEntryLine(Long entryLineId) {
		return entryRepo.findById(entryLineId)
				.orElseThrow().toDomain();
	}

	@Override
	public Journal journalUpdate(Journal journal) {
		JournalEntity journalEntity = journalRepo.findById(journal.getId())
				.orElseThrow();
		
		journalEntity.changeMemo(journal.getMemo());
		journalEntity.changeTransactionDate(journal.getTransactionDate());
		journalEntity.changeUpdatedAt(journal.getUpdatedAt());
		journalEntity.changeTotalAmount(journal.getTotalAmount());
		
		for(EntryLineEntity line : journalEntity.getEntryLines()) {
			for(EntryLine domainEntryLine : journal.getEntryLines()) {
				if(line.getType() == domainEntryLine.getType()) {
					AccountEntity account = accountRepo.getReferenceById(domainEntryLine.getAccountId());
					line.changeAccount(account);
					line.changeAmount(domainEntryLine.getAmount(), domainEntryLine.getUpdatedAt());
				}
			}
		}
		return journalRepo.save(journalEntity).toDomain();
	}
	
	
}
