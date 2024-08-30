/**
 * 
 */
package com.moriset.bcephal.initiation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface IncrementalNumberRepository extends PersistentRepository<IncrementalNumber> {

	IncrementalNumber findByName(String name);

}
