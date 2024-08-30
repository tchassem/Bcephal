/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

/**
 * 
 * @author MORISET-004
 *
 */
public enum PostingStatus {

	DRAFT,
	VALIDATED;
	
	public boolean isDraft(){
		return this == DRAFT;
	}
	
	public boolean isValidated(){
		return this == VALIDATED;
	}
}
