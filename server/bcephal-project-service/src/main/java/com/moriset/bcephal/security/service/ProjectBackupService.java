package com.moriset.bcephal.security.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.project.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.security.repository.SimpleArchiveRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ProjectBackupService {

	@Autowired
	SimpleArchiveRepository simpleArchiveRepository;
	
	@Autowired
	protected MessageSource messageSource;

	public SimpleArchiveRepository getRepository() {
		return simpleArchiveRepository;
	}
	
	@Transactional
	public SimpleArchive create(SimpleArchive archive, Locale locale) {
		log.debug("Try to create Simple Archive : {}", archive);
		if (getRepository() == null) {
			return archive;
		}
		try {
			if (archive == null) {
				String message = messageSource.getMessage("unable.to.create.null.simple.archive", new Object[] { archive }, locale);
				throw new BcephalException(message);
			}
			archive = getRepository().save(archive);
			
			log.debug(" Simple archive saved : {} ", archive.getId());

			return archive;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while create archive : {}", archive, e);
			String message = messageSource.getMessage("unable.to.create.simple.archive", new Object[] { archive }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public BrowserDataPage<SimpleArchive> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<SimpleArchive> page = new BrowserDataPage<SimpleArchive>();
		page.setPageSize(filter.getPageSize());

		Specification<SimpleArchive> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<SimpleArchive> items = getRepository().findAll(specification, Sort.by(Order.desc("id")));
			// Sort.by(Order.asc("name")));
			page.setItems(items);

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			log.debug("Nb items found : {}", getRepository().count());
			Page<SimpleArchive> oPage = getRepository().findAll(specification,
					getPageable(filter, Sort.by(Order.desc("id"))));
			page.setItems(oPage.getContent());

			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageCount(oPage.getTotalPages());
			page.setTotalItemCount(Long.valueOf(oPage.getTotalElements()).intValue());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		}

		return page;
	
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

	protected Specification<SimpleArchive> getBrowserDatasSpecification(BrowserDataFilter filter, java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SimpleArchive> qBuilder = new RequestQueryBuilder<SimpleArchive>(root, query,
					cb);
			qBuilder.select(SimpleArchive.class);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			
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

	private void build(ColumnFilter columnFilter) {
		
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		} else if ("description".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("description");
			columnFilter.setType(String.class);
		}  else if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		} else if ("userName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("userName");
			columnFilter.setType(String.class);
		} else if ("archiveMaxCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("archiveMaxCount");
			columnFilter.setType(Integer.class);
		} else if ("fileName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("fileName");
			columnFilter.setType(String.class);
		} else if ("archiveId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("archiveId");
			columnFilter.setType(Long.class);
		} else if ("clientId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientId");
			columnFilter.setType(Long.class);
		} else if ("creationDate".equalsIgnoreCase(columnFilter.getName()) || "CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		}
	}
	

	/**
	 * Deletes a project backup.
	 * 
	 * @param archiveId The code of the project to delete
	 * @param locale    User locale
	 * @return Renamed project
	 */
	public boolean deleteProjectBackup(Long archiveId, java.util.Locale locale) throws BcephalException {
		log.debug("Try to delete project backup : {}", archiveId);
		try {
			if (archiveId == null) {
				String message = messageSource.getMessage("unable.to.delete.project.backup.with.empty.id",
						new Object[] { archiveId }, locale);
				throw new BcephalException(message);
			}
			
			Optional<SimpleArchive> OptArchive = simpleArchiveRepository.findById(archiveId);
			if (OptArchive != null && OptArchive.isEmpty()) {
				String message = messageSource.getMessage("unable.to.find.project.backup.with.id",
						new Object[] { archiveId }, locale);
				throw new BcephalException(message);
			}
			
			SimpleArchive archive = OptArchive.get();
			archive.setRepository(FilenameUtils.separatorsToSystem(archive.getRepository()));
			if (StringUtils.hasText(archive.getRepository())) {
				java.io.File archiveFile = new java.io.File(archive.getRepository());
				if (archiveFile.exists()) {
				 	FileUtils.forceDelete(archiveFile);
				}
			}

			simpleArchiveRepository.delete(archive);
			log.trace("Delete status : {}", true);
			return true;
			
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while deleting project backup: {}", archiveId, e);
			String message = messageSource.getMessage("unable.to.delete.project.backup", new Object[] { archiveId }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

}
