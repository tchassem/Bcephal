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
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.reconciliation.domain.WriteOffField;
import com.moriset.bcephal.reconciliation.domain.WriteOffFieldValue;
import com.moriset.bcephal.reconciliation.repository.WriteOffFieldRepository;
import com.moriset.bcephal.reconciliation.repository.WriteOffFieldValueRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class WriteOffFieldService extends PersistentService<WriteOffField, BrowserData> {

	@Autowired
	WriteOffFieldRepository writeOffFieldRepository;
	
	@Autowired
	WriteOffFieldValueRepository writeOffFieldValueRepository;
	
	@Override
	public WriteOffFieldRepository getRepository() {
		return writeOffFieldRepository;
	}

	
	@Transactional
	public WriteOffField save(WriteOffField writeOffField, Locale locale) {
		log.debug("Try to  Save WriteOffField : {}", writeOffField);		
		try {	
			if(writeOffField == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.Write.off.field", new Object[]{writeOffField} , locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<WriteOffFieldValue> values = writeOffField.getValueListChangeHandler();
			
			writeOffField = getRepository().save(writeOffField);
			WriteOffField id = writeOffField;
			
			values.getNewItems().forEach( item -> {
				log.trace("Try to save WriteOffFieldValue : {}", item);
				item.setField(id);
				writeOffFieldValueRepository.save(item);
				log.trace("WriteOffFieldValue saved : {}", item.getId());
			});
			values.getUpdatedItems().forEach( item -> {
				log.trace("Try to save WriteOffFieldValue : {}", item);
				item.setField(id);
				writeOffFieldValueRepository.save(item);
				log.trace("WriteOffFieldValue saved : {}", item.getId());
			});
			values.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete WriteOffFieldValue : {}", item);
					writeOffFieldValueRepository.deleteById(item.getId());
					log.trace("WriteOffFieldValue deleted : {}", item.getId());
				}
			});
			
			log.debug("WriteOffField saved : {} ", writeOffField.getId());
	        return writeOffField;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save ReconciliationModel : {}", writeOffField, e);
			String message = getMessageSource().getMessage("unable.to.save.reconciliation.model", new Object[]{writeOffField} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public void delete(WriteOffField writeOffField) {
		log.debug("Try to delete WriteOffFieldValue : {}", writeOffField);	
		if(writeOffField == null || writeOffField.getId() == null) {
			return;
		}
		
		ListChangeHandler<WriteOffFieldValue> values = writeOffField.getValueListChangeHandler();
		values.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete WriteOffFieldValue : {}", item);
				writeOffFieldValueRepository.deleteById(item.getId());
				log.trace("WriteOffFieldValue deleted : {}", item.getId());
			}
		});
		values.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete WriteOffFieldValue : {}", item);
				writeOffFieldValueRepository.deleteById(item.getId());
				log.trace("WriteOffFieldValue deleted : {}", item.getId());
			}
		});
		getRepository().deleteById(writeOffField.getId());
		log.debug("WriteOffFieldValue successfully to delete : {} ", writeOffField);
	    return;	
	}
}
