/**
 * 
 */
package com.moriset.bcephal.grid.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.grid.domain.FileLoaderColumnSoft;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FileLoaderColumnSoftRepository extends PersistentRepository<FileLoaderColumnSoft> {
	
	@Modifying
	@Query("UPDATE com.moriset.bcephal.grid.domain.FileLoaderColumnSoft SET grilleColumn = NULL WHERE grilleColumn = :gridColumn")
	void resetByGrilleColumn(@Param("gridColumn") Long gridColumn);
}
