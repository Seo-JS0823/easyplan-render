package com.easyplan._03_domain.auth.model;

import java.util.regex.Pattern;

import com.easyplan._03_domain.auth.exception.AuthError;
import com.easyplan._03_domain.auth.exception.AuthException;

import lombok.Getter;

@Getter
public class RefreshTokenHash {
	private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+/]+=*$");
	
	private final String value;
	
	private RefreshTokenHash(String value) {
		if(value == null || value.isBlank() || !PATTERN.matcher(value).matches()) {
			throw new AuthException(AuthError.NOT_FOUND_RT);
		}
		this.value = value;
	}
	
	public static RefreshTokenHash of(String value) {
		return new RefreshTokenHash(value);
	}
	
	public boolean matches(String hashValue) {
		return this.value.equals(hashValue);
	}
}
