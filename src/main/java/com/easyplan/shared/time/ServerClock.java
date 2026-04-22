package com.easyplan.shared.time;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

@Component
public class ServerClock implements Clock {

	@Override
	public Instant now() {
		return Instant.now();
	}

	@Override
	public Instant nowSecond() {
		return Instant.now().truncatedTo(ChronoUnit.SECONDS);
	}

}
