package com.easyplan._01_web.webutil;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieProvider {
	
	public void addCookie(CookieProp prop, String value, HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, createCookie(prop, value));
	}
	
	private String createCookie(CookieProp prop, String value) {
		ResponseCookie responseCookie = ResponseCookie.from(prop.getName(), value)
				.path("/")
				.httpOnly(prop.isHttpOnly())
				.secure(true)
				.maxAge(prop.getMaxAge())
				.sameSite("strict")
				.build();
		
		return responseCookie.toString();
	}
	
	public String getCookieValue(CookieProp prop, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		
		for(Cookie c : cookies) {
			if(c.getName().equals(prop.getName())) {
				return c.getValue();
			}
		}
		
		return null;
	}
}
