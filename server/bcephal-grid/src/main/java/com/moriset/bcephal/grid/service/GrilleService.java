/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.RightEditorData;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseExternalSourceType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.FindReplaceFilter;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleBrowserData;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleDimension;
import com.moriset.bcephal.grid.domain.GrilleEditedElement;
import com.moriset.bcephal.grid.domain.GrilleEditedResult;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.GrilleSource;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.domain.SmartJoin;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridCreateColumnData;
import com.moriset.bcephal.grid.repository.FileLoaderColumnSoftRepository;
import com.moriset.bcephal.grid.repository.GrilleColumnRepository;
import com.moriset.bcephal.grid.repository.GrilleDimensionRepository;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.JoinGridRepository;
import com.moriset.bcephal.grid.repository.JoinRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.service.form.Reference;
import com.moriset.bcephal.grid.service.form.ReferenceCondition;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.repository.filters.AttributeRepository;
import com.moriset.bcephal.repository.filters.AttributeValueRepository;
import com.moriset.bcephal.repository.filters.MeasureRepository;
import com.moriset.bcephal.repository.filters.PeriodRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
@Primary
public class GrilleService extends DataSourcableService<Grille, BrowserData> {
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	ParameterRepository parameterRepository;
		
	@Autowired
	GrilleColumnRepository grilleColumnRepository;
	
	@Autowired
	GrilleDimensionRepository grilleDimensionRepository;
	
	@Autowired
	FileLoaderColumnSoftRepository fileLoaderColumnSoftRepository;
			
	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	GridPublicationManager publicationManager;
		
	@PersistenceContext
	protected EntityManager session;
	
	@Autowired
	InitiationService initiationService;
	
	@Autowired
	AttributeRepository attributeRepository;
	

	@Autowired
	AttributeValueRepository attributeValueRepository;
	
	@Autowired
	MeasureRepository measureRepository;
	
	@Autowired
	PeriodRepository periodRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	JoinRepository joinRepository;
	
	@Autowired
	JoinGridRepository joinGridRepository;
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired
	CalculatedMeasureRepository calculatedMeasureRepository;
	
