/**
 * 
 */
package com.moriset.bcephal.domain.filters;

/**
 * @author Joseph Wambo
 *
 */
public enum PeriodGranularity {

	DAY, WEEK, MONTH, QUARTER, YEAR;

	public boolean isDay() {
		return this == DAY;
	}

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
