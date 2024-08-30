/**
 * 
 */
package com.moriset.bcephal.grid.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum JoinPublicationMethod {
	
	NEW_GRID,
	
	APPEND,
	
	REPLACE;
	
	public boolean isNewGrid() {
		return this == NEW_GRID;
	}
	
	public boolean isAppend() {
		return this == APPEND;
	}
	
	public boolean isReplace() {
		return this == REPLACE;
	}
}
