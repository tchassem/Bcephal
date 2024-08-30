/**
 * 
 */
package com.moriset.bcephal.accounting.service;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.accounting.domain.Booking;
import com.moriset.bcephal.accounting.repository.BookingRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.service.BaseService;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class BookingService extends PersistentService<Booking, BrowserData> implements BaseService<Booking, BrowserData> {
	
	@Autowired
	BookingRepository bookingRepository;
	
	@Override
	public BookingRepository getRepository() {
		return bookingRepository;
	}


	@Override
	public Specification<Booking> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Booking> qBuilder = new RequestQueryBuilder<Booking>(root, query, cb);
			qBuilder.select(BrowserData.class);		   
		    if (filter != null && !org.apache.commons.lang3.StringUtils.isBlank(filter.getCriteria())) {
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
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("Manual".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("manual");
			columnFilter.setType(Boolean.class);
		} else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("entryDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("bookingDate1");
			columnFilter.setType(Date.class);
		}
	}
	
	@Override
	@Transactional
	public Booking save(Booking booking, Locale locale) {
		log.debug("Try to  Save Booking : {}", booking);		
		try {	
			if(booking == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{booking} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			booking = bookingRepository.save(booking);
			log.debug("Booking saved : {} ", booking.getId());
	        return booking;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save Booking : {}", booking, e);
			String message = getMessageSource().getMessage("unable.to.save.booking", new Object[]{booking} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

	@Override
	@Transactional
	public void delete(Booking booking) {
		log.debug("Try to delete booking : {}", booking);	
		if(booking == null || booking.getId() == null) {
			return;
		}
		bookingRepository.deleteById(booking.getId());		
		
		log.debug("Booking successfully to delete : {} ", booking);
	    return;	
	}


	@Override
	public BrowserData getNewBrowserData(Booking item) {
		// TODO Auto-generated method stub
		return new BrowserData(item.getId(),null);
	}
}
