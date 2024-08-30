/**
 * 3 juin 2024 - LoaderTemplateService.java
 *
 */
package com.moriset.bcephal.loader.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.loader.domain.LoaderTemplate;
import com.moriset.bcephal.loader.domain.LoaderTemplateBrowserData;
import com.moriset.bcephal.loader.repository.LoaderTemplateRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FileUtil;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Service
@Slf4j
public class LoaderTemplateService extends MainObjectService<LoaderTemplate, BrowserData> {
	
	
	@Value("${bcephal.project.data-dir}")
	String projectDataDir;
	
	
	@Value("${bcephal.languages:en}")
	List<String> locales;
	
	
	@Autowired
	LoaderTemplateRepository loaderTemplateRepository;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	SecurityService securityService;

	@Override
	public MainObjectRepository<LoaderTemplate> getRepository() {
		return loaderTemplateRepository;
	}

	@Override
	protected LoaderTemplate getNewItem() {
		LoaderTemplate loaderTemplate = new LoaderTemplate();
		String baseName = "LoaderTemplate ";
		int i = 1;
		loaderTemplate.setName(baseName + i);
		while(getByName(loaderTemplate.getName()) != null) {
			i++;
			loaderTemplate.setName(baseName + i);
		}
		return loaderTemplate;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_LOADER_TEMPLATE;
	}
	
	public LoaderTemplate save(LoaderTemplate template, Locale locale, String projectName) {
		String tempDir = template.getRepository();
		if(!StringUtils.hasText(tempDir)) {
			throw new BcephalException("Repository is not setted!");
		}
		
		if(!StringUtils.hasText(template.getCode())) {
			template.setCode("" + System.currentTimeMillis());
		}
		Path tempDirPath = Paths.get(tempDir);
		Path path = Paths.get(projectDataDir, projectName, "loadertemplate", template.getCode());
		Path repository = Paths.get(path.toString(), tempDirPath.toFile().getName());
		template.setRepository(repository.toString());
		template = super.save(template, locale);
		if(!path.toFile().exists()) {
			path.toFile().mkdirs();
		}
		
		if (tempDirPath.toFile().exists()) {
			try {
				if (path.toFile().exists()) {
					FileUtil.delete(path.toFile());
				}
				FileUtils.copyToDirectory(tempDirPath.toFile(), path.toFile());
				FileUtil.delete(tempDirPath.toFile());
				FileUtil.delete(tempDirPath.toFile().getParentFile());
			} catch (IOException e) {
				log.error("Unable to unzipfile {}", e);
			}
		}
		return template;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		log.debug("User session saved");
		logService.saveUserSessionLog(username, clientId, projectCode, usersession, objectId, functionalityCode, rightLevel, profileId);
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	protected LoaderTemplateBrowserData getNewBrowserData(LoaderTemplate item) {
		return new LoaderTemplateBrowserData(item);
	}

	@Override
	protected Specification<LoaderTemplate> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<LoaderTemplate> qBuilder = new RequestQueryBuilder<LoaderTemplate>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("group"), root.get("visibleInShortcut"), 
		    		root.get("creationDate"), root.get("modificationDate"));	
		    if (filter != null && StringUtils.hasText(filter.getCriteria())) {
		    	qBuilder.addLikeCriteria("name", filter.getCriteria());
		    }
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    if(filter.getColumnFilters() != null) {
				qBuilder.addFilter(filter.getColumnFilters());
			}
	        return qBuilder.build();
		};
	}

}
