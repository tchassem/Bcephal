package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.CalculateBillingItem;
import com.moriset.bcephal.repository.PersistentRepository;

@Repository
public interface CalculateBillingItemRepository extends PersistentRepository<CalculateBillingItem> {

}
