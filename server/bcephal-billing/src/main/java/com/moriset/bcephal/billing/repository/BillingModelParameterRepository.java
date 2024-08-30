package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.BillingModelParameter;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author ROLAND
 *
 * @param <P>
 */

@Repository
public interface BillingModelParameterRepository extends PersistentRepository<BillingModelParameter> {

}