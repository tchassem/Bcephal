/**
 * 
 */
package com.moriset.bcephal.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.domain.IPersistent;

/**
 * @author MORISET-004
 *
 */
@NoRepositoryBean
public interface IPersistentRepository<P extends IPersistent>
		extends JpaRepository<P, Long>, JpaSpecificationExecutor<P> {

}
