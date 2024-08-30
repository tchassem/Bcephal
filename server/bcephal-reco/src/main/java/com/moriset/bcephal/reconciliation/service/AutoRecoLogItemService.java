/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.reconciliation.domain.AutoRecoLogItem;
import com.moriset.bcephal.reconciliation.repository.AutoRecoLogItemRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class AutoRecoLogItemService extends PersistentService<AutoRecoLogItem, BrowserData> {

	@Autowired
	AutoRecoLogItemRepository autoRecoLogItemRepository;
	
	@Override
	public AutoRecoLogItemRepository getRepository() {
		return autoRecoLogItemRepository;
	}

	
	@Transactional
	public AutoRecoLogItem save(AutoRecoLogItem autoRecoLogItem, Locale locale) {
		log.debug("Try to  Save Grid : {}", autoRecoLogItem);		
		try {	
			if(autoRecoLogItem == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{autoRecoLogItem} , locale);
				throw new BcephalException(message);
			}
	        return getRepository().save(autoRecoLogItem);	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save grille : {}", autoRecoLogItem, e);
			String message = getMessageSource().getMessage("unable.to.save.grille", new Object[]{autoRecoLogItem} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
}
