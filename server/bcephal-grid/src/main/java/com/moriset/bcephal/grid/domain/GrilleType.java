/**
 * 
 */
package com.moriset.bcephal.grid.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum GrilleType {

	/**
	 * Grid used to edit data
	 */
	INPUT,
	
	/**
	 * Grid used to report data
	 */
	REPORT,
	
	/**
	 * Grid used in reconciliation process
	 */
	RECONCILIATION,
	
	/**
	 * Billing event repository grid
	 */
	BILLING_EVENT_REPOSITORY,
	
	/**
	 * Client repository grid
	 */
	CLIENT_REPOSITORY,
	
	INVOICE_REPOSITORY,
	
	CREDIT_NOTE_REPOSITORY,
	
	/**
	 * Posting entry repository grid
	 */
	POSTING_ENTRY_REPOSITORY,

	/**
	 * Booking repository grid
	 */
	BOOKING_REPOSITORY,
	
	/**
	 * Archive backup grid
	 */
	ARCHIVE_BACKUP,

	/**
	 * Archive replacement grid
	 */
	ARCHIVE_REPLACEMENT,
	
	DASHBOARD_REPORT,
	
}
