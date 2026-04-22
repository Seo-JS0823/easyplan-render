package com.easyplan._04_infra.dsl.readModel;

import com.querydsl.core.annotations.QueryProjection;

public record NetWorthDTO(Long totalAssets, Long totalLiabilities, Long netWorth) {
	@QueryProjection
	public NetWorthDTO {}
}
