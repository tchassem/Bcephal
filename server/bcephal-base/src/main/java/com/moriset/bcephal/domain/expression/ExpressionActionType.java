package com.moriset.bcephal.domain.expression;

public enum ExpressionActionType {

	CONDITION("If"), SET_VALUE("Set value"), NO_ACTION("No action"), STOP("Stop");

	private String value;

	ExpressionActionType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

}
