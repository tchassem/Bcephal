package com.moriset.bcephal.etl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.etl.domain.EmailFilter;
import com.moriset.bcephal.etl.repository.EmailFilterRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;

import lombok.Data;

@Data
@Service
public class EmailFilterService {
	
	@Autowired
	private EmailFilterRepository emailFilterRepository;
	
	
	
	public BrowserDataPage<EmailFilter> search(BrowserDataFilter filter, java.util.Locale locale, Long profileId,String projectCode) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<EmailFilter> page = new BrowserDataPage<EmailFilter>();
		page.setPageSize(filter.getPageSize());
		
//		String functionalityCode = getBrowserFunctionalityCode();
//		List<Long> hidedObjectIds = new ArrayList<>(0);
//		if(StringUtils.hasText(functionalityCode)) {
//			hidedObjectIds =  getHidedObjectId(profileId,functionalityCode,projectCode);
//		}
		
		
		Specification<EmailFilter> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<EmailFilter> items = emailFilterRepository.findAll(specification, getBrowserDatasSort(filter, locale));
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
			Page<EmailFilter> oPage =  emailFilterRepository.findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
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
	protected  String getBrowserFunctionalityCode() {
		return "" ;
	}
	
	protected  List<Long> getHidedObjectId(Long profileId, String functionalityCode,String projectCode){
		return new ArrayList<>() ;
	}
	
	protected  Sort  getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			
			if(filter.getColumnFilters().isSortFilter()) {
	    		build(filter.getColumnFilters());
	    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
	    			
	    			if(!filter.getColumnFilters().isJoin()) {
	    				return Sort.by(Order.desc(filter.getColumnFilters().getName()));
	    			}else {
	    				String name = filter.getColumnFilters().getJoinName() + "_" + filter.getColumnFilters().getName();
	    				return Sort.by(Order.desc(name));
	    			}
	    		}else {
	    			if(!filter.getColumnFilters().isJoin()) {
	    				return Sort.by(Order.asc(filter.getColumnFilters().getName()));
	    			}else {
	    				String name = filter.getColumnFilters().getJoinName() + "_" + filter.getColumnFilters().getName();
	    				return Sort.by(Order.asc(name));
	    			}
	    		}
	    	}else {
	    		if(filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
	    			for(ColumnFilter columnFilter : filter.getColumnFilters().getItems()){
	    				if(columnFilter.isSortFilter()) {
	    		    		build(columnFilter);
	    		    		if(columnFilter.getLink() != null && columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
	    		    			if(!columnFilter.isJoin()) {
	    		    				return Sort.by(Order.desc(columnFilter.getName()));
	    		    			}else {
	    		    				String name = columnFilter.getJoinName() + "_" + columnFilter.getName();	    		    				
	    		    				return Sort.by(Order.desc(name));
	    		    			}
	    		    		}else {
	    		    			if(!columnFilter.isJoin()) {
	    		    				return Sort.by(Order.asc(columnFilter.getName()));
	    		    			}else {
	    		    				String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
	    		    				return Sort.by(Order.asc(name));
	    		    			}
	    		    		}
	    		    	}
	    			}
	    		}
	    	}
    	}
		return Sort.by(Order.asc("id"));
	}
	
	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}
	
	protected List<EmailFilter> buildBrowserData(List<EmailFilter> contents) {
		List<EmailFilter> items = new ArrayList<EmailFilter>(0);
		if (contents != null) {
			contents.forEach(item -> {
				EmailFilter element = getNewBrowserData(item);
				if (element != null) {
					items.add(element);
				}
			});
		}
		return items;
	}
	
	protected EmailFilter getNewBrowserData(EmailFilter item) {
		return item;
	}
	
	protected Specification<EmailFilter> getBrowserDatasSpecification(BrowserDataFilter filter,
			java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<EmailFilter> qBuilder = new RequestQueryBuilder<EmailFilter>(root, query,
					cb);
//			qBuilder.select(JobDataExecution.class, root.get("id"),root.get("version"),root.get("instance"),
//					root.get("status"),root.get("exitCode"),root.get("message"),root.get("creationDate"),
//					root.get("startDate"),root.get("endDate"),root.get("modificationDate"));
			qBuilder.select(EmailFilter.class);
			
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
		if ("id".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("id");
			columnFilter.setType(Long.class);
			
		} 
		else if ("Expeditor".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("expeditor");
			columnFilter.setType(String.class);
		} else if ("Subject".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("subject");
			columnFilter.setType(String.class);
		} else if ("SendDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("sendDate");
			columnFilter.setType(Date.class);
		} else if ("Attachment".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("attachment");
			columnFilter.setType(String.class);
		} 
//		else if ("cycle".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("cycle");
//			columnFilter.setType(Boolean.class);
//		}
//		else if ("size".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("size");
//			columnFilter.setType(Integer.class);
//		}
//		else if ("prefix".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("prefix");
//			columnFilter.setType(String.class);
//		}
//		else if ("suffix".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("suffix");
//			columnFilter.setType(String.class);
//		}
	}

}
