package com.moriset.bcephal.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.project.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.FunctionalityBlock;
import com.moriset.bcephal.security.repository.FunctionalityBlockRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class FunctionalityBlockService {

	@Autowired
	private FunctionalityBlockRepository functionalityBlockRepository;

	/**
	 * Saves the given ProjectBlock in the DB.
	 * 
	 * @param functionalityBlock ProjectBlock to save
	 * @return saved ProjectBlock
	 */
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public FunctionalityBlock save(FunctionalityBlock functionalityBlock, Long projectId, String username) {
		log.trace("Try to save FunctionalityBlock : Name = {}", functionalityBlock.getName());
		if (!StringUtils.hasText(functionalityBlock.getUsername())) {
			functionalityBlock.setUsername(username);
		}
		if (functionalityBlock.getProjectId() == null) {
			functionalityBlock.setProjectId(projectId);
		}
		return functionalityBlockRepository.saveAndFlush(functionalityBlock);
	}

	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public void save(List<FunctionalityBlock> functionalityBlocks, Long projectId, String username) {
		log.trace("Try to save  FunctionalityBlock : Count = {}", functionalityBlocks.size());
		for (FunctionalityBlock block : functionalityBlocks) {
			if (!StringUtils.hasText(block.getUsername())) {
				block.setUsername(username);
			}
			if (block.getProjectId() == null) {
				block.setProjectId(projectId);
			}
			functionalityBlockRepository.saveAndFlush(block);
		}
	}

	/**
	 * Find all functionality block
	 * 
	 * @return
	 */
	public List<FunctionalityBlock> findAll() {
		log.trace("Try to retrieve all functionality block");
		List<FunctionalityBlock> subscriptions = functionalityBlockRepository.findAll();
		return subscriptions;
	}

	public void deleteById(Long projectBlockId) {
		log.trace("Try to retrieve all functionality block");
		functionalityBlockRepository.deleteById(projectBlockId);
	}

	public List<FunctionalityBlock> findByUsername(String username) {
		log.trace("Try to retrieve all functionality block");
		return functionalityBlockRepository.findByUsername(username);
	}

	public List<FunctionalityBlock> findByProjectIdAndUsername(Long projectId, String username) {
		log.trace("Try to retrieve all functionality block");
		return functionalityBlockRepository.findByProjectIdAndUsername(projectId, username);
	}

}
