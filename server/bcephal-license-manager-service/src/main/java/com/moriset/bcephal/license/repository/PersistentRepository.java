package com.moriset.bcephal.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.license.domain.IPersistent;


@NoRepositoryBean
public interface PersistentRepository<P extends IPersistent>
		extends JpaRepository<P, Long>, JpaSpecificationExecutor<P> {
	
}
