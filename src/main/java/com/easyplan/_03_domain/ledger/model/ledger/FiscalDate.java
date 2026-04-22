package com.easyplan._03_domain.ledger.model.ledger;

import java.time.MonthDay;

public record FiscalDate(int month, int day) {
	public static FiscalDate createDefault() {
		return new FiscalDate(1,1);
	}
	
	public MonthDay toMonthDay() {
		return MonthDay.of(month, day);
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public Integer getDay() {
		return day;
	}
}
