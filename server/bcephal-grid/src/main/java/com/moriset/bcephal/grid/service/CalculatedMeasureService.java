package com.moriset.bcephal.grid.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureBrowserData;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureEditorData;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureItem;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureExcludeFilterRepository;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureItemRepository;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculatedMeasureService extends MainObjectService<CalculatedMeasure, CalculatedMeasureBrowserData>{

	@Autowired
	CalculatedMeasureRepository calculatedMeasureRepository;
	
	@Autowired
	CalculatedMeasureItemRepository calculatedMeasureItemRepository;
	
	@Autowired
	CalculatedMeasureExcludeFilterRepository calculatedMeasureExcludeFilterRepository;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@Autowired 
	MaterializedGridRepository materializedGridRepository;
	
	
	@Override
	public EditorData<CalculatedMeasure> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		CalculatedMeasureEditorData data = new CalculatedMeasureEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			data.getItem().setDataSourceType(filter.getDataSourceType());
			data.getItem().setDataSourceId( filter.getDataSourceId());
		} else {
			data.setItem(getById(filter.getId()));
		}
		if(data.getItem() != null) {
			if(data.getItem().getDataSourceType().isMaterializedGrid()) {
				String dName = initEditorDataForMaterializedGrid(data, data.getItem().getDataSourceId(), session, locale);
				data.getItem().setDataSourceName(dName);
			}
			else if(data.getItem().getDataSourceType().isJoin()) {
				
			}
			else {
				initEditorData(data, session, locale);
			}
		}		
		data.setMatGrids(materializedGridRepository.findGenericAllAsNameables());		
		return data;
	}
	
	@Override
	public CalculatedMeasure getById(Long id) {
		CalculatedMeasure item = super.getById(id);
		if(item != null) {
			setDataSourceName(item);
		}
		return item;
	}
	
	protected void setDataSourceName(CalculatedMeasure item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}
	
	protected String initEditorDataForMaterializedGrid(EditorData<CalculatedMeasure> data, Long datatSourceId, HttpSession session, Locale locale) throws Exception {
		Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(datatSourceId);				
		SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
		data.setCalendarCategories(getInitiationService().getCalendarsAsNameable(session, locale));
		data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
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
//		data.setCalculatedMeasures(initiationService.getCalculatedMeasures(DataSourceType.MATERIALIZED_GRID, datatSourceId, session, locale));
		return grid.getName();
	}
	
	@Override
	@Transactional
	public CalculatedMeasure save(CalculatedMeasure calculatedMeasure, Locale locale) {
		log.debug("Try to  Save calculated measure : {}", calculatedMeasure);
		try {
			if (calculatedMeasure == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.calculated.measure",
						new Object[] { calculatedMeasure }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(calculatedMeasure.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.calculated.measure\".with.empty.name",
						new String[] { calculatedMeasure.getName() }, locale);
				throw new BcephalException(message);
			}

			ListChangeHandler<CalculatedMeasureItem> items = calculatedMeasure.getItemsListChangeHandler();

			calculatedMeasure.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(calculatedMeasure, locale);
			calculatedMeasure = calculatedMeasureRepository.save(calculatedMeasure);
			CalculatedMeasure id = calculatedMeasure;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save calculated measure item : {}", item);
				item.setCalculatedMeasure(id);
				save(item);
				log.trace("calculated measure item saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save calculated measure item : {}", item);
				item.setCalculatedMeasure(id);
				save(item);
				log.trace("calculated measure item saved : {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete calculated measure item : {}", item);
					delete(item);
					log.trace("calculated measure item deleted : {}", item.getId());
				}
			});

			log.debug("calculated measure saved : {} ", calculatedMeasure.getId());
			return calculatedMeasure;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save calculated measure : {}", calculatedMeasure, e);
			String message = getMessageSource().getMessage("unable.to.save.calculated.measure", new Object[] { calculatedMeasure },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void save(CalculatedMeasureItem measureItem) {
		ListChangeHandler<CalculatedMeasureExcludeFilter> items = measureItem.getExcludeFiltersListChangeHandler();
		measureItem = calculatedMeasureItemRepository.save(measureItem);
		CalculatedMeasureItem id = measureItem;
		items.getNewItems().forEach(item -> {
			log.trace("Try to save calculated measure exclude filter : {}", item);
			item.setItem(id);
			calculatedMeasureExcludeFilterRepository.save(item);
			log.trace("calculated measure exclude filter saved : {}", item.getId());
		});
		items.getUpdatedItems().forEach(item -> {
			log.trace("Try to save calculated measure exclude filter : {}", item);
			item.setItem(id);
			calculatedMeasureExcludeFilterRepository.save(item);
			log.trace("calculated measure exclude filter saved : {}", item.getId());
		});
		items.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete calculated measure exclude filter : {}", item);
				calculatedMeasureExcludeFilterRepository.deleteById(item.getId());
				log.trace("calculated measure exclude filter deleted : {}", item.getId());
			}
		});
	}

	private void delete(CalculatedMeasureItem measureItem) {
		ListChangeHandler<CalculatedMeasureExcludeFilter> items = measureItem.getExcludeFiltersListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete calculated measure exclude filter : {}", item);
				calculatedMeasureExcludeFilterRepository.deleteById(item.getId());
				log.trace("calculated measure exclude filter deleted : {}", item.getId());
			}
		});
		calculatedMeasureItemRepository.deleteById(measureItem.getId());
	}

	@Override
	@Transactional
	public void delete(CalculatedMeasure calculatedMeasure) {
		log.debug("Try to delete calculated measure : {}", calculatedMeasure);
		if (calculatedMeasure == null || calculatedMeasure.getId() == null) {
			return;
		}
		ListChangeHandler<CalculatedMeasureItem> items = calculatedMeasure.getItemsListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete calculated measure item : {}", item);
				delete(item);
				log.trace("calculated measure item deleted : {}", item.getId());
			}
		});
		calculatedMeasureRepository.deleteById(calculatedMeasure.getId());
		log.debug("calculated measure successfully to delete : {} ", calculatedMeasure);
		return;
	}
	
	@Override
	public CalculatedMeasureRepository getRepository() {
		return calculatedMeasureRepository;
	}

	@Override
	protected CalculatedMeasure getNewItem() {
		CalculatedMeasure measure = new CalculatedMeasure();
		int i = 0;
		String baseName = "Calculated measure";
		measure.setName(baseName);

		while (getByName(measure.getName()) != null) {
			i++;
			measure.setName(baseName + i);
		}
		return measure;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.INITIATION_CALCULATED_MEASURE;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return new ArrayList<>();
	}

	@Override
	protected CalculatedMeasureBrowserData getNewBrowserData(CalculatedMeasure item) {
		return new CalculatedMeasureBrowserData(item);
	}

	@Override
	protected Specification<CalculatedMeasure> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<CalculatedMeasure> qBuilder = new RequestQueryBuilder<CalculatedMeasure>(root, query, cb);
			qBuilder.select(BrowserData.class);
						
			if (filter != null && filter.getDataSourceType() != null) {
				qBuilder.addEquals("dataSourceType", filter.getDataSourceType());
				if (filter.getDataSourceId() != null) {
					qBuilder.addEquals("dataSourceId", filter.getDataSourceId());
				}
			}

			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
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
