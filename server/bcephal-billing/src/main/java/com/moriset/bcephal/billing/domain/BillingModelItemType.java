/**
 * 
 */
package com.moriset.bcephal.billing.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum BillingModelItemType {

	EVENT_TYPE,
	EVENT_CATEGORY,
	CLIENT,
	CLIENT_GROUP;
	
	public boolean isEventType() {
		return this == EVENT_TYPE;
	}
	
	public boolean isEventCategory() {
		return this == EVENT_CATEGORY;
	}
	
	public boolean isClient() {
		return this == CLIENT;
	}
	
	public boolean isClientGroup() {
		return this == CLIENT_GROUP;
	}
	
}
