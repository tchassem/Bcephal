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
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.reconciliation.domain.AutoRecoLog;
import com.moriset.bcephal.reconciliation.repository.AutoRecoLogRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class AutoRecoLogService extends PersistentService<AutoRecoLog, BrowserData> {

	@Autowired
	AutoRecoLogRepository autoRecoLogRepository;
	
	@Override
	public AutoRecoLogRepository getRepository() {
		return autoRecoLogRepository;
	}

	
	@Transactional
	public AutoRecoLog save(AutoRecoLog autoRecoLog, Locale locale) {
		log.debug("Try to  Save Grid : {}", autoRecoLog);		
		try {	
			if(autoRecoLog == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{autoRecoLog} , locale);
				throw new BcephalException(message);
			}
	        return getRepository().save(autoRecoLog);	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save grille : {}", autoRecoLog, e);
			String message = getMessageSource().getMessage("unable.to.save.grille", new Object[]{autoRecoLog} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public AutoRecoLog getCurrentLogByReco(Long recoId) {
	
		return autoRecoLogRepository.getCurrentLogByReco(recoId, RunStatus.ENDED);
	}
	
}
