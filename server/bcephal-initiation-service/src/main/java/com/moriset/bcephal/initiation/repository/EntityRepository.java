/**
 * 
 */
package com.moriset.bcephal.initiation.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author MORISET-004
 *
 */
@Repository
public interface EntityRepository extends PersistentRepository<Entity> {

	List<Entity> findByModel(Model model);

	Entity findByName(String name);

}
