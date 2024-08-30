package com.moriset.bcephal.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.moriset.bcephal.security.domain.SimpleArchive;

public interface SimpleArchiveRepository extends JpaRepository<SimpleArchive, Long>, JpaSpecificationExecutor<SimpleArchive> {

	boolean existsByName(String name);
	
}
