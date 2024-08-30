package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.InvoiceItemInfo;
import com.moriset.bcephal.repository.PersistentRepository;

@Repository
public interface InvoiceItemInfoRepository extends PersistentRepository<InvoiceItemInfo> {

}
