/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.project.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.ProjectBlock;
import com.moriset.bcephal.security.repository.ProjectBlockRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ProjectBlockService {

	@Autowired
	public ProjectBlockRepository projectBlockRepository;

	/**
	 * Saves the given ProjectBlock in the DB.
	 * 
	 * @param projectBlock ProjectBlock to save
	 * @return saved ProjectBlock
	 */
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public ProjectBlock save(ProjectBlock projectBlock, Long subscriptionId, String username) {
		log.trace("Try to save ProjectBlock : Name = {}", projectBlock.getName());
		if (!StringUtils.hasText(projectBlock.getUsername())) {
			projectBlock.setUsername(username);
		}
		if (projectBlock.getSubcriptionId() == null) {
			projectBlock.setSubcriptionId(subscriptionId);
		}
		return projectBlockRepository.saveAndFlush(projectBlock);
	}

	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public void save(List<ProjectBlock> projectBlocks, Long subscriptionId, String username) {
		log.trace("Try to save ProjectBlocks : Count = {}", projectBlocks.size());
		for (ProjectBlock block : projectBlocks) {
			if (!StringUtils.hasText(block.getUsername())) {
				block.setUsername(username);
			}
			if (block.getSubcriptionId() == null) {
				block.setSubcriptionId(subscriptionId);
			}
			projectBlockRepository.saveAndFlush(block);
		}
	}

	/**
	 * Find all ProjectBlocks
	 * 
	 * @return
	 */
	public List<ProjectBlock> findAll() {
		log.trace("Try to retrieve all project llocks");
		List<ProjectBlock> subscriptions = projectBlockRepository.findAll();
		return subscriptions;
	}

	public void deleteById(Long projectBlockId) {
		log.trace("Try to delete project block by Id : {}", projectBlockId);
		projectBlockRepository.deleteById(projectBlockId);
	}

	public List<ProjectBlock> findByUsername(String username) {
		log.trace("Try to retrieve all project blocks by username :  {}", username);
		return projectBlockRepository.findByUsername(username);
	}

	public List<ProjectBlock> findBySubcriptionIdAndUsername(Long subscriptionId, String username) {
		log.trace("Try to retrieve  project blocks by subscriptionId :  {} and username :  {}", subscriptionId,
				username);
		return projectBlockRepository.findBySubcriptionIdAndUsername(subscriptionId, username);
	}

	public int deleteByProjectI(Long projectId) {
		log.trace("Try to delete project block by projectId : {}", projectId);
		return projectBlockRepository.deleteByProjectId(projectId);
	}

	public int deleteByCode(String code) {
		log.trace("Try to delete project block by code : {}", code);
		return projectBlockRepository.deleteByCode(code);
	}

	public ProjectBlock findBySubcriptionIdAndProjectId(Long subscriptionId, Long projectId) {
		log.trace("Try to retrieve project blocks by subscriptionId :  {} and projectId :  {}", subscriptionId,
				projectId);
		return projectBlockRepository.findBySubcriptionIdAndProjectId(subscriptionId, projectId);
	}

}
