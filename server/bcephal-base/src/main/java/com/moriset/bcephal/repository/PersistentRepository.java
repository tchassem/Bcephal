package com.moriset.bcephal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.domain.IPersistent;

@NoRepositoryBean
public interface PersistentRepository<P extends IPersistent>
		extends JpaRepository<P, Long>, JpaSpecificationExecutor<P> {
	@Query(value = "SELECT id, name FROM BCP_TRANSFORMATION_ROUTINE ORDER BY name", nativeQuery = true)
	List<Object[]> findRoutines();
}
