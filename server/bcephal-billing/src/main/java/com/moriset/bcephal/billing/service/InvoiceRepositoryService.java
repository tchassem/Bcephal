/**
 * 
 */
package com.moriset.bcephal.billing.service;

import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.grid.domain.GrilleType;

@Service("InvoiceRepositoryService")
public class InvoiceRepositoryService extends BaseInvoiceRepositoryService {
		
	@Override
	protected GrilleType getGrilleType() {		
		return GrilleType.INVOICE_REPOSITORY;
	}
	
	@Override
	protected String getRepositoryBaseName() {
		return "Invoice repository ";
	}

	@Override
	protected String getRepositoryCode() {
		return BillingParameterCodes.BILLING_INVOICE_REPOSITORY_GRID;
	}

}
