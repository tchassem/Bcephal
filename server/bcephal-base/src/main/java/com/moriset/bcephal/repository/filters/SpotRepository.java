package com.moriset.bcephal.repository.filters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.repository.MainObjectRepository;

@Repository
public interface SpotRepository extends MainObjectRepository<Spot> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.domain.dimension.Spot model ORDER BY name")
	List<Nameable> findAllAsNameables();

	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.domain.dimension.Spot model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
}
