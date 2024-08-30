/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

/**
 * @author Moriset
 *
 */
public enum MappingConditionSide {

	REFERENCE_GRID,
	TARGET_GRID,
	FREE;
	
	public boolean isReferenceGrid() {
		return this == REFERENCE_GRID;
	}
	
	public boolean isTargetGrid() {
		return this == TARGET_GRID;
	}
	
	public boolean isFree() {
		return this == FREE;
	}
		
}
