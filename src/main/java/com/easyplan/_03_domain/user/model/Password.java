package com.easyplan._03_domain.user.model;

import java.util.regex.Pattern;

import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Password {
	private static final Pattern REGEX = Pattern.compile("^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[a-z\\d!@#$%^&*]{9,}$");
	
	private final String value;
	
	Password(String value) {
		this.value = value;
	}
	
	public static Password of(String value) {
		if(value == null || value.isBlank() || !REGEX.matcher(value).matches()) {
			throw new UserException(UserError.IN_PASSWORD_ERROR);
		}
		
		return new Password(value);
	}
	
	public static Password loginOf(String value) {
		return new Password(value);
	}
}
