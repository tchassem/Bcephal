/**
 * 
 */
package com.moriset.bcephal.domain.filters;

/**
 * @author Joseph Wambo
 *
 */
public enum PeriodGrouping {

	DAY_OF_WEEK, DAY_OF_MONTH, WEEK, MONTH, QUARTER, YEAR;

	public boolean isDayOfMonth() {
		return this == DAY_OF_MONTH;
	}

	public boolean isDayOfWeek() {
		return this == DAY_OF_WEEK;
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
