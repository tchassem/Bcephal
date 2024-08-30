package com.moriset.bcephal.grid.domain.form;

public enum FormModelFieldNature {
	
	INPUT,
	REPORT,
	CALCULATED,
	CONCATENATED,
	SPOT;
	
	public boolean isInput() {
		return this == INPUT;
	}
	
	public boolean isReport() {
		return this == REPORT;
	}
	
	public boolean isCalculated() {
		return this == CALCULATED;
	}
	
	public boolean isConcatenated() {
		return this == CONCATENATED;
	}
	
	public boolean isSpot() {
		return this == SPOT;
	}
}
