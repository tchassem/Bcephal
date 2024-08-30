package com.moriset.bcephal.etl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.etl.domain.JobDataExecution;
import com.moriset.bcephal.etl.repository.JobDataExecutionRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Service
public class JobDataExecutionService  {

	@Autowired
	private JobDataExecutionRepository executionRepository;
	
	
	
	public BrowserDataPage<JobDataExecution> search(BrowserDataFilter filter, java.util.Locale locale, Long profileId,String projectCode) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<JobDataExecution> page = new BrowserDataPage<JobDataExecution>();
		page.setPageSize(filter.getPageSize());
		
//		String functionalityCode = getBrowserFunctionalityCode();
//		List<Long> hidedObjectIds = new ArrayList<>(0);
//		if(StringUtils.hasText(functionalityCode)) {
//			hidedObjectIds =  getHidedObjectId(profileId,functionalityCode,projectCode);
//		}
		
		
		Specification<JobDataExecution> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<JobDataExecution> items = executionRepository.findAll(specification, getBrowserDatasSort(filter, locale));
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
			Page<JobDataExecution> oPage =  executionRepository.findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
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
	
	public  Sort  getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
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
	    			for(com.moriset.bcephal.domain.filters.ColumnFilter columnFilter : filter.getColumnFilters().getItems()){
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
	
	public Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}
	
//	public List<BrowserData> buildBrowserData(List<JobDataExecution> contents) {
//		List<BrowserData> items = new ArrayList<BrowserData>(0);
//		if (contents != null) {
//			contents.forEach(item -> {
//				BrowserData element = getNewBrowserData(item);
//				if (element != null) {
//					items.add(element);
//				}
//			});
//		}
//		return items;
//	}
	
	public BrowserData getNewBrowserData(BrowserData item) {
		return item;
	}
	
	public Specification<JobDataExecution> getBrowserDatasSpecification(BrowserDataFilter filter,
			java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<JobDataExecution> qBuilder = new RequestQueryBuilder<JobDataExecution>(root, query,
					cb);
//			qBuilder.select(JobDataExecution.class, root.get("id"),root.get("version"),root.get("instance"),
//					root.get("status"),root.get("exitCode"),root.get("message"),root.get("creationDate"),
//					root.get("startDate"),root.get("endDate"),root.get("modificationDate"));
			qBuilder.select(JobDataExecution.class);
			
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
		if ("Instance".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
			columnFilter.setJoin(true);
			columnFilter.setJoinName("instance");
		} 
		else if ("id".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("id");
			columnFilter.setType(Long.class);
		} else if ("Version".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("version");
			columnFilter.setType(Long.class);
		} else if ("CreationDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("StartDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("startDate");
			columnFilter.setType(Date.class);
		} else if ("Status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(String.class);
		} else if ("EndDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		} else if ("ExitCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("exitCode");
			columnFilter.setType(String.class);
		}
		else if ("Message".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("message");
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

	
	public List<JobDataExecution> getFirstExecutions(int count) {
		log.trace("Try to find all executions");
		return executionRepository.findByLimit(count);
	}

	public List<JobDataExecution> getAllExecutions() {
		log.trace("Try to find all executions");
		return executionRepository.findAll();
	}
	
	public List<JobDataExecution> getExecutionsByInstanceId(long instanceId) {
		log.trace("Try to find executions by instance Id");
		return executionRepository.findAllByInstanceId(instanceId);
	}
	
	public Optional<JobDataExecution> getExecutionsById(long id) {
		
		Optional<JobDataExecution> instance = executionRepository.findById(id);
		
		if(instance.isEmpty()) {
			log.trace("this execution does not exist");
			return null;
		}
		
		log.trace("Try to find by Id");
		return instance;
	}
	
	public List<JobDataExecution> getExecutionsByVersion(long version){
		
		log.trace("Try to find by version");
		return executionRepository.findByVersion(version);
	}
	

	public List<JobDataExecution> getExecutionsByStatus(String status){
		log.trace("Try to find by status");
		return executionRepository.findByStatus(status);
	}
	
	public List<JobDataExecution> getExecutionsByExitCode(String code){
		log.trace("Try to find by exit code");
		return executionRepository.findByExitCode(code);
	}


	public List<JobDataExecution> getExecutionsByCreationDate(Date creationDate){
		log.trace("Try to find by creation date");
		return executionRepository.findByCreationDate(creationDate);
	}
	
	public List<JobDataExecution> getExecutionsByStartDate(Date startDate){
		log.trace("Try to find by start date");
		return executionRepository.findByStartDate(startDate);
	}
	
	List<JobDataExecution> getExecutionsByEndDate(Date endDate){
			log.trace("Try to find by end date");
			return executionRepository.findByEndDate(endDate);
	}
	
	List<JobDataExecution> getExecutionsByModificationDate(Date modificatioDate){
		log.trace("Try to find by last modification date");
		return executionRepository.findByModificationDate(modificatioDate);
	}
	
	public JobDataExecution saveDataExecution(JobDataExecution job) {

		log.trace("try to save job executions");
		
		return executionRepository.save(job);
		
	}
//	@Override
//	public PersistentRepository<JobDataExecution> getRepository() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public BrowserData getNewBrowserData(JobDataExecution item) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
