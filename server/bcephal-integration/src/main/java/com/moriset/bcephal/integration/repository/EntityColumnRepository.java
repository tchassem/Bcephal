package com.moriset.bcephal.integration.repository;

import java.util.List;

import com.moriset.bcephal.integration.domain.EntityColumn;
import com.moriset.bcephal.repository.PersistentRepository;

public interface EntityColumnRepository extends PersistentRepository<EntityColumn> {

	List<EntityColumn> findByName(String name);

}
