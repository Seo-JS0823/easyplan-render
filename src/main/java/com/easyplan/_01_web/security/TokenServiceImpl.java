package com.easyplan._01_web.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.easyplan._03_domain.auth.model.RefreshTokenHash;
import com.easyplan._03_domain.auth.model.TokenClaims;
import com.easyplan._03_domain.auth.service.TokenService;
import com.easyplan.shared.time.Clock;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class TokenServiceImpl implements TokenService {
	private static final int REFRESH_TOKEN_LENGTH = 64;
	
	private static final String REFRESH_TOKEN_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	@Value("${jwt.times.access}")
	private long accessTokenExpiration;
	
	private final SecretKey key;
	
	private final SecureRandom secureRandom = new SecureRandom();
	
	private final Clock clock;
	
	public TokenServiceImpl(@Value("${jwt.secret}") String secretKey, Clock clock) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
		this.clock = clock;
	}

	@Override
	public String createAccessToken(String publicId, String role) {
		Instant now = clock.now().truncatedTo(ChronoUnit.SECONDS);
		Date iss = Date.from(now);
		Date exp = new Date(iss.getTime() + accessTokenExpiration);
		
		Claims claims = Jwts.claims()
				.subject(publicId)
				.add("role", role)
				.issuedAt(iss)
				.expiration(exp)
				.build();
		
		String accessToken = Jwts.builder()
				.claims(claims)
				.signWith(key)
				.compact();
		
		return accessToken;
	}

	@Override
	public String createRefreshToken() {
		String token = createSecureRandomToken();
		return token;
	}
	
	private String createSecureRandomToken() {
		StringBuilder token = new StringBuilder(REFRESH_TOKEN_LENGTH);
		for(int i = 0; i < REFRESH_TOKEN_LENGTH; i++) {
			int index = secureRandom.nextInt(REFRESH_TOKEN_ALPHABET.length());
			token.append(REFRESH_TOKEN_ALPHABET.charAt(index));
		}
		return token.toString();
	}

	@Override
	public TokenClaims extractTokenClaims(String accessToken) {
		Claims claims = extractClaims(accessToken);
		
		String publicId = claims.getSubject();
		String role = claims.get("role", String.class);
		Instant expiresAt = claims.getExpiration().toInstant();
		
		return new TokenClaims(publicId, role, expiresAt);
	}
	
	private Claims extractClaims(String accessToken) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(accessToken)
					.getPayload();
			
			return claims;
		} catch (ExpiredJwtException e) {
			return null;
		} catch (SignatureException | UnsupportedJwtException e) {
			return null;
		} catch (MalformedJwtException e) {
			return null;
		} catch (Exception e) {
			return null;			
		}
	}

	@Override
	public RefreshTokenHash hashToken(String refreshToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
			String hash = Base64.getEncoder().encodeToString(hashBytes);
			return RefreshTokenHash.of(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("TokenService NoSuchAlgorithm");
		}
	}

	@Override
	public boolean validateRefreshToken(String rawToken, RefreshTokenHash tokenHash) {
		RefreshTokenHash rawTokenHash = hashToken(rawToken);
		return constantTimeEquals(rawTokenHash.getValue(), tokenHash.getValue());
	}
	
	private boolean constantTimeEquals(String a, String b) {
		if(a.length() != b.length()) {
			return false;
		}
		
		int result = 0;
		for(int i = 0; i < a.length(); i++) {
			result |= a.charAt(i) ^ b.charAt(i);
		}
		return result == 0;
	}
	
}






















