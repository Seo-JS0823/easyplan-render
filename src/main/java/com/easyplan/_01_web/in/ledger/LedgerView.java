package com.easyplan._01_web.in.ledger;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.easyplan._01_web.security.user.CUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerView {
	@GetMapping("/create")
	public String ledgerView(@AuthenticationPrincipal CUserDetails user, Model model) {
		setUserNicknameModel(user, model);
		
		return "ledger/ledger-create";
	}
	
	@GetMapping("/dashboard")
	public String ledgerDashboard(@AuthenticationPrincipal CUserDetails user, Model model) {
		setUserNicknameModel(user, model);
		
		return "ledger/ledger-dashboard";
	}
	
	@GetMapping("/category")
	public String ledgerCategory(@AuthenticationPrincipal CUserDetails user, Model model) {
		setUserNicknameModel(user, model);
		
		return "ledger/ledger-category";
	}
	
	private void setUserNicknameModel(CUserDetails user, Model model) {
		model.addAttribute("user", user.getNickname());
	}
	
	// 거래내역
	
	@GetMapping("/detail/recent-transaction")
	public String recentTransactionDetail(@AuthenticationPrincipal CUserDetails user, Model model) {
		setUserNicknameModel(user, model);
		
		return "ledger/transaction/recent-detail";
	}
}
