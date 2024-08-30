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
public class ExpressionActionItem {

	private ExpressionActionType type;

	private ExpressionActionValueType valueType;

	private Condition condition;

	private ExpressionValue value;

	private boolean onlyIfEmpty;

	private int position;

}
