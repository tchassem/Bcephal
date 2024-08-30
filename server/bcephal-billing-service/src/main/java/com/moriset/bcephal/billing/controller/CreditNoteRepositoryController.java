/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.service.BaseInvoiceRepositoryService;
import com.moriset.bcephal.billing.service.CreditNoteRepositoryService;

@RestController
@RequestMapping("/billing/credit-note-repository")
public class CreditNoteRepositoryController extends BaseInvoiceRepositoryController {

	@Autowired
	CreditNoteRepositoryService creditNoteRepositoryService;
	
	@Override
	protected BaseInvoiceRepositoryService getInvoiceService() {
		return creditNoteRepositoryService;
	}
	
	@Override
	protected CreditNoteRepositoryService getService() {
		return creditNoteRepositoryService;
	}
	
	

}
