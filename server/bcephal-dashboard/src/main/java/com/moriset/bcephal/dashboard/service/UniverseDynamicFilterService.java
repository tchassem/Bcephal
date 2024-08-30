package com.moriset.bcephal.dashboard.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilter;
import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilterItem;
import com.moriset.bcephal.dashboard.repository.UniverseDynamicFilterItemRepository;
import com.moriset.bcephal.dashboard.repository.UniverseDynamicFilterRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.filters.AttributeFilterService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UniverseDynamicFilterService extends PersistentService<UniverseDynamicFilter, BrowserData> {

	@Autowired
	UniverseDynamicFilterRepository dynamicFilterRepository;
	
	@Autowired
	DynamicPeriodFilterService dynamicPeriodFilterService;
	
	@Autowired
	AttributeFilterService attributeFilterService;
	
	@Autowired
	UniverseDynamicFilterItemRepository itemRepository;

	@Override
	public UniverseDynamicFilterRepository getRepository() {
		return dynamicFilterRepository;
	}

	@Override
	@Transactional
	public UniverseDynamicFilter save(UniverseDynamicFilter dynamicFilter, Locale locale) {
		log.debug("Try to  Save Universe Dynamic Filter : {}", dynamicFilter);
		try {
			if (dynamicFilter == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.universe.dynamic.filter",
						new Object[] { dynamicFilter }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<UniverseDynamicFilterItem> items = dynamicFilter.getItemsListChangeHandler();
			dynamicFilter = dynamicFilterRepository.save(dynamicFilter);
			UniverseDynamicFilter id = dynamicFilter;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save UniverseDynamicFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("UniverseDynamicFilterItem saved :  {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save UniverseDynamicFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("UniverseDynamicFilterItem saved :  {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete UniverseDynamicFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("UniverseDynamicFilterItem deleted :  {}", item.getId());
				}
			});			
			log.debug("Universe Dynamic Filter saved : {} ", dynamicFilter.getId());
			return dynamicFilter;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save Universe Dynamic Filter : {}", dynamicFilter, e);
			String message = getMessageSource().getMessage("unable.to.save.Univers.dynamic.filter", new Object[] { dynamicFilter },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	@Transactional
	public void delete(UniverseDynamicFilter universeDynamicFilter) {
		log.debug("Try to delete dashboard report : {}", universeDynamicFilter);
		if (universeDynamicFilter == null || universeDynamicFilter.getId() == null) {
			return;
		}
		
		universeDynamicFilter.getItemsListChangeHandler().getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete UniverseDynamicFilterItem : {}", item.getId());
				itemRepository.deleteById(item.getId());
				log.trace("UniverseDynamicFilterItem deleted :  {}", item.getId());
			}
		});
		universeDynamicFilter.getItemsListChangeHandler().getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete UniverseDynamicFilterItem : {}", item.getId());
				itemRepository.deleteById(item.getId());
				log.trace("UniverseDynamicFilterItem deleted :  {}", item.getId());
			}
		});
		dynamicFilterRepository.deleteById(universeDynamicFilter.getId());
		log.debug("dashboard report successfully to delete : {} ", universeDynamicFilter);
		return;
	}

}
