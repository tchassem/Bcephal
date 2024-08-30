/**
 * 
 */
package com.moriset.bcephal.domain;

/**
 * @author Joseph Wambo
 * 
 */
public enum ModelPeriodSide {

	ALL,
	CURRENT,
	PREVIOUS,
	INTERVAL;
	
	public boolean isAll() {
		return this == ALL;
	}
	
	public boolean isCurrent() {
		return this == CURRENT;
	}
	
	public boolean isPrevious() {
		return this == PREVIOUS;
	}
	
	public boolean isInterval() {
		return this == INTERVAL;
	}
	
}
