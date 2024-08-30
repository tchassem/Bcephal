package com.moriset.bcephal.archive.service;

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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.archive.domain.ArchiveLog;
import com.moriset.bcephal.archive.domain.ArchiveLogAction;
import com.moriset.bcephal.archive.domain.ArchiveLogBrowserData;
import com.moriset.bcephal.archive.domain.ArchiveLogStatus;
import com.moriset.bcephal.archive.repository.ArchiveLogRepository;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;

/**
 * 
 * @author MORISET-004
 *
 */
@Service
public class ArchiveLogService extends PersistentService<ArchiveLog, ArchiveLogBrowserData> {

	@Autowired
	ArchiveLogRepository archiveLogRepository;

	@Override
	public ArchiveLogRepository getRepository() {
		return archiveLogRepository;
	}

	public BrowserDataPage<ArchiveLogBrowserData> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<ArchiveLogBrowserData> page = new BrowserDataPage<ArchiveLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<ArchiveLog> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<ArchiveLog> items = getRepository().findAll(specification, Sort.by(Order.asc("id")));
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
			Page<ArchiveLog> oPage = getRepository().findAll(specification,
					getPageable(filter, Sort.by(Order.asc("id"))));
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

	public Specification<ArchiveLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ArchiveLog> qBuilder = new RequestQueryBuilder<ArchiveLog>(root, query, cb);
			qBuilder.select(ArchiveLogBrowserData.class);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
				qBuilder.addLikeCriteria("user", filter.getCriteria());
				qBuilder.addLikeCriteria("message", filter.getCriteria());
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
		if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("Message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(String.class);
		} else if ("Status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(ArchiveLogStatus.class);
		} else if ("Action".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("action");
			columnFilter.setType(ArchiveLogAction.class);
		} 
//		else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("creationDate");
//			columnFilter.setType(Date.class);
//		} 
	}
	
	
	protected List<ArchiveLogBrowserData> buildBrowserData(List<ArchiveLog> contents) {
		List<ArchiveLogBrowserData> items = new ArrayList<ArchiveLogBrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				ArchiveLogBrowserData element = getNewBrowserData(item);
				if (element != null) {
					items.add(element);
				}

			});
		}
		return items;
	}

	public ArchiveLogBrowserData getNewBrowserData(ArchiveLog item) {
		return new ArchiveLogBrowserData(item);
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

}
