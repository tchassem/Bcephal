package com.moriset.bcephal.etl.domain;

public enum DimensionType {

	MEASURE, PERIOD, ATTRIBUTE, SPOT, LOOP, BILLING_EVENT, FREE, CALCULATED_MEASURE;

	public boolean isAttribute() {
		return this == ATTRIBUTE;
	}

	public boolean isPeriod() {
		return this == PERIOD;
	}

	public boolean isMeasure() {
		return this == MEASURE;
	}
	
	public boolean isCalculatedMeasure() {
		return this == CALCULATED_MEASURE;
	}

	public boolean isSpot() {
		return this == SPOT;
	}

}
