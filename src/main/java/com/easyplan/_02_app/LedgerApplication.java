package com.easyplan._02_app;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._02_app.command.LedgerCommand;
import com.easyplan._02_app.command.LedgerCommand.AccountCreate;
import com.easyplan._03_domain.ledger.model.account.Account;
import com.easyplan._03_domain.ledger.model.account.Category;
import com.easyplan._03_domain.ledger.model.account.CategoryOption;
import com.easyplan._03_domain.ledger.model.ledger.Ledger;
import com.easyplan._03_domain.ledger.model.ledger.LedgerName;
import com.easyplan._03_domain.ledger.readModel.CategoryMetadata;
import com.easyplan._03_domain.ledger.readModel.DashboardMetadataGroup;
import com.easyplan._03_domain.ledger.readModel.LedgerCategorisWithAccounts;
import com.easyplan._03_domain.ledger.readModel.LedgerGroupDTO;
import com.easyplan._03_domain.ledger.service.LedgerQueryService;
import com.easyplan._03_domain.ledger.service.LedgerService;
import com.easyplan._03_domain.shared.PublicId;
import com.easyplan._03_domain.user.model.User;
import com.easyplan._03_domain.user.service.UserService;
import com.easyplan.shared.time.Clock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerApplication {
	private final LedgerService ledgerService;
	
	private final LedgerQueryService ledgerQS;
	
	private final UserService userService;
	
	private final Clock clock;
	
	/*
	 * 가계부 생성
	 * 
	 * 트랜잭션
	 * 1. Ledger(가계부) 1개 생성
	 * 2. 기본 카테고리 대그룹 5개 생성
	 * 3. 사용자가 선택한 항목계정이 카테고리에 맞게 Insert
	 */
	@Transactional
	public LedgerGroupDTO ledgerCreate(LedgerCommand.LedgerCreate ledgerCreate) {
		User user = userService.findByPublicId(PublicId.of(ledgerCreate.publicId()));
		Long ownerId = user.getId();
		Instant now = clock.nowSecond();
		LedgerName ledgerName = LedgerName.of(ledgerCreate.name());
		
		Ledger ledger = ledgerService.ledgerCreate(ownerId, ledgerName, ledgerCreate.description(), ledgerCreate.type(), now);
		
		List<Category> categories = ledgerService.categoryDefaultCreate(ledger.getId());
		
		List<Account> accounts = ledgerService.accountUserSelectedCreate(categories, ledgerCreate.categories());
		
		return new LedgerGroupDTO(ledger, categories, accounts);
	}
	
	@Transactional(readOnly = true)
	public CategoryMetadata getLedgerCategoriesWithAccounts(Long ownerId) {
		List<CategoryOption> options = ledgerQS.findOptionsAll();
		List<LedgerCategorisWithAccounts> accounts = ledgerQS.dashboardMetadata(ownerId);
		
		CategoryMetadata metadata = new CategoryMetadata(accounts, options);
		
		return metadata;
	}
	
	@Transactional(readOnly = true)
	public DashboardMetadataGroup readLedgerGroup(Long ownerId, LocalDate startDate, LocalDate endDate) {
		List<LedgerCategorisWithAccounts> ledgerMeta = ledgerQS.dashboardMetadata(ownerId);
		
		if(ledgerMeta.isEmpty()) return null;
		
		Long firshLedgerId = ledgerMeta.get(0).getLedgerId();
		Long ledgerMetaTime = System.currentTimeMillis();
		DashboardMetadataGroup metaGroup =  ledgerQS.dashboardMetadateGroup(ownerId, firshLedgerId, startDate, endDate);
		log.info("dashboardMetadata={}ms", System.currentTimeMillis() - ledgerMetaTime);
		
		metaGroup.setLedgerMeta(ledgerMeta);
		
		return metaGroup;
	}
	
	@Transactional(readOnly = true)
	public DashboardMetadataGroup dashboardQueryGroup(Long ownerId, Long ledgerId, LocalDate startDate, LocalDate endDate) {
		return ledgerQS.dashboardMetadateGroup(ownerId, ledgerId, startDate, endDate);
	}
	
	@Transactional
	public Account categoryOptionUpdate(LedgerCommand.CategoryOptionUpdate optionUpdateRequest) {
		Ledger targetLedger = ledgerService.getLedger(optionUpdateRequest.ledgerId());
		
		Account targetAccount = ledgerService.getAccount(optionUpdateRequest.accountId());
		
		if(!targetLedger.getId().equals(targetAccount.getLedgerId())) {
			throw new IllegalArgumentException("시스템 오류가 발생하여 잠시 후 다시 시도해주시기 바랍니다.");
		}
		
		targetAccount.changeCategoryOption(optionUpdateRequest.optionId());
		
		ledgerService.categoryOptionUpdate(targetAccount);
		
		return targetAccount;
	}

	public Account accountCreate(AccountCreate accountCreateCommand) {
		return ledgerService.createAccount(accountCreateCommand.account());
	}
}