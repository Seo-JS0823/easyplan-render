package com.easyplan._03_domain.user.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Birthdate {
	private final String value;
	
	private Birthdate(String value) {
		this.value = value;
	}
	
	public static Birthdate of(String value) {
		if(value == null) {
			return null;
		}
		
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd")
          .withResolverStyle(ResolverStyle.STRICT);
      LocalDate.parse(value, formatter);
		} catch(DateTimeParseException e) {
			throw new UserException(UserError.IN_BIRTHDAY_ERROR);
		}
		
		return new Birthdate(value);
	}
}
