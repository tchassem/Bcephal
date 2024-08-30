package com.moriset.bcephal.billing.enumeration;

public enum BillingElementType {
	BILLING_AMOUNT,
	DRIVER,
	UNIT_COST, UNIT_COST_EQUALS_BILLING_AMOUNT;
	
	public boolean isBillingAmount() {
		return this == BILLING_AMOUNT;
	}
	
	public boolean isDriver() {
		return this == DRIVER;
	}
	
	public boolean isUnitCost() {
		return this == UNIT_COST;
	}
	
	public boolean isUnitCostEqualsBillingAmount() {
		return this == UNIT_COST_EQUALS_BILLING_AMOUNT;
	}
}
