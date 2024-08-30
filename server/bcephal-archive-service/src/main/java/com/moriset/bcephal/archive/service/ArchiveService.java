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

import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.archive.domain.ArchiveBrowserData;
import com.moriset.bcephal.archive.repository.ArchiveRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class ArchiveService extends MainObjectService<Archive, ArchiveBrowserData> {

	@Autowired
	ArchiveRepository archiveRepository;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE;
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
	public ArchiveRepository getRepository() {
		return archiveRepository;
	}

	protected Archive getNewItem(String baseName, boolean startWithOne) {
		Archive archive = new Archive();
		int i = 0;
		archive.setName(baseName);
		if (startWithOne) {
			i = 1;
			archive.setName(baseName + i);
		}
		while (getByName(archive.getName()) != null) {
			i++;
			archive.setName(baseName + i);
		}
		return archive;
	}

	@Override
	protected Archive getNewItem() {
		return getNewItem("Archive ", false);
	}

	@Override
	protected ArchiveBrowserData getNewBrowserData(Archive item) {
		return new ArchiveBrowserData(item);
	}

	@Override
	protected Specification<Archive> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Archive> qBuilder = new RequestQueryBuilder<Archive>(root, query, cb);
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
	public Archive save(Archive archive, Locale locale) {
		log.debug("Try to  Save Archive : {}", archive);
		try {
			if (archive == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[] { archive },
						locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(archive.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.archive.with.empty.name",
						new String[] { archive.getName() }, locale);
				throw new BcephalException(message);
			}

			archive.setModificationDate(new Timestamp(System.currentTimeMillis()));
			archive = archiveRepository.save(archive);
			log.debug("Archive saved : {} ", archive.getId());
			return archive;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save Archive : {}", archive, e);
			String message = getMessageSource().getMessage("unable.to.save.archive", new Object[] { archive }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	@Transactional
	public void delete(Archive archive) {
		log.debug("Try to delete Archive : {}", archive);
		if (archive == null || archive.getId() == null) {
			return;
		}
		archiveRepository.deleteById(archive.getId());

		log.debug("Archive successfully to delete : {} ", archive);
		return;
	}
}
