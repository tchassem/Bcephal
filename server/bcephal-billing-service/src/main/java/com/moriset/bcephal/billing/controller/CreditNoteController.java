/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.service.CreditNoteService;

/**
 * @author MORISET-6
 *
 */


@RestController
@RequestMapping("/billing/credit-note")
public class CreditNoteController extends InvoiceController {

	@Autowired
	CreditNoteService creditNoteService;
	
	@Override
	protected CreditNoteService getService() {
		return creditNoteService;
	}

}
