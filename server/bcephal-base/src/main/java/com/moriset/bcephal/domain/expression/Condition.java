package com.moriset.bcephal.domain.expression;

import lombok.Data;

@Data
public class Condition {

	private Expression ifExpression;

	private ExpressionAction thenExpression;

	private ExpressionAction elseExpression;

}
