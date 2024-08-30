/**
 * 
 */
package com.moriset.bcephal.repository.filters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface IncrementalNumberRepository extends PersistentRepository<IncrementalNumber> {

	IncrementalNumber findByName(String name);

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(nbr.id, nbr.name) FROM IncrementalNumber nbr ORDER BY nbr.name")
	List<Nameable> getAllIncrementalNumbers();

}
