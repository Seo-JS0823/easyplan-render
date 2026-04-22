package com.easyplan._03_domain.ledger.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefaultCategories {
	ass_01(AccountType.ASSET, "현금", true, CategoryOptionCode.GENERAL),
	ass_02(AccountType.ASSET, "체크카드", true, CategoryOptionCode.LIQUID),
	
	lia_01(AccountType.LIABILITIES, "신용카드", true, CategoryOptionCode.CREDIT_CARD),
	lia_02(AccountType.LIABILITIES, "대출", false, CategoryOptionCode.LOAN),
	lia_03(AccountType.LIABILITIES, "갚을 돈", false, CategoryOptionCode.GENERAL),
	
	equ_01(AccountType.EQUITY, "기초 잔액", false, CategoryOptionCode.GENERAL),
	
	inc_01(AccountType.INCOME, "월급", true, CategoryOptionCode.VARIABLE_INCOME),
	inc_02(AccountType.INCOME, "상여금", true, CategoryOptionCode.VARIABLE_INCOME),
	inc_03(AccountType.INCOME, "사업 소득", true, CategoryOptionCode.VARIABLE_INCOME),
	inc_04(AccountType.INCOME, "판매 수익", true, CategoryOptionCode.VARIABLE_INCOME),
	inc_05(AccountType.INCOME, "기타 수익", true, CategoryOptionCode.VARIABLE_INCOME),
	
	exp_01(AccountType.EXPENSE, "식비", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_02(AccountType.EXPENSE, "교통비", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_03(AccountType.EXPENSE, "월세", true, CategoryOptionCode.FIXED_EXPENSE),
	exp_04(AccountType.EXPENSE, "생활용품", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_05(AccountType.EXPENSE, "취미", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_06(AccountType.EXPENSE, "학업", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_07(AccountType.EXPENSE, "의류 및 미용", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_08(AccountType.EXPENSE, "의료 및 건강", true, CategoryOptionCode.VARIABLE_EXPENSE),
	exp_09(AccountType.EXPENSE, "이자", true, CategoryOptionCode.VARIABLE_EXPENSE),
	;
	private final AccountType type;
	
	private final String accountName;
	
	private final boolean payment;
	
	private final CategoryOptionCode categoryOptionCode;
}
