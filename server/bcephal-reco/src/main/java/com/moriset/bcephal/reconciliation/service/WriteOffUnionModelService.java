/**
 * 4 avr. 2024 - WriteOffUnionModelService.java
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
import com.moriset.bcephal.reconciliation.domain.WriteOffUnionModel;
import com.moriset.bcephal.reconciliation.repository.WriteOffUnionModelRepository;
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
public class WriteOffUnionModelService extends PersistentService<WriteOffUnionModel, BrowserData> {


	@Autowired
	WriteOffUnionModelRepository writeOffModelRepository;
	
	@Autowired
	WriteOffUnionFieldService writeOffFieldService;

	@Override
	public PersistentRepository<WriteOffUnionModel> getRepository() {
		return writeOffModelRepository;
	}

	
	@Transactional
	public WriteOffUnionModel save(WriteOffUnionModel writeOffModel, Locale locale) {
		log.debug("Try to  Save WriteOffModel : {}", writeOffModel);		
		try {	
			if(writeOffModel == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.Write.off.model", new Object[]{writeOffModel} , locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<WriteOffUnionField> field = writeOffModel.getFieldListChangeHandler();
			
			writeOffModel = getRepository().save(writeOffModel);
			WriteOffUnionModel id = writeOffModel;
			
			field.getNewItems().forEach( item -> {
				log.trace("Try to save WriteOffFieldValue : {}", item);
				item.setModel(id);
				writeOffFieldService.save(item,locale);
				log.trace("WriteOffFieldValue saved : {}", item.getId());
			});
			field.getUpdatedItems().forEach( item -> {
				log.trace("Try to save WriteOffFieldValue : {}", item);
				item.setModel(id);
				writeOffFieldService.save(item,locale);
				log.trace("WriteOffFieldValue saved : {}", item.getId());
			});
			field.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete WriteOffFieldValue : {}", item);
					writeOffFieldService.deleteById(item.getId());
					log.trace("WriteOffFieldValue deleted : {}", item.getId());
				}
			});
			
			log.debug("WriteOffModel saved : {} ", writeOffModel.getId());
	        return writeOffModel;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save WriteOffModel : {}", writeOffModel, e);
			String message = getMessageSource().getMessage("unable.to.save.Write.off.model", new Object[]{writeOffModel} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public void delete(WriteOffUnionModel writeOffModel) {
		log.debug("Try to delete WriteOffModel : {}", writeOffModel);	
		if(writeOffModel == null || writeOffModel.getId() == null) {
			return;
		}
		
		ListChangeHandler<WriteOffUnionField> field = writeOffModel.getFieldListChangeHandler();
		field.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete WriteOffField : {}", item);
				writeOffFieldService.deleteById(item.getId());
				log.trace("WriteOffField deleted : {}", item.getId());
			}
		});
		field.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete WriteOffField : {}", item);
				writeOffFieldService.deleteById(item.getId());
				log.trace("WriteOffField deleted : {}", item.getId());
			}
		});
		getRepository().deleteById(writeOffModel.getId());
		log.debug("WriteOffModel successfully to delete : {} ", writeOffModel);
	    return;	
	}

}
