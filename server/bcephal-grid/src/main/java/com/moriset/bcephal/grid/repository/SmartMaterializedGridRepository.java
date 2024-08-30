package com.moriset.bcephal.grid.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.repository.PersistentRepository;

public interface SmartMaterializedGridRepository extends PersistentRepository<SmartMaterializedGrid> {

	@Query("SELECT grid FROM SmartMaterializedGrid grid where grid.id NOT IN :excludedIds")
	public List<SmartMaterializedGrid> findAllExclude(@Param("excludedIds")List<Long> excludedId);
	
	@Query("SELECT grid.name FROM SmartMaterializedGrid grid where grid.id = :id")
	public String findNameById(@Param("id")Long id);
	
	@Query("SELECT col FROM SmartMaterializedGridColumn col where col.grid.id = :gridId AND col.name = :columnName")
	public Optional<SmartMaterializedGridColumn> findColumnByGridAndName(@Param("gridId")Long gridId, @Param("columnName")String columnName);
}
