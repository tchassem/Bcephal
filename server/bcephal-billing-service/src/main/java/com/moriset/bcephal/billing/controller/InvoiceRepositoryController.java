/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.service.BaseInvoiceRepositoryService;
import com.moriset.bcephal.billing.service.InvoiceRepositoryService;

@RestController
@RequestMapping("/billing/invoice-repository")
public class InvoiceRepositoryController extends BaseInvoiceRepositoryController {

	@Autowired
	InvoiceRepositoryService invoiceRepositoryService;
		
	@Override
	protected InvoiceRepositoryService getService() {
		return invoiceRepositoryService;
	}
		
	@Override
	protected BaseInvoiceRepositoryService getInvoiceService() {
		return invoiceRepositoryService;
	}
	
}
