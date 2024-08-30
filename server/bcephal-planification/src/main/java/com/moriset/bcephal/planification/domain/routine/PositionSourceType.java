package com.moriset.bcephal.planification.domain.routine;

public enum PositionSourceType {

	FIRST_N_CHAR,
	LAST_N_CHAR,
	AFTER_STRING, 
	BEFORE_STRING,
	INTERVAL,
	INTERVAL_R;
	
	public boolean isAfter() {
		return this == AFTER_STRING;
	}
	
	public boolean isBefore() {
		return this == BEFORE_STRING;
	}
	
	public boolean isInterval() {
		return this == INTERVAL;
	}
	
	public boolean isIntervalR() {
		return this == INTERVAL_R;
	}
	
	public boolean isLastNChar() {
		return this == LAST_N_CHAR;
	}
	
	public boolean isFirstNChar() {
		return this == FIRST_N_CHAR;
	}
	
	
}
