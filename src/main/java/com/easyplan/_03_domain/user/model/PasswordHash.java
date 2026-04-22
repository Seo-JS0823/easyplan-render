package com.easyplan._03_domain.user.model;

import java.util.regex.Pattern;

import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.Getter;

@Getter
public class PasswordHash {
	private static final Pattern PASSWORD_HASH = Pattern.compile("^\\$2[ayb]\\$.{56}$");
	
	private final String value;
	
	PasswordHash(String value) {
		this.value = value;
	}
	
	public static PasswordHash of(String value) {
		if(value == null || value.isBlank() || !PASSWORD_HASH.matcher(value).matches()) {
			throw new UserException(UserError.IN_PASSWORD_HASH_ERROR);
		}
		
		return new PasswordHash(value);
	}
}
