package com.moriset.bcephal.initiation.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.domain.ModelEditorData;
import com.moriset.bcephal.initiation.repository.ModelRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModelService extends PersistentService<Model, BrowserData> {

	@Autowired
	ModelRepository modelRepository;

	@Autowired
	EntityService entityService;
	
	@Autowired
	AttributeService attributeService;

	@Override
	public ModelRepository getRepository() {
		return modelRepository;
	}

	public ModelEditorData getEditorData(EditorDataFilter filter, Locale locale) {
		ModelEditorData data = new ModelEditorData();
		data.getAllModels().setOriginalList(getModels(locale));
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else if (filter.getId() != null) {
			data.setItem(getById(filter.getId()));
		}
		return data;
	}

	private Model getNewItem() {
		Model model = new Model();
		model.setVisibleInShortcut(true);
		String baseName = "Model ";
		int i = 1;
		model.setName(baseName + i);
		while (getRepository().findByNameIgnoreCase(model.getName()) != null) {
			i++;
			model.setName(baseName + i);
		}
		return model;
	}

	public List<Model> getModels(java.util.Locale locale) {
		log.debug("Try to  retrieve models.");
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findAll();
	}

	@Transactional
	public Model createModel(String name, Locale locale) {
		log.debug("Try to  create model : {}", name);
		try {
			if (name == null) {
				String message = messageSource.getMessage("unable.to.save.null.model", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(name)) {
				String message = messageSource.getMessage("unable.to.create.model.without.name", new Object[] { "" },
						locale);
				throw new BcephalException( message);
			}

			Model model = modelRepository.findByName(name);
			if (model != null) {
				String message = messageSource.getMessage("duplicate.model.name", new Object[] { name }, locale);
				throw new BcephalException(message);
			}

			model = new Model();
			model.setName(name);
			model.setPosition(((Long) modelRepository.count()).intValue());

			model = modelRepository.save(model);
			log.debug("Model successfully to created : {} ", model.getId());
			return model;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating model : {}", name, e);
			String message = messageSource.getMessage("unable.to.create.model", new Object[] { name }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional
	public void saveAll(ListChangeHandler<Model> allModels, Locale locale) {
		log.debug("Try to  save all model : {}", allModels);
		try {
		List<Model> newItems = allModels.getNewItems();
		List<Model> updatedItems = allModels.getUpdatedItems();
		List<Model> deletedItems = allModels.getDeletedItems();
		if (newItems != null) {
			newItems.forEach(item -> {
				save(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				save(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				deleteModel(item, locale);
			});
		}
		
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save all model : {}", allModels, e);
			String message = messageSource.getMessage("unable.to.save.all.model", new Object[] { allModels }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Transactional
	@Override
	public Model save(Model model, Locale locale) {
		log.debug("Try to  Save model : {}", model);
		try {
			if (model == null) {
				String message = messageSource.getMessage("unable.to.save.null.model", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(model.getName())) {
				String message = messageSource.getMessage("unable.to.save.model.with.empty.name",
						new String[] { model.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<Entity> entities = model.getEntityListChangeHandler();
			if(entities.getItems().size() == 0) {
				model.setDiagramXml(null);
			}
			if(model.getCreationDate() == null) {
				model.setCreationDate(new Timestamp(System.currentTimeMillis()));
				model.setModificationDate(model.getCreationDate());
			}else {
				model.setModificationDate(new Timestamp(System.currentTimeMillis()));				
			}
			
			model = super.save(model, locale);
			saveEntities(model, locale, entities);
			log.debug("Model successfully saved : {} ", model);
			return model;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save model : {}", model, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { model }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveEntities(Model model, Locale locale, ListChangeHandler<Entity> listHandler) {
		List<Entity> newItems = listHandler.getNewItems();
		List<Entity> updatedItems = listHandler.getUpdatedItems();
		List<Entity> deletedItems = listHandler.getDeletedItems();
		Model id = model;
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setModel(id);
				entityService.saveEntity(item, locale);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setModel(id);
				entityService.saveEntity(item, locale);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				entityService.deleteEntity(item, locale);
			});
		}
	}

	/**
	 * delete model
	 * 
	 * @param item
	 * @param locale
	 */
	@Transactional
	public void deleteModel(Model model, Locale locale) {
		log.debug("Try to  delete model : {}", model);
		try {
			if (model == null || model.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.model", new Object[] { model },
						locale);
				throw new BcephalException(message);
			}
			model.getEntityListChangeHandler().getItems().forEach(item -> {
				entityService.deleteEntity(item, locale);
			});
			model.getEntityListChangeHandler().getDeletedItems().forEach(item -> {
				entityService.deleteEntity(item, locale);
			});
			deleteById(model.getId());
			log.debug("model successfully to delete : {} ", model);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete model : {}", model, e);
			String message = messageSource.getMessage("unable.to.delete.model", new Object[] { model }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public boolean canDeleteEntity(Long id) {
		Entity entiry = entityService.getById(id);
		return canDeleteEntity(entiry);
	}
	
	protected boolean canDeleteEntity(Entity entiry) {
		if(entiry != null) {
			for(Attribute attribute : entiry.getAttributeListChangeHandler().getItems()){
				if(!canDeleteAttribute(attribute)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean canDeleteAttribute(Long id) {
		Attribute attribute = attributeService.getById(id);
		return canDeleteAttribute(attribute);
	}
	
	protected boolean canDeleteAttribute(Attribute attribute) {
		if(attribute != null) {
			if(!attributeService.canDeleteDimension(attribute.getUniverseTableColumnName())) {
				return false;
			}
			for(Attribute child : attribute.getChildrenListChangeHandler().getItems()){
				if(!canDeleteAttribute(child)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean canDeleteModel(Long id) {
		Model model = getById(id);
		if(model != null) {
			for(Entity entiry : model.getEntityListChangeHandler().getItems()){
				if(!canDeleteEntity(entiry)) {
					return false;
				}
			}
		}
		return true;
	}

	

}
