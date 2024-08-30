/**
 * 
 */
package com.moriset.bcephal.billing.service;

import org.springframework.stereotype.Service;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceType;

/**
 * @author MORISET-6
 *
 */
@Service
public class CreditNoteService extends InvoiceService {

	protected InvoiceType getInvoiceType() {
		return InvoiceType.CREDIT_NOTE;
	}
	
	@Override
	protected Invoice getNewItem() {
		Invoice invoice = new Invoice();
		invoice.setType(InvoiceType.CREDIT_NOTE);
		String baseName = "Credit Note ";
		int i = 1;
		invoice.setName(baseName + i);
		while(getByName(invoice.getName()) != null) {
			i++;
			invoice.setName(baseName + i);
		}
		return invoice;
	}
	

}
