/**
 * 
 */
package com.moriset.bcephal.grid.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum JoinColumnCategory {
	
	CUSTOM,	
	STANDARD;
	
	public boolean isCustom() {
		return this == CUSTOM;
	}
	
	public boolean isStandard() {
		return this == STANDARD;
	}
	
}
