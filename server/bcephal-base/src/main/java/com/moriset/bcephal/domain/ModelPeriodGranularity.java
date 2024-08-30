/**
 * 
 */
package com.moriset.bcephal.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum ModelPeriodGranularity {

	WEEK,
	MONTH,
	QUARTER,
	YEAR;
	
	public boolean isWeek() {
		return this == WEEK;
	}
	
	public boolean isMonth() {
		return this == MONTH;
	}
	
	public boolean isQuarter() {
		return this == QUARTER;
	}
	
	public boolean isYear() {
		return this == YEAR;
	}
	
}
