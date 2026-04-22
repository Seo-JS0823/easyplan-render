package com.easyplan._01_web.webutil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookieProp {
	ACCESS("AT", 60 * 24 * 60, true),
	REFRESH("RT", 60 * 60 * 24 * 7, true),
	REMEMBER("RMT", 60 * 60 * 24 * 7, true),
	ZONE_ID("ZONE", 60 * 60 * 24 * 7, true),
	;
	private final String name;
	
	private final int maxAge;
	
	private final boolean httpOnly;
}
