package com.easyplan._03_domain.user.model;

import java.util.regex.Pattern;

import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Nickname {
	private static final Pattern REGEX = Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");
	
	private final String value;
	
	Nickname(String value) {
		this.value = value;
	}
	
	public static Nickname of(String value) {
		if(value == null || value.isBlank() || !REGEX.matcher(value).matches()) {
			throw new UserException(UserError.IN_NICKNAME_ERROR);
		}
		
		return new Nickname(value);
	}
}
