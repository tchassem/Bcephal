/**
 * 
 */
package com.moriset.bcephal.grid.service.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.domain.SmartGrilleColumn;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.form.Form;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelButtonAction;
import com.moriset.bcephal.grid.domain.form.FormModelField;
import com.moriset.bcephal.grid.domain.form.FormModelFieldCategory;
import com.moriset.bcephal.grid.domain.form.FormModelSpotItem;
import com.moriset.bcephal.grid.repository.GrilleColumnRepository;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridColumnRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.repository.form.FormModelButtonRepository;
import com.moriset.bcephal.grid.service.DataSourcableService;
import com.moriset.bcephal.grid.service.InputGridQueryBuilder;
import com.moriset.bcephal.repository.BLabelRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class FormService extends DataSourcableService<Form, Object[]> {

	@PersistenceContext
	EntityManager session;

	@Autowired
	FormModelService formModelService;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
		
	@Autowired
	FormModelButtonRepository frmModelButtonRepository;
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired
	GrilleColumnRepository grilleColumnRepository;
	
	@Autowired
	MaterializedGridRepository materializedGridRepository;
	
	@Autowired
	MaterializedGridColumnRepository materializedGridColumnRepository;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@Autowired
	BLabelRepository labelRepository;
	
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.DYNAMIC_FORM;
	}
	
	protected String getBrowserFunctionalityCode(Long clientId, String projectCode, Long modelId) {
		return "Form_".concat(clientId +"").concat("_").concat(projectCode).concat("_").concat(modelId + "");
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	@Override
	public MainObjectRepository<Form> getRepository() {
		return null;
	}
	

	private Form getForm(Long id, FormModel model,Long clientId, Long profileId, String projectCode, Locale locale) {
		if(model == null) {
			throw new BcephalException("Form model not found!");
		}
		FormModel model_ = new FormModel();// formModelService.getById(model.getId());
		List<FormModelField> fields = model.getMainFieldSortedFields();
		model_.setFields(fields);						
		Grille grid = new Grille();
		grid.setColumns(new ArrayList<GrilleColumn>());			
		FormModelFieldCategory category = model_.hasDetails() ? FormModelFieldCategory.DETAILS : FormModelFieldCategory.MAIN;			
		for(FormModelField field : fields) {
			if(field.getCategory() == category) {
				GrilleColumn column = new  GrilleColumn();
				column.setDimensionId(field.getDimensionId());
				column.setType(field.getDimensionType());
				column.setDimensionName(field.getName());
				column.setPosition(field.getPosition());	
				column.setDataSourceId(model.getDataSourceId());
				column.setDataSourceType(model.getDataSourceType());
				grid.getColumns().add(column);
			}
		}
		Collections.sort(grid.getColumns(), new Comparator<GrilleColumn>() {
			@Override
			public int compare(GrilleColumn value1, GrilleColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		
		GrilleDataFilter filter2 = new GrilleDataFilter();
		filter2.setDataSourceId(model.getDataSourceId());
		filter2.setDataSourceType(model.getDataSourceType());			
		grid.setDataSourceId(model.getDataSourceId());
		grid.setDataSourceType(model.getDataSourceType());
					
		filter2.setGrid(grid);
		grid.setUserFilter(model.getUserFilter());
		grid.setAdminFilter(model.getAdminFilter());	
		grid.setType(model.getDataSourceType().isInputGrid() ? GrilleType.INPUT : GrilleType.REPORT);
		if(model.getDataSourceType().isInputGrid()) {
			grid.setId(model.getDataSourceId());
		}
		filter2.setAllowRowCounting(false);
		filter2.setGroupId(model.getId());
		filter2.setGrid(grid);
		//filter2.setColumnFilters(filter.getColumnFilters());
		if(filter2.getIds() == null) {
			filter2.setIds(new ArrayList<>());
		}
		filter2.getIds().add(id);
		
		BrowserDataPage<GridItem> items = searchEditRows(filter2, clientId, projectCode, profileId, locale);
		GridItem item = null;
		for(GridItem it : items.getItems()) {
			if(compareTo(it, id)) {
				item = it;
			}
		}
		if(item != null) {
			return getById(model.getId(), item.datas, clientId, profileId, projectCode);
		}
		return null;
	}
	

	@Override
	public FormEditorData getEditorData(EditorDataFilter filter,HttpSession session, Locale locale) throws Exception {
		FormEditorData data = new FormEditorData();
		FormModel model = formModelService.getById(filter.getSecondId());
		if(model == null) {
			throw new BcephalException("Form model not found : {}", filter.getSecondId());
		}
		data.setModel(model);
		
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			data.getItem().setModelId(filter.getSecondId());
//			data.getItem().setDataSourceId(filter.getDataSourceId());
//			data.getItem().setDataSourceType(filter.getDataSourceType());
		} else {		
			FormModel model_ = formModelService.getById(filter.getSecondId());
			List<FormModelField> fields = model_.getMainFieldSortedFields();
			model_.setFields(fields);						
			Grille grid = new Grille();
			grid.setColumns(new ArrayList<GrilleColumn>());			
			FormModelFieldCategory category = model_.hasDetails() ? FormModelFieldCategory.DETAILS : FormModelFieldCategory.MAIN;			
			for(FormModelField field : fields) {
				if(field.getCategory() == category) {
					GrilleColumn column = new  GrilleColumn();
					column.setDimensionId(field.getDimensionId());
					column.setType(field.getDimensionType());
					column.setDimensionName(field.getName());
					column.setPosition(field.getPosition());	
					column.setDataSourceId(model.getDataSourceId());
					column.setDataSourceType(model.getDataSourceType());
					grid.getColumns().add(column);
				}
			}
			Collections.sort(grid.getColumns(), new Comparator<GrilleColumn>() {
				@Override
				public int compare(GrilleColumn value1, GrilleColumn value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			
			GrilleDataFilter filter2 = new GrilleDataFilter();
			filter2.setDataSourceId(model.getDataSourceId());
			filter2.setDataSourceType(model.getDataSourceType());			
			grid.setDataSourceId(model.getDataSourceId());
			grid.setDataSourceType(model.getDataSourceType());
						
			filter2.setGrid(grid);
			grid.setUserFilter(model_.getUserFilter());
			grid.setAdminFilter(model_.getAdminFilter());	
			grid.setType(model.getDataSourceType().isInputGrid() ? GrilleType.INPUT : GrilleType.REPORT);
			if(model.getDataSourceType().isInputGrid()) {
				grid.setId(model.getDataSourceId());
			}
			filter2.setAllowRowCounting(false);
			filter2.setGroupId(filter.getSecondId());
			filter2.setGrid(grid);
			filter2.setColumnFilters(filter.getColumnFilters());
			if(filter2.getIds() == null) {
				filter2.setIds(new ArrayList<>());
			}
			filter2.getIds().add(filter.getId());
			Long profilId = (Long)session.getAttribute(RequestParams.BC_PROFILE); 
			Long clientId = (Long)session.getAttribute(RequestParams.BC_CLIENT); 
			String projectCode = (String)session.getAttribute(RequestParams.BC_PROJECT); 
			BrowserDataPage<GridItem> items = searchEditRows(filter2,clientId,projectCode,profilId, locale);
			GridItem item = null;
			for(GridItem it : items.getItems()) {
				if(compareTo(it, filter.getId())) {
					item = it;
				}
			}
			if(item != null) {
				data.setItem(getById(filter.getSecondId(), item.datas, clientId, profilId, projectCode));
			}
		}
		
		data.getItem().setDataSourceId(model.getDataSourceId());
		data.getItem().setDataSourceType(model.getDataSourceType());
		initializeModelLabels(model, locale);
		return data;
	}
	
	@Override
	protected void setDataSourceName(Form item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}

	private void initializeModelLabels(FormModel model, Locale locale) {
		if(model != null) {
			if(locale == null) {
				locale = Locale.ENGLISH;
			}
			Set<String> codes = model.getLabelCodes();
			if(codes.size() > 0) {
				List<String[]> labels = labelRepository.findAllLabels(codes, locale.getLanguage());
				if(labels.size() > 0) {
					Map<String, String> map = new HashMap<>();
					for (String[] label : labels) {
						if(label[0] != null && label[1] != null) {
							map.put(label[0], label[1]);						
						}
					}
					model.setLabels(map);
				}
			}
		}
	}

	private boolean compareTo(GridItem item, Long id) {
		if(item == null || item.datas == null || id == null || item.datas.length == 0 || item.datas[item.datas.length - 1] == null) {
			return false;
		}
		Long id_ = Long.valueOf(item.datas[item.datas.length - 1].toString());
		return id_.equals(id);
	}
		
	@Override
	protected Form getNewItem() {
		Form form = new Form();
		String baseName = "Form ";
		int i = 1;
		form.setName(baseName + i);
//		while (getByName(form.getName()) != null) {
//			i++;
//			form.setName(baseName + i);
//		}
		return form;
	}

	protected Form getNewItem(FormModel model) {
		Form item = getNewItem();
		//List<FormModelField> details = model.getDetailsFieldListChangeHandler().getItems();
//		details.forEach(grid -> {
//			item.getSubGridDatas().put(grid.getId(), new ListChangeHandler<Form>());
//		});
		return item;
	}

	@Override
	protected Object[] getNewBrowserData(Form item) {
		return null;
	}

	@Override
	protected Specification<Form> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return null;
	}


	@Transactional
	public Form saveForm(Form data,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  Save form data : {}", data);
		try {
			if (data == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.object", new Object[] { data },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (data.getModelId() == null) {
				String message = getMessageSource().getMessage("form.model.id.is.null", new Object[] { data }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			FormModel model = formModelService.getById(data.getModelId());
			if (model == null) {
				String message = getMessageSource().getMessage("unknown.form.model",
						new Object[] { data.getModelId() }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			return saveWithoutComit(data, model, null, clientId, profileId, projectCode);
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save form data : {}", data, e);
			String message = getMessageSource().getMessage("unable.to.save.form.data", new Object[] { data }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Transactional
	public int duplicate(List<Long> ids, Long modelId,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  duplicate : {} form(s)", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = getMessageSource().getMessage("unable.to.duplicate.empty.list", new Object[] { ids },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (modelId == null) {
				String message = getMessageSource().getMessage("form.model.id.is.null", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			FormModel model = formModelService.getById(modelId);
			if (model == null) {
				String message = getMessageSource().getMessage("unknown.form.model", new Object[] { modelId }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			ids.forEach(id -> {
				duplicateById(id, model, clientId, profileId, projectCode, locale);
			});
			log.debug("{} form(s) successfully duplicated ", ids.size());
			return ids.size();
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while duplicate forms : {}", ids.size(), e);
			String message = getMessageSource().getMessage("unable.to.duplicate", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Transactional
	public Form duplicate(Long id, Long modelId,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  duplicate : {} form", id);
		try {
			if (id == null) {
				String message = getMessageSource().getMessage("unable.to.duplicate.empty.list", new Object[] { id },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (modelId == null) {
				String message = getMessageSource().getMessage("form.model.id.is.null", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			FormModel model = formModelService.getById(modelId);
			if (model == null) {
				String message = getMessageSource().getMessage("unknown.form.model", new Object[] { modelId }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			Form form = duplicateById(id, model, clientId, profileId, projectCode, locale);
			log.debug("One form successfully duplicated ");
			return form;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while duplicate form : {}", id, e);
			String message = getMessageSource().getMessage("unable.to.duplicate", new Object[] { id }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private Form duplicateById(Long id, FormModel model,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  dumplicate form by id : {}", id);
		if (id == null) {
			String message = getMessageSource().getMessage("unable.to.duplicate.null.object", new Object[] {},
					Locale.ENGLISH);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
		Form form = getForm(id, model, clientId, profileId, projectCode, locale);
		if(form != null) {
			Form copy = form.builCopy(model);
			return saveWithoutComit(copy, model, null, clientId, profileId, projectCode);
		}		
		log.debug("Form data successfully diuplicated : {} ", id);		
		return null;
	}
	

	@Transactional
	public void delete(List<Long> ids, Long modelId,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  delete : {} entities", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = getMessageSource().getMessage("unable.to.delete.empty.list", new Object[] { ids },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (modelId == null) {
				String message = getMessageSource().getMessage("form.model.id.is.null", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			FormModel model = formModelService.getById(modelId);
			if (model == null) {
				String message = getMessageSource().getMessage("unknown.form.model", new Object[] { modelId }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			ids.forEach(id -> {
				deleteById(id, model, clientId, profileId, projectCode, locale);
			});
			log.debug("{} entities successfully deleted ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete entities : {}", ids.size(), e);
			String message = getMessageSource().getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional
	public Form validate(Form data, boolean validated,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  validate form data : {}", data);
		try {
			if (data == null) {
				String message = getMessageSource().getMessage("unable.to.validate.null.object", new Object[] { data },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (data.getModelId() == null) {
				String message = getMessageSource().getMessage("form.model.id.is.null", new Object[] { data }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			FormModel model = formModelService.getById(data.getModelId());
			if (model == null) {
				String message = getMessageSource().getMessage("unknown.form.model",
						new Object[] { data.getModelId() }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}

			validateById(data.getId(), model, validated);

			log.debug("Form data successfully validated : {} ", data);
			return getById(model.getId(), null, clientId, profileId, projectCode);
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while validating form data : {}", data, e);
			String message = getMessageSource().getMessage("unable.to.validate.form.data", new Object[] { data },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional
	public void validate(List<Long> ids, Long modelId, boolean validated, Locale locale) {
		log.debug("Try to  delete : {} entities", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = getMessageSource().getMessage("unable.to.validate.empty.list", new Object[] { ids },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (modelId == null) {
				String message = getMessageSource().getMessage("form.model.id.is.null", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			FormModel model = formModelService.getById(modelId);
			if (model == null) {
				String message = getMessageSource().getMessage("unknown.form.model", new Object[] { modelId }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			ids.forEach(id -> {
				validateById(id, model, validated);
			});
			log.debug("{} entities successfully validated ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while validating entities : {}", ids.size(), e);
			String message = getMessageSource().getMessage("unable.to.validate", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void validateById(Long id, FormModel model, boolean validated) {
//		Form data = getById(id);
//		if (data != null) {
//			//List<FormModel> subGrids = model.getSubGridListChangeHandler().getItems();
//			int count = subGrids.size();
//			if (validated) {
//				if (count == 0) {
//					FormDataSaveQueryBuilder builder = new FormDataSaveQueryBuilder(data, model);
//					String sql = builder.buildResetUniverseQuery(model.getId(), id, model.getGridId());
//					log.trace("Reset universe query : {}", sql);
//					Query query = this.session.createNativeQuery(sql);
//					int position = 1;
//					for (Object parameter : builder.getParameters()) {
//						query.setParameter(position++, parameter);
//					}
//					Number universeId = (Number) query.getSingleResult();
//					validateData(data, model, validated, null, universeId.longValue());
//				} else {
//					validateData(data, model, validated, null, null);
//					subGrids.forEach(subModel -> {
//						if (data.getSubGridDatas().containsKey(subModel.getId())) {
//							ListChangeHandler<FormData> datas = data.getSubGridDatas().get(subModel.getId());
//							datas.getItems().forEach(subData -> {
//								FormDataSaveQueryBuilder builder = new FormDataSaveQueryBuilder(data, model);
//								String sql = builder.buildValidateUniverseQuery(subData, subModel, model.getId(), id,
//										model.getGridId());
//								log.trace("Reset universe query : {}", sql);
//								Query query = this.session.createNativeQuery(sql);
//								int position = 1;
//								for (Object parameter : builder.getParameters()) {
//									query.setParameter(position++, parameter);
//								}
//								Number universeId = (Number) query.getSingleResult();
//								validateData(subData, subModel, validated, null, universeId.longValue());
//							});
//						}
//					});
//				}
//			} else {
//				FormDataSaveQueryBuilder builder = new FormDataSaveQueryBuilder(data, model);
//				String sql = builder.buildResetUniverseQuery(model.getId(), id, model.getGridId());
//				log.trace("Reset universe query : {}", sql);
//				Query query = this.session.createNativeQuery(sql);
//				int position = 1;
//				for (Object parameter : builder.getParameters()) {
//					query.setParameter(position++, parameter);
//				}
//				query.executeUpdate();
//
//				validateData(data, model, validated, null, null);
//				subGrids.forEach(subGrid -> {
//					validateData(null, subGrid, validated, data.getId(), null);
//				});
//
//			}
//		}
	}

	private void deleteById(Long id, FormModel model,Long clientId, Long profileId, String projectCode, Locale locale) {
		log.debug("Try to  delete form by id : {}", id);
		if (id == null) {
			String message = getMessageSource().getMessage("unable.to.delete.null.object", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
		Form form = getForm(id, model, clientId, profileId, projectCode, locale);
		if(form != null) {	
			List<Long> ids = form.buildIds();
			if(ids.size() > 0) {
				String sql = form.buildDeleteSql(model, ids);
				Query query = session.createNativeQuery(sql);
				query.executeUpdate();
			}
		}		
		log.debug("Form data successfully deleted : {} ", id);
	}

	public Form saveWithoutComit(Form form, FormModel formModel, String username,Long clientId, Long profileId, String projectCode) {	
		if(formModel != null) {
			formModel.sortFields();	
			formModel.getFields().forEach(field -> {field.setDataSourceType(formModel.getDataSourceType()); field.setDataSourceId(formModel.getDataSourceId());});
			if(form.getDeleteddRows().size() > 0) {
				String sql = form.buildDeleteSql(formModel, form.getDeleteddRows());
				Query query = session.createNativeQuery(sql);
				query.executeUpdate();
			}
			Number  id = null;
			MaterializedGrid matGrid = null;
			if(formModel.getDataSourceType().isMaterializedGrid()) {
				Optional<MaterializedGrid> response = materializedGridRepository.findById(formModel.getDataSourceId());
				if(response.isPresent()) {
					matGrid = response.get();
				}
			}
			
			loadSequenceValues(formModel, form);
			
			if(formModel.hasDetails()) {
				if(form.getDetailsData() != null && form.getDetailsData().size() > 0) {
					for(Object[] detailData : form.getDetailsData()) {
						id = (Number)detailData[detailData.length - 1];
						String sql = null;
						if(id == null) {
							sql = form.buildInsertSql(form.getMainData(), detailData, formModel, matGrid, username);
						}
						else {
							sql = form.buildUpdateSql(form.getMainData(), detailData, formModel, matGrid, id.longValue());
						}
						Query query = session.createNativeQuery(sql);
						if(id != null) {
							query.executeUpdate();
						}
						else {
							id = (Number)query.getSingleResult();
							if( id != null) {
								form.getMainData()[form.getMainData().length - 1] =  id;
							}
						}
					}					
				}
				else {
					throw new BcephalException("Unable to save '" + form.getName() + "' without details elements");
				}
			}
			else {
				 id = (Number)form.getMainData()[form.getMainData().length - 1];
				String sql = null;
				if( id == null) {
					sql = form.buildInsertSql(form.getMainData(), form.getMainData(), formModel, matGrid, username);
				}
				else {
					sql = form.buildUpdateSql(form.getMainData(), form.getMainData(), formModel, matGrid,  id.longValue());
				}
				Query query = session.createNativeQuery(sql);
				if(id != null) {
					query.executeUpdate();
				}else {
					id = (Number)query.getSingleResult();
					if( id != null) {
						form.getMainData()[form.getMainData().length - 1] =  id;
					}
				}
			}
		}				
		form = getById(formModel.getId(), form.getMainData(), clientId, profileId, projectCode);
		return form;
	}
	
	
	private void loadSequenceValues(FormModel formModel, Form form) {
		for(FormModelField field : formModel.getFields()) {
			if(field.getType() != null && field.getType().isSequence() && field.getObjectId() != null) {
				Optional<IncrementalNumber> result = incrementalNumberRepository.findById(field.getObjectId());
				IncrementalNumber sequence = result.isPresent() ? result.get() : null;
				if(sequence != null) {
					if(field.getCategory() == FormModelFieldCategory.DETAILS) {
						for(Object[] data : form.getDetailsData()) {
							Object value = data[field.getPosition()];
							if(value == null || !StringUtils.hasText(value.toString())) {
				    			data[field.getPosition()] = sequence.buildNextValue();
				    		}
						}
					}
					else {
						Object[] data = form.getMainData();
						Object value = data[field.getPosition()];
						if(value == null || !StringUtils.hasText(value.toString())) {
			    			data[field.getPosition()] = sequence.buildNextValue();
			    		}
					}		    		
				}
			}
		}
	}

	public Form getById(Long formModelId, Object[] datas, Long clientId, Long profileId, String projectCode) {
		FormModel formModel = formModelService.getById(formModelId);
		if(formModel != null) {			
			formModel.sortFields();						
			Grille grid = new Grille();
			grid.setColumns(new ArrayList<GrilleColumn>());			
			FormModelFieldCategory category = formModel.hasDetails() ? FormModelFieldCategory.DETAILS : FormModelFieldCategory.MAIN;			
			for(FormModelField field : formModel.getFields()) {
				if(field.getCategory() == category) {
					GrilleColumn column = new  GrilleColumn();
					column.setDimensionId(field.getDimensionId());
					column.setType(field.getDimensionType());
					column.setDimensionName(field.getName());
					column.setPosition(field.getPosition());	
					column.setDataSourceType(formModel.getDataSourceType());
					column.setDataSourceId(formModel.getDataSourceId());
					grid.getColumns().add(column);
				}
			}			
			Collections.sort(grid.getColumns(), new Comparator<GrilleColumn>() {
				@Override
				public int compare(GrilleColumn value1, GrilleColumn value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			session.clear();
			
			GrilleDataFilter filter = new GrilleDataFilter();
			filter.setDataSourceType(formModel.getDataSourceType());
			filter.setDataSourceId(formModel.getDataSourceId());
			grid.setDataSourceType(formModel.getDataSourceType());
			grid.setDataSourceId(formModel.getDataSourceId());
			filter.setGrid(grid);
			grid.setUserFilter(formModel.getUserFilter());
			grid.setAdminFilter(formModel.getAdminFilter());
			filter.setFilter(formModel.getAdminFilter());
			filter.setIds(new ArrayList<>());
			if(!formModel.hasDetails()) {
				filter.getIds().add(Long.valueOf(datas[datas.length-1].toString()));
			}
			String codef = getBrowserFunctionalityCode(clientId,projectCode,filter.getGroupId());			
			FormRowQueryBuilder builder = new FormRowQueryBuilder(filter, formModel, getHidedObjectId(profileId, codef, projectCode));	
			builder.keys = datas;
			String sql = builder.buildQuery();
			log.trace("Search query : \n{}", sql);
			Query query = session.createNativeQuery(sql);			
			@SuppressWarnings("unchecked")
			List<Object[]> objects = query.getResultList();						
//			if(filter.formatDate) {
//				grilleService.formatDates(objects, filter.getGrid());
//			}
			
			Form form = new Form();
			form.setModelId(formModelId);
			if(formModel.hasDetails()) {
				form.setMainData(datas);
				form.setDetailsData(objects);	
			}
			else {
				form.setMainData(objects.size() > 0 ? objects.get(0) : null);
				form.setDetailsData(null);	
			}
			return form;						
		}		
		return null;
	}
	
	

	
	
	
	
	
	public BrowserDataPage<GridItem> searchEditRows(GrilleDataFilter filter,Long clientId, String projectCode, Long profileId, java.util.Locale locale) {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		page.setPageSize(filter.getPageSize());
		if(filter.getGrid() != null) {
			if (filter.getGrid() != null && filter.getGrid().getId() == null && !filter.getGrid().isReport()
					&& !filter.getGrid().isReconciliation()) {
				return page;
			}
			FormModel formModel = formModelService.getById(filter.getGroupId());
			loadFilterClosures(filter);	
			if(formModel != null) { 
				formModel.sortFields();
			}
			this.session.clear();
			Integer count = 0;
			String codef = getBrowserFunctionalityCode(clientId,projectCode,filter.getGroupId());
			FormQueryBuilder builder = new FormQueryBuilder(filter, formModel, getHidedObjectId(profileId, codef, projectCode));
			builder.addId = true;
			if(filter.isAllowRowCounting()) {
				String sql = builder.buildCountQuery();
				log.trace("Count query : {}", sql);
				Query query = this.session.createNativeQuery(sql);
				Number number = (Number)query.getSingleResult();
				count = number.intValue();
				if(count == 0) {
					return page;
				}
				if(filter.isShowAll()) {
					page.setTotalItemCount(count);
					page.setPageCount(1);
					page.setCurrentPage(1);
					page.setPageFirstItem(1);
				}
				else {
					page.setTotalItemCount(count);
					page.setPageCount(((int)count/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
					page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
					if(page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
				}
			} 
			else {
				page.setTotalItemCount(1);
				page.setPageCount(1);
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
						
			String sql = builder.buildQuery();
			log.trace("Search query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			@SuppressWarnings("unchecked")
			List<Object[]> objects = query.getResultList();
			//formatDates(objects);
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(GridItem.buildItems(objects)); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	public BrowserDataPage<GridItem> searchRows(GrilleDataFilter filter,Long clientId, String projectCode, Long profileId, java.util.Locale locale) {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		page.setPageSize(filter.getPageSize());
		if(filter.getGrid() != null) {
			if (filter.getGrid() != null && filter.getGrid().getId() == null && !filter.getGrid().isReport()
					&& !filter.getGrid().isReconciliation()) {
				return page;
			}
			FormModel formModel = formModelService.getById(filter.getGroupId());
			loadFilterClosures(filter);	
			if(formModel != null) { 				
				formModel.sortFields();
				filter.getGrid().setUserFilter(formModel.getUserFilter());
				filter.getGrid().setAdminFilter(formModel.getAdminFilter());
			}
			this.session.clear();
			Integer count = 0;
			String codef = getBrowserFunctionalityCode(clientId,projectCode,filter.getGroupId());
			InputGridQueryBuilder builder = new FormQueryBuilder(filter, formModel, getHidedObjectId(profileId, codef, projectCode));
			if(filter.isAllowRowCounting()) {
				String sql = builder.buildCountQuery();
				log.trace("Count query : {}", sql);
				Query query = this.session.createNativeQuery(sql);
				Number number = (Number)query.getSingleResult();
				count = number.intValue();
				if(count == 0) {
					return page;
				}
				if(filter.isShowAll()) {
					page.setTotalItemCount(count);
					page.setPageCount(1);
					page.setCurrentPage(1);
					page.setPageFirstItem(1);
				}
				else {
					page.setTotalItemCount(count);
					page.setPageCount(((int)count/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
					page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
					if(page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
				}
			} 
			else {
				page.setTotalItemCount(1);
				page.setPageCount(1);
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
						
			String sql = builder.buildQuery();
			log.trace("Search query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			@SuppressWarnings("unchecked")
			List<Object[]> objects = query.getResultList();
			//formatDates(objects);
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(GridItem.buildItems(objects)); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	public void loadFilterClosures(GrilleDataFilter filter) {
		List<GrilleColumn> columns = filter.getGrid().getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<GrilleColumn>() {
			@Override
			public int compare(GrilleColumn o1, GrilleColumn o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		filter.getGrid().setColumns(columns);
		filter.getGrid().setDataSourceType(filter.getDataSourceType());
		filter.getGrid().setDataSourceId(filter.getDataSourceId());
		columns.forEach(column -> {
			column.setDataSourceType(filter.getDataSourceType());
			column.setDataSourceId(filter.getDataSourceId());
			});
		
		if(filter.getGrid().getUserFilter() != null) {
			
		}
		if(filter.getGrid().getAdminFilter() != null) {
			
		}
	}
	
	@Transactional
	public Form runAction(Form form, FormModel model, FormModelButtonAction action,Long clientId, Long profileId, String projectCode) {		
		if(model != null) {
			model.sortFields();			
			if(form.getDeleteddRows().size() > 0) {
				String sql = form.buildDeleteSql(model, form.getDeleteddRows());
				Query query = session.createNativeQuery(sql);
				query.executeUpdate();
			}
			List<Long> oids = new ArrayList<>();
			if(model.hasDetails()) {
				for(Object[] detailData : form.getDetailsData()) {
					Number oid = (Number)detailData[detailData.length - 1];
					if(oid != null) {
						oids.add(oid.longValue());					
					}
				}
			}
			else {
				Number oid = (Number)form.getMainData()[form.getMainData().length - 1];
				if(oid != null) {
					oids.add(oid.longValue());		
				}
			}
			
			if(oids.size() > 0 && action != null) {
				MaterializedGrid matGrid = null;
				if(model.getDataSourceType().isMaterializedGrid()) {
					Optional<MaterializedGrid> response = materializedGridRepository.findById(model.getDataSourceId());
					if(response.isPresent()) {
						matGrid = response.get();
					}
				}
				
				String sql = form.buildChangeValueSql(action, model, matGrid, oids);		
				Query query = session.createNativeQuery(sql);
				query.executeUpdate();
				form = getById(model.getId(), form.getMainData(), clientId, profileId, projectCode);					
			}
		}	
		return form;
	}
		
	public List<ReferenceValue> getReferenceValues(List<Reference> references) throws BcephalException {
		List<ReferenceValue> values = new ArrayList<>();	
		for(Reference reference : references) {
			ReferenceValue value = getReferenceValue(reference);
			if(value != null) {
				values.add(value);
			}
		}
		return values;
	}
	
	public ReferenceValue getReferenceValue(Reference reference) throws BcephalException {
		ReferenceValue value = new ReferenceValue();		
		try {
			loadClosure(reference);
			value.setDimensionType(reference.getColumn().getType());
			String sql = reference.buildSql();
			log.trace("Reference value query : \n{}", sql);			
			Query query = session.createNativeQuery(sql);			
			@SuppressWarnings("unchecked")
			List<Object> objects = query.getResultList();
			if(objects.size() > 0) {
				Object item = objects.get(0);
				value.setValue(item);
			}
		} 
		catch (BcephalException ex) {
			log.error("Unable to retrieve reference fiield value ", ex);
			throw ex;
		}	
		catch (Exception ex) {
			log.error("Unable to retrieve reference fiield value ", ex);
		}
		return value;
	}

	
	public List<ReferenceValue> getSpotValues(List<FormModelSpotData> spots) throws BcephalException {
		List<ReferenceValue> values = new ArrayList<>();	
		for(FormModelSpotData reference : spots) {
			ReferenceValue value = getSpotValue(reference);
			if(value != null) {
				values.add(value);
			}
		}
		return values;
	}
	
	public ReferenceValue getSpotValue(FormModelSpotData spot) throws BcephalException {
		ReferenceValue value = new ReferenceValue();		
		try {
			loadClosure(spot);
			value.setDimensionType(spot.getColumn() != null ? spot.getColumn().getType() : DimensionType.MEASURE);
			String sql = spot.buildSql();
			log.trace("Reference value query : \n{}", sql);			
			Query query = session.createNativeQuery(sql);			
			@SuppressWarnings("unchecked")
			List<Object> objects = query.getResultList();
			if(objects.size() > 0) {
				Object item = objects.get(0);
				value.setValue(item);
			}
		} 
		catch (BcephalException ex) {
			log.error("Unable to retrieve reference fiield value ", ex);
			throw ex;
		}	
		catch (Exception ex) {
			log.error("Unable to retrieve reference fiield value ", ex);
		}
		return value;
	}
	
	private void loadClosure(Reference reference) throws Exception {
		if(JoinGridType.MATERIALIZED_GRID.equals(reference.getDataSourceType())) {
			Optional<SmartMaterializedGrid> grid = smartMaterializedGridRepository.findById(reference.getDataSourceId());
			if(grid.isPresent()) {
				reference.setGrid(grid.get());
				reference.setColumn(reference.getGrid().getColumnById(reference.getColumnId()));
			}
			else{
				throw new BcephalException("Unable to evalute reference value. Source column not found!");
			}
		}
		else {
			Optional<SmartGrille> grid = smartGrilleRepository.findById(reference.getDataSourceId());
			if(grid.isPresent()) {
				reference.setGrid(grid.get());
				reference.setColumn(reference.getGrid().getColumnById(reference.getColumnId()));
			}
			else{
				throw new BcephalException("Unable to evalute reference value. Source column not found!");
			}
		}
		
		for(ReferenceCondition condition : reference.getConditions()) {	
			condition.setColumn(reference.getGrid().getColumnById(condition.getKeyId()));		
		}
		
	}
	
	
	private void loadClosure(FormModelSpotData spot) throws Exception {
		if(DataSourceType.MATERIALIZED_GRID.equals(spot.getDataSourceType())) {
			Optional<SmartMaterializedGrid> grid = smartMaterializedGridRepository.findById(spot.getDataSourceId());
			if(grid.isPresent()) {
				spot.setGrid(grid.get());
				spot.setColumn(spot.getGrid().getColumnById(spot.getMeasureId()));
			}
			else{
				throw new BcephalException("Unable to evalute spot value. Source column not found!");
			}
			for(FormModelSpotItem spotItem : spot.getItems()) {	
				spotItem.setColumn(spot.getGrid().getColumnById(spotItem.getDimensionId()));		
			}
		}
		else {
			if(spot.getDataSourceId() != null) {
				Optional<SmartGrille> grid = smartGrilleRepository.findById(spot.getDataSourceId());
				if(grid.isPresent()) {
					spot.setGrid(grid.get());
					spot.setColumn(spot.getGrid().getColumnById(spot.getMeasureId()));
				}
				else{
					throw new BcephalException("Unable to evalute spot value. Source column not found!");
				}
				for(FormModelSpotItem spotItem : spot.getItems()) {	
					spotItem.setColumn(spot.getGrid().getColumnById(spotItem.getDimensionId()));		
				}
			}
			else {
				SmartGrille grid = new SmartGrille();
				SmartGrilleColumn col= new SmartGrilleColumn();
				col.setId(spot.getMeasureId());
				col.setDimensionId(spot.getMeasureId());
				col.setType(DimensionType.MEASURE);
				col.setDimensionFunction(spot.getFormula());
				col.setGridType(JoinGridType.REPORT_GRID);
				List<SmartGrilleColumn> columns = Arrays.asList(col);
				grid.setColumns(columns);
				spot.setColumn(col);
				spot.setGrid(grid);
				
				for(FormModelSpotItem spotItem : spot.getItems()) {	
					SmartGrilleColumn column = new SmartGrilleColumn();
					column.setId(spotItem.getDimensionId());
					column.setDimensionId(spotItem.getDimensionId());
					column.setType(spotItem.getDimensionType());
					column.setGridType(JoinGridType.GRID);					
					spotItem.setColumn(column);		
				}
			}
			
		}
		
		
		
	}

}
