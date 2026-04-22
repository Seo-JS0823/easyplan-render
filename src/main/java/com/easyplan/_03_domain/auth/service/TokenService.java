package com.easyplan._03_domain.auth.service;

import com.easyplan._03_domain.auth.model.RefreshTokenHash;
import com.easyplan._03_domain.auth.model.TokenClaims;

public interface TokenService {
	String createAccessToken(String publicId, String role);
	
	String createRefreshToken();
	
	TokenClaims extractTokenClaims(String accessToken);
	
	RefreshTokenHash hashToken(String refreshToken);
	
	boolean validateRefreshToken(String rawToken, RefreshTokenHash tokenHash);
}
