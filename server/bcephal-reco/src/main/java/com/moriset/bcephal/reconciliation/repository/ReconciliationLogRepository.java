/**
 * 
 */
package com.moriset.bcephal.reconciliation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.reconciliation.domain.ReconciliationLog;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ReconciliationLogRepository extends PersistentRepository<ReconciliationLog> {

}
