/**
 * 
 */
package com.moriset.bcephal.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.moriset.bcephal.messenger.model.AlarmMessageLog;

/**
 * @author Moriset
 *
 */
@NoRepositoryBean
public interface AlarmMessageLogRepository<P extends AlarmMessageLog> extends JpaRepository<P, Long>, JpaSpecificationExecutor<P>, PagingAndSortingRepository<P, Long> {

}
