/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.repository.ProjectRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Project service
 * 
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ProjectService {

	@Autowired
	ProjectRepository projectRepository;

	
	@Autowired
	MessageSource messageSource;
	
	/**
	 * Retrieves project with the given ID.
	 * 
	 * @param id ID of project to find
	 * @return Project with the given ID
	 */
	public Optional<Project> findById(Long id) {
		log.trace("Find security project by ID: {}", id);
		return projectRepository.findById(id);
	}
	
	public Optional<Project> findByCode(String name) {
		log.trace("Try to retrieve all projects by name : {}", name);
		return projectRepository.findByCode(name);
	}

}
