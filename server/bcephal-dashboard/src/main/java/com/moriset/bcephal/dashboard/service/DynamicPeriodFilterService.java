package com.moriset.bcephal.dashboard.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.dashboard.domain.DynamicPeriodFilter;
import com.moriset.bcephal.dashboard.domain.DynamicPeriodFilterItem;
import com.moriset.bcephal.dashboard.repository.DynamicPeriodFilterItemRepository;
import com.moriset.bcephal.dashboard.repository.DynamicPeriodFilterRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.repository.filters.PeriodFilterItemRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DynamicPeriodFilterService extends PersistentService<DynamicPeriodFilter, BrowserData> {

	@Autowired
	PeriodFilterItemRepository periodFilterItemRepository;
	
	@Autowired
	DynamicPeriodFilterItemRepository dynamicPeriodFilterItemRepository;
	
	@Autowired
	DynamicPeriodFilterRepository dynamicPeriodFilterRepository;

	
	public DynamicPeriodFilterRepository getRepository() {
		return dynamicPeriodFilterRepository;
	}



	@Override
	@Transactional
	public DynamicPeriodFilter save(DynamicPeriodFilter dynamicPeriodFilter, Locale locale) {
		log.debug("Try to  Save Dynamic Period Filter : {}", dynamicPeriodFilter);
		try {
			if (dynamicPeriodFilter == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.dynamic.period.filter",
						new Object[] { dynamicPeriodFilter }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<DynamicPeriodFilterItem> items = dynamicPeriodFilter.getItemListChangeHandler();			
			dynamicPeriodFilter = dynamicPeriodFilterRepository.save(dynamicPeriodFilter);
			DynamicPeriodFilter id = dynamicPeriodFilter;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save Dynamic Period Filter Item : {}", item);
				item.setFilter(id);
				saveDynamicPeriodFilterItem(item, locale);
				log.trace(" Dynamic Period Filter saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save  Dynamic Period Filter : {}", item);
				item.setFilter(id);
				saveDynamicPeriodFilterItem(item, locale);
				log.trace(" Dynamic Period Filter saved : {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete  Dynamic Period Filter : {}", item);
					deleteDynamicPeriodFilterItem(item);
					log.trace(" Dynamic Period Filter deleted : {}", item.getId());
				}
			});

			log.debug("Dashboard saved : {} ", dynamicPeriodFilter.getId());
			return dynamicPeriodFilter;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save dashboard : {}", dynamicPeriodFilter, e);
			String message = getMessageSource().getMessage("unable.to.save.dashboard", new Object[] { dynamicPeriodFilter },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void deleteDynamicPeriodFilterItem(DynamicPeriodFilterItem item) {
		log.trace("Try to delete Dynamic Period Filter Item : {}", item);
		if(item == null || item.getId() == null) {
			return;
		}
		if(item.getStartPeriodFilter() != null && item.getStartPeriodFilter().getId() != null) {			
			periodFilterItemRepository.delete(item.getStartPeriodFilter());
		}
		if(item.getEndPeriodFilter() != null && item.getEndPeriodFilter().getId() != null) {			
			periodFilterItemRepository.deleteById(item.getEndPeriodFilter().getId());
		}
		dynamicPeriodFilterItemRepository.deleteById(item.getId());
		log.trace(" Dynamic Period Filter deleted : {}", item.getId());		
	}



	private void saveDynamicPeriodFilterItem(DynamicPeriodFilterItem item, Locale locale) {
		log.trace("Try to save Dynamic Period Filter Item : {}", item);
		if(item == null) {
			return;
		}
		if(item.getStartPeriodFilter() != null) {			
			item.setStartPeriodFilter(periodFilterItemRepository.save(item.getStartPeriodFilter()));
		}
		if(item.getEndPeriodFilter() != null) {			
			item.setEndPeriodFilter(periodFilterItemRepository.save(item.getEndPeriodFilter()));
		}
		item = dynamicPeriodFilterItemRepository.save(item);
		log.trace(" Dynamic Period Filter saved : {}", item.getId());
	}



	@Override
	@Transactional
	public void delete(DynamicPeriodFilter dynamicPeriodFilter) {
		log.debug("Try to delete Dynamic Period Filter : {}", dynamicPeriodFilter);
		if (dynamicPeriodFilter == null || dynamicPeriodFilter.getId() == null) {
			return;
		}
		ListChangeHandler<DynamicPeriodFilterItem> items = dynamicPeriodFilter.getItemListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete Dynamic Period Filter Item : {}", item);
				deleteDynamicPeriodFilterItem(item);
				log.trace("Dynamic Period Filter Item deleted : {}", item.getId());
			}
		});
		dynamicPeriodFilterRepository.deleteById(dynamicPeriodFilter.getId());
		log.debug("dashboard report successfully to delete : {} ", dynamicPeriodFilter);
		return;
	}

}
