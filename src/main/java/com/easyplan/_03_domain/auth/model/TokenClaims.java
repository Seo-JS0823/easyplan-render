package com.easyplan._03_domain.auth.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenClaims {

	private final String publicId;
	
	private final String role;
	
	private final Instant expiresAt;
}
