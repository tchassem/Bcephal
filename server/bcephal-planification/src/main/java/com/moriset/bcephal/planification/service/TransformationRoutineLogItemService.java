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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineLogItem;
import com.moriset.bcephal.planification.repository.TransformationRoutineLogItemRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;

@Service
public class TransformationRoutineLogItemService extends PersistentService<TransformationRoutineLogItem, TransformationRoutineLogItem> {

	@Autowired
	TransformationRoutineLogItemRepository logItemRepository;

	@Override
	public TransformationRoutineLogItemRepository getRepository() {
		return logItemRepository;
	}

	public BrowserDataPage<TransformationRoutineLogItem> search(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<TransformationRoutineLogItem> page = new BrowserDataPage<TransformationRoutineLogItem>();
		page.setPageSize(filter.getPageSize());
		Specification<TransformationRoutineLogItem> specification = getBrowserDatasSpecification(filter, locale);

		if (filter.isShowAll()) {
			List<TransformationRoutineLogItem> items = getRepository().findAll(specification, Sort.by(Order.desc("id")));
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
			Page<TransformationRoutineLogItem> oPage = getRepository().findAll(specification,
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

	protected Specification<TransformationRoutineLogItem> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<TransformationRoutineLogItem> qBuilder = new RequestQueryBuilder<TransformationRoutineLogItem>(root, query, cb);
			qBuilder.select(TransformationRoutineLogItem.class);
			//qBuilder.select(TransformationRoutineLog.class, root.get("id"), root.get("loaderName"));
//			qBuilder.select(TransformationRoutineLog.class, root.get("id"), root.get("loaderName"), 
//		    		root.get("group"), root.get("visibleInShortcut"), 
//		    		root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && filter.getGroupId() != null) {
				qBuilder.addEqualsCriteria("logId", filter.getGroupId());
			}
			
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("itemName", filter.getCriteria());
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
		if ("itemName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("itemName");
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
