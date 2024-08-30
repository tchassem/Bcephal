package com.moriset.bcephal.billing.domain;

public enum BillingModelAppendicyType {

	GRID,
	SUB_INVOICE;
	
	public boolean isGrid() {
		return this == GRID;
	}
	
	public boolean isSubInvoice() {
		return this == SUB_INVOICE;
	}
	
}
