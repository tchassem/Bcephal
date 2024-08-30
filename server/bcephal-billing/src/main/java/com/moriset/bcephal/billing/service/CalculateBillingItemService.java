package com.moriset.bcephal.billing.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.billing.domain.CalculateBillingFilterItem;
import com.moriset.bcephal.billing.domain.CalculateBillingItem;
import com.moriset.bcephal.billing.repository.CalculateBillingFilterItemRepository;
import com.moriset.bcephal.billing.repository.CalculateBillingItemRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculateBillingItemService extends PersistentService<CalculateBillingItem, BrowserData> {
	
	@Autowired
	CalculateBillingItemRepository calculateBillingItemRepository;
	
	@Autowired
	CalculateBillingFilterItemRepository calculateBillingFilterItemRepository;
	
	@Override
	public PersistentRepository<CalculateBillingItem> getRepository() {
		return calculateBillingItemRepository;
	}
	
	public CalculateBillingItem save(CalculateBillingItem calculateItem, Locale locale) {
		log.debug("Try to Save CalculateBillingItem : {}", calculateItem);		
		try {	
			if(calculateItem == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.CalculateBillingItem", new Object[]{calculateItem} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			
			ListChangeHandler<CalculateBillingFilterItem> items = calculateItem.getBillingFilterListChangeHandler();
			
			calculateItem = getRepository().save(calculateItem);
			CalculateBillingItem id = calculateItem;
			
			items.getNewItems().forEach( item -> {
				log.trace("Try to save CalculateBillingFilterItem : {}", item);
				item.setCalculateEltId(id);
				calculateBillingFilterItemRepository.save(item);
				log.trace("CalculateBillingFilterItem saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				log.trace("Try to save CalculateBillingFilterItem : {}", item);
				item.setCalculateEltId(id);
				calculateBillingFilterItemRepository.save(item);
				log.trace("CalculateBillingFilterItem updated : {}", item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete CalculateBillingFilterItem : {}", item);
					calculateBillingFilterItemRepository.deleteById(item.getId());
					log.trace("CalculateBillingFilterItem deleted : {}", item.getId());
				}
			});
			
			log.debug("CalculateBillingItem saved : {} ", calculateItem.getId());
	        return calculateItem;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save CalculateBillingItem : {}", calculateItem, e);
			String message = getMessageSource().getMessage("unable.to.save.CalculateBillingItem", new Object[]{calculateItem} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public void delete(CalculateBillingItem calculateItem) {
		log.debug("Try to delete calculateItem : {}", calculateItem);
		if (calculateItem == null || calculateItem.getId() == null) {
			return;
		}
		ListChangeHandler<CalculateBillingFilterItem> items = calculateItem.getBillingFilterListChangeHandler();
		items.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete CalculateBillingFilterItem : {}", item);
				calculateBillingFilterItemRepository.deleteById(item.getId());
				log.trace("CalculateBillingFilterItem deleted : {}", item);
			}			
		});
		
		getRepository().deleteById(calculateItem.getId());
		log.debug("CalculateItem successfully deleted : {}", calculateItem);
	}

}
