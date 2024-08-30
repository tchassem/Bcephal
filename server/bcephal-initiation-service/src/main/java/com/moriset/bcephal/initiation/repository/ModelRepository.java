package com.moriset.bcephal.initiation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author MORISET-004
 *
 * @param <Model>
 */
@Repository
public interface ModelRepository extends PersistentRepository<Model> {

	Model findByNameIgnoreCase(String name);

	Model findByName(String name);
}
