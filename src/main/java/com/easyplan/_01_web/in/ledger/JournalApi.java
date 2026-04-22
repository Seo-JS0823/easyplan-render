 package com.easyplan._01_web.in.ledger;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._01_web.in.ledger.request.JournalCreateRequest;
import com.easyplan._01_web.in.ledger.request.JournalUpdateRequest;
import com.easyplan._01_web.security.user.CUserDetails;
import com.easyplan._02_app.JournalApplication;
import com.easyplan._02_app.command.JournalCommand;
import com.easyplan._02_app.query.JournalQuery;
import com.easyplan._02_app.query.JournalQuery.JournalTransactionList;
import com.easyplan._03_domain.ledger.model.journal.Journal;
import com.easyplan._03_domain.ledger.readModel.JournalThumbTransactionScroll;
import com.easyplan._03_domain.ledger.readModel.SearchOptions;
import com.easyplan._03_domain.ledger.readModel.SortedOptions;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
public class JournalApi {
	private final JournalApplication journalApp;
	
	@PostMapping("/create")
	public ResponseEntity<?> postJournal(@RequestBody JournalCreateRequest journal,
			@AuthenticationPrincipal CUserDetails userDetails) {
		JournalCommand.PostJournal postJournal = journal.toCommand(userDetails.getUserId());
		
		Journal result = journalApp.postJournal(postJournal);
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/search")
	public ResponseEntity<?> journalSearch(
			@RequestParam Long ledger,
			@RequestParam LocalDate startDate,
			@RequestParam LocalDate endDate,
			@RequestParam int offset,
			@RequestParam("search-option") SearchOptions searchOption,
			@RequestParam("sorted-option") SortedOptions sortedOption,
			@RequestParam("keyword") String keyword
			) {
		
		JournalQuery.JournalTransactionList journal =
				new JournalTransactionList(ledger, startDate, endDate, offset, searchOption, sortedOption, keyword);
		
		JournalThumbTransactionScroll journalTransactionList = journalApp.getTransactionThumbHistory(journal);
		journalTransactionList.setNextRequestUrlHint(ledger, startDate, endDate, offset, searchOption, sortedOption, keyword);
		
		return ResponseEntity.ok(journalTransactionList);
	}
	
	@GetMapping("/update")
	public ResponseEntity<?> journalUpdateData(@RequestParam Long journal) {
		Journal result = journalApp.getJournalUpdateBeginData(journal);
		
		return ResponseEntity.ok(result);
	}
	
	@PatchMapping("/update")
	public ResponseEntity<?> journalUpdate(@AuthenticationPrincipal CUserDetails userDetails,
			@RequestBody JournalUpdateRequest journalUpdate) {
		
		JournalCommand.JournalUpdate journal = journalUpdate.toCommand();
		
		var result = journalApp.journalUpdate(journal);
		
		return ResponseEntity.ok(result);
	}
	
}
