/**
 * 
 */
package com.moriset.bcephal.billing.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum InvoiceStatus {
	
	DRAFT,
	VALIDATED;
	
	public boolean isDraft() {
		return this == DRAFT;
	}
	
	public boolean isValidated() {
		return this == VALIDATED;
	}
}
