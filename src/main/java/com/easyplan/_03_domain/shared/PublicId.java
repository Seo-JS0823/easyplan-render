package com.easyplan._03_domain.shared;

import java.util.UUID;

import com.easyplan._03_domain.user.exception.UserError;
import com.easyplan._03_domain.user.exception.UserException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PublicId {
	private final String value;
	
	PublicId(String value) {
		this.value = value;
	}
	
	public static PublicId of(String value) {
		if(value == null || value.isBlank()) {
			throw new UserException(UserError.IN_PUBLIC_ID_ERROR);
		}
		
		try {
			UUID.fromString(value);
		} catch (IllegalArgumentException e) {
			throw new UserException(UserError.IN_PUBLIC_ID_ERROR);
		}
		
		return new PublicId(value);
	}
	
	public static PublicId create() {
		return new PublicId(UUID.randomUUID().toString());
	}
}
