/**
 * 
 */
package com.moriset.bcephal.billing.websocket;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class BillingRequest {

	public enum BillingRequestType{INVOICE, RUN_OUTCOME}
	
	private BillingRequestType type;
	
	private boolean reprint;
	
	private List<Long> ids;
	
	private boolean fromRepository;
	
	private Long modelId;
	
	public BillingRequest()
	{
		this.ids = new ArrayList<Long>();
	}
	
	public boolean isInvoice() {
		return this.type == BillingRequestType.INVOICE;
	}
	
	public boolean isRunOutcome() {
		return this.type == BillingRequestType.RUN_OUTCOME;
	}
}
