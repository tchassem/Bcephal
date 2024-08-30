/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

/**
 * @author Moriset
 *
 */
public enum ReconciliationModelSide {

	LEFT,
	RIGHT,
	CUSTOM,
	LOG_GRID;
	
	public boolean isLeft() {
		return this == LEFT;
	}
	
	public boolean isRight() {
		return this == RIGHT;
	}
	
	public boolean isCustom() {
		return this == CUSTOM;
	}
	
	public boolean isLogGrid() {
		return this == LOG_GRID;
	}
	
}
