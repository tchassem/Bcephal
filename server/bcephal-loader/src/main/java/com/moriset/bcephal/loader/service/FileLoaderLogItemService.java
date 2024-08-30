/**
 * 
 */
package com.moriset.bcephal.loader.service;

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
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.loader.domain.FileLoaderLogItem;
import com.moriset.bcephal.loader.repository.FileLoaderLogItemRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;

import jakarta.persistence.criteria.Predicate;

/**
 * @author Joseph Wambo
 *
 */
@Service
public class FileLoaderLogItemService extends PersistentService<FileLoaderLogItem, FileLoaderLogItem> {

	@Autowired
	FileLoaderLogItemRepository logRepository;

	@Override
	public FileLoaderLogItemRepository getRepository() {
		return logRepository;
	}

	public BrowserDataPage<FileLoaderLogItem> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<FileLoaderLogItem> page = new BrowserDataPage<FileLoaderLogItem>();
		page.setPageSize(filter.getPageSize());
		Specification<FileLoaderLogItem> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<FileLoaderLogItem> items = getRepository().findAll(specification, Sort.by(Order.desc("id")));
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
			Page<FileLoaderLogItem> oPage = getRepository().findAll(specification,
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

	protected Specification<FileLoaderLogItem> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<FileLoaderLogItem> qBuilder = new RequestQueryBuilder<FileLoaderLogItem>(root, query,
					cb);
			qBuilder.select(FileLoaderLogItem.class);
			//qBuilder.select(FileLoaderLogItem.class, root.get("id"), root.get("file"));
//			qBuilder.select(FileLoaderLog.class, root.get("id"), root.get("loaderName"), 
//		    		root.get("group"), root.get("visibleInShortcut"), 
//		    		root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && filter.getGroupId() != null) {
				Predicate predicate = qBuilder.getCriteriaBuilder().equal(root.get("log"), filter.getGroupId());
				qBuilder.add(predicate);
			}
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("file", filter.getCriteria());
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
		if ("file".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("file");
			columnFilter.setType(String.class);
		} else if ("lineCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("lineCount");
			columnFilter.setType(Integer.class);
		} else if ("empty".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("empty");
			columnFilter.setType(Boolean.class);
		}else if ("loaded".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("loaded");
			columnFilter.setType(Boolean.class);
		}else if ("error".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("error");
			columnFilter.setType(Boolean.class);
		}else if ("message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
			columnFilter.setType(String.class);
		}
	}
}
