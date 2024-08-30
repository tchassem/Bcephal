/**
 * 
 */
package com.moriset.bcephal.loader.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.loader.domain.FileLoaderLog;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FileLoaderLogRepository extends PersistentRepository<FileLoaderLog> {

	List<FileLoaderLog> findAllByOperationCode(String operationCode);
	
}
