/**
 * 
 */
package com.moriset.bcephal.domain.expression;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class ExpressionItem {

	private ExpressionValue value1;

	private ExpressionValue value2;

	private ExpressionOperator operator;

	private ExpressionVerb verb;

	private int position;

}
