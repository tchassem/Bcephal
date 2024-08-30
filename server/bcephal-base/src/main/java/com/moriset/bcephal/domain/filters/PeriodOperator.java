package com.moriset.bcephal.domain.filters;

public enum PeriodOperator {

	SPECIFIC, TODAY, BEGIN_WEEK, END_WEEK, BEGIN_MONTH, END_MONTH, BEGIN_YEAR, END_YEAR, CALENDAR,COPY_DATE,VARIABLE;

	public boolean isSpecific() {
		return this == SPECIFIC;
	}

	public boolean isCalendar() {
		return this == CALENDAR;
	}

	public boolean isToday() {
		return this == TODAY;
	}

	public boolean isBeginOfWeek() {
		return this == BEGIN_WEEK;
	}

	public boolean isEndOfWeek() {
		return this == END_WEEK;
	}

	public boolean isBeginOfMonth() {
		return this == BEGIN_MONTH;
	}

	public boolean isEndOfMonth() {
		return this == END_MONTH;
	}

	public boolean isBeginOfYear() {
		return this == BEGIN_YEAR;
	}

	public boolean isEndOfYear() {
		return this == END_YEAR;
	}
	
	public boolean isCopyDate() {
		return this == END_YEAR;
	}
	
	public boolean isVariable() {
		return this == VARIABLE;
	}

}
