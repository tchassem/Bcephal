package com.moriset.bcephal.grid.service.form;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.grid.domain.form.FormModelFieldReference;
import com.moriset.bcephal.grid.domain.form.FormModelFieldReferenceCondition;
import com.moriset.bcephal.grid.repository.form.FormModelFieldReferenceConditionRepository;
import com.moriset.bcephal.grid.repository.form.FormModelFieldReferenceRepository;
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
public class FormModelFieldReferenceService extends PersistentService<FormModelFieldReference, BrowserData> {

	@Autowired
	FormModelFieldReferenceRepository formModelFieldReferenceRepository;
	
	@Autowired
	FormModelFieldReferenceConditionRepository formModelFieldReferenceConditionRepository;

	@Override
	public PersistentRepository<FormModelFieldReference> getRepository() {
		return formModelFieldReferenceRepository;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public FormModelFieldReference save(FormModelFieldReference entity, Locale locale) {
		try {
		ListChangeHandler<FormModelFieldReferenceCondition> conditions = entity.getConditionListChangeHandler();
		entity = getRepository().save(entity);
		FormModelFieldReference id = entity;
		conditions.getNewItems().forEach(item -> {
			log.trace("Try to save form field Reference : {}", item);
			item.setReference(id);
			formModelFieldReferenceConditionRepository.save(item);
			log.trace("Form field Reference saved : {}", item.getId());
		});
		conditions.getUpdatedItems().forEach(item -> {
			log.trace("Try to save form field Reference : {}", item);
			item.setReference(id);
			formModelFieldReferenceConditionRepository.save(item);
			log.trace("Form field Reference saved : {}", item.getId());
		});
		conditions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field Reference: {}", item);
				formModelFieldReferenceConditionRepository.deleteById(item.getId());
				log.trace("Form field Reference deleted : {}", item.getId());
			}
		});
		return entity;
	} catch (BcephalException e) {
		throw e;
	} catch (Exception e) {
		log.error("Unexpected error while save Form field Reference : {}", entity, e);
		String message = getMessageSource().getMessage("unable.to.save.form.field.reference", new Object[] { entity },
				locale);
		throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}
	}
	
	
	@Override
	public void delete(FormModelFieldReference entity) {
		log.debug("Try to delete form field : {}", entity);
		if (entity == null || entity.getId() == null) {
			return;
		}
		ListChangeHandler<FormModelFieldReferenceCondition> conditions = entity.getConditionListChangeHandler();
		
		conditions.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field reference condition : {}", item);
				formModelFieldReferenceConditionRepository.deleteById(item.getId());
				log.trace("Form field reference condition deleted : {}", item.getId());
			}
		});
		conditions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field reference condition : {}", item);
				formModelFieldReferenceConditionRepository.deleteById(item.getId());
				log.trace("Form field reference condition deleted : {}", item.getId());
			}
		});
		getRepository().deleteById(entity.getId());
		log.debug("Form field reference successfully to delete : {} ", entity);
		return;
	}

}
