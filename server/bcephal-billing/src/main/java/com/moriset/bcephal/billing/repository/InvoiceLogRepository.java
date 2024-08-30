package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.InvoiceLog;
import com.moriset.bcephal.repository.PersistentRepository;

@Repository
public interface InvoiceLogRepository extends PersistentRepository<InvoiceLog> {
	
	void deleteByInvoice(Long invoice);
}
