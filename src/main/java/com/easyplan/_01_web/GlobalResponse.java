package com.easyplan._01_web;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalResponse<T> {
	private final boolean success;
	
	private final int status;
	
	private final String message;
	
	private final T data;
	
	public static <T> GlobalResponse<T> success(String message) {
		return new GlobalResponse<>(true, 200, message, null);
	}
	
	public static <T> GlobalResponse<T> success(String message, T data) {
		return new GlobalResponse<>(true, 200, message, data);
	}
	
	public static <T> GlobalResponse<T> noSearchData() {
		return new GlobalResponse<>(true, 204, "204", null);
	}
	
	public static <T> GlobalResponse<T> fail(int status, String message) {
		return new GlobalResponse<>(false, status, message, null);
	}
	
	public static <T> GlobalResponse<T> fail(int status, String message, T data) {
		return new GlobalResponse<>(false, status, message, data);
	}
}
