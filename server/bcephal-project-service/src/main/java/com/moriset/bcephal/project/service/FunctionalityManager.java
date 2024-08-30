package com.moriset.bcephal.project.service;

import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.project.archive.PostgresProperties;
import com.moriset.bcephal.project.archive.ProjectFileUtil;
import com.moriset.bcephal.security.domain.FunctionalityBlockGroup;
import com.moriset.bcephal.security.domain.FunctionalityWorkspace;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.service.FunctionalityBlockGroupService;
import com.moriset.bcephal.security.service.ProjectService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FunctionalityManager {

	@Autowired
	MessageSource messageSource;

	@Autowired
	DataSourceProperties properties;

	@Autowired
	ProjectFileUtil projectFileUtil;

	@Autowired
	Map<String, DataSource> bcephalDataSources;

	@Autowired
	PostgresProperties postgresProperties;

	@Autowired
	FunctionalityBlockGroupService functionalityBlockGroupService;

	@Autowired
	ProjectService projectService;

	@Autowired
	FuntionalityCode funtionalityCode;

	/**
	 * Load Functionality Workspace
	 * 
	 * @param locale
	 * @return The Functionality workspace
	 */
	public FunctionalityWorkspace getFunctionalityWorkspace(Long projectId, String username, java.util.Locale locale) {
		log.debug("Try to get functionality workspace by projectId {} and username {}", projectId, username);
		FunctionalityWorkspace workspace = new FunctionalityWorkspace();

		workspace.setAvailableFunctionalities(funtionalityCode.getAllfuntionalities(locale));
		if (projectId == null && TenantContext.getCurrentTenant() != null) {
			String tenantId = TenantContext.getCurrentTenant();
			if (StringUtils.hasText(tenantId) && !tenantId.equalsIgnoreCase("postgres")) {
				Optional<ProjectBrowserData> project = projectService.findByCode(tenantId);
				if (project.isPresent()) {
					projectId = project.get().getId();
				}
			}
		}
		if (projectId != null) {
			workspace.setFunctionalityBlockGroups(
					functionalityBlockGroupService.findByProjectIdAndUsername(projectId, username));
			if (workspace.getFunctionalityBlockGroups().size() == 0) {
				FunctionalityBlockGroup group = new FunctionalityBlockGroup();
				String message = messageSource.getMessage("home.page", new String[] {}, locale);
				group.setName(message);
				group.setProjectId(projectId);
				group.setUsername(username);
				group.setDefaultGroup(true);
				group = functionalityBlockGroupService.save(group, projectId, username);
				workspace.getFunctionalityBlockGroups().add(group);
			}
		}

		return workspace;
	}

//    /**
//	 * Retrieves functionality blocks by project ant user
//	 * 
//	 * @param projectId project
//	 * @param username Connected user name
//	 * @param locale User locale
//	 * @return List of functionality block
//	 */
//	public List<FunctionalityBlock> getFunctionalityBlocksBySubscriptionAndUser(Long projectId, String username, Locale locale) {
//		log.debug("Try to retrieve project blocks by projectId: {} and user : {}", projectId, username);
//		return functionalityBlockService.findByProjectIdAndUsername(projectId, username);
//	}
//    
//
//	public FunctionalityBlock saveFunctionalityBlock(FunctionalityBlock functionalityBlock, Long projectId, String username, Locale locale) {
//		return functionalityBlockService.save(functionalityBlock, projectId, username);
//	}
//	
//	public void saveFunctionalityBlocks(List<FunctionalityBlock> functionalityBlocks, Long projectId, String username, Locale locale) {		
//		functionalityBlockService.save(functionalityBlocks, projectId, username);
//	}
//
//	/**
//	 * delete functionality block by id
//	 * @param functionalityBlock
//	 * @param locale
//	 */
//
//	public void deleteProjectBlock(Long functionalityBlockId, Locale locale) {
//		log.debug("Try to delete functionality block by ID: {}", functionalityBlockId);
//		functionalityBlockService.deleteById(functionalityBlockId);
//	}

}
