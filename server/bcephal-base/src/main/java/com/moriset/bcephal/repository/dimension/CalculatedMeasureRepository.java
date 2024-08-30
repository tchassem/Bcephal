package com.moriset.bcephal.repository.dimension;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.repository.MainObjectRepository;

public interface CalculatedMeasureRepository extends MainObjectRepository<CalculatedMeasure> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(P.id, P.name) FROM com.moriset.bcephal.domain.dimension.CalculatedMeasure P "
			+ "WHERE P.dataSourceType = :dataSourceType AND P.dataSourceId = :dataSourceId ORDER BY name")
	List<Nameable> findAllByDataSourceAsNameable(@Param("dataSourceType")DataSourceType dataSourceType, @Param("dataSourceId")Long dataSourceId);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(P.id, P.name) FROM com.moriset.bcephal.domain.dimension.CalculatedMeasure P "
			+ "WHERE P.dataSourceType = :dataSourceType ORDER BY name")
	List<Nameable> findAllByDataSourceAsNameable(@Param("dataSourceType")DataSourceType dataSourceType);
	
}
