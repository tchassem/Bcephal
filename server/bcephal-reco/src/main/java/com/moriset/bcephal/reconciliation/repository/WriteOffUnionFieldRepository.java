/**
 * 4 avr. 2024 - WriteOffUnionFieldRepository.java
 *
 */
package com.moriset.bcephal.reconciliation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.reconciliation.domain.WriteOffUnionField;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Emmanuel Emmeni
 *
 */
@Repository
public interface WriteOffUnionFieldRepository extends PersistentRepository<WriteOffUnionField> {

}
