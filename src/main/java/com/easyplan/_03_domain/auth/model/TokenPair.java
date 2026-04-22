package com.easyplan._03_domain.auth.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TokenPair {
	private final String accessToken;
	
	private final String refreshToken;
	
	public TokenPair(String accessToken, String refreshToken) {
		if(accessToken == null || refreshToken == null) {
			// TODO: EXCEPTION
		}
		
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
