package com.easyplan._01_web.in.ledger;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._01_web.GlobalResponse;
import com.easyplan._01_web.in.ledger.request.AccountCreateRequest;
import com.easyplan._01_web.in.ledger.request.CategoryOptionUpdateRequest;
import com.easyplan._01_web.in.ledger.request.LedgerCreateRequest;
import com.easyplan._01_web.security.user.CUserDetails;
import com.easyplan._02_app.LedgerApplication;
import com.easyplan._02_app.command.LedgerCommand;
import com.easyplan._03_domain.ledger.readModel.DashboardMetadataGroup;
import com.easyplan._03_domain.ledger.readModel.LedgerGroupDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerApi {
	private final LedgerApplication ledgerApp;
	
	@PostMapping("/create")
	public ResponseEntity<?> ledgerCreate(
			@RequestBody LedgerCreateRequest ledgerCreate,
			Authentication auth) {
		LedgerCommand.LedgerCreate ledger = ledgerCreate.toCommand(auth.getName());
		
		LedgerGroupDTO ledgerGroup = ledgerApp.ledgerCreate(ledger);
		
		return ResponseEntity.ok(ledgerGroup);
	}
	
	@GetMapping("/category-list")
	public ResponseEntity<?> ledgerCategoryWithAccount(@AuthenticationPrincipal CUserDetails userDetails) {
		return ResponseEntity.ok(
				ledgerApp.getLedgerCategoriesWithAccounts(userDetails.getUserId())
		);
	}
	
	@PatchMapping("/category")
	public ResponseEntity<?> categoryOptionUpdate(@AuthenticationPrincipal CUserDetails userDetails,
			@RequestBody CategoryOptionUpdateRequest categoryOptionUpdate) {
		
		LedgerCommand.CategoryOptionUpdate categoryOptionUpdateCommand = categoryOptionUpdate.toCommand();
		
		return ResponseEntity.ok(ledgerApp.categoryOptionUpdate(categoryOptionUpdateCommand));
	}
	
	@PostMapping("/category")
	public ResponseEntity<?> categoryInnerAccountCreate(@AuthenticationPrincipal CUserDetails userDetails,
			@RequestBody AccountCreateRequest accountCreateRequest) {
		
		LedgerCommand.AccountCreate accountCreateCommand = accountCreateRequest.toCommand();
		
		return ResponseEntity.ok(ledgerApp.accountCreate(accountCreateCommand));
	}
	
	@GetMapping("")
	public ResponseEntity<?> allLedger(@AuthenticationPrincipal CUserDetails userDetails,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		
		DashboardMetadataGroup metadata = ledgerApp.readLedgerGroup(userDetails.getUserId(), startDate, endDate);
		
		if(metadata == null) {
			return ResponseEntity.ok(GlobalResponse.noSearchData());
		}
		
		return ResponseEntity.ok(GlobalResponse.success(null, metadata));
	}
	
	@GetMapping("/meta/{ledgerId}")
	public ResponseEntity<?> dashboardQuery(@PathVariable Long ledgerId,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@AuthenticationPrincipal CUserDetails userDetails) {
		return ResponseEntity.ok(
				ledgerApp.dashboardQueryGroup(userDetails.getUserId(), ledgerId, startDate, endDate)
		);
	}
}
