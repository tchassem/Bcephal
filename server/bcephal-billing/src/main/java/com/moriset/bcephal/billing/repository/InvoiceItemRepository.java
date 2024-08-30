package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.InvoiceItem;
import com.moriset.bcephal.repository.PersistentRepository;

@Repository
public interface InvoiceItemRepository extends PersistentRepository<InvoiceItem> {

}
