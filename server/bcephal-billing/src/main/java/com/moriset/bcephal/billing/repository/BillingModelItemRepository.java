package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.BillingModelItem;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author ROLAND
 *
 * @param <P>
 */

@Repository
public interface BillingModelItemRepository extends PersistentRepository<BillingModelItem> {

}
