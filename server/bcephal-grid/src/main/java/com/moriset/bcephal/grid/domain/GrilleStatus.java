/**
 * 
 */
package com.moriset.bcephal.grid.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum GrilleStatus {

	/**
	 * Grid rows are ready to be report
	 */
	LOADED,
	
	/**
	 * Grid rows can not be report
	 */
	UNLOADED;
	
	
	public boolean isLoaded() {
		return this == LOADED;
	}
	
	public boolean isUnloaded() {
		return this == UNLOADED;
	}
	
}
