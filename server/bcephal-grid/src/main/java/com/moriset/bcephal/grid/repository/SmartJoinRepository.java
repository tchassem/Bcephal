package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.grid.domain.SmartJoin;
import com.moriset.bcephal.repository.PersistentRepository;

public interface SmartJoinRepository extends PersistentRepository<SmartJoin> {

	@Query("SELECT grid FROM SmartJoin grid where grid.id != ?1")
	public List<SmartJoin> findAllExclude(Long excludedId);
	
	@Query("SELECT grid FROM SmartJoin grid where grid.id NOT IN :excludedIds")
	public List<SmartJoin> findAllExclude(@Param("excludedIds")List<Long> excludedId);
	
	@Query("SELECT grid.name FROM SmartJoin grid where grid.id = :id")
	public String findNameById(@Param("id")Long id);
	
}
