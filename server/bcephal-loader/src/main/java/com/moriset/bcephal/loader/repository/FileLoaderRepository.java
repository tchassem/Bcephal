/**
 * 
 */
package com.moriset.bcephal.loader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FileLoaderRepository extends MainObjectRepository<FileLoader> {

	List<FileLoader> findByActiveAndScheduledAndCronExpressionIsNotNull(boolean active, boolean scheduled);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.loader.domain.FileLoader model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.loader.domain.FileLoader model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
	
}
