/**
 * 
 */
package com.moriset.bcephal.domain.expression;

import lombok.Getter;

/**
 * @author Joseph Wambo
 *
 */
@Getter
public enum ExpressionValueType {

	COLUMN("Column"), LOOP("Loop"), NULL("null"), PARAMETER("Parameter"), SPOT("Spot"), NO("No condition");

	private String value;

	ExpressionValueType(String value) {
		this.value = value;
	}

}
