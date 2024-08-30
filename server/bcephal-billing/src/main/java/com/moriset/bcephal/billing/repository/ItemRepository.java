package com.moriset.bcephal.billing.repository;


import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.repository.PersistentRepository;


/**
 * 
 * @author Moriset
 *
 * @param <P>
 */

@Repository
public interface ItemRepository extends PersistentRepository<Invoice> {

	

}
