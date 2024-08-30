/**
 * 
 */
package com.moriset.bcephal.scheduler.service;

import java.util.ArrayList;
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
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerLog;
import com.moriset.bcephal.scheduler.domain.SchedulerLogBrowserData;
import com.moriset.bcephal.scheduler.repository.SchedulerLogRepository;
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
public class SchedulerLogService extends PersistentService<SchedulerLog, SchedulerLogBrowserData> {
	
	@Autowired
	SchedulerLogRepository repository;
	
	@Override
	public SchedulerLogRepository getRepository() {
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
	
	public BrowserDataPage<SchedulerLogBrowserData> search(SchedulerBrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<SchedulerLogBrowserData> page = new BrowserDataPage<SchedulerLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<SchedulerLog> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<SchedulerLog> items = getRepository().findAll(specification, Sort.by(Order.asc("id")));
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
			Page<SchedulerLog> oPage = getRepository().findAll(specification, getPageable(filter, Sort.by(Order.asc("id"))));
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

	protected List<SchedulerLogBrowserData> buildBrowserData(List<SchedulerLog> contents) {
		List<SchedulerLogBrowserData> items = new ArrayList<SchedulerLogBrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				SchedulerLogBrowserData element = new SchedulerLogBrowserData(item);
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

	protected Specification<SchedulerLog> getBrowserDatasSpecification(SchedulerBrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SchedulerLog> qBuilder = new RequestQueryBuilder<SchedulerLog>(root, query, cb);
			qBuilder.select(SchedulerLog.class);	
			if (filter != null && filter.getObjectType() != null) {
				qBuilder.addEquals("objectType", filter.getObjectType());
			}
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("objectName", filter.getCriteria());
			}
			if(filter.getColumnFilters() != null) {
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

}
