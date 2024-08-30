/**
 * 
 */
package com.moriset.bcephal.scheduler.service;

import java.sql.Timestamp;
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
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemType;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogItem;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogItemBrowserData;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerLogItemRepository;
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
public class SchedulerPlannerLogItemService extends PersistentService<SchedulerPlannerLogItem, SchedulerPlannerLogItemBrowserData> {
	
	@Autowired
	SchedulerPlannerLogItemRepository repository;
	
	@Override
	public SchedulerPlannerLogItemRepository getRepository() {
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
	
	public BrowserDataPage<SchedulerPlannerLogItemBrowserData> search(SchedulerBrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<SchedulerPlannerLogItemBrowserData> page = new BrowserDataPage<SchedulerPlannerLogItemBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<SchedulerPlannerLogItem> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<SchedulerPlannerLogItem> items = getRepository().findAll(specification, Sort.by(Order.asc("id")));
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
			Page<SchedulerPlannerLogItem> oPage = getRepository().findAll(specification, getPageable(filter, Sort.by(Order.asc("id"))));
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

	protected List<SchedulerPlannerLogItemBrowserData> buildBrowserData(List<SchedulerPlannerLogItem> contents) {
		List<SchedulerPlannerLogItemBrowserData> items = new ArrayList<SchedulerPlannerLogItemBrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				SchedulerPlannerLogItemBrowserData element = new SchedulerPlannerLogItemBrowserData(item);
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

	protected Specification<SchedulerPlannerLogItem> getBrowserDatasSpecification(SchedulerBrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SchedulerPlannerLogItem> qBuilder = new RequestQueryBuilder<SchedulerPlannerLogItem>(root, query, cb);
			qBuilder.select(SchedulerPlannerLogItem.class);	
			if (filter != null && filter.getGroupId() != null) {
				qBuilder.addEquals("logId", filter.getGroupId());
			}
			if (filter != null && filter.getObjectType() != null) {
				qBuilder.addEquals("objectType", filter.getObjectType());
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
		if ("type".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("type");
			columnFilter.setType(SchedulerPlannerItemType.class);
		}else if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("objectName");
			columnFilter.setType(String.class);
		}else if ("message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(String.class);
		} else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		}else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		}else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		}else if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		}else if ("currentItemId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("currentItemId");
			columnFilter.setType(Long.class);
		}else if ("runnedSetps".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("runnedSetps");
			columnFilter.setType(Integer.class);
		}else if ("position".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("position");
			columnFilter.setType(Integer.class);
		} else if ("setps".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("setps");
			columnFilter.setType(Integer.class);
		} else if ("startDate".equalsIgnoreCase(columnFilter.getName()) || "creationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("startDate");
			columnFilter.setType(Timestamp.class);
		} else if ("endDate".equalsIgnoreCase(columnFilter.getName()) || "endDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Timestamp.class);
		}
	}

}
