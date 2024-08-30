/**
 * 
 */
package com.moriset.bcephal.domain.expression;

/**
 * @author Joseph Wambo
 *
 */
public enum ExpressionVerb {

	AND("and"), OR("or"), NOT("!");

	@SuppressWarnings("unused")
	private String value;

	ExpressionVerb(String value) {
		this.value = value;
	}

}
