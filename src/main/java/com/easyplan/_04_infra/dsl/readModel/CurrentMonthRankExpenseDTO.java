package com.easyplan._04_infra.dsl.readModel;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

public record CurrentMonthRankExpenseDTO(Long totalIncome, Long totalExpense, List<CategoryUsageDTO> topCategories) {
	@QueryProjection
	public CurrentMonthRankExpenseDTO {}
}
