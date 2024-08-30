/**
 * 4 avr. 2024 - WriteOffUnionFieldService.java
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
import com.moriset.bcephal.reconciliation.domain.WriteOffUnionField;
import com.moriset.bcephal.reconciliation.domain.WriteOffUnionFieldValue;
import com.moriset.bcephal.reconciliation.repository.WriteOffUnionFieldRepository;
import com.moriset.bcephal.reconciliation.repository.WriteOffUnionFieldValueRepository;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Service
@Slf4j
public class WriteOffUnionFieldService extends PersistentService<WriteOffUnionField, BrowserData> {

	@Autowired
	WriteOffUnionFieldRepository writeOffFieldRepository;
	
	@Autowired
	WriteOffUnionFieldValueRepository writeOffFieldValueRepository;

	@Override
	public PersistentRepository<WriteOffUnionField> getRepository() {
		return writeOffFieldRepository;
	}

	
	@Transactional
	public WriteOffUnionField save(WriteOffUnionField writeOffField, Locale locale) {
		log.debug("Try to Save WriteOffField : {}", writeOffField);		
		try {	
			if(writeOffField == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.Write.off.field", new Object[]{writeOffField} , locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<WriteOffUnionFieldValue> values = writeOffField.getValueListChangeHandler();
			
			writeOffField = getRepository().save(writeOffField);
			WriteOffUnionField id = writeOffField;
			
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
	public void delete(WriteOffUnionField writeOffField) {
		log.debug("Try to delete WriteOffFieldValue : {}", writeOffField);	
		if(writeOffField == null || writeOffField.getId() == null) {
			return;
		}
		
		ListChangeHandler<WriteOffUnionFieldValue> values = writeOffField.getValueListChangeHandler();
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
