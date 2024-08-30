/**
 * 
 */
package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author Joseph Wambo
 *
 */
public interface JoinRepository extends MainObjectRepository<Join> {

	List<Join> findByActiveAndScheduledAndCronExpressionIsNotNull(boolean b, boolean c);
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.grid.domain.Join model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.grid.domain.Join model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);

}
