/**
 * 
 */
package com.moriset.bcephal.accounting.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.accounting.domain.BookingModel;
import com.moriset.bcephal.accounting.domain.BookingModelPivot;
import com.moriset.bcephal.accounting.repository.BookingModelPivotRepository;
import com.moriset.bcephal.accounting.repository.BookingModelRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class BookingModelService extends MainObjectService<BookingModel, BrowserData> {
	
	@Autowired
	BookingModelRepository bookingModelRepository;
	
	@Autowired
	UniverseFilterService universeFilterService;
	
	
	@Autowired
	BookingModelPivotRepository bookingModelPivotRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.ACCOUNTING_BOOKING;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode,String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel,profileId);
	}
	
	@Override
	public BookingModelRepository getRepository() {
		return bookingModelRepository;
	}

	protected BookingModel getNewItem(String baseName, boolean startWithOne) {
		BookingModel config = new BookingModel();
		int i = 0;
		config.setName(baseName);
		if(startWithOne) {
			i = 1;
			config.setName(baseName + i);
		}
		while(getByName(config.getName()) != null) {
			i++;
			config.setName(baseName + i);
		}
		return config;
	}
	
	@Override
	protected BookingModel getNewItem() {
		return getNewItem("Booking Model", false);
	}

	@Override
	protected BrowserData getNewBrowserData(BookingModel item) {
		return new BrowserData(item.getId(), item.getName(), item.getCreationDate(), item.getModificationDate());
	}

	@Override
	protected Specification<BookingModel> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<BookingModel> qBuilder = new RequestQueryBuilder<BookingModel>(root, query, cb);
			qBuilder.select(BrowserData.class);		   
		    if (filter != null && !org.apache.commons.lang3.StringUtils.isBlank(filter.getCriteria())) {
		    	qBuilder.addLikeCriteria("name", filter.getCriteria());
		    }
		    qBuilder.addNoTInObjectId(hidedObjectIds);
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

	
	
	@Override
	@Transactional
	public BookingModel save(BookingModel bookingModel, Locale locale) {
		log.debug("Try to  Booking model : {}", bookingModel);		
		try {	
			if(bookingModel == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{bookingModel} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if(!StringUtils.hasLength(bookingModel.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.booking.model.with.empty.name", new String[]{bookingModel.getName()} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			
			if(bookingModel.getFilter() != null) {
				universeFilterService.save(bookingModel.getFilter(), locale);
			}
			
			ListChangeHandler<BookingModelPivot> items = bookingModel.getPivotListChangeHandler();
			
			bookingModel.setModificationDate(new Timestamp(System.currentTimeMillis()));
			bookingModel = bookingModelRepository.save(bookingModel);
			BookingModel id = bookingModel;
			items.getNewItems().forEach( item -> {
				log.trace("Try to save booking model pivot : {}", item);
				item.setBooking(id);
				bookingModelPivotRepository.save(item);
				log.trace("Booking model pivot saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				log.trace("Try to save  booking model pivot : {}", item);
				item.setBooking(id);
				bookingModelPivotRepository.save(item);
				log.trace("Booking model pivot saved : {}", item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete  booking model pivot : {}", item);
					bookingModelPivotRepository.deleteById(item.getId());
					log.trace("Booking model pivot deleted : {}", item.getId());
				}
			});
			
			log.debug("Booking model saved : {} ", bookingModel.getId());
	        return bookingModel;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save Booking model : {}", bookingModel, e);
			String message = getMessageSource().getMessage("unable.to.save.booking.model", new Object[]{bookingModel} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

	@Override
	@Transactional
	public void delete(BookingModel bookingModel) {
		log.debug("Try to delete Booking model : {}", bookingModel);	
		if(bookingModel == null || bookingModel.getId() == null) {
			return;
		}		
		ListChangeHandler<BookingModelPivot> items = bookingModel.getPivotListChangeHandler();
		items.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Booking model pivot : {}", item);
				bookingModelPivotRepository.deleteById(item.getId());
				log.trace("Booking model deleted : {}", item.getId());
			}
		});
		bookingModelRepository.deleteById(bookingModel.getId());	
		
		log.debug("Booking model successfully to delete : {} ", bookingModel);
	    return;	
	}
}
