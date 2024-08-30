/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.Project;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	public Optional<Project> findByCode(String code);

	public int deleteByCode(String code);

	public List<Project> findByNameOrCode(String name, String code);

	//public Optional<Project> findByName(String name);
	
	List<Project> findByName(String name);
	
	boolean existsByName(String name);
	
	boolean existsByCode(String code);

}
