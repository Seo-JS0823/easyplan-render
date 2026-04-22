package com.easyplan.shared.time;

import java.time.Instant;

public interface Clock {
	Instant now();
	
	Instant nowSecond();
}
