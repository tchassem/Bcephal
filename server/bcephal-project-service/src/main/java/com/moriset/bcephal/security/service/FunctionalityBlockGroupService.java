/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.project.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.FunctionalityBlock;
import com.moriset.bcephal.security.domain.FunctionalityBlockGroup;
import com.moriset.bcephal.security.repository.FunctionalityBlockGroupRepository;
import com.moriset.bcephal.security.repository.FunctionalityBlockRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class FunctionalityBlockGroupService {

	@Autowired
	private FunctionalityBlockGroupRepository functionalityBlockGroupRepository;

	@Autowired
	private FunctionalityBlockRepository functionalityBlockRepository;

	/**
	 * Saves the given ProjectBlock in the DB.
	 * 
	 * @param functionalityBlock ProjectBlock to save
	 * @return saved ProjectBlock
	 */
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public FunctionalityBlockGroup save(FunctionalityBlockGroup group, Long projectId, String username) {
		log.trace("Try to save FunctionalityBlock group : Name = {}", group.getName());
		if (!StringUtils.hasText(group.getUsername())) {
			group.setUsername(username);
		}
		if (group.getProjectId() == null) {
			group.setProjectId(projectId);
		}

		ListChangeHandler<FunctionalityBlock> blocks = group.getBlockListChangeHandler();

		group = functionalityBlockGroupRepository.save(group);
		FunctionalityBlockGroup id = group;
		blocks.getNewItems().forEach(item -> {
			log.trace("Try to save FunctionalityBlock : {}", item);
			item.setGroupId(id);
			if (!StringUtils.hasText(item.getUsername())) {
				item.setUsername(username);
			}
			if (item.getProjectId() == null) {
				item.setProjectId(projectId);
			}
			functionalityBlockRepository.save(item);
			log.trace("GrilleColumn saved : {}", item.getId());
		});
		blocks.getUpdatedItems().forEach(item -> {
			log.trace("Try to save FunctionalityBlock : {}", item);
			item.setGroupId(id);
			if (!StringUtils.hasText(item.getUsername())) {
				item.setUsername(username);
			}
			if (item.getProjectId() == null) {
				item.setProjectId(projectId);
			}
			functionalityBlockRepository.save(item);
			log.trace("GrilleColumn saved : {}", item.getId());
		});
		blocks.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete FunctionalityBlock : {}", item);
				functionalityBlockRepository.deleteById(item.getId());
				log.trace("GrilleColumn deleted : {}", item.getId());
			}
		});
		group.setBlocks(blocks.getItems());
		log.debug("FunctionalityBlock group saved : {} - {}", group.getId(), group.getName());
		return group;
	}

	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public boolean delete(Long id) {
		log.trace("Try to delete FunctionalityBlock group : Name = {}", id);
		functionalityBlockRepository.deleteByGroupId(id);
		functionalityBlockGroupRepository.deleteById(id);
		return true;
	}

	public List<FunctionalityBlockGroup> findByUsername(String username) {
		log.trace("Try to retrieve all functionality block Group");
		return functionalityBlockGroupRepository.findByUsername(username);
	}

	public FunctionalityBlockGroup GetById(Long id) {
		log.trace("Try to retrieve  functionality block Group by id");
		Optional<FunctionalityBlockGroup> item = functionalityBlockGroupRepository.findById(id);
		if (item != null && item.isPresent()) {
			return item.get();
		}
		return null;
	}

	public List<FunctionalityBlockGroup> findByProjectIdAndUsername(Long projectId, String username) {
		log.trace("Try to retrieve all functionality block Group");
		return functionalityBlockGroupRepository.findByProjectIdAndUsername(projectId, username);
	}

}
