/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

/**
 * @author Moriset
 *
 */
public enum ReconciliationModelBalanceFormula {

	LEFT_MINUS_RIGHT,
	LEFT_PLUS_RIGHT;
	
	public boolean isLeftMinusRight() {
		return this == LEFT_MINUS_RIGHT;
	}
	
	public boolean isLeftPlusRight() {
		return this == LEFT_PLUS_RIGHT;
	}
	
	
}
