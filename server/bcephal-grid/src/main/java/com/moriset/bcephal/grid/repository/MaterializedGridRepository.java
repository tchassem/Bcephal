package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.repository.MainObjectRepository;

@Repository
public interface MaterializedGridRepository extends MainObjectRepository<MaterializedGrid> {
	
	List<MaterializedGrid> findByName(String name);
	
	List<MaterializedGrid> findByNameAndCategory(String name, GrilleCategory category);
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.grid.domain.MaterializedGrid model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.grid.domain.MaterializedGrid model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);

}
