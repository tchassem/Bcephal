/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.repository.EntityRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class EntityService extends PersistentService<Entity, BrowserData> {

	@Autowired
	EntityRepository entityRepository;

	@Autowired
	AttributeService attributeService;

	@Override
	public EntityRepository getRepository() {
		return entityRepository;
	}

	@Transactional
	public Entity createEntity(String name, Long modelId, Locale locale) {
		log.debug("Try to  create entity : {}", name);
		try {
			if (name == null) {
				String message = messageSource.getMessage("unable.to.save.null.entity", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(name)) {
				String message = messageSource.getMessage("unable.to.create.entity.without.name", new Object[] { "" },
						locale);
				throw new BcephalException(message);
			}

			Entity entity = entityRepository.findByName(name);
			if (entity != null) {
				String message = messageSource.getMessage("duplicate.entity.name", new Object[] { name }, locale);
				throw new BcephalException(message);
			}

			entity = new Entity();
			entity.setModel(new Model(modelId));
			entity.setName(name);
			entity = entityRepository.save(entity);
			log.debug("Entity successfully to created : {} ", entity.getId());
			return entity;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating model : {}", name, e);
			String message = messageSource.getMessage("unable.to.create.model", new Object[] { name }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public Entity saveEntity(Entity entity, Locale locale) {
		log.debug("Try to  Save entity : {}", entity);
		try {
			if (entity == null) {
				String message = messageSource.getMessage("unable.to.save.null.entity", new Object[] { entity },
						locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(entity.getName())) {
				String message = messageSource.getMessage("unable.to.save.entity.with.empty.name",
						new String[] { entity.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<Attribute> attributes = entity.getAttributeListChangeHandler();
			entity = super.save(entity, locale);
			saveAttributes(entity, locale, attributes);
			// entity.setAttributes(attributes.getItems());
			log.debug("Entity successfully saved : {} ", entity);
			return entity;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save model : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { entity }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveAttributes(Entity entity, Locale locale, ListChangeHandler<Attribute> listHandler) {
		List<Attribute> newItems = listHandler.getNewItems();
		List<Attribute> updatedItems = listHandler.getUpdatedItems();
		List<Attribute> deletedItems = listHandler.getDeletedItems();
		Entity id = entity;
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setEntity(id);
				attributeService.save(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setEntity(id);
				attributeService.save(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				attributeService.deleteAttribute(item, locale);
			});
		}
	}

	public void deleteEntity(Entity entity, Locale locale) {
		log.debug("Try to  delete entity : {}", entity);
		try {
			if (entity == null || entity.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.entity", new Object[] { entity },
						locale);
				throw new BcephalException(message);
			}
			entity.getAttributeListChangeHandler().getItems().forEach(item -> {
				attributeService.deleteAttribute(item, locale);
			});
			entity.getAttributeListChangeHandler().getDeletedItems().forEach(item -> {
				attributeService.deleteAttribute(item, locale);
			});
			deleteById(entity.getId());
			log.debug("entity successfully to delete : {} ", entity);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.delete.entity", new Object[] { entity }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public List<Entity> getEntityByModelId(Long id, Locale locale) {
		log.debug("Try to  get entity by model id : {}", id);
		if (id == null) {
			String message = messageSource.getMessage("unable.to.find.model.entity.by.null.id", new Object[] { id },
					locale);
			throw new BcephalException(message);
		}
		Model model = new Model();
		model.setId(id);
		return entityRepository.findByModel(model);
	}

}
