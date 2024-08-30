package com.moriset.bcephal.billing.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillingModelDriverGroup;
import com.moriset.bcephal.billing.domain.BillingModelDriverGroupItem;
import com.moriset.bcephal.billing.repository.BillingModelDriverGroupItemRepository;
import com.moriset.bcephal.billing.repository.BillingModelDriverGroupRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillingModelDriverGroupService extends PersistentService<BillingModelDriverGroup, BrowserData> {

	@Autowired
	BillingModelDriverGroupRepository billingModelDriverGroupRepository;
	
	@Autowired
	BillingModelDriverGroupItemRepository billingModelDriverGroupItemRepository;

	@Override
	public PersistentRepository<BillingModelDriverGroup> getRepository() {
		return billingModelDriverGroupRepository;
	}
	
	public BillingModelDriverGroup save(BillingModelDriverGroup driverGroup, Locale locale) {
		log.debug("Try to  Save BillingModelDriverGroup : {}", driverGroup);		
		try {	
			if(driverGroup == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.BillingModelDriverGroup", new Object[]{driverGroup} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if(!StringUtils.hasLength(driverGroup.getGroupName())) {
				String message = getMessageSource().getMessage("unable.to.save.grid.with.empty.GroupName", new String[]{driverGroup.getGroupName()} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			
			ListChangeHandler<BillingModelDriverGroupItem> items = driverGroup.getItemListChangeHandler();
			
			driverGroup = billingModelDriverGroupRepository.save(driverGroup);
			BillingModelDriverGroup id = driverGroup;
			
			items.getNewItems().forEach( item -> {
				log.trace("Try to save BillingModelDriverGroupItem : {}", item);
				item.setBGroup(id);
				billingModelDriverGroupItemRepository.save(item);
				log.trace("BillingModelDriverGroupItem saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				log.trace("Try to save BillingModelDriverGroupItem : {}", item);
				item.setBGroup(id);
				billingModelDriverGroupItemRepository.save(item);
				log.trace("BillingModelDriverGroupItem saved : {}", item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete BillingModelDriverGroup : {}", item);
					billingModelDriverGroupItemRepository.deleteById(item.getId());
					log.trace("BillingModelDriverGroup deleted : {}", item.getId());
				}
			});
			
			log.debug("BillingModelDriverGroup saved : {} ", driverGroup.getId());
	        return driverGroup;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save BillingModelDriverGroup : {}", driverGroup, e);
			String message = getMessageSource().getMessage("unable.to.save.driverGroup", new Object[]{driverGroup} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
}
