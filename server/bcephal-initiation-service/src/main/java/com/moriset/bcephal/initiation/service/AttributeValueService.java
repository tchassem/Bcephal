/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.AttributeValue;
import com.moriset.bcephal.initiation.repository.AttributeValueRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class AttributeValueService extends PersistentService<AttributeValue, BrowserData> {

	@Autowired
	AttributeValueRepository attributeValueRepository;

	@Override
	public AttributeValueRepository getRepository() {
		return attributeValueRepository;
	}

	@Override
	public AttributeValue save(AttributeValue attributeValue, Locale locale) {
		log.debug("Try to  Save attribute value : {}", attributeValue);
		try {
			if (attributeValue == null) {
				String message = messageSource.getMessage("unable.to.save.null.attribute.value",
						new Object[] { attributeValue }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(attributeValue.getName())) {
				String message = messageSource.getMessage("unable.to.save.attribute.value.with.empty.name",
						new String[] { attributeValue.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<AttributeValue> children = attributeValue.getChildrenListChangeHandler();
			attributeValue = super.save(attributeValue, locale);
			saveChildren(attributeValue, locale, children);
			log.debug("Attribute Value successfully saved : {} ", attributeValue);
			return attributeValue;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save attribute value : {}", attributeValue, e);
			String message = messageSource.getMessage("unable.to.save.attribute.value", new Object[] { attributeValue },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveChildren(AttributeValue attributeValue, Locale locale,
			ListChangeHandler<AttributeValue> listHandler) {
		List<AttributeValue> newItems = listHandler.getNewItems();
		List<AttributeValue> updatedItems = listHandler.getUpdatedItems();
		List<AttributeValue> deletedItems = listHandler.getDeletedItems();
		AttributeValue id = attributeValue;
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setParent(id);
				save(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setParent(id);
				save(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				deleteValue(item, locale);
			});
		}
	}

	public void deleteValue(AttributeValue attributeValue, Locale locale) {
		log.debug("Try to  delete attribute value : {}", attributeValue);
		try {
			if (attributeValue == null || attributeValue.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.attribute.value",
						new Object[] { attributeValue }, locale);
				throw new BcephalException(message);
			}
			attributeValue.getChildrenListChangeHandler().getItems().forEach(item -> {
				deleteValue(item, locale);
			});

			attributeValue.getChildrenListChangeHandler().getDeletedItems().forEach(item -> {
				deleteValue(item, locale);
			});
			deleteById(attributeValue.getId());
			log.debug("attribute value successfully to delete : {} ", attributeValue);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete attribute value : {}", attributeValue, e);
			String message = messageSource.getMessage("unable.to.delete.attribute.value",
					new Object[] { attributeValue }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public List<AttributeValue> getAttributeValueByAttributeId(Long id, Locale locale) {
		log.debug("Try to  get entity by model id : {}", id);
		if (id == null) {
			String message = messageSource.getMessage("unable.to.find.model.entity.by.null.id", new Object[] { id },
					locale);
			throw new BcephalException(message);
		}
		Attribute attribute = new Attribute();
		attribute.setId(id);
		return attributeValueRepository.findByAttribute(attribute);
	}

}
