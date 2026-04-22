package com.easyplan._03_domain.auth.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthSession {
	private Long id;
	
	private final String publicId;
	
	private RefreshTokenHash tokenHash;
	
	private Instant expiresAt;
	
	private final Instant createdAt;
	
	public static AuthSession create(String publicId, RefreshTokenHash tokenHash, Instant expiresAt, Instant createdAt) {
		return AuthSession.builder()
				.id(null)
				.publicId(publicId)
				.tokenHash(tokenHash)
				.expiresAt(expiresAt)
				.createdAt(createdAt)
				.build();
	}
	
	public void loginUpdate(RefreshTokenHash tokenHash, Instant expiresAt) {
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
	}
}
