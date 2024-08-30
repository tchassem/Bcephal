/**
 * 
 */
package com.moriset.bcephal.loader.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.loader.domain.FileLoaderLog;
import com.moriset.bcephal.loader.domain.FileLoaderLogItem;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FileLoaderLogItemRepository extends PersistentRepository<FileLoaderLogItem> {

	@Query("SELECT log FROM com.moriset.bcephal.loader.domain.FileLoaderLogItem item LEFT JOIN com.moriset.bcephal.loader.domain.FileLoaderLog log ON (item.log = log.id) "
			+ "WHERE log.loaderId = :loaderId AND item.file = :file AND item.error = false")
	List<FileLoaderLog> getDuplicationFile(@Param("loaderId")Long loaderId, @Param("file")String file);
	
	Optional<FileLoaderLogItem> findByLogAndFile(Long log, String file);

}
