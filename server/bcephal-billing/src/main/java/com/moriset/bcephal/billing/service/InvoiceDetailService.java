/**
 * 
 */
package com.moriset.bcephal.billing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.billing.domain.InvoiceDetail;
import com.moriset.bcephal.billing.repository.InvoiceDetailRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;


/**
 * @author MORISET-6
 *
 */

@Service
public class InvoiceDetailService extends PersistentService<InvoiceDetail, BrowserData> {

	@Autowired
	InvoiceDetailRepository invoiceDetailRepository;

	@Override
	public PersistentRepository<InvoiceDetail> getRepository() {
		// TODO Auto-generated method stub
		return invoiceDetailRepository;
	}
	
	

}
