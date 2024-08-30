/**
 * 
 */
package com.moriset.bcephal.grid.service.form;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BLabel;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.LabelCategory;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelButton;
import com.moriset.bcephal.grid.domain.form.FormModelButtonAction;
import com.moriset.bcephal.grid.domain.form.FormModelField;
import com.moriset.bcephal.grid.domain.form.FormModelFieldCalculateItem;
import com.moriset.bcephal.grid.domain.form.FormModelFieldConcatenateItem;
import com.moriset.bcephal.grid.domain.form.FormModelFieldValidationItem;
import com.moriset.bcephal.grid.domain.form.FormModelMenu;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.repository.form.FormModelButtonActionRepository;
import com.moriset.bcephal.grid.repository.form.FormModelButtonRepository;
import com.moriset.bcephal.grid.repository.form.FormModelFieldCalculateItemRepository;
import com.moriset.bcephal.grid.repository.form.FormModelFieldConcatenateItemRepository;
import com.moriset.bcephal.grid.repository.form.FormModelFieldRepository;
import com.moriset.bcephal.grid.repository.form.FormModelFieldValidationItemRepository;
import com.moriset.bcephal.grid.repository.form.FormModelMenuRepository;
import com.moriset.bcephal.grid.repository.form.FormModelRepository;
import com.moriset.bcephal.grid.service.DataSourcableService;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.repository.BLabelRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelService extends DataSourcableService<FormModel, BrowserData> {

	@Autowired
	FormModelRepository formModelRepository;

	@Autowired
	FormModelButtonRepository formModelButtonRepository;

	@Autowired
	FormModelMenuRepository formModelMenuRepository;

	@Autowired
	FormModelFieldRepository formModelFieldRepository;
	
	@Autowired
	FormModelButtonActionRepository formModelButtonActionRepository;

	@Autowired
	FormModelFieldValidationItemRepository formModelFieldValidationItemRepository;
	
	@Autowired
	FormModelFieldCalculateItemRepository formModelFieldCalculateItemRepository;
	
	@Autowired
	FormModelFieldConcatenateItemRepository formModelFieldConcatenateItemRepository;

	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
 
	@PersistenceContext
	EntityManager session;

	@Autowired
	SecurityService securityService;

	@Autowired
	MaterializedGridService materializedGridService;

	@Autowired
	InitiationService initiationService;

	@Autowired
	GrilleService grilleService;

	@Autowired
	SmartGrilleRepository smartGrilleRepository;

	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;

	@Autowired
	UniverseFilterService universeFilterService;

	@Autowired
	UserSessionLogService logService;

	@Autowired
	FormModelFieldReferenceService formModelFieldReferenceService;
	
	@Autowired 
	FormModelSpotService formModelSpotService;
	
	@Autowired
	BLabelRepository labelRepository;

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_MODEL_FORM;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		logService.saveUserSessionLog(username, clientId, projectCode, usersession, objectId, functionalityCode,
				rightLevel, profileId);
	}

	@Override
	public FormModelRepository getRepository() {
		return formModelRepository;
	}

	public List<FormModelMenu> getActiveMenus() {
		return this.formModelMenuRepository.findByActive(true);
	}
	
	public List<ClientFunctionality> getFunctionalityRight(Long clientId, String projectCode){
		List<ClientFunctionality> items = new ArrayList<>(0);
		List<FormModel>  models = getRepository().findAll();
		for (FormModel formModel : models) {
			addModel(items, formModel, clientId, projectCode);
		}
		return items;
	}

	private void addModel(List<ClientFunctionality> items, FormModel formModel,Long clientId, String projectCode) {
		ClientFunctionality f = new ClientFunctionality();
		f.setCode("Form_" + clientId + "_" + projectCode + "_" + formModel.getId());
		f.setName(formModel.getName());
		f.setClientId(clientId);
		f.setActive(true);
		f.setPosition(items.size());
		f.setActions(Arrays.asList(RightLevel.values()));
		f.setLowLevelActions(Arrays.asList(RightLevel.values()));
		items.add(f);
	}
	
	@Override
	public FormModel getById(Long id) {
		FormModel model = super.getById(id);
		if(model != null) {
			model.getFields().forEach(item -> {
				if(item.isTypeSpot() && item.getObjectId() != null) {
					item.setFormModelSpot(formModelSpotService.getById(item.getObjectId()));
				}
			});
		}
		return model;
	}

	@Override
	public FormModelEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		Long clientId = (Long) session.getAttribute(RequestParams.BC_CLIENT);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		FormModelEditorData data = new FormModelEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			data.getItem().setDataSourceId(filter.getDataSourceId());
			data.getItem().setDataSourceType(filter.getDataSourceType());
			

		} else {
			data.setItem(getById(filter.getId()));
			if(data.getItem() != null) {
				data.getItem().setCode("Form_" + clientId + "_" + projectCode + "_" + filter.getId());
				filter.setDataSourceId(data.getItem().getDataSourceId());
				filter.setDataSourceType(data.getItem().getDataSourceType());
			}
		}
		
		if(filter.getDataSourceType().isMaterializedGrid()) {
			String dName = initEditorDataForMaterializedGrid(data, filter.getDataSourceId(), session, locale);
			if (data.getItem() != null) {				
				data.getItem().setDataSourceName(dName);
			}
		}
		else {
			if(filter.getDataSourceType().isInputGrid()) {
				Optional<SmartGrille> response = smartGrilleRepository.findById(filter.getDataSourceId());				
				SmartGrille grid = response.isPresent() ? response.get() : null;
				String dName = grid != null ? grid.getName() : null;
				if (data.getItem() != null) {				
					data.getItem().setDataSourceName(dName);
				}
			}
			initEditorData(data, session, locale);
		}
		
		List<GrilleType> types = getSmallGridTypes();
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		List<Long> hightGridItems = getHidedObjectId(profileId, grilleService.getFunctionalityCode(), projectCode);
		List<Long> hightMaterializedItems = getHidedObjectId(profileId,
				FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);

		if (hightGridItems != null && hightGridItems.size() > 0) {
			data.getGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightGridItems));
		} else {
			data.getGrids().addAll(smartGrilleRepository.findByTypes(types));
		}

		if (hightMaterializedItems != null && hightMaterializedItems.size() > 0) {
			data.getGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMaterializedItems));
		} else {
			data.getGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		data.setSequences(incrementalNumberRepository.getAllIncrementalNumbers());
		return data;

	}
	
	@Override
	protected void setDataSourceName(FormModel item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}

	protected List<GrilleType> getSmallGridTypes() {
		List<GrilleType> types = new ArrayList<>();
		types.add(GrilleType.INPUT);
		types.add(GrilleType.REPORT);
		return types;
	}

	@Override
	protected FormModel getNewItem() {
		FormModel form = new FormModel();
		String baseName = "Form Model ";
		int i = 1;
		form.setName(baseName + i);
		while (getByName(form.getName()) != null) {
			i++;
			form.setName(baseName + i);
		}
		return form;
	}

	@Override
	@Transactional
	public FormModel save(FormModel formModel, Locale locale) {
		log.debug("Try to  Save Form model : {}", formModel);
		try {
			if (formModel == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.form.model",
						new Object[] { formModel }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(formModel.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.form.model.with.empty.name",
						new String[] { formModel.getName() }, locale);
				throw new BcephalException(message);
			}

			ListChangeHandler<FormModelField> fields = formModel.getMainFieldListChangeHandler();
			ListChangeHandler<FormModelField> details = formModel.getDetailsFieldListChangeHandler();
			ListChangeHandler<FormModelButton> buttoms = formModel.getButtonListChangeHandler();

			if (formModel.getMenu() == null) {
				formModel.setMenu(new FormModelMenu());
				formModel.getMenu().initialize(formModel.getName());
			}

			if (formModel.getMenu() != null) {
				formModel.getMenu().setModelId(formModel.getId());
				if (!StringUtils.hasText(formModel.getMenu().getName())) {
					formModel.getMenu().initializeName(formModel.getName());
				}
				formModel.setMenu(formModelMenuRepository.save(formModel.getMenu()));
			}

			if (formModel.getUserFilter() != null) {
				formModel.setUserFilter(universeFilterService.save(formModel.getUserFilter()));
			}
			if (formModel.getAdminFilter() != null) {
				formModel.setAdminFilter(universeFilterService.save(formModel.getAdminFilter()));
			}
			
			formModel.setModificationDate(new Timestamp(System.currentTimeMillis()));
			formModel = getRepository().save(formModel);
			FormModel id = formModel;
			if (formModel.getMenu() != null) {
				formModel.getMenu().setModelId(id.getId());
				formModel.setMenu(formModelMenuRepository.save(formModel.getMenu()));
			}
			
			fields.getNewItems().forEach(item -> {
				log.trace("Try to save form field : {}", item);
				item.setModelId(id);
				saveField(item, locale);
				log.trace("Form field saved : {}", item.getId());
			});
			fields.getUpdatedItems().forEach(item -> {
				log.trace("Try to save form field : {}", item);
				item.setModelId(id);
				saveField(item, locale);
				log.trace("Form field saved : {}", item.getId());
			});
			fields.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete form field : {}", item);
					deleteField(item);
					log.trace("Form field deleted : {}", item.getId());
				}
			});

			details.getNewItems().forEach(item -> {
				log.trace("Try to save Form field : {}", item);
				item.setModelId(id);
				saveField(item, locale);
				log.trace("Form field saved : {}", item.getId());
			});
			details.getUpdatedItems().forEach(item -> {
				log.trace("Try to save sub form : {}", item);
				item.setModelId(id);
				saveField(item, locale);
				log.trace("Form field saved : {}", item.getId());
			});
			details.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Form field  : {}", item);
					deleteField(item);
					log.trace("Form field  deleted : {}", item.getId());
				}
			});

			buttoms.getNewItems().forEach(item -> {
				log.trace("Try to save button form field: {}", item);
				item.setModelId(id);
				saveButton(item, locale);
				log.trace("Button form field saved : {}", item.getId());
			});
			buttoms.getUpdatedItems().forEach(item -> {
				log.trace("Try to save button form field : {}", item);
				item.setModelId(id);
				saveButton(item, locale);
				log.trace("Button form field saved : {}", item.getId());
			});
			buttoms.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete button form field : {}", item);
					deleteButton(item);
					log.trace("Button form field deleted : {}", item.getId());
				}
			});

			log.debug("Form model saved : {} ", formModel.getId());
			return formModel;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save Form model : {}", formModel, e);
			String message = getMessageSource().getMessage("unable.to.save.form.model", new Object[] { formModel },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	public void delete(FormModel formModel) {
		log.debug("Try to delete form model : {}", formModel);
		if (formModel == null || formModel.getId() == null) {
			return;
		}
		ListChangeHandler<FormModelField> fields = formModel.getMainFieldListChangeHandler();
		ListChangeHandler<FormModelField> daetails = formModel.getDetailsFieldListChangeHandler();
		ListChangeHandler<FormModelButton> buttoms = formModel.getButtonListChangeHandler();

		fields.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field : {}", item);
				deleteField(item);
				log.trace("Form field deleted : {}", item.getId());
			}
		});
		fields.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field : {}", item);
				deleteField(item);
				log.trace("Form field deleted : {}", item.getId());
			}
		});

		daetails.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field : {}", item);
				deleteField(item);
				log.trace("Form field  : {}", item.getId());
			}
		});
		daetails.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field : {}", item);
				deleteField(item);
				log.trace("Form field  : {}", item.getId());
			}
		});

		buttoms.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form buttom field : {}", item);
				formModelButtonRepository.deleteById(item.getId());
				log.trace("Form field  : {}", item.getId());
			}
		});
		buttoms.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form buttom field : {}", item);
				formModelButtonRepository.deleteById(item.getId());
				log.trace("Form field  : {}", item.getId());
			}
		});

		getRepository().deleteById(formModel.getId());
		if (formModel.getMenu() != null && formModel.getMenu().getId() != null) {
			formModelMenuRepository.deleteById(formModel.getMenu().getId());
		}
		if (formModel.getUserFilter() != null) {
			universeFilterService.delete(formModel.getUserFilter());
		}
		if (formModel.getAdminFilter() != null) {
			universeFilterService.delete(formModel.getAdminFilter());
		}
		log.debug("Form model successfully to delete : {} ", formModel);
		return;
	}

	private void saveField(FormModelField field, Locale locale) {
		ListChangeHandler<FormModelFieldValidationItem> validators = field.getValidationItemListChangeHandler();
		ListChangeHandler<FormModelFieldCalculateItem> calculateItems = field.getCalculateItemListChangeHandler();
		ListChangeHandler<FormModelFieldConcatenateItem> concatenateItems = field.getConcatenateItemListChangeHandler();
		
		if (field.getReference() != null) {
			field.setReference(formModelFieldReferenceService.save(field.getReference(), locale));
		}
		if(field.getType().isSpot()) {
			if (field.getFormModelSpot() != null) {
				field.setFormModelSpot(formModelSpotService.save(field.getFormModelSpot(), locale));
			}			
			field.setObjectId(field.getFormModelSpot() != null ? field.getFormModelSpot().getId() : null);
		}
		field.initValueImpl();
		field = formModelFieldRepository.save(field);
		FormModelField id = field;

		validators.getNewItems().forEach(item -> {
			log.trace("Try to save form field validator : {}", item);
			item.setFieldId(id);
			formModelFieldValidationItemRepository.save(item);
			log.trace("Form field validator saved : {}", item.getId());
		});
		validators.getUpdatedItems().forEach(item -> {
			log.trace("Try to save form field validator : {}", item);
			item.setFieldId(id);
			formModelFieldValidationItemRepository.save(item);
			log.trace("Form field validator saved : {}", item.getId());
		});
		validators.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field validator : {}", item);
				formModelFieldValidationItemRepository.deleteById(item.getId());
				log.trace("Form field validator deleted : {}", item.getId());
			}
		});
		
		calculateItems.getNewItems().forEach(item -> {
			log.trace("Try to save form field calculate : {}", item);
			item.setParentId(id);
			formModelFieldCalculateItemRepository.save(item);
			log.trace("Form field calculate saved : {}", item.getId());
		});
		calculateItems.getUpdatedItems().forEach(item -> {
			log.trace("Try to save form field calculate : {}", item);
			item.setParentId(id);
			formModelFieldCalculateItemRepository.save(item);
			log.trace("Form field calculate saved : {}", item.getId());
		});
		calculateItems.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field calculate : {}", item);
				formModelFieldCalculateItemRepository.deleteById(item.getId());
				log.trace("Form field calculate deleted : {}", item.getId());
			}
		});
		
		concatenateItems.getNewItems().forEach(item -> {
			log.trace("Try to save form field concatenate : {}", item);
			item.setParentId(id);
			formModelFieldConcatenateItemRepository.save(item);
			log.trace("Form field concatenate saved : {}", item.getId());
		});
		concatenateItems.getUpdatedItems().forEach(item -> {
			log.trace("Try to save form field concatenate : {}", item);
			item.setParentId(id);
			formModelFieldConcatenateItemRepository.save(item);
			log.trace("Form field concatenate saved : {}", item.getId());
		});
		concatenateItems.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field concatenate : {}", item);
				formModelFieldConcatenateItemRepository.deleteById(item.getId());
				log.trace("Form field concatenate items deleted : {}", item.getId());
			}
		});
		
		log.debug("Form model field saved : {} ", field.getId());
	}

	private void deleteField(FormModelField field) {
		log.trace("Try to delete form model field : {}", field);
		if (field == null || field.getId() == null) {
			return;
		}
		ListChangeHandler<FormModelFieldValidationItem> validators = field.getValidationItemListChangeHandler();
		validators.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field validator : {}", item);
				formModelFieldValidationItemRepository.deleteById(item.getId());
				log.trace("Form field validator deleted : {}", item.getId());
			}
		});
		validators.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field validator : {}", item);
				formModelFieldValidationItemRepository.deleteById(item.getId());
				log.trace("Form field validator deleted : {}", item.getId());
			}
		});
		ListChangeHandler<FormModelFieldConcatenateItem> concatenateItems = field.getConcatenateItemListChangeHandler();
		concatenateItems.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field concatenate item : {}", item);
				formModelFieldConcatenateItemRepository.deleteById(item.getId());
				log.trace("Form field concatenate deleted : {}", item.getId());
			}
		});
		concatenateItems.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field concatenate item: {}", item);
				formModelFieldConcatenateItemRepository.deleteById(item.getId());
				log.trace("Form field concatenate deleted : {}", item.getId());
			}
		});
		ListChangeHandler<FormModelFieldCalculateItem> calculateItems = field.getCalculateItemListChangeHandler();
		calculateItems.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field calculate item : {}", item);
				formModelFieldCalculateItemRepository.deleteById(item.getId());
				log.trace("Form field calculate deleted : {}", item.getId());
			}
		});
		calculateItems.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form field calculate item: {}", item);
				formModelFieldCalculateItemRepository.deleteById(item.getId());
				log.trace("Form field calculate deleted : {}", item.getId());
			}
		});
		formModelFieldRepository.deleteById(field.getId());

		formModelFieldReferenceService.delete(field.getReference());
		
		if (field.getFormModelSpot() != null) {
			formModelSpotService.delete(field.getFormModelSpot());
		}	

		log.debug("Form model field successfully to delete : {} ", field);
		return;
	}
	
	private void saveButton(FormModelButton button, Locale locale) {
		ListChangeHandler<FormModelButtonAction> actions = button.getActionListChangeHandler();
		button = formModelButtonRepository.save(button);
		FormModelButton id = button;

		actions.getNewItems().forEach(item -> {
			log.trace("Try to save form button action : {}", item);
			item.setButtonId(id);
			formModelButtonActionRepository.save(item);
			log.trace("Form button action saved : {}", item.getId());
		});
		actions.getUpdatedItems().forEach(item -> {
			log.trace("Try to save form button action : {}", item);
			item.setButtonId(id);
			formModelButtonActionRepository.save(item);
			log.trace("Form button action saved : {}", item.getId());
		});
		actions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form button action : {}", item);
				formModelButtonActionRepository.deleteById(item.getId());
				log.trace("Form button action deleted : {}", item.getId());
			}
		});
		log.debug("Form model button saved : {} ", button.getId());
	}

	private void deleteButton(FormModelButton button) {
		log.trace("Try to delete form model button : {}", button);
		if (button == null || button.getId() == null) {
			return;
		}
		ListChangeHandler<FormModelButtonAction> actions = button.getActionListChangeHandler();
		actions.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form button action : {}", item);
				formModelButtonActionRepository.deleteById(item.getId());
				log.trace("Form button action deleted : {}", item.getId());
			}
		});
		actions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete form button action : {}", item);
				formModelButtonActionRepository.deleteById(item.getId());
				log.trace("Form button action deleted : {}", item.getId());
			}
		});
		formModelButtonRepository.deleteById(button.getId());
		log.debug("Form model button successfully to delete : {} ", button);
		return;
	}
	

	public int importAllLabels(FormModel model) {
		int count = 0;
		List<String> langs = new ArrayList<>();
		langs.add("en");
		langs.add("fr");
		model.setFields(model.getMainFieldListChangeHandler().getItems());
		model.getFields().addAll(model.getDetailsFieldListChangeHandler().getItems());
		Set<String> codes = model.getLabelCodes();
		for(String code : codes) {
			Number nbr = labelRepository.countByCode(code);
			if(nbr.intValue() == 0) {
				for(String lang : langs) {
					BLabel label = new BLabel();
					label.setCode(code);
					label.setLang(lang);
					label.setCategory(LabelCategory.FORM);
					labelRepository.save(label);
					count++;
				}
			}
		}
		
		return count;
	}

	

	@Override
	protected BrowserData getNewBrowserData(FormModel item) {
		return new BrowserData(item);
	}

	@Override
	protected Specification<FormModel> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<FormModel> qBuilder = new RequestQueryBuilder<FormModel>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("creationDate"),
					root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			qBuilder.addNoTInObjectId(hidedObjectIds);
			if (filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				filter.getColumnFilters().getItems().forEach(filte -> {
					build(filte);
				});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

}
