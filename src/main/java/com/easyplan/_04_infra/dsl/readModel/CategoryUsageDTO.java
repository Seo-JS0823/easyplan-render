package com.easyplan._04_infra.dsl.readModel;

import com.querydsl.core.annotations.QueryProjection;

public record CategoryUsageDTO(String categoryName, Long amount, Double percentage) {
	@QueryProjection
	public CategoryUsageDTO {}
}
