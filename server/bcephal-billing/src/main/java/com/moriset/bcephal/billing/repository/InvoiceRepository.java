/**
 * 
 */
package com.moriset.bcephal.billing.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author MORISET-6
 *
 */
@Repository
public interface InvoiceRepository extends MainObjectRepository<Invoice> {

}
