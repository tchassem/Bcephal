package com.moriset.bcephal.billing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.billing.domain.InvoiceItem;
import com.moriset.bcephal.billing.repository.InvoiceItemRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;

@Service
public class InvoiceItemService extends PersistentService<InvoiceItem, BrowserData> {

	@Autowired
	InvoiceItemRepository invoiceItemRepository;
	
	@Override
	public PersistentRepository<InvoiceItem> getRepository() {
		// TODO Auto-generated method stub
		return invoiceItemRepository;
	}

}
