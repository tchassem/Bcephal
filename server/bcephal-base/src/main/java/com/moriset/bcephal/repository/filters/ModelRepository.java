package com.moriset.bcephal.repository.filters;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
@Repository
public interface ModelRepository extends PersistentRepository<Model> {

	Model findByNameIgnoreCase(String name);

}
