/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.grid.domain.SmartJoin;
import com.moriset.bcephal.grid.domain.SmartJoinColumn;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.repository.SmartJoinRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.MainObjectService;

import jakarta.servlet.http.HttpSession;

public abstract class DataSourcableService<P extends MainObject, B> extends MainObjectService<P, B> {

	@Autowired
	protected
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@Autowired
	SmartJoinRepository smartJoinRepository;
	
	@Autowired
	InitiationService initiationService;
	
	@Override
	public P getById(Long id) {
		P item = super.getById(id);
		if(item != null) {
			setDataSourceName(item);
		}
		return item;
	}
	
	protected void setDataSourceName(P item) {
		
	}

	protected String initEditorDataForMaterializedGrid(EditorData<P> data, Long datatSourceId, HttpSession session, Locale locale) throws Exception {
		Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(datatSourceId);				
		SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
		data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
		data.setSpots(initiationService.getSpotsAsNameable(session, locale));
		List<Model> models = new ArrayList<>(0);
		List<Measure> measures = new ArrayList<>(0);
		List<Period> periods = new ArrayList<>(0);
		Model model = new Model();
		models.add(model);
		model.setName(grid != null ? grid.getName() : null);
		Entity entity = new Entity();
		entity.setName("Columns");
		model.getEntities().add(entity);

		if(grid != null) {
			for (SmartMaterializedGridColumn column : grid.getColumns()) {
				if (column.getType() == DimensionType.MEASURE) {
					measures.add(new Measure(column.getId(), column.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));						
				} else if (column.getType() == DimensionType.ATTRIBUTE) {						
					entity.getAttributes().add(new Attribute(column.getId(), column.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
				} else if (column.getType() == DimensionType.PERIOD) {
					periods.add(new Period(column.getId(), column.getName(), DataSourceType.MATERIALIZED_GRID, datatSourceId));
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
		return grid != null ? grid.getName() : null;
	}
	
	protected String initEditorDataForJoin(EditorData<P> data, Long datatSourceId, HttpSession session, Locale locale) throws Exception {
		data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
		data.setSpots(initiationService.getSpotsAsNameable(session, locale));
		
		Optional<SmartJoin> response = smartJoinRepository.findById(datatSourceId);				
		SmartJoin grid = response.isPresent() ? response.get() : null;
		
		List<Model> models = new ArrayList<>(0);
		List<Measure> measures = new ArrayList<>(0);
		List<Period> periods = new ArrayList<>(0);
		Model model = new Model();
		models.add(model);
		model.setName(grid.getName());
		Entity entity = new Entity();
		entity.setName("Columns");
		model.getEntities().add(entity);

		for (SmartJoinColumn column : grid.getColumns()) {
			if (column.getType() == DimensionType.MEASURE) {
				measures.add(new Measure(column.getId(), column.getName(), DataSourceType.JOIN, datatSourceId));						
			} else if (column.getType() == DimensionType.ATTRIBUTE) {						
				entity.getAttributes().add(new Attribute(column.getId(), column.getName(), DataSourceType.JOIN, datatSourceId));
			} else if (column.getType() == DimensionType.PERIOD) {
				periods.add(new Period(column.getId(), column.getName(), DataSourceType.JOIN, datatSourceId));
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
		return grid != null ? grid.getName() : null;
	}
	
}
