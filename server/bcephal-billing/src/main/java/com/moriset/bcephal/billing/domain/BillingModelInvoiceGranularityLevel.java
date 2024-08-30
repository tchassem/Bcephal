/**
 * 
 */
package com.moriset.bcephal.billing.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum BillingModelInvoiceGranularityLevel {

	EVENT,
	CATEGORY,
	NO_CONSOLIDATION,
	CUSTOM;
	
	public boolean isEvent() {
		return this == EVENT;
	}
	
	public boolean isCategory() {
		return this == CATEGORY;
	}
	
	public boolean isNoConsolidation() {
		return this == NO_CONSOLIDATION;
	}
	
	public boolean isCustom() {
		return this == CUSTOM;
	}
	
}
