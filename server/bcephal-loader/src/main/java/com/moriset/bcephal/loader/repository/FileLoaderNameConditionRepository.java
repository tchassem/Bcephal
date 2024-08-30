/**
 * 
 */
package com.moriset.bcephal.loader.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.loader.domain.FileLoaderNameCondition;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FileLoaderNameConditionRepository extends PersistentRepository<FileLoaderNameCondition> {

	@Modifying
	@Query("DELETE FROM com.moriset.bcephal.loader.domain.FileLoaderNameCondition col WHERE col.loader.id = :loaderId")
	void deleteByLoader(@Param("loaderId")Long id);

}
