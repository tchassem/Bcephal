package com.moriset.bcephal.grid.service.form;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.grid.domain.form.FormModelSpot;
import com.moriset.bcephal.grid.domain.form.FormModelSpotItem;
import com.moriset.bcephal.grid.repository.form.FormModelSpotItemRepository;
import com.moriset.bcephal.grid.repository.form.FormModelSpotRepository;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelSpotService extends PersistentService<FormModelSpot, BrowserData> {

	@Autowired
	FormModelSpotRepository spotRepository;
	
	@Autowired
	FormModelSpotItemRepository spotItemRepository;

	@Override
	public PersistentRepository<FormModelSpot> getRepository() {
		return spotRepository;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public FormModelSpot save(FormModelSpot entity, Locale locale) {
		try {
		ListChangeHandler<FormModelSpotItem> conditions = entity.getItemsListChangeHandler();
		entity = getRepository().save(entity);
		FormModelSpot id = entity;
		conditions.getNewItems().forEach(item -> {
			log.trace("Try to save form field spot : {}", item);
			item.setSpot(id);
			spotItemRepository.save(item);
			log.trace("Form field spot saved : {}", item.getId());
		});
		conditions.getUpdatedItems().forEach(item -> {
			log.trace("Try to save form field spot : {}", item);
			item.setSpot(id);
			spotItemRepository.save(item);
			log.trace("Form field spot saved : {}", item.getId());
		});
		conditions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field spot: {}", item);
				spotItemRepository.deleteById(item.getId());
				log.trace("Form field spot deleted : {}", item.getId());
			}
		});
		return entity;
	} catch (BcephalException e) {
		throw e;
	} catch (Exception e) {
		log.error("Unexpected error while save Form field spot : {}", entity, e);
		String message = getMessageSource().getMessage("unable.to.save.form.field.spot", new Object[] { entity },
				locale);
		throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}
	}
	
	
	@Override
	public void delete(FormModelSpot entity) {
		log.debug("Try to delete form field spot : {}", entity);
		if (entity == null || entity.getId() == null) {
			return;
		}
		ListChangeHandler<FormModelSpotItem> conditions = entity.getItemsListChangeHandler();
		
		conditions.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field spot item : {}", item);
				spotItemRepository.deleteById(item.getId());
				log.trace("Form field spot item  deleted : {}", item.getId());
			}
		});
		conditions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field spot item  : {}", item);
				spotItemRepository.deleteById(item.getId());
				log.trace("Form field spot item  deleted : {}", item.getId());
			}
		});
		getRepository().deleteById(entity.getId());
		log.debug("Form field reference successfully to delete : {} ", entity);
		return;
	}

}
