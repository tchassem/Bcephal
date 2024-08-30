package com.moriset.bcephal.grid.domain.form;

public enum FormModelButtonType {
	
	SCHEDULER,
	VALUE,
	DUPLICATE,
	VALUE_SCHEDULER;
	
	public boolean isDuplicate() {
		return this == DUPLICATE;
	}
	
	public boolean isScheduler() {
		return this == SCHEDULER;
	}
	
	public boolean isValue() {
		return this == VALUE;
	}
	
	public boolean isValueScheduler() {
		return this == VALUE_SCHEDULER;
	}
}
