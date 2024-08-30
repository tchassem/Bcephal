package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.grid.domain.SmartUnionGrid;
import com.moriset.bcephal.repository.PersistentRepository;

public interface SmartUnionGridRepository extends PersistentRepository<SmartUnionGrid> {

	@Query("SELECT grid FROM SmartUnionGrid grid where grid.id != ?1")
	public List<SmartUnionGrid> findAllExclude(Long excludedId);
	
	@Query("SELECT grid FROM SmartUnionGrid grid where grid.id NOT IN :excludedIds")
	public List<SmartUnionGrid> findAllExclude(@Param("excludedIds")List<Long> excludedId);
	
	@Query("SELECT grid.name FROM SmartUnionGrid grid where grid.id = :id")
	public String findNameById(@Param("id")Long id);
	
}
