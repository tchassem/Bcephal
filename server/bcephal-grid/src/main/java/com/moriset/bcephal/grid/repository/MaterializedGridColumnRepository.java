package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.repository.PersistentRepository;

public interface MaterializedGridColumnRepository extends PersistentRepository<MaterializedGridColumn>{
	
	@Query("SELECT col FROM com.moriset.bcephal.grid.domain.MaterializedGridColumn col WHERE col.grid.id = :gridId")
	List<MaterializedGridColumn> findByGrid(@Param("gridId")Long gridId);
	
	@Query("SELECT id FROM com.moriset.bcephal.grid.domain.MaterializedGridColumn WHERE grid.id = :gridId")
	List<Long> getColumnIds(@Param("gridId") Long gridId);

}
