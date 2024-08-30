package com.moriset.bcephal.grid.domain;

public enum GrilleRowType {

	ALL,
	RECONCILIATED(true),
	NOT_RECONCILIATED,
	ON_HOLD,
	BILLED(true),
	NOT_BILLED,
	DRAFT, 
	VALIDATED(true), 
	NOT_BOOKED, 
	BOOKED(true);
	
	private boolean positive;
	
	private GrilleRowType() {
		this.positive = false;
	}
	
	private GrilleRowType(boolean value) {
		this.positive = value;
	}
	
	public boolean isPositive() {
		return positive;
	}
	
	public boolean isForBilling() {
		return this == BILLED || this == NOT_BILLED;
	}
	
}