	@Override
	public GrilleRepository getRepository() {
		return grilleRepository;
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel,profileId);
	}
	
	public boolean setEditable(Long id, boolean editable, Locale locale) {
		log.debug("Try to set etitable : {}", editable);		
		try {	
			if(id == null) {
				String message = getMessageSource().getMessage("id.is.null", new Object[]{} , locale);
				throw new BcephalException(message);
			}
			Grille grid = getById(id);
			if(grid == null) {
				String message = getMessageSource().getMessage("unkown.grid", new Object[]{id} , locale);
				throw new BcephalException(message);
			}
			
			grid.setEditable(editable);			
			grid = grilleRepository.save(grid);
	        return true;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while setting editable : {}", id, e);
			String message = getMessageSource().getMessage("unable.to.save.grille", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	public EditorData<Grille> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<Grille> data = new EditorData<>();
		if (filter.isNewData()) {
			Grille grid = getNewItem();
			grid.setDataSourceType(filter.getDataSourceType());
			grid.setDataSourceId(filter.getDataSourceId());
			data.setItem(grid);
		} else {
			data.setItem(getById(filter.getId()));
		}
		
		if(data.getItem() != null && data.getItem().getGroup() == null) {
			data.getItem().setGroup(getDefaultGroup());
		}	
		
		if(data.getItem() != null) {
			if(data.getItem().getDataSourceType() == DataSourceType.MATERIALIZED_GRID) {
				String dName = initEditorDataForMaterializedGrid(data, data.getItem().getDataSourceId(), session, locale);
				data.getItem().setDataSourceName(dName);
			}
			else {
				if(data.getItem().getPublished()) {
					data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
					 data.setSpots(initiationService.getSpotsAsNameable(session, locale));
					 List<Model> models= new ArrayList<>(0);
					 List<Measure> measures = new ArrayList<>(0);
					 List<Period> periods = new ArrayList<>(0);
					 Model model = new Model();
					 models.add(model);
					 model.setName("Model");
					 Entity entity = new Entity();
					 entity.setName("Entity");
					 model.getEntities().add(entity);
					 
					for (GrilleColumn column : data.getItem().getColumnListChangeHandler().getItems()) {
						if(column.getType() == DimensionType.MEASURE) {
							if(column.getDimensionName() == null) {
								Optional<Measure> measure = measureRepository.findById(column.getDimensionId());
								if(measure.isPresent()) {
									column.setDimensionName(measure != null ? measure.get().getName() : "");
								}
							}
							measures.add(new Measure(column.getDimensionId(), column.getDimensionName()));
						}
						else if(column.getType() == DimensionType.ATTRIBUTE) {
							if(column.getDimensionName() == null) {
								Optional<Attribute> attribute = attributeRepository.findById(column.getDimensionId());
								if(attribute.isPresent()) {
									column.setDimensionName(attribute != null ? attribute.get().getName() : "");
								}
							}						
							entity.getAttributes().add(new Attribute(column.getDimensionId(), column.getDimensionName()));
						}
						else if(column.getType() == DimensionType.PERIOD) {
							if(column.getDimensionName() == null) {
								Optional<Period> period = periodRepository.findById(column.getDimensionId());
								if(period.isPresent()) {
									column.setDimensionName(period != null ? period.get().getName() : "");
								}
							}	
							periods.add(new Period(column.getDimensionId(), column.getDimensionName()));
						}
					}
					
					for (GrilleDimension dimension : data.getItem().getDimensionListChangeHandler().getItems()) {
						GrilleColumn col = getColumnByTypeAndDimensionId(dimension.getType(), dimension.getDimensionId(), data.getItem());
						if(col == null) {
							if(dimension.getType() == DimensionType.MEASURE) {
								measures.add(new Measure(dimension.getDimensionId(), dimension.getName()));
							}
							else if(dimension.getType() == DimensionType.ATTRIBUTE) {
								entity.getAttributes().add(new Attribute(dimension.getDimensionId(), dimension.getName()));
							}
							else if(dimension.getType() == DimensionType.PERIOD) {
								periods.add(new Period(dimension.getDimensionId(), dimension.getName()));
							}
						}			
					}
					
					Comparator<Dimension> comparator = new Comparator<Dimension>() {
						@Override
						public int compare(Dimension data1, Dimension data2) {						
							return data1.getName().compareTo(data2.getName());
						}
					};
					
					Comparator<Model> comparatorModel = new Comparator<Model>() {
						@Override
						public int compare(Model data1, Model data2) {						
							return data1.getName().compareTo(data2.getName());
						}
					};
					
					Comparator<Entity> comparatorEntity = new Comparator<Entity>() {
						@Override
						public int compare(Entity data1, Entity data2) {						
							return data1.getName().compareTo(data2.getName());
						}
					};
					
					Collections.sort(measures, comparator);
					Collections.sort(periods, comparator);
					Collections.sort(models, comparatorModel);
					if(models.size() > 0) {
						Collections.sort(models.get(0).getEntities(), comparatorEntity);
						if(models.get(0).getEntities().size() > 0) {
							Collections.sort(models.get(0).getEntities().get(0).getAttributes(), comparator);
						}
					}
					data.setModels(models);
					data.setMeasures(measures);
					data.setPeriods(periods);
				}
				else {
					data.setModels(initiationService.getModels(session, locale));
					data.setPeriods(initiationService.getPeriods(session, locale));
					data.setMeasures(initiationService.getMeasures(session, locale));
					data.setCalculatedMeasures(initiationService.getCalculatedMeasures(DataSourceType.UNIVERSE, null, session, locale));
					data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
					data.setSpots(initiationService.getSpotsAsNameable(session, locale));
				}
			}
		}
		
		
		
//		if(data.getItem() != null && !data.getItem().getPublished()) {
//			data.setModels(initiationService.getModels(session, locale));
//			data.setPeriods(initiationService.getPeriods(session, locale));
//			data.setMeasures(initiationService.getMeasures(session, locale));
//			data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
//			data.setSpots(initiationService.getSpotsAsNameable(session, locale));
//		}
//		else if(data.getItem() != null){
//			
//			 data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
//			 data.setSpots(initiationService.getSpotsAsNameable(session, locale));
//			 List<Model> models= new ArrayList<>(0);
//			 List<Measure> measures = new ArrayList<>(0);
//			 List<Period> periods = new ArrayList<>(0);
//			 Model model = new Model();
//			 models.add(model);
//			 model.setName("Model");
//			 Entity entity = new Entity();
//			 entity.setName("Entity");
//			 model.getEntities().add(entity);
//			 
//			for (GrilleColumn column : data.getItem().getColumnListChangeHandler().getItems()) {
//				if(column.getType() == DimensionType.MEASURE) {
//					if(column.getDimensionName() == null) {
//						Optional<Measure> measure = measureRepository.findById(column.getDimensionId());
//						if(measure.isPresent()) {
//							column.setDimensionName(measure != null ? measure.get().getName() : "");
//						}
//					}
//					measures.add(new Measure(column.getDimensionId(), column.getDimensionName()));
//				}
//				else if(column.getType() == DimensionType.ATTRIBUTE) {
//					if(column.getDimensionName() == null) {
//						Optional<Attribute> attribute = attributeRepository.findById(column.getDimensionId());
//						if(attribute.isPresent()) {
//							column.setDimensionName(attribute != null ? attribute.get().getName() : "");
//						}
//					}						
//					entity.getAttributes().add(new Attribute(column.getDimensionId(), column.getDimensionName()));
//				}
//				else if(column.getType() == DimensionType.PERIOD) {
//					if(column.getDimensionName() == null) {
//						Optional<Period> period = periodRepository.findById(column.getDimensionId());
//						if(period.isPresent()) {
//							column.setDimensionName(period != null ? period.get().getName() : "");
//						}
//					}	
//					periods.add(new Period(column.getDimensionId(), column.getDimensionName()));
//				}
//			}
//			
//			for (GrilleDimension dimension : data.getItem().getDimensionListChangeHandler().getItems()) {
//				GrilleColumn col = getColumnByTypeAndDimensionId(dimension.getType(), dimension.getDimensionId(), data.getItem());
//				if(col == null) {
//					if(dimension.getType() == DimensionType.MEASURE) {
//						measures.add(new Measure(dimension.getDimensionId(), dimension.getName()));
//					}
//					else if(dimension.getType() == DimensionType.ATTRIBUTE) {
//						entity.getAttributes().add(new Attribute(dimension.getDimensionId(), dimension.getName()));
//					}
//					else if(dimension.getType() == DimensionType.PERIOD) {
//						periods.add(new Period(dimension.getDimensionId(), dimension.getName()));
//					}
//				}			
//			}
//			
//			Comparator<Dimension> comparator = new Comparator<Dimension>() {
//				@Override
//				public int compare(Dimension data1, Dimension data2) {						
//					return data1.getName().compareTo(data2.getName());
//				}
//			};
//			
//			Comparator<Model> comparatorModel = new Comparator<Model>() {
//				@Override
//				public int compare(Model data1, Model data2) {						
//					return data1.getName().compareTo(data2.getName());
//				}
//			};
//			
//			Comparator<Entity> comparatorEntity = new Comparator<Entity>() {
//				@Override
//				public int compare(Entity data1, Entity data2) {						
//					return data1.getName().compareTo(data2.getName());
//				}
//			};
//			
//			Collections.sort(measures, comparator);
//			Collections.sort(periods, comparator);
//			Collections.sort(models, comparatorModel);
//			if(models.size() > 0) {
//				Collections.sort(models.get(0).getEntities(), comparatorEntity);
//				if(models.get(0).getEntities().size() > 0) {
//					Collections.sort(models.get(0).getEntities().get(0).getAttributes(), comparator);
//				}
//			}
//			data.setModels(models);
//			data.setMeasures(measures);
//			data.setPeriods(periods);
//		}
		return data;
	}
	
	@Override
	protected void setDataSourceName(Grille item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}
	
	@Override
	protected String initEditorDataForMaterializedGrid(EditorData<Grille> data, Long datatSourceId, HttpSession session, Locale locale) throws Exception {
		Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(datatSourceId);				
		SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
		data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
		data.setSpots(initiationService.getSpotsAsNameable(session, locale));
		List<Model> models = new ArrayList<>(0);
		List<Measure> measures = new ArrayList<>(0);
		List<Period> periods = new ArrayList<>(0);
		Model model = new Model();
		models.add(model);
		model.setName(grid.getName());
		Entity entity = new Entity();
		entity.setName("Columns");
		model.getEntities().add(entity);

		for (SmartMaterializedGridColumn column : grid.getColumns()) {
			if (column.getType() == DimensionType.MEASURE) {
				measures.add(new Measure(column.getId(), column.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));						
			} else if (column.getType() == DimensionType.ATTRIBUTE) {						
				entity.getAttributes().add(new Attribute(column.getId(), column.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
			} else if (column.getType() == DimensionType.PERIOD) {
				periods.add(new Period(column.getId(), column.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
			}
		}

		for (GrilleDimension dimension : data.getItem().getDimensionListChangeHandler().getItems()) {
			GrilleColumn col = getColumnByTypeAndDimensionId(dimension.getType(), dimension.getDimensionId(),
					data.getItem());
			if (col == null) {
				if (dimension.getType() == DimensionType.MEASURE) {
					measures.add(new Measure(dimension.getDimensionId(), dimension.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
				} else if (dimension.getType() == DimensionType.ATTRIBUTE) {
					entity.getAttributes().add(new Attribute(dimension.getDimensionId(), dimension.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
				} else if (dimension.getType() == DimensionType.PERIOD) {
					periods.add(new Period(dimension.getDimensionId(), dimension.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
				}
			}
		}

		Comparator<Dimension> comparator = new Comparator<Dimension>() {
			@Override
			public int compare(Dimension data1, Dimension data2) {
				return data1.getName().compareTo(data2.getName());
			}
		};

		if (models.get(0).getEntities().size() > 0) {
			Collections.sort(models.get(0).getEntities().get(0).getAttributes(), comparator);
		}
		Collections.sort(measures, comparator);
		Collections.sort(periods, comparator);
		
		data.setModels(models);
		data.setMeasures(measures);
		data.setPeriods(periods);
		data.setCalculatedMeasures(initiationService.getCalculatedMeasures(DataSourceType.MATERIALIZED_GRID, datatSourceId, session, locale));
		return grid.getName();
	}
	

	@Override
	public RightEditorData<Grille> getRightLowLevelEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		RightEditorData<Grille> data = super.getRightLowLevelEditorData(filter, session, locale);
		if(filter != null && StringUtils.hasText(filter.getSubjectType())) {
			data.setItems(grilleRepository.findByType(GrilleType.valueOf(filter.getSubjectType())));
		}
		return data;
	}
	
	public GrilleColumn getColumnByTypeAndDimensionId(DimensionType type, Long dimensionId, Grille grid) {
		for (GrilleColumn column : grid.getColumnListChangeHandler().getItems()) {
			if (column.getType() == type && dimensionId.equals(column.getDimensionId())) {				
				return column;
			}
		}
		return null;
	}
	
	protected Grille getNewItem(String baseName, boolean startWithOne) {
		Grille grid = new Grille();
		grid.setType(GrilleType.INPUT);
		int i = 0;
		grid.setName(baseName);
		if(startWithOne) {
			i = 1;
			grid.setName(baseName + i);
		}
		while(getByName(grid.getName()) != null) {
			i++;
			grid.setName(baseName + i);
		}
		return grid;
	}
	
	protected Grille getNewItem(String baseName) {
		return getNewItem(baseName, true);
	}
	
	@Override
	protected Grille getNewItem() {		
		return getNewItem("Input Grid ");
	}
	
		
	@Override
	@Transactional
	public Grille save(Grille grille, Locale locale) {
		log.debug("Try to  Save Grid : {}", grille);		
		try {	
			if(grille == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{grille} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(grille.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.grid.with.empty.name", new String[]{grille.getName()} , locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			
			validateBeforeSave(grille, locale);
			
			if(grille.getUserFilter() != null) {
				universeFilterService.save(grille.getUserFilter());
			}
			if(grille.getAdminFilter() != null) {
				universeFilterService.save(grille.getAdminFilter());
			}
			
			ListChangeHandler<GrilleColumn> columns = grille.getColumnListChangeHandler();
			ListChangeHandler<GrilleDimension> dimensions = grille.getDimensionListChangeHandler();
			
			grille.setModificationDate(new Timestamp(System.currentTimeMillis()));
			grille = grilleRepository.save(grille);
			Grille id = grille;
			columns.getNewItems().forEach( item -> {
				log.trace("Try to save GrilleColumn : {}", item);
				item.setGrid(id);
				grilleColumnRepository.save(item);
				log.trace("GrilleColumn saved : {}", item.getId());
			});
			columns.getUpdatedItems().forEach( item -> {
				log.trace("Try to save GrilleColumn : {}", item);
				item.setGrid(id);
				grilleColumnRepository.save(item);
				log.trace("GrilleColumn saved : {}", item.getId());
			});
			columns.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete GrilleColumn : {}", item);					
					grilleColumnRepository.deleteById(item.getId());
					fileLoaderColumnSoftRepository.resetByGrilleColumn(item.getId());
					log.trace("GrilleColumn deleted : {}", item.getId());
				}
			});
			
			dimensions.getNewItems().forEach( item -> {
				log.trace("Try to save GrilleDimension : {}", item);
				item.setGrid(id);
				grilleDimensionRepository.save(item);
				log.trace("GrilleDimension saved : {}", item.getId());
			});
			dimensions.getUpdatedItems().forEach( item -> {
				log.trace("Try to save GrilleDimension : {}", item);
				item.setGrid(id);
				grilleDimensionRepository.save(item);
				log.trace("GrilleDimension saved : {}", item.getId());
			});
			dimensions.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete GrilleDimension : {}", item);
					grilleDimensionRepository.deleteById(item.getId());
					log.trace("GrilleDimension deleted : {}", item.getId());
				}
			});
			
			log.debug("Grid saved : {} ", grille.getId());
	        return grille;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save grille : {}", grille, e);
			String message = getMessageSource().getMessage("unable.to.save.grille", new Object[]{grille} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	protected void validateBeforeSave(Grille grille, Locale locale) {
		if(!grille.isReconciliation() && !grille.isArchiveBackup() && !grille.isArchiveReplacement()) {
			List<Grille> objects = getAllByName(grille.getName());
			if(grille.isInput()) {
				objects = getAllInputByName(grille.getName());
			}else
				if(grille.isReport()) {
					objects = getAllReportByName(grille.getName());
				}
			for(Grille obj : objects) {
				if(!obj.getId().equals(grille.getId())) {
					log.trace("Duplicate grid name : {}", grille.getName());
					String message = messageSource.getMessage("duplicate.name", new Object[] { grille.getName() },
							locale);
					throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
				}
			}
		}
	}
	
	public List<Grille> getAllInputByName(String name) {
		log.debug("Try to  get all input by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByNameAndType(name, GrilleType.INPUT);
	}
	
	public List<Grille> getAllReportByName(String name) {
		log.debug("Try to  get all report by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByNameAndType(name, GrilleType.REPORT);
	}
	

	@Override
	@Transactional
	public void delete(Grille grid) {
		log.debug("Try to delete Grid : {}", grid);	
		if(grid == null || grid.getId() == null) {
			return;
		}
		
		List<Long> ids = grilleColumnRepository.getColumnIds(grid.getId());
		
		ListChangeHandler<GrilleColumn> columns = grid.getColumnListChangeHandler();
		columns.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete GrilleColumn : {}", item);
			    grilleColumnRepository.deleteById(item.getId());
			    fileLoaderColumnSoftRepository.resetByGrilleColumn(item.getId());
				log.trace("GrilleColumn deleted : {}", item.getId());
			}
		});
		
		ListChangeHandler<GrilleDimension> dimensions = grid.getDimensionListChangeHandler();
		dimensions.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete GrilleDimension : {}", item);
				grilleDimensionRepository.deleteById(item.getId());
				log.trace("GrilleDimension deleted : {}", item.getId());
			}
		});
				
		grilleRepository.deleteById(grid.getId());
		if(grid.getUserFilter() != null) {
			universeFilterService.delete(grid.getUserFilter());
		}
		if(grid.getAdminFilter() != null) {
			universeFilterService.delete(grid.getAdminFilter());
		}		
		
		String sql = "DELETE FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE "
				+ UniverseParameters.SOURCE_TYPE + " = :sourceType AND "
				+ UniverseParameters.SOURCE_ID + " = :gridId";
		Query query = session.createNativeQuery(sql);
		query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
		query.setParameter("gridId", grid.getId());
		query.executeUpdate();
		
		sql = "UPDATE BCP_FILE_LOADER_COLUMN SET grilleColumn = null WHERE grilleColumn IN (:ids)";
		query = session.createNativeQuery(sql);
		query.setParameter("ids", ids);
		query.executeUpdate();
		
		sql = "UPDATE BCP_FILE_LOADER SET targetId = null WHERE targetId = :gridId";
		query = session.createNativeQuery(sql);
		query.setParameter("gridId", grid.getId());
		query.executeUpdate();
		
		log.debug("Grid successfully to delete : {} ", grid);
	    return;	
	}
	
	
	@Transactional
	public GrilleEditedResult editCell(GrilleEditedElement element, Locale locale) {
		return editOneCell(element, locale);
	}
	
	@Transactional
	public Long editCells(List<GrilleEditedElement> elements, Locale locale) {
		Long id = null;
		for(GrilleEditedElement element : elements) {
			if(id != null && element.getId() == null) {
				element.setId(id);
			}
			editOneCell(element, locale);
			if(id == null) {
				id = element.getId();
			}
		}					
		return id;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected GrilleEditedResult editOneCell(GrilleEditedElement element, Locale locale) {
		try {			
			if (element.getId() == null) {
				String sql = "INSERT INTO " + UniverseParameters.UNIVERSE_TABLE_NAME + " (" + UniverseParameters.USERNAME + ","
						+ UniverseParameters.SOURCE_ID + "," + UniverseParameters.SOURCE_NAME + ","
						+ UniverseParameters.SOURCE_TYPE + "," + UniverseParameters.ISREADY + ","
						+ UniverseParameters.EXTERNAL_SOURCE_TYPE
						+ ") values(:username, :gridId, :gridName, :sourceType, :isReady, :externalSourceType) returning ID";
				Query query = session.createNativeQuery(sql);
				query.setParameter("username", "");
				query.setParameter("gridId", element.getGrid().getId());
				query.setParameter("gridName", element.getGrid().getName());
				query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
				query.setParameter("isReady", element.getGrid().getStatus().isLoaded());
				query.setParameter("externalSourceType", UniverseExternalSourceType.USER.toString());
				Number id = (Number)query.getSingleResult();
				if(id != null) {
					element.setId(id.longValue());
				}
			}

			if (element.getColumn().isMeasure()) {
				String col = element.getColumn().getUniverseTableColumnName();
				String sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET " + col
						+ " = :measure WHERE id = :id";
				Query query = session.createNativeQuery(sql);
				query.setParameter("measure", new TypedParameterValue(StandardBasicTypes.BIG_DECIMAL, element.getDecimalValue()));
				query.setParameter("id", element.getId());
				query.executeUpdate();
			} else if (element.getColumn().isAttribute()) {
				String col = element.getColumn().getUniverseTableColumnName();
				String sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET " + col
						+ " = :scope WHERE id = :id";
				Query query = session.createNativeQuery(sql);
				org.hibernate.query.Query query1 = (org.hibernate.query.Query) query;
				query1.setParameter("scope", new TypedParameterValue(StandardBasicTypes.STRING, element.getStringValue()));
				query.setParameter("id", element.getId());
				query.executeUpdate();
			} else if (element.getColumn().isPeriod()) {
				String col = element.getColumn().getUniverseTableColumnName();
				String sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET " + col
						+ " = :period WHERE id = :id";
				Query query = session.createNativeQuery(sql);
				query.setParameter("period", new TypedParameterValue(StandardBasicTypes.DATE, element.getDateValue()));
				query.setParameter("id", element.getId());
				query.executeUpdate();
			}
			applyDefaultValues(element.getGrid(), element.getId(), element.getColumn());
			GrilleEditedResult result = new GrilleEditedResult();			
			return result;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to edit cell", ex);
			String message = getMessageSource().getMessage("unable.to.edit.cell", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	
	public int countAffectedRowsByApplyDefaultValue(Long gridId, GrilleColumn column, Locale locale) {
		try {			
			if (gridId != null) {
				return new DefaultValueQueryBuilder().countAffectedRowsByApplyDefaultValue(gridId, column, null, session);
			}
			return 0;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to count row affected by apply default values", ex);
			String message = getMessageSource().getMessage("unable.to.apply.default.values", new Object[]{gridId, column} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public int applyDefaultValue(Long gridId, GrilleColumn column, Locale locale) {
		try {			
			if (gridId != null) {
				return new DefaultValueQueryBuilder().applyDefaultValue(gridId, column, null, session);
			}
			return 0;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to apply default values", ex);
			String message = getMessageSource().getMessage("unable.to.apply.default.value", new Object[]{gridId, column} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	protected void applyDefaultValues(Grille grid, Long rowId, GrilleColumn excludeColumn) {
		if(grid != null && rowId != null) {
			DefaultValueQueryBuilder builder = new DefaultValueQueryBuilder();
			for(GrilleColumn column : grid.getColumnListChangeHandler().getItems()) {
				if(excludeColumn != null && excludeColumn.getDimensionId() == column.getDimensionId()
						&& excludeColumn.getType() == column.getType()) {
					continue;
				}
				builder.applyDefaultValue(grid.getId(), column, rowId, session);
			}
		}
	}
	
	public int findAndReplaceCount(FindReplaceFilter criateria, Locale locale) {		
		try {
			if(criateria.getFilter() != null && criateria.getFilter().getGrid() != null) {
				loadFilterClosures(criateria.getFilter());	
				InputGridFindReplaceQueryBuilder builder = new InputGridFindReplaceQueryBuilder(criateria);
				String sql = builder.buildCountQuery();
				Query query = this.session.createNativeQuery(sql);
				Number number = (Number)query.getSingleResult();
				return number.intValue();
			}
		} catch (Exception ex) {
			log.error("Unable to count value to remplace", ex);
			String message = getMessageSource().getMessage("unable.to.count.value.to.replace", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
		return -1;
	}

	@Transactional
	public int findAndReplace(FindReplaceFilter criateria, Locale locale) {		
		try {
			if(criateria.getFilter() != null && criateria.getFilter().getGrid() != null) {
				loadFilterClosures(criateria.getFilter());	
				InputGridFindReplaceQueryBuilder builder = new InputGridFindReplaceQueryBuilder(criateria);
				String sql = builder.buildQuery();
				boolean isMeasure = criateria.getColumn().isMeasure();
				boolean isPeriod = criateria.getColumn().isPeriod();
				Object value = criateria.getReplaceValue();
				if(isMeasure) {
					value = criateria.getMeasureReplaceValue();
				}
				else if(isPeriod) {
					value = builder.buildPeriodDate(criateria.getReplaceValue());
				}
				else {
					if(value == null) {
						value = "";
					}
				}
				Query query = session.createNativeQuery(sql);
				query.setParameter("value", value);
				int count = query.executeUpdate();
				return count;
			}
		} catch (Exception ex) {
			log.error("Unable to replace value", ex);
			String message = getMessageSource().getMessage("unable.to.replace.value", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
		return -1;		
	}

	public Object[] getGridRow(Grille grid, Long id, Locale locale) {
		try {			
			if (grid != null || id != null) {
				
				List<GrilleColumn> columns = grid.getColumnListChangeHandler().getItems();
				Collections.sort(columns, new Comparator<GrilleColumn>() {
					@Override
					public int compare(GrilleColumn o1, GrilleColumn o2) {
						return o1.getPosition() - o2.getPosition();
					}
				});
				
				String sql = "SELECT ";
				String coma = "";
				for(GrilleColumn column : columns) {
					String col = column.getUniverseTableColumnName();
					if(col != null) {
						sql = sql.concat(coma).concat(col);
						coma = ", ";
					}
				}
				sql = sql.concat(coma).concat(UniverseParameters.ID);
				sql = sql.concat(" FROM " ).concat(UniverseParameters.UNIVERSE_TABLE_NAME);
				sql = sql.concat(" WHERE " ).concat(UniverseParameters.ID).concat(" = :id");
				Query query = session.createNativeQuery(sql);
				query.setParameter("id", id);
				Object[] objects = (Object[])query.getSingleResult();
				return objects;
			}
		} 
		catch (NoResultException e) { }
		catch (Exception ex) {
			log.error("Unable to read row data", ex);
			String message = getMessageSource().getMessage("unable.to.read.row.data", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
		return null;
	}

	@Transactional
	public boolean changeStatus(Long gridId, GrilleStatus status, Locale locale) {
		try {			
			if (gridId != null) {
				grilleRepository.changeStatus(gridId, status);
				String sql = "UPDATE ".concat(UniverseParameters.UNIVERSE_TABLE_NAME).concat(" SET ").concat(UniverseParameters.ISREADY).concat(" = :isReady ")
						.concat("WHERE ").concat(UniverseParameters.SOURCE_TYPE).concat(" = :sourceType ")
						.concat("AND ").concat(UniverseParameters.SOURCE_ID).concat(" = :sourceId ");
				Query query = session.createNativeQuery(sql);
				query.setParameter("isReady", status.isLoaded());
				query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
				query.setParameter("sourceId", gridId);
				query.executeUpdate();
				return true;
			}
			return false;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to change grid status", ex);
			String message = getMessageSource().getMessage("unable.to.change.grid.status", new Object[]{gridId, status} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public int duplicateRows(List<Long> ids, Locale locale) {
		try {	
			String sql = "SELECT upper(column_name) FROM information_schema.columns "
					+ "WHERE table_schema = '" + UniverseParameters.SCHEMA_NAME.toLowerCase().substring(0, UniverseParameters.SCHEMA_NAME.length() - 1) + "'"
					+ " AND table_name = '" + UniverseParameters.UNIVERSE_TABLE_NAME.toLowerCase() 
					+ "' AND column_name != 'id'";
			Query query = session.createNativeQuery(sql);
			@SuppressWarnings("unchecked")
			List<String> cols = query.getResultList();
			
			String coma = "";
			String colNames = "";
			for (String col : cols) {
				colNames += coma + col;
				coma = ", ";
			}
						
			sql = "INSERT INTO ".concat(UniverseParameters.UNIVERSE_TABLE_NAME).concat(" (").concat(colNames).concat(") ")
					.concat(" SELECT ").concat(colNames).concat(" FROM ").concat(UniverseParameters.UNIVERSE_TABLE_NAME)
					.concat(" WHERE ").concat(UniverseParameters.ID).concat(" IN :ids");
			query = session.createNativeQuery(sql);
			query.setParameter("ids", ids);
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to duplicate rows", ex);
			String message = getMessageSource().getMessage("unable.to.duplicate.rows", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public int deleteRows(List<Long> ids, Locale locale) {
		try {			
			String sql = "DELETE FROM ".concat(UniverseParameters.UNIVERSE_TABLE_NAME)
					.concat(" WHERE ").concat(UniverseParameters.ID).concat(" IN :ids");
			Query query = session.createNativeQuery(sql);
			query.setParameter("ids", ids);
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.rows", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public int deleteAllRows(Long id, Locale locale) {
		try {			
			String sql = "DELETE FROM ".concat(UniverseParameters.UNIVERSE_TABLE_NAME)
					.concat(" WHERE ").concat(UniverseParameters.SOURCE_ID).concat(" = :id AND ")
					.concat(UniverseParameters.SOURCE_TYPE).concat(" = :type");
			Query query = session.createNativeQuery(sql);
			query.setParameter("id", id);
			query.setParameter("type", UniverseSourceType.INPUT_GRID.name());
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete all rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.all.rows", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public int deleteAllRows(GrilleDataFilter filter, Locale locale) {
		try {	
			loadFilterClosures(filter);	
			InputGridQueryBuilder builder = getGridQueryBuilder(filter);
			String sql = builder.buildDeleteQuery();
			log.trace("Delete all rows : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete all rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.all.rows", new Object[]{filter} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}

	@Transactional
	public EditorData<Grille> publish(Long id, Locale locale, HttpSession session) {
		try {
			log.debug("Try to read grid by id : {}", id);
			Grille grid = getById(id);
			if(grid == null) {
				throw new BcephalException("There is no grid with id : " + id);
			}	
			if(grid.getPublished()) {
				publicationManager.refresh(grid);
			}
			else {
				publicationManager.publish(grid);						
			}
			publicationManager.publish(grid);			
			EditorDataFilter filter = new EditorDataFilter();
			filter.setId(id);
			return getEditorData(filter, session, locale);
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to publish the grid", ex);
			String message = getMessageSource().getMessage("unable.to.publish.grid", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public boolean publish(List<Long> ids, Locale locale, HttpSession session) {
		try {
			for(Long id : ids) {
				publish(id, locale, session);
			}
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to publish the grids", ex);
			String message = getMessageSource().getMessage("unable.to.publish.grids", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public void publish(Long id) throws Exception {
		Grille grid = getById(id);
		if(grid == null) {
			throw new BcephalException("There is no grid with id : " + id);
		}	
		if(grid.getPublished()) {
			publicationManager.refresh(grid);
		}
		else {
			publicationManager.publish(grid);						
		}
		publicationManager.publish(grid);	
	}
	
	@Transactional
	public EditorData<Grille> resetPublication(Long id, Locale locale, HttpSession session) {
		try {
			log.debug("Try to read grid by id : {}", id);
			Grille grid = getById(id);
			if(grid == null) {
				throw new BcephalException("There is no grid with id : " + id);
			}
			publicationManager.unpublish(grid);
			EditorDataFilter filter = new EditorDataFilter();
			filter.setId(id);
			return getEditorData(filter, session, locale);
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to reset the grid", ex);
			String message = getMessageSource().getMessage("unable.to.reset.grid", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public boolean resetPublication(List<Long> ids, Locale locale, HttpSession session) {
		try {
			for(Long id : ids) {
				resetPublication(id, locale, session);
			}
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to reset the grids", ex);
			String message = getMessageSource().getMessage("unable.to.reset.grids", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}

	@Transactional
	public boolean refreshPublication(Long id, Locale locale) {
		try {
			log.debug("Try to read grid by id : {}", id);
			Grille grid = getById(id);
			if(grid == null) {
				throw new BcephalException("There is no grid with id : " + id);
			}
			publicationManager.refresh(grid);
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to refresh grid publication", ex);
			String message = getMessageSource().getMessage("unable.to.refresh.grid.publication", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public boolean refreshPublication(List<Long> ids, Locale locale) {
		try {
			for(Long id : ids) {
				Grille grid = getById(id);
				if(grid == null) {
					throw new BcephalException("There is no grid with id : " + id);
				}
				if(grid.getPublished()) {
					refreshPublication(id, locale);
				}else {
					publish(id, locale, null);
				}
			}
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to refresh the grids", ex);
			String message = getMessageSource().getMessage("unable.to.refresh.grids", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public boolean republish(Long id, Locale locale) {
		try {
			log.debug("Try to read grid by id : {}", id);
			Grille grid = getById(id);
			if(grid == null) {
				throw new BcephalException("There is no grid with id : " + id);
			}
			if(grid.getPublished()) {
				publicationManager.unpublish(grid);
			}
			publicationManager.publish(grid);
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to refresh grid publication", ex);
			String message = getMessageSource().getMessage("unable.to.refresh.grid.publication", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public boolean republish(List<Long> ids, Locale locale) {
		try {
			for(Long id : ids) {
				Grille grid = getById(id);
				if(grid == null) {
					throw new BcephalException("There is no grid with id : " + id);
				}
				if(grid.getPublished()) {
					publicationManager.unpublish(grid);
				}
				publicationManager.publish(grid);
			}
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to refresh the grids", ex);
			String message = getMessageSource().getMessage("unable.to.refresh.grids", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	
	public GrilleColumnCount getColumnCountDetails(GrilleDataFilter filter, java.util.Locale locale) {	
		GrilleColumnCount columnCount = new GrilleColumnCount();
		loadFilterClosures(filter);	
		InputGridQueryBuilder builder = getGridQueryBuilder(filter);
		String sql = builder.buildColumnCountDetailsQuery();
		log.trace("Column count details query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		Object[] number = (Object[])query.getSingleResult();
		columnCount.setCountItems(((Number)number[0]).longValue());
		columnCount.setSumItems(BigDecimal.valueOf(((Number)number[1]).doubleValue()));
		columnCount.setMaxItem(BigDecimal.valueOf(((Number)number[2]).doubleValue()));
		columnCount.setMinItem(BigDecimal.valueOf(((Number)number[3]).doubleValue()));
		columnCount.setAverageItems(BigDecimal.valueOf(((Number)number[4]).doubleValue()));
		return columnCount;
	}
	
	
	public long getColumnDuplicateCount(GrilleDataFilter filter, java.util.Locale locale) {	
		loadFilterClosures(filter);	
		InputGridQueryBuilder builder = getGridQueryBuilder(filter);
		String sql = builder.buildColumnDuplicateCountQuery();
		log.trace("Column duplicate count query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		Number number = (Number)query.getSingleResult();
		return number.longValue();
	}
	
	public BrowserDataPage<Object[]> getColumnDuplicate(GrilleDataFilter filter, java.util.Locale locale) {	
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		loadFilterClosures(filter);	
		InputGridQueryBuilder builder = getGridQueryBuilder(filter);
		
		Long count = 0L;
		if(filter.isAllowRowCounting()) {			
			count = getColumnDuplicateCount(filter, locale);
			if(count == 0) {
				return page;
			}
			if(filter.isShowAll()) {
				page.setTotalItemCount(count.intValue());
				page.setPageCount(1);
				page.setCurrentPage(1);
				page.setPageFirstItem(1);
			}
			else {
				page.setTotalItemCount(count.intValue());
				page.setPageCount((count.intValue()/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
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
		
		String sql = builder.buildColumnDuplicateQuery();
		log.trace("Column duplicate query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		if(!filter.isShowAll()) {
			query.setFirstResult(page.getPageFirstItem() - 1);
			query.setMaxResults(page.getPageSize());
		}
		@SuppressWarnings("unchecked")
		List<Object[]> objects = query.getResultList();
		//formatDates(objects);
		page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
		page.setItems(objects); 
		log.debug("Row found : {}", objects.size());
		return page;
	}
	
	
	
	
	public BrowserDataPage<Object[]> searchRows(GrilleDataFilter filter, java.util.Locale locale) {		
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		if(filter.getGrid() != null) {
			loadFilterClosures(filter);	
			InputGridQueryBuilder builder = getGridQueryBuilder(filter);
			Integer count = 0;
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
			page.setItems(objects); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	
	public BrowserDataPage<GridItem> searchRows2(GrilleDataFilter filter, java.util.Locale locale) {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		page.setPageSize(filter.getPageSize());
		if(filter.getGrid() != null) {
			loadFilterClosures(filter);	
			InputGridQueryBuilder builder = getGridQueryBuilder(filter);
			Integer count = 0;
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
			log.trace("Query created!");
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			@SuppressWarnings("unchecked")
			List<Object[]> objects = query.getResultList();
			log.trace("Query executed!");
			//formatDates(objects);
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(GridItem.buildItems(objects)); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	public InputGridQueryBuilder getGridQueryBuilder(GrilleDataFilter filter) {
		InputGridQueryBuilder builder = new InputGridQueryBuilder(filter);
		if(filter.getGrid() != null && filter.getGrid().isReport()) {
			builder = new ReportGridQueryBuilder(filter);
			((ReportGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && (filter.getGrid().isInvoiceRepo() || filter.getGrid().isCreditNoteRepo())) {
			builder = new InvoiceGridQueryBuilder(filter);
			((InvoiceGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isBillingEventRepo()) {
			builder = new ReconciliationGridQueryBuilder(filter);
			((ReconciliationGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isClientRepo()) {
			builder = new ReconciliationGridQueryBuilder(filter);
			((ReconciliationGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isReconciliation()) {
			builder = new ReconciliationGridQueryBuilder(filter);
			((ReconciliationGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isArchiveBackup()) {
			builder = new ArchiveGridQueryBuilder(filter);
			((ArchiveGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isArchiveReplacement()) {
			builder = new ArchiveGridQueryBuilder(filter);
			((ArchiveGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isDashboardReport()) {
			filter.getGrid().setType(GrilleType.REPORT);
			builder = new DashboardReportQueryBuilder(filter);
			((DashboardReportQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		
		return builder;
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
		columns.forEach(column -> {
			column.setDataSourceType(filter.getGrid().getDataSourceType());
			column.setDataSourceId(filter.getGrid().getDataSourceId());
			
			if(column.isCalculatedMeasure() && column.getDimensionId() != null) {
				Optional<CalculatedMeasure> result = calculatedMeasureRepository.findById(column.getDimensionId());	
				column.setCalculatedMeasure(result.isPresent() ? result.get() : null);
				if(column.getCalculatedMeasure() != null) {
					column.getCalculatedMeasure().sortItems();
				}
			}
			
			});
		
		if(filter.getGrid().getUserFilter() != null) {
			
		}
		if(filter.getGrid().getAdminFilter() != null) {
			
		}
		if(filter.getGrid().getDataSourceType() != null) {
			if(filter.getGrid().getDataSourceType().isJoin()) {
				Optional<Join> result = joinRepository.findById(filter.getGrid().getDataSourceId());				
				if(result.isEmpty()) {
					throw new BcephalException("Join not found : {}", filter.getGrid().getDataSourceId());
				}
				Join join = result.get();
				if(!join.isPublished()){
					throw new BcephalException("You ave to materialized the underlying join : {}", join.getName());
				}
			}
		}	
		
		if(filter.getRowType() != null && filter.getGrid().getType() == GrilleType.BILLING_EVENT_REPOSITORY) {
			Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
			filter.setBillingStatusAttributeId(parameter != null ? parameter.getLongValue() : null);
			
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE, ParameterType.ATTRIBUTE_VALUE);
			filter.setBillingStatusBilledValue(parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "Billed");
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_EVENT_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
			filter.setBillingStatusDraftValue(parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "Draft");
		}
		else if(filter.getGrid().getType() == GrilleType.INVOICE_REPOSITORY || filter.getGrid().getType() == GrilleType.CREDIT_NOTE_REPOSITORY) {
			Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
			filter.setInvoiceStatusAttributeId(parameter != null ? parameter.getLongValue() : null);				
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_STATUS_VALIDATED_VALUE, ParameterType.ATTRIBUTE_VALUE);
			filter.setInvoiceStatusValidatedValue(parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "Billed");
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
			filter.setInvoiceStatusDraftValue(parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "Draft");
			
			
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
			filter.setInvoiceMailStatusAttributeId(parameter != null ? parameter.getLongValue() : null);				
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
			filter.setInvoiceMailStatusSentValue(parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "Sent");				
			parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
			filter.setInvoiceMailStatusNotSentValue(parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "Not yet sent");
			
		}
		
	}
	
	
	public InputGridQueryBuilder getDimensionValuesGridQueryBuilder(DimensionDataFilter filter) {
		InputGridQueryBuilder builder = new DimensionValuesInputGridQueryBuilder(filter);
		if(filter.getGrid() != null && filter.getGrid().isReport()) {
			builder = new DimensionValuesReportGridQueryBuilder(filter);
			((ReportGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && (filter.getGrid().isInvoiceRepo() || filter.getGrid().isCreditNoteRepo())) {
//			builder = new InvoiceGridQueryBuilder(filter);
//			((InvoiceGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isBillingEventRepo()) {
			builder = new DimensionValuesRecoGridQueryBuilder(filter);
			((ReconciliationGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isClientRepo()) {
			builder = new DimensionValuesRecoGridQueryBuilder(filter);
			((ReconciliationGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isReconciliation()) {
			builder = new DimensionValuesRecoGridQueryBuilder(filter);
			((ReconciliationGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isArchiveBackup()) {
			builder = new DimensionValuesArchiveGridQueryBuilder(filter);
			((ArchiveGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isArchiveReplacement()) {
			builder = new DimensionValuesArchiveGridQueryBuilder(filter);
			((ArchiveGridQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		else if(filter.getGrid() != null && filter.getGrid().isDashboardReport()) {
//			filter.getGrid().setType(GrilleType.REPORT);
//			builder = new DashboardReportQueryBuilder(filter);
//			((DashboardReportQueryBuilder)builder).setParameterRepository(parameterRepository);
		}
		
		return builder;
	}

	@Override
	protected Specification<Grille> getBrowserDatasSpecification(BrowserDataFilter filter, java.util.Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Grille> qBuilder = new RequestQueryBuilder<Grille>(root, query, cb);
			qBuilder.select(GrilleBrowserData.class);
		    Predicate predicate = qBuilder.getCriteriaBuilder().equal(qBuilder.getRoot().get("type"), getGrilleType());		    
		    qBuilder.add(predicate);
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    if (filter != null && StringUtils.hasText(filter.getCriteria())) {
		    	qBuilder.addLikeCriteria("name", filter.getCriteria());
		    }
		    if(filter.getColumnFilters() != null) {
		    	build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
	        return qBuilder.build();
		};
	}
	
	protected void build(ColumnFilter columnFilter) {
		if ("Status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(GrilleStatus.class);
		} else if ("Editable".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("editable");
			columnFilter.setType(Boolean.class);
		} else if ("published".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("published");
			columnFilter.setType(Boolean.class);
		}else if ("showAllRowsByDefault".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("showAllRowsByDefault");
			columnFilter.setType(Boolean.class);
		}else if ("allowLineCounting".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("allowLineCounting");
			columnFilter.setType(Boolean.class);
		}else if ("consolidated".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("consolidated");
			columnFilter.setType(Boolean.class);
		}else if ("debit".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("debit");
			columnFilter.setType(Boolean.class);
		}else if ("credit".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("credit");
			columnFilter.setType(Boolean.class);
		} else if ("rowType".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("rowType");
			columnFilter.setType(GrilleRowType.class);
		}
		super.build(columnFilter);
	}

	protected GrilleType getGrilleType() {		
		return GrilleType.INPUT;
	}

	@Override
	protected BrowserData getNewBrowserData(Grille item) {		
		return new GrilleBrowserData(item);
	}


	public Grille buildNewGridName(String baseName, Locale locale) {
		if(StringUtils.hasText(baseName)) {
			return getNewItem(baseName, false);
        }
		return getNewItem();
	}


	public List<GrilleColumn> getColumns(Long gridId) {
		return grilleColumnRepository.findByGrid(gridId);
	}
	
	static Long PAGE_SIZE = (long) 100000;
	
	public List<String> export(GrilleExportData data, java.util.Locale locale) throws Exception {
		return export(data.getFilter(), data.getType(), locale);
	}
	
	public List<String> export(GrilleDataFilter filter, GrilleExportDataType type, java.util.Locale locale)
			throws Exception {
		List<String> paths = new ArrayList<String>();
		boolean canSearch = true;
		Long minPageSize = (long) 25000;
		filter.setPageSize(minPageSize.intValue());
		filter.setPage(1);
		filter.setAllowRowCounting(false);
		int offste = 0;
		String path = null;
		GridExporter excelExporter = null;
		GridCsvExporter csvExporter = null;
		GridJsonExporter jsonExporter = null;
		while (canSearch) {
			offste++;
			BrowserDataPage<Object[]> page = searchRows(filter, locale);
			if(path == null) {
				path = getPath(offste + " - " + filter.getGrid().getName(), type);
			}
			boolean end = page.getItems().size() < minPageSize || (page.getPageLastItem()) % PAGE_SIZE == 0;
			if (type == GrilleExportDataType.EXCEL) {
				if(excelExporter == null) {
					excelExporter = new GridExporter(filter.getGrid(),filter.getExportColumnIds(), filter.isExportAllColumns());
				}
				excelExporter.writeRowss(page.getItems(), true);
				if(end) {
					excelExporter.writeFiles(path);
				}
			} else if (type == GrilleExportDataType.CSV) {
				if(csvExporter == null) {
					csvExporter = new GridCsvExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
				}else {				
					csvExporter.export(page.getItems(), true);
				}
				if(end) {
					csvExporter.close();
				}
			} else if (type == GrilleExportDataType.JSON) {
				if(jsonExporter == null) {
					jsonExporter = new GridJsonExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					jsonExporter.export(page.getItems(), true);
				}else {				
					jsonExporter.export(page.getItems(), true);
				}
				if(end) {
					jsonExporter.close();
				}
			}
			if(end) {
				paths.add(path);
				path = null;
				jsonExporter = null;
				csvExporter = null;
				excelExporter = null;
			}
			
			if (page.getItems().size() < minPageSize ) {
				canSearch = false;
			} else {
				filter.setPage(filter.getPage() + 1);
			}
		}
		return paths;
	}
	
	
	public List<String> export(XSSFWorkbook workbook,OutputStream fileOut,GrilleDataFilter filter, GrilleExportDataType type, java.util.Locale locale)
			throws Exception {
		List<String> paths = new ArrayList<String>();
		boolean canSearch = true;
		Long minPageSize = (long) 25000;
		filter.setPageSize(minPageSize.intValue());
		filter.setPage(1);
		filter.setAllowRowCounting(false);
		int offste = 0;
		String path = null;
		GridExporter excelExporter = null;
		GridCsvExporter csvExporter = null;
		GridJsonExporter jsonExporter = null;
		while (canSearch) {
			offste++;
			BrowserDataPage<Object[]> page = searchRows(filter, locale);
			if(path == null) {
				path = getPath(offste + " - " + filter.getGrid().getName(), type);
			}
			boolean end = page.getItems().size() < minPageSize || (page.getPageLastItem()) % PAGE_SIZE == 0;
			if (type == GrilleExportDataType.EXCEL) {
				if(excelExporter == null) {
					excelExporter = new GridExporter(workbook,filter.getGrid(),filter.getExportColumnIds(), filter.isExportAllColumns());
				}
				excelExporter.writeRowss(page.getItems(), true);
				if(end) {
					if(fileOut != null) {
						excelExporter.writeFiles(fileOut);
					}else {
						excelExporter.writeFiles(path);
					}
				}
			} else if (type == GrilleExportDataType.CSV) {
				if(csvExporter == null) {
					csvExporter = new GridCsvExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
				}else {				
					csvExporter.export(page.getItems(), true);
				}
				if(end) {
					csvExporter.close();
				}
			} else if (type == GrilleExportDataType.JSON) {
				if(jsonExporter == null) {
					jsonExporter = new GridJsonExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					jsonExporter.export(page.getItems(), true);
				}else {				
					jsonExporter.export(page.getItems(), true);
				}
				if(end) {
					jsonExporter.close();
				}
			}
			if(end) {
				paths.add(path);
				path = null;
				jsonExporter = null;
				csvExporter = null;
				excelExporter = null;
			}
			
			if (page.getItems().size() < minPageSize ) {
				canSearch = false;
			} else {
				filter.setPage(filter.getPage() + 1);
			}
		}
		return paths;
	}
	
	
	
	public List<String> performExport(GrilleDataFilter filter, GrilleExportDataType type, int maxRowCountPerFile) throws Exception {
		List<String> paths = new ArrayList<>();
		int fileNbr = 0;
		String baseName = filter.getGrid().getName() + System.currentTimeMillis();
		
		boolean canSearch = true;
		filter.setPageSize(maxRowCountPerFile);
		filter.setPage(1);
		filter.setAllowRowCounting(false);
		GridExporter excelExporter = null;
		GridCsvExporter csvExporter = null;
		GridJsonExporter jsonExporter = null;
		while (canSearch) {
			BrowserDataPage<Object[]> page = searchRows(filter, Locale.ENGLISH);	
			int count = page.getItems().size();
			if(count > 0) {
				String path = getPath(baseName, type, ++fileNbr);
				paths.add(path);
				if (type == GrilleExportDataType.EXCEL) {
					excelExporter = new GridExporter(filter.getGrid(), filter.getExportColumnIds(), filter.isExportAllColumns());
					excelExporter.writeRowss(page.getItems(), true);
					excelExporter.writeFiles(path);
				} else if (type == GrilleExportDataType.CSV) {
					csvExporter = new GridCsvExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
					csvExporter.close();
				} else if (type == GrilleExportDataType.JSON) {
					jsonExporter = new GridJsonExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					jsonExporter.export(page.getItems(), true);
					jsonExporter.close();
				}
			}
						
			if (count < maxRowCountPerFile ) {
				canSearch = false;
			} else {
				filter.setPage(filter.getPage() + 1);
			}
		}
		return paths;
	}
	
	public String getPath(String baseName, GrilleExportDataType type, int filenbr) {
		String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "bcephal");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		String fileName = baseName + "_" + filenbr + type.getExtension(); 
		return FilenameUtils.concat(path, fileName);
	}
	
	public String getPath(String fileName, GrilleExportDataType type) {
		String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "bcephal");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		return FilenameUtils.concat(path, getFileName(fileName, type));
	}

	public String getFileName(String fileName, GrilleExportDataType type ) {
		return fileName + System.currentTimeMillis() + type.getExtension();
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_INPUT_GRID;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	public void loadDimensionFilterClosures(DimensionDataFilter filter) throws Exception {
		if(filter.getGrid() != null) {
			List<GrilleColumn> columns = filter.getGrid().getColumnListChangeHandler().getItems();
			if(columns != null && columns.size() > 0) {
				Collections.sort(columns, new Comparator<GrilleColumn>() {
					@Override
					public int compare(GrilleColumn o1, GrilleColumn o2) {
						return o1.getPosition() - o2.getPosition();
					}
				});
				filter.getGrid().setColumns(columns);
			}
		}
		if (filter.getDataSourceType() != null) {
			if (filter.getDataSourceType().isJoin()) {
				Optional<Join> result = joinRepository.findById(filter.getDataSourceId());
				if (result.isEmpty()) {
					throw new BcephalException("Join not found : {}", filter.getDataSourceId());
				}
				Join join = result.get();
				if (!join.isPublished()) {
					JoinColumn column = join.getColumnByOid(filter.getDimensionId());
					if (column == null) {
						throw new BcephalException("Column not found : {}", join.getName());
					}
					Optional<JoinGrid> jg = joinGridRepository.findByGridIdAndGridTypeAndJoinId(column.getGridId(),
							column.getGridType(), column.getJoinId());
					if (jg.isPresent()) {
						JoinGrid grid = jg.get();
						filter.setDataSourceId(grid.getGridId());
						filter.setDataSourceType(grid.getGridType().GetDataSource());
						filter.setDimensionId(column.getDimensionId());
						if (grid.getGridType() == JoinGridType.JOIN
								|| grid.getGridType() == JoinGridType.MATERIALIZED_GRID) {
							filter.setDimensionId(column.getColumnId());
						}
					} else {
						throw new BcephalException("You have to materialized the underlying join : {}", join.getName());
					}
				}
			}
		}
		if(filter.getReference() != null) {
			loadClosure(filter.getReference());
		}
		
		if(filter.getVariableReference() != null) {
			loadClosure(filter.getVariableReference());
		}
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
		else if(JoinGridType.JOIN.equals(reference.getDataSourceType())) {
			Optional<SmartJoin> grid = smartJoinRepository.findById(reference.getDataSourceId());
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

	private void loadClosure(DashboardItemVariableData reference) throws Exception {
		if(JoinGridType.MATERIALIZED_GRID.equals(reference.getDataSourceType())) {
			Optional<SmartMaterializedGrid> grid = smartMaterializedGridRepository.findById(reference.getDataSourceId());
			if(grid.isPresent()) {
				reference.setGrid(grid.get());
				reference.setColumn(reference.getGrid().getColumnById(reference.getSourceId()));
			}
			else{
				throw new BcephalException("Unable to evalute reference value. Source column not found!");
			}
		}
		else if(JoinGridType.JOIN.equals(reference.getDataSourceType())) {
			Optional<SmartJoin> grid = smartJoinRepository.findById(reference.getDataSourceId());
			if(grid.isPresent()) {
				reference.setGrid(grid.get());
				reference.setColumn(reference.getGrid().getColumnById(reference.getSourceId()));
			}
			else{
				throw new BcephalException("Unable to evalute reference value. Source column not found!");
			}
		}
		else {
			Optional<SmartGrille> grid = smartGrilleRepository.findById(reference.getDataSourceId());
			if(grid.isPresent()) {
				reference.setGrid(grid.get());
				reference.setColumn(reference.getGrid().getColumnById(reference.getSourceId()));
			}
			else{
				throw new BcephalException("Unable to evalute reference value. Source column not found!");
			}
		}
		
		for(DashboardItemVariableReferenceConditionData condition : reference.getItems()) {	
			condition.setColumn(reference.getGrid().getColumnById(condition.getKeyId()));		
		}		
	}
	
	public BrowserDataPage<Object> searchAttributeValues(DimensionDataFilter filter, Locale locale) throws Exception {
		BrowserDataPage<Object> page = new BrowserDataPage<Object>();
		page.setPageSize(filter.getPageSize());
		if(filter != null) {
			loadDimensionFilterClosures(filter);	
//			InputGridQueryBuilder builder = getDimensionValuesGridQueryBuilder(filter);
			DimensionValuesGridQueryBuilder builder = new DimensionValuesGridQueryBuilder(filter);
			builder.setParameterRepository(parameterRepository);
			Integer count = 0;
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
			List<Object> objects = query.getResultList();
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(objects); 
			log.debug("Row found : {}", objects.size());
		}
		return page;
	}

	public Long createReport(Long gridId, String reportName, Locale locale) {
		Grille grid = getById(gridId);
		if(grid != null) {
			Grille report = grid.copy();
			report.setName(reportName);
			report.setDataSourceType(DataSourceType.UNIVERSE);
			report.setDataSourceId(null);
			report.setType(GrilleType.REPORT);	
			report.setSourceType(GrilleSource.USER);
			report.setSourceId(null);
			report.setEditable(false);	
			report.setConsolidated(true);
			report.setDebit(false);
			report.setCredit(false);
			report.setPublished(false);
			
			report = save(report, locale);
			return report.getId();
		}
		return null;
	}

	public List<String> exportWithXSSFWorkbook(GrilleExportData data, XSSFWorkbook workbook,OutputStream fileOut) throws Exception {
		if(workbook != null && fileOut != null) {
			return export(workbook, fileOut,data.getFilter(), data.getType(), Locale.ENGLISH);
		}
		return export(data.getFilter(), data.getType(), Locale.ENGLISH);
	}
	
	
	
	
	@Transactional
	public GrilleColumn createColumn(UnionGridCreateColumnData data, Locale locale) throws Exception {
		if(data == null) {
			throw new BcephalException("Grid creation data is NULL!");
		}
		if(data.getGridId() == null) {
			throw new BcephalException("Grid ID is NULL!");
		}
		Grille grid = getById(data.getGridId());
		if(grid == null) {
			throw new BcephalException("Grid not found!");
		}
		GrilleColumn column = createColumn(grid, data.getDimensionType(), data.getColumnName(), locale);		
		return column;
	}
	
	
	public GrilleColumn createColumn(Grille grid, DimensionType dimensionType, String columnName, Locale locale) throws Exception {		
		GrilleColumn column = grid.getColumnByDimensionAndName(dimensionType, columnName);				
		if(column == null) {
			String modelName = "Default Model";
			String entityName = "Reconciliation";
			Dimension dimension = initiationService.getOrBuildDimension(dimensionType, columnName, modelName, entityName, null, locale);			
			column = new GrilleColumn();
			column.setName(columnName);
			column.setType(dimensionType);
			column.setDimensionId((Long)dimension.getId());
			column.setPosition(grid.getColumnListChangeHandler().getItems().size());
			grid.getColumnListChangeHandler().addNew(column);
			grid = save(grid, locale);
		}		
		return column;
	}
	
	
	
}
