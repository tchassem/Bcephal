/**
 * 
 */
package com.moriset.bcephal.domain.expression;

/**
 * @author Joseph Wambo
 *
 */
public enum ExpressionOperator {

	GREATER(">");

	@SuppressWarnings("unused")
	private String value;

	ExpressionOperator(String value) {
		this.value = value;
	}
}
