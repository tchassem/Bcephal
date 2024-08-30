/**
 * 
 */
package com.moriset.bcephal.integration.service;

import java.util.ArrayList;
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
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.integration.domain.SchedulerConnectEntityLog;
import com.moriset.bcephal.integration.repository.SchedulerConnectEntityLogRepository;
import com.moriset.bcephal.integration.scheduler.SchedulerConnectEntityBrowserDataFilter;
import com.moriset.bcephal.integration.scheduler.SchedulerConnectEntityLogBrowserData;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Service
@Slf4j
public class SchedulerConnectEntityLogService extends PersistentService<SchedulerConnectEntityLog, SchedulerConnectEntityLogBrowserData> {
	
	@Autowired
	SchedulerConnectEntityLogRepository repository;
	
	@Override
	public SchedulerConnectEntityLogRepository getRepository() {
		return repository;
	}
	
	@Transactional
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to  delete : {} logs", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			deleteByIds(ids);
			log.debug("{} logs successfully deleted ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete logs : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public BrowserDataPage<SchedulerConnectEntityLogBrowserData> search(SchedulerConnectEntityBrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<SchedulerConnectEntityLogBrowserData> page = new BrowserDataPage<SchedulerConnectEntityLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<SchedulerConnectEntityLog> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<SchedulerConnectEntityLog> items = getRepository().findAll(specification, Sort.by(Order.asc("id")));
			page.setItems(buildBrowserData(items));

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<SchedulerConnectEntityLog> oPage = getRepository().findAll(specification, getPageable(filter, Sort.by(Order.asc("id"))));
			page.setItems(buildBrowserData(oPage.getContent()));

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

	protected List<SchedulerConnectEntityLogBrowserData> buildBrowserData(List<SchedulerConnectEntityLog> contents) {
		List<SchedulerConnectEntityLogBrowserData> items = new ArrayList<SchedulerConnectEntityLogBrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				SchedulerConnectEntityLogBrowserData element = new SchedulerConnectEntityLogBrowserData(item);
				if (element != null) {
					items.add(element);
				}

			});
		}
		return items;
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

	protected Specification<SchedulerConnectEntityLog> getBrowserDatasSpecification(SchedulerConnectEntityBrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SchedulerConnectEntityLog> qBuilder = new RequestQueryBuilder<SchedulerConnectEntityLog>(root, query, cb);
			qBuilder.select(SchedulerConnectEntityLog.class);	
			if (filter != null && filter.getClientId() != null) {
				qBuilder.addEquals("clientId", filter.getClientId());
			}
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("objectName", filter.getCriteria());
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
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		}else
		if ("clientId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientId");
			columnFilter.setType(Long.class);
		}else
		if ("objectId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("objectId");
			columnFilter.setType(Long.class);
		}
		else if ("objectName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("objectName");
			columnFilter.setType(String.class);
		} 
		else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		} 
		else if ("message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(String.class);
		} 
		 
		else if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		}
		 
		else if ("details".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("details");
			columnFilter.setType(String.class);
		}
		else if ("creationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} 
		else if ("EndDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Date.class);
		}
	}
}
