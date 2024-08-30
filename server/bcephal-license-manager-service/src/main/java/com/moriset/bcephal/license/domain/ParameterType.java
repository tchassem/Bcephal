package com.moriset.bcephal.license.domain;

public enum ParameterType {

	DATE,
	NUMERIC,
	STRING;
	
	public boolean IsDate() {
		return this == DATE;
	}
	
	public boolean IsNumeric() {
		return this == NUMERIC;
	}
	
	public boolean IsString() {
		return this == STRING;
	}
	
}
