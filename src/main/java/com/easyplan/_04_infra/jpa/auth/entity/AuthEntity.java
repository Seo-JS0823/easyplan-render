package com.easyplan._04_infra.jpa.auth.entity;

import java.time.Instant;

import com.easyplan._03_domain.auth.model.AuthSession;
import com.easyplan._03_domain.auth.model.RefreshTokenHash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authentication")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class AuthEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "public_id", unique = true, nullable = false, length = 44)
	private String publicId;
	
	@Column(name = "refresh_token_hash", unique = true, nullable = false, length = 100)
	private String tokenHash;
	
	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;
	
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	public static AuthEntity from(AuthSession auth) {
		 return AuthEntity.builder()
				 .id(auth.getId())
				 .publicId(auth.getPublicId())
				 .tokenHash(auth.getTokenHash().getValue())
				 .expiresAt(auth.getExpiresAt())
				 .createdAt(auth.getCreatedAt())
				 .build();
	}

	public AuthSession toDomain() {
		return AuthSession.builder()
				.id(id)
				.publicId(publicId)
				.tokenHash(RefreshTokenHash.of(tokenHash))
				.expiresAt(expiresAt)
				.createdAt(createdAt)
				.build();
	}
	
	public void apply(AuthSession auth) {
		this.tokenHash = auth.getTokenHash().getValue();
		this.expiresAt = auth.getExpiresAt();
	}
}
