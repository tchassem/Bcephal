/**
 * 
 */
package com.moriset.bcephal.domain.filters;

/**
 * @author Joseph Wambo
 *
 */
public enum PeriodSign {

	ADD("+"), SUBSTRACT("-");

	private String sign;

	PeriodSign(String sign) {
		this.sign = sign;
	}

	public static boolean isAdd(String sign) {
		return ADD.sign.equalsIgnoreCase(sign);
	}

	public static boolean isSubstract(String sign) {
		return SUBSTRACT.sign.equalsIgnoreCase(sign);
	}
}
