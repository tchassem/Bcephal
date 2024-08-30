/**
 * 
 */
package com.moriset.bcephal.accounting.domain;
/**
 * 
 * @author MORISET-004
 *
 */
public enum PostingSign {

	D,
	C;
	
	public boolean isDebit() {
		return this == D;
	}
	
	public boolean isCredit() {
		return this == C;
	}
	
}
