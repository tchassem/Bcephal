package com.moriset.bcephal.planification.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.planification.domain.script.ScriptLog;
import com.moriset.bcephal.planification.repository.ScriptLogRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScriptLogService extends PersistentService<ScriptLog, ScriptLog> {

	@Autowired
	ScriptLogRepository logRepository;
	

	@Override
	public ScriptLogRepository getRepository() {
		return logRepository;
	}
	
	@Transactional
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to delete : {} logs", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(message);
			}
			deleteByIds(ids);
			log.debug("{} logs successfully deleted", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete logs : {}", ids.size(), e);
			String message = "Unexpected error while delete logs";
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Transactional
	public void deleteAll(BrowserDataFilter filtere, Locale locale) {
		log.debug("Try to delete all logs");
		
	}
	
	@Override
	public void delete(ScriptLog item) {
		log.debug("Try to delete log : {}", item.getId());
		if (item == null || item.getId() == null) {
			return;
		}
		getRepository().deleteById(item.getId());
		log.debug("Log successfully deleted : {}", item.getId());
	}

	public BrowserDataPage<ScriptLog> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<ScriptLog> page = new BrowserDataPage<ScriptLog>();
		page.setPageSize(filter.getPageSize());
		Specification<ScriptLog> specification = getBrowserDatasSpecification(filter, locale);

		if (filter.isShowAll()) {
			List<ScriptLog> items = getRepository().findAll(specification, Sort.by(Order.desc("id")));
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
			Page<ScriptLog> oPage = getRepository().findAll(specification,
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

	protected Specification<ScriptLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ScriptLog> qBuilder = new RequestQueryBuilder<ScriptLog>(root, query, cb);
			qBuilder.select(ScriptLog.class);
			//qBuilder.select(ScriptLog.class, root.get("id"), root.get("loaderName"));
//			qBuilder.select(ScriptLog.class, root.get("id"), root.get("loaderName"), 
//		    		root.get("group"), root.get("visibleInShortcut"), 
//		    		root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && filter.getGroupId() != null) {
				qBuilder.addEqualsCriteria("routineId", filter.getGroupId());
			}
			
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
	
	
	protected void build(ColumnFilter columnFilter) {
		if ("routineName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("routineName");
			columnFilter.setType(String.class);
		} else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		}else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		}else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		}else if ("count".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("count");
			columnFilter.setType(Integer.class);
		}else if ("message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(String.class);
		} else if ("startDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("startDate");
			columnFilter.setType(Date.class);
		} else if ("endDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Date.class);
		}
	}

}
