/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.Optional;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.Project;

/**
 * @author Moriset
 *
 */
public interface ProjectRepository extends PersistentRepository<Project> {
	Optional<Project> findByCode(String login);
}
