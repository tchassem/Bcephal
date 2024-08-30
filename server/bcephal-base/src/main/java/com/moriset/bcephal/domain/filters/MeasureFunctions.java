/**
 * 
 */
package com.moriset.bcephal.domain.filters;

/**
 * @author Joseph Wambo
 *
 */
public enum MeasureFunctions {

	COUNT("Count"),
	AVERAGE("Avg"),
	MAX( "Max"),
	MIN("Min"),
	SUM("Sum");

	public String code;

	private MeasureFunctions(String code) {
		this.code = code;
	}

	public static MeasureFunctions getByCode(String code) {
		if (code == null)
			return SUM;
		if (AVERAGE.code.equals(code))
			return AVERAGE;
		if (COUNT.code.equals(code))
			return COUNT;
		if (MAX.code.equals(code))
			return MAX;
		if (MIN.code.equals(code))
			return MIN;
		if (SUM.code.equals(code))
			return SUM;
		return SUM;
	}

	@Override
	public String toString() {
		return this.code;
	}

}
