package com.easyplan._03_domain.auth.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;

import com.easyplan._03_domain.auth.model.AuthSession;
import com.easyplan._03_domain.auth.model.TokenPair;
import com.easyplan._03_domain.auth.repository.AuthRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthService {
	private final AuthRepository repo;
	
	private final TokenService tokenService;
	
	@Value("${jwt.times.refresh}")
	private long refreshTokenTime;
	
	public TokenPair createTokens(String publicId, String role, Instant now) {
		String accessToken = tokenService.createAccessToken(publicId, role);
		String refreshToken = tokenService.createRefreshToken();
		return new TokenPair(accessToken, refreshToken);
	}
	
	public AuthSession loginAuthUpdate(String publicId, TokenPair tokens, Instant now) {
		AuthSession auth = repo.findByPublicId(publicId)
				.orElseGet(() -> {
					return AuthSession.create(
							publicId,
							tokenService.hashToken(tokens.getRefreshToken()),
							now.plusMillis(refreshTokenTime),
							now);
				});
		
		auth.loginUpdate(tokenService.hashToken(tokens.getRefreshToken()), now.plusMillis(refreshTokenTime));
		
		return repo.saveAuth(auth);
	}
}
