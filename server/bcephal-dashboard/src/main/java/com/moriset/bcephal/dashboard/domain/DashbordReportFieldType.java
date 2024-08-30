package com.moriset.bcephal.dashboard.domain;

public enum DashbordReportFieldType {

	ATTRIBUTE, PERIOD, MEASURE, FREE;

	public boolean isAttribute() {
		return this == ATTRIBUTE;
	}

	public boolean isPeriod() {
		return this == PERIOD;
	}

	public boolean isMeasure() {
		return this == MEASURE;
	}

}
