/**
 * 
 */
package com.moriset.bcephal.billing.service;

import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.grid.domain.GrilleType;

@Service("CreditNoteRepositoryService")
public class CreditNoteRepositoryService extends BaseInvoiceRepositoryService {
		
	@Override
	protected GrilleType getGrilleType() {		
		return GrilleType.CREDIT_NOTE_REPOSITORY;
	}

	@Override
	protected String getRepositoryBaseName() {
		return "Credit note repository ";
	}

	@Override
	protected String getRepositoryCode() {
		return BillingParameterCodes.BILLING_CREDIT_NOTE_REPOSITORY_GRID;
	}

}
