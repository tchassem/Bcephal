/**
 * 
 */
package com.moriset.bcephal.reconciliation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ReconciliationConditionRepository extends PersistentRepository<ReconciliationCondition> {

}
