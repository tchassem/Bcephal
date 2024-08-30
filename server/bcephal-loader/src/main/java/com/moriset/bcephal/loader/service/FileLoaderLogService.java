/**
 * 
 */
package com.moriset.bcephal.loader.service;

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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.loader.domain.FileLoaderLog;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.loader.repository.FileLoaderLogRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;

/**
 * @author Joseph Wambo
 *
 */
@Service
public class FileLoaderLogService extends PersistentService<FileLoaderLog, FileLoaderLog> {

	@Autowired
	FileLoaderLogRepository logRepository;

	@Override
	public FileLoaderLogRepository getRepository() {
		return logRepository;
	}

	public BrowserDataPage<FileLoaderLog> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<FileLoaderLog> page = new BrowserDataPage<FileLoaderLog>();
		page.setPageSize(filter.getPageSize());
		Specification<FileLoaderLog> specification = getBrowserDatasSpecification(filter, locale);

		if (filter.isShowAll()) {
			List<FileLoaderLog> items = getRepository().findAll(specification, Sort.by(Order.desc("id")));
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
			Page<FileLoaderLog> oPage = getRepository().findAll(specification,
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

	protected Specification<FileLoaderLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<FileLoaderLog> qBuilder = new RequestQueryBuilder<FileLoaderLog>(root, query, cb);
			qBuilder.select(FileLoaderLog.class);
			//qBuilder.select(FileLoaderLog.class, root.get("id"), root.get("loaderName"));
//			qBuilder.select(FileLoaderLog.class, root.get("id"), root.get("loaderName"), 
//		    		root.get("group"), root.get("visibleInShortcut"), 
//		    		root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && filter.getGroupId() != null) {
				qBuilder.addEqualsCriteria("loaderId", filter.getGroupId());
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
		if ("loaderName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("loaderName");
			columnFilter.setType(String.class);
		} else if ("uploadMethod".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("uploadMethod");
			columnFilter.setType(FileLoaderMethod.class);
		} else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(RunModes.class);
		}else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(RunStatus.class);
		}else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		}else if ("fileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("fileCount");
			columnFilter.setType(Integer.class);
		}else if ("emptyFileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("emptyFileCount");
			columnFilter.setType(Integer.class);
		}else if ("errorFileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("errorFileCount");
			columnFilter.setType(Integer.class);
		} else if ("loadedFileCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("loadedFileCount");
			columnFilter.setType(Integer.class);
		} else if ("error".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("error");
			columnFilter.setType(Boolean.class);
		} else if ("message".equalsIgnoreCase(columnFilter.getName())) {
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
