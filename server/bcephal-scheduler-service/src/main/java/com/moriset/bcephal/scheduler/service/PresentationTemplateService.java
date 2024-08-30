package com.moriset.bcephal.scheduler.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.PresentationTemplate;
import com.moriset.bcephal.scheduler.domain.PresentationTemplateBrowserData;
import com.moriset.bcephal.scheduler.domain.PresentationTemplateEditorData;
import com.moriset.bcephal.scheduler.repository.PresentationTemplateRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PresentationTemplateService extends MainObjectService<PresentationTemplate, PresentationTemplateBrowserData> {
	
	@Value("${bcephal.project.data-dir}")
	String projectDataDir;
	
	@Autowired
	PresentationTemplateRepository presentationTemplateRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	public PresentationTemplateRepository getRepository() {
		return presentationTemplateRepository;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION_TEMPLATE;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	@Override
	protected PresentationTemplate getNewItem() {
		PresentationTemplate template = new PresentationTemplate();
		String baseName = "Presentation template ";
		int i = 1;
		template.setName(baseName + i);
		while(getByName(template.getName()) != null) {
			i++;
			template.setName(baseName + i);
		}
		return template;
	}
	
	@Override
	protected PresentationTemplateBrowserData getNewBrowserData(PresentationTemplate item) {
		return new PresentationTemplateBrowserData(item);
	}

	@Override
	protected Specification<PresentationTemplate> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<PresentationTemplate> qBuilder = new RequestQueryBuilder<PresentationTemplate>(root, query, cb);
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
	
		
	@Override
	public PresentationTemplateEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<PresentationTemplate> base = super.getEditorData(filter, session, locale);
		PresentationTemplateEditorData data = new PresentationTemplateEditorData(base);
					
		return data;
	}
	
	@Override
	protected PresentationTemplate getCopy(PresentationTemplate item) {
		PresentationTemplate copy = item.copy();
		String dir = item.getRepository();		
		Path path = Paths.get(dir).getParent();		
		copy.setCode("" + System.currentTimeMillis());
		String repository = Paths.get(path.toString(), copy.getCode()).toString();
		copy.setRepository(repository);
		
		try {
			copyDirectory(dir, repository);
		} catch (Exception e) {
			log.error("Unable to copy Presentation Template : {}", item.getName(), e);
		}		
		return copy;
	}
	
	public PresentationTemplate save(PresentationTemplate template, Locale locale, String projectName) {
		String tempDir = template.getRepository();
		if(!StringUtils.hasText(tempDir)) {
			throw new BcephalException("Repository is not setted!");
		}
				
		if(!StringUtils.hasText(template.getCode())) {
			template.setCode("" + System.currentTimeMillis());
		}
		String repository = Paths.get(projectDataDir, projectName, "presentationtemplates", template.getCode()).toString();
				
		Path source = Paths.get(tempDir);
		if (source.toFile().exists()) {
			Path destination = Paths.get(repository);
			if (!destination.getParent().toFile().exists()) {
				destination.getParent().toFile().mkdirs();
			}
			try {
				if (source.toString().compareTo(destination.toString()) != 0
						&& !source.startsWith(destination)) {
					deleteDirectory(destination.toFile());
					copyDirectory(source.toString(), destination.toString());

					File[] files = destination.toFile().listFiles();
					if (files != null && files.length > 0) {
						repository = files[0].getAbsolutePath();
					}

					deleteDirectory(source.toFile());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		template.setRepository(repository);	
		template = super.save(template, locale);
		return template;
	}
	
	@Override
	public PresentationTemplate save(PresentationTemplate template, Locale locale) {
		String tempDir = template.getRepository();
		if(!StringUtils.hasText(tempDir)) {
			throw new BcephalException("Repository is not setted!");
		}		
		if(!StringUtils.hasText(template.getCode())) {
			template.setCode("" + System.currentTimeMillis());
		}		
		template = super.save(template, locale);		
		return template;
	}
	
	@Override
	public void delete(PresentationTemplate template) {
		log.debug("Try to delete PresentationTemplate : {}", template);	
		if(template == null || template.getId() == null) {
			return;
		}					
		getRepository().deleteById(template.getId());
		String repository = template.getRepository();		
		try {
			if (Files.exists(Path.of(repository))) {
				deleteDirectory(Path.of(repository).toFile());
			}			
		} catch (Exception e) {
			log.error("Unable to detete dir : {}", repository, e);
		}
		
		log.debug("Grid successfully to delete : {} ", template);
	    return;	
	}
	
	
	
	
	
	private void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws Exception {
		File file = new File(destinationDirectoryLocation); 
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
	    Files.walk(Paths.get(sourceDirectoryLocation))
	      .forEach(source -> {
	          Path destination = Paths.get(destinationDirectoryLocation, source.toString()
	            .substring(sourceDirectoryLocation.length()));
	          try {
	              Files.copy(source, destination);
	          } catch (IOException e) {
	              e.printStackTrace();
	          }
	      });
	}
	
	private boolean deleteDirectory(File directoryToBeDeleted) throws Exception {
		File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}

}
