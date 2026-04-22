package com.easyplan._03_domain.user.model;

import java.util.regex.Pattern;

import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Email {
	private static final Pattern REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	
	private final String value;
	
	private Email(String value) {
		this.value = value;
	}
	
	public static Email of(String value) {
		if(value == null || value.isBlank() || !REGEX.matcher(value).matches()) {
			throw new UserException(UserError.IN_EMAIL_ERROR);
		}
		
		return new Email(value);
	}
}
