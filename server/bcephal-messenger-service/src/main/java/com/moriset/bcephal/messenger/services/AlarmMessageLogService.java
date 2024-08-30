/**
 * 
 */
package com.moriset.bcephal.messenger.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.messenger.controller.BcephalException;
import com.moriset.bcephal.messenger.model.AlarmMessageLog;
import com.moriset.bcephal.messenger.model.AlarmMessageLogBrowserData;
import com.moriset.bcephal.messenger.model.AlarmMessageLogFail;
import com.moriset.bcephal.messenger.model.AlarmMessageLogSuccess;
import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.model.AlarmMessageStatus;
import com.moriset.bcephal.messenger.model.BrowserDataFilter;
import com.moriset.bcephal.messenger.model.BrowserDataPage;
import com.moriset.bcephal.messenger.model.ColumnFilter;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogFailRepository;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogSuccessRepository;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogToSendRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Service
@Slf4j
public class AlarmMessageLogService {

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected AlarmMessageLogToSendRepository toSendRepository;
	
	@Autowired
	protected AlarmMessageLogFailRepository failRepository;
	
	@Autowired
	protected AlarmMessageLogSuccessRepository successRepository;
		
	
	public BrowserDataPage<AlarmMessageLogBrowserData> searchTosend(BrowserDataFilter filter, String projectCode, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<AlarmMessageLogBrowserData> page = new BrowserDataPage<AlarmMessageLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<AlarmMessageLogToSend> specification = getBrowserDatasSpecificationToSend(filter, projectCode, locale);
		if (filter.isShowAll()) {
			List<AlarmMessageLogToSend> items = toSendRepository.findAll(specification, getBrowserDatasSort(filter, locale));
			for(AlarmMessageLog message : items) {
				page.getItems().add(new AlarmMessageLogBrowserData(message));
			}

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<AlarmMessageLogToSend> oPage = toSendRepository.findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
			for(AlarmMessageLog message : oPage.getContent()) {
				page.getItems().add(new AlarmMessageLogBrowserData(message));
			}
			
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
	
	public BrowserDataPage<AlarmMessageLogBrowserData> searchSuccess(BrowserDataFilter filter, String projectCode, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<AlarmMessageLogBrowserData> page = new BrowserDataPage<AlarmMessageLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<AlarmMessageLogSuccess> specification = getBrowserDatasSpecificationSucess(filter, projectCode, locale);
		if (filter.isShowAll()) {
			List<AlarmMessageLogSuccess> items = successRepository.findAll(specification, getBrowserDatasSort(filter, locale));
			for(AlarmMessageLog message : items) {
				page.getItems().add(new AlarmMessageLogBrowserData(message));
			}

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<AlarmMessageLogSuccess> oPage = successRepository.findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
			for(AlarmMessageLog message : oPage.getContent()) {
				page.getItems().add(new AlarmMessageLogBrowserData(message));
			}

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
	

	public BrowserDataPage<AlarmMessageLogBrowserData> searchFail(BrowserDataFilter filter, String projectCode, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<AlarmMessageLogBrowserData> page = new BrowserDataPage<AlarmMessageLogBrowserData>();
		page.setPageSize(filter.getPageSize());
		Specification<AlarmMessageLogFail> specification = getBrowserDatasSpecificationFail(filter, projectCode, locale);
		if (filter.isShowAll()) {
			List<AlarmMessageLogFail> items = failRepository.findAll(specification, getBrowserDatasSort(filter, locale));
			for(AlarmMessageLog message : items) {
				page.getItems().add(new AlarmMessageLogBrowserData(message));
			}		

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<AlarmMessageLogFail> oPage = failRepository.findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
			for(AlarmMessageLog message : oPage.getContent()) {
				page.getItems().add(new AlarmMessageLogBrowserData(message));
			}

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
	
	protected Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {			
			if(filter.getColumnFilters().isSortFilter()) {
	    		build(filter.getColumnFilters());
	    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
	    			return Sort.by(Order.desc(filter.getColumnFilters().getName()));
	    			
	    		}
	    		else {
	    			return Sort.by(Order.asc(filter.getColumnFilters().getName()));
	    		}
	    	}
			else {
	    		if(filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
	    			List<Order> orders = new ArrayList<Order>();
					for (ColumnFilter columnFilter : filter.getColumnFilters().getItems()) {
						if (columnFilter.isSortFilter()) {
							build(columnFilter);
							if (columnFilter.getLink() != null && columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
								orders.add(Order.desc(columnFilter.getName()));
							} else {
								orders.add(Order.asc(columnFilter.getName()));
							}
						}
					}
					if(orders.size() > 0) {
						return Sort.by(orders);
					}
	    		}
	    	}
    	}
		return getDefaultSort();
	}
	
	protected Sort getDefaultSort() {
		return Sort.by(Order.asc("id"));
	}
	
	
	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}


	protected Specification<AlarmMessageLogFail> getBrowserDatasSpecificationFail(BrowserDataFilter filter, String projectCode, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<AlarmMessageLogFail> qBuilder = new RequestQueryBuilder<AlarmMessageLogFail>(root, query, cb);
			qBuilder.select(AlarmMessageLogFail.class);	
			if (projectCode != null) {
				qBuilder.addEquals("projectCode", projectCode);
			}
			if (filter != null && filter.getType() != null) {
				qBuilder.addEquals("messageType", filter.getType());
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
	
	protected Specification<AlarmMessageLogSuccess> getBrowserDatasSpecificationSucess(BrowserDataFilter filter, String projectCode, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<AlarmMessageLogSuccess> qBuilder = new RequestQueryBuilder<AlarmMessageLogSuccess>(root, query, cb);
			qBuilder.select(AlarmMessageLogSuccess.class);	
			if (projectCode != null) {
				qBuilder.addEquals("projectCode", projectCode);
			}
			if (filter != null && filter.getType() != null) {
				qBuilder.addEquals("messageType", filter.getType());
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
	
	protected Specification<AlarmMessageLogToSend> getBrowserDatasSpecificationToSend(BrowserDataFilter filter, String projectCode, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<AlarmMessageLogToSend> qBuilder = new RequestQueryBuilder<AlarmMessageLogToSend>(root, query, cb);
			qBuilder.select(AlarmMessageLogToSend.class);
			if (projectCode != null) {
				qBuilder.addEquals("projectCode", projectCode);
			}
			if (filter != null && filter.getType() != null) {
				qBuilder.addEquals("messageType", filter.getType());
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
		if ("Id".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("id");
			columnFilter.setType(Long.class);
		} else if ("Username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("messageType".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("messageType");
			columnFilter.setType(String.class);
		} else if ("Subject".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("title");
			columnFilter.setType(String.class);
		} else if ("content".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("content");
			columnFilter.setType(String.class);
		} else if ("messageLogStatus".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(AlarmMessageStatus.class);
		} else if ("log".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("errorMessage");
			columnFilter.setType(String.class);
		} else if ("errorCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("errorCode");
			columnFilter.setType(Integer.class);
		} else if ("maxSendCount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("maxSendCount");
			columnFilter.setType(Long.class);
		} else if ("sendAttempts".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("sendCount");
			columnFilter.setType(Long.class);
		} else if ("mode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("mode");
			columnFilter.setType(String.class);
		} else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("lastSendDate");
			columnFilter.setType(Date.class);
		} else if ("FirstSendDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("firstSendDate");
			columnFilter.setType(Date.class);			
		} else if ("Audience".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("contactsImpl");
			columnFilter.setType(String.class);
		}
		else if ("ccAudience".equalsIgnoreCase(columnFilter.getName()) || "cc".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("ccContacts");
			columnFilter.setType(String.class);
		}
		
		
	}
	
	@Transactional
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to  delete : {} messages", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ids.forEach(item -> {
				deleteById(item);
			});
			log.debug("{} entities successfully deleteed ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete messages : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}

	}

	@Transactional
	public void cancel(List<Long> ids, String username, Locale locale) {
		log.debug("Try to  cancel : {} messages", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.cancel.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ids.forEach(item -> {
				cancelById(item, username);
			});
			log.debug("{} entities successfully canceled ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while cancel messages : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.cancel", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}

	}
	
	@Transactional
	public void reset(List<Long> ids, Locale locale) {
		log.debug("Try to  reset : {} messages", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.reset.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ids.forEach(item -> {
				resetById(item);
			});
			log.debug("{} entities successfully reseted ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while reset messages : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.reset", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}

	}
	
	private void deleteById(Long id) {
		if(toSendRepository.findById(id).isPresent()) {
			toSendRepository.deleteById(id);
		}
		else if(failRepository.findById(id).isPresent()) {
			failRepository.deleteById(id);		
		}
		else if(successRepository.findById(id).isPresent()) {
			successRepository.deleteById(id);
		}		
		log.debug("Entity successfully deleted : {} ", id);
	}

	private void cancelById(Long id, String username) {		
		Optional<AlarmMessageLogToSend> toSendResponse = toSendRepository.findById(id);
		if(toSendResponse.isPresent()) {
			AlarmMessageLogToSend item = toSendResponse.get();
			AlarmMessageLogFail fail = new AlarmMessageLogFail(item);
			fail.setStatus(AlarmMessageStatus.CANCELED);
			fail.setErrorMessage("Canceled by user : " + username);
			failRepository.save(fail);
			toSendRepository.deleteById(id);
			log.debug("Message successfully canceled : {} ", id);
		}
	}
	
	private void resetById(Long id) {
		Optional<AlarmMessageLogFail> failResponse = failRepository.findById(id);
		if(failResponse.isPresent()) {
			AlarmMessageLogFail item = failResponse.get();
			AlarmMessageLogToSend toSend = new AlarmMessageLogToSend(item);
			toSend.setStatus(AlarmMessageStatus.NEW);
			toSend.setErrorMessage(null);
			toSend.setSendCount(0L);	
			toSendRepository.save(toSend);
			failRepository.deleteById(id);
			log.debug("Message successfully reseted : {} ", id);
		}
	}

	
}
