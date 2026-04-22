package com.easyplan._04_infra.dsl.readModel;

import java.time.LocalDate;

import com.easyplan._03_domain.ledger.model.account.AccountType;
import com.querydsl.core.annotations.QueryProjection;

public record RecentTransaction(String createdUserNickname, String accountName, LocalDate transactionDate, Long totalAmount, String useAssetName, AccountType useType) {
	@QueryProjection
	public RecentTransaction {}
}
