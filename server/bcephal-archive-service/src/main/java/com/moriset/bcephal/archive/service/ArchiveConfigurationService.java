/**
 * 
 */
package com.moriset.bcephal.archive.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.archive.domain.ArchiveConfigEditorData;
import com.moriset.bcephal.archive.domain.ArchiveConfiguration;
import com.moriset.bcephal.archive.domain.ArchiveConfigurationEnrichmentItem;
import com.moriset.bcephal.archive.repository.ArchiveConfigurationEnrichmentItemRepository;
import com.moriset.bcephal.archive.repository.ArchiveConfigurationRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class ArchiveConfigurationService extends MainObjectService<ArchiveConfiguration, BrowserData> {

	@Autowired
	ArchiveConfigurationRepository archiveConfigurationRepository;

	@Autowired
	ArchiveConfigurationEnrichmentItemRepository enrichmentItemRepository;

	@Autowired
	GrilleService grilleService;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired MaterializedGridRepository materializedGridRepository;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG;
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
	public ArchiveConfigurationRepository getRepository() {
		return archiveConfigurationRepository;
	}
	
	@Override
	public EditorData<ArchiveConfiguration> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		ArchiveConfigEditorData data = new ArchiveConfigEditorData(super.getEditorData(filter, session, locale));
		data.setMatGrids(materializedGridRepository.findGenericAllAsNameables());
		return data;
	}

	protected ArchiveConfiguration getNewItem(String baseName, boolean startWithOne) {
		ArchiveConfiguration config = new ArchiveConfiguration();
		int i = 0;
		config.setName(baseName);
		if (startWithOne) {
			i = 1;
			config.setName(baseName + i);
		}
		while (getByName(config.getName()) != null) {
			i++;
			config.setName(baseName + i);
		}

		Grille grid = new Grille();
		grid.setType(GrilleType.ARCHIVE_BACKUP);
		int i2 = 0;
		String GridbaseName = " Archive Backup grid";
		grid.setName(baseName + GridbaseName);
		grid.setConsolidated(false);
		if (startWithOne) {
			i2 = 1;
			grid.setName(baseName + GridbaseName + i2);
		}
		while (getByName(grid.getName()) != null) {
			i2++;
			grid.setName(baseName + GridbaseName + i2);
		}
		config.setBackupGrid(grid);
		Grille grid2 = new Grille();
		grid2.setType(GrilleType.ARCHIVE_REPLACEMENT);
		grid2.setConsolidated(true);
		GridbaseName = " Archive Replacement grid";
		grid2.setName(baseName + GridbaseName);
		if (startWithOne) {
			i2 = 1;
			grid2.setName(baseName + GridbaseName + i2);
		}
		while (getByName(grid2.getName()) != null) {
			i2++;
			grid2.setName(baseName + GridbaseName + i2);
		}
		config.setReplacementGrid(grid2);
		return config;
	}

	@Override
	protected ArchiveConfiguration getNewItem() {
		return getNewItem("Archive Configuration", false);
	}

	@Override
	protected BrowserData getNewBrowserData(ArchiveConfiguration item) {
		return new BrowserData(item.getId(), item.getName(), item.getCreationDate(), item.getModificationDate());
	}

	@Override
	protected Specification<ArchiveConfiguration> getBrowserDatasSpecification(BrowserDataFilter filter,
			Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ArchiveConfiguration> qBuilder = new RequestQueryBuilder<ArchiveConfiguration>(root,
					query, cb);
			qBuilder.select(BrowserData.class);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			qBuilder.addNoTInObjectId(hidedObjectIds);
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	@Override
	@Transactional
	public ArchiveConfiguration save(ArchiveConfiguration archiveConfiguration, Locale locale) {
		log.debug("Try to  Save Archive Configuration : {}", archiveConfiguration);
		try {
			if (archiveConfiguration == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid",
						new Object[] { archiveConfiguration }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(archiveConfiguration.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.archive.configuration.with.empty.name",
						new String[] { archiveConfiguration.getName() }, locale);
				throw new BcephalException(message);
			}

			if (archiveConfiguration.getBackupGrid() != null) {
				grilleService.save(archiveConfiguration.getBackupGrid(), locale);
			}
			if (archiveConfiguration.getReplacementGrid() != null) {
				grilleService.save(archiveConfiguration.getReplacementGrid(), locale);
			}

			ListChangeHandler<ArchiveConfigurationEnrichmentItem> items = archiveConfiguration
					.getEnrichmentItemListChangeHandler();

			archiveConfiguration.setModificationDate(new Timestamp(System.currentTimeMillis()));
			archiveConfiguration = archiveConfigurationRepository.save(archiveConfiguration);
			ArchiveConfiguration id = archiveConfiguration;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save Archive Configuration Enrichment Item : {}", item);
				item.setConfigurationId(id);
				item.prePersistOrUpdate();
				enrichmentItemRepository.save(item);
				log.trace("Archive Configuration Enrichment Item saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Archive Configuration Enrichment Item : {}", item);
				item.setConfigurationId(id);
				item.prePersistOrUpdate();
				enrichmentItemRepository.save(item);
				log.trace("Archive Configuration Enrichment Item saved : {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Archive Configuration Enrichment Item : {}", item);
					enrichmentItemRepository.deleteById(item.getId());
					log.trace("Archive Configuration Enrichment Item deleted : {}", item.getId());
				}
			});

			log.debug("Archive Configuration saved : {} ", archiveConfiguration.getId());
			return archiveConfiguration;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save Archive Configuration : {}", archiveConfiguration, e);
			String message = getMessageSource().getMessage("unable.to.save.archive.configuration",
					new Object[] { archiveConfiguration }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	@Transactional
	public void delete(ArchiveConfiguration archiveConfiguration) {
		log.debug("Try to delete Archive Configuration : {}", archiveConfiguration);
		if (archiveConfiguration == null || archiveConfiguration.getId() == null) {
			return;
		}

		ListChangeHandler<ArchiveConfigurationEnrichmentItem> items = archiveConfiguration
				.getEnrichmentItemListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete Archive Configuration Enrichment Item : {}", item);
				enrichmentItemRepository.deleteById(item.getId());
				log.trace("Archive Configuration Enrichment Item deleted : {}", item.getId());
			}
		});

		if (archiveConfiguration.getBackupGrid() != null) {
			grilleService.delete(archiveConfiguration.getBackupGrid());
		}
		if (archiveConfiguration.getReplacementGrid() != null) {
			grilleService.delete(archiveConfiguration.getReplacementGrid());
		}
		archiveConfigurationRepository.deleteById(archiveConfiguration.getId());

		log.debug("Archive Configuration successfully to delete : {} ", archiveConfiguration);
		return;
	}
}
