/**
 * 
 */
package com.moriset.bcephal.loader.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.loader.domain.FileLoaderColumn;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FileLoaderColumnRepository extends PersistentRepository<FileLoaderColumn> {
}
