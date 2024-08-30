/**
 * 
 */
package com.moriset.bcephal.repository.filters;

import java.util.List;

import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
public interface EntityRepository extends PersistentRepository<Entity> {

	Entity findByNameIgnoreCase(String name);
	
	List<Entity> findByName(String name);

}
