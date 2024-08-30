/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.Variable;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.service.VariableService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.InitiationService;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public abstract class ParameterBuilder {

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected ParameterService parameterService;

	@Autowired
	InitiationService initiationService;

	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	VariableService variableService;

	@Autowired
	protected IncrementalNumberService incrementalNumberService;

	public ParameterBuilder() {

	}
	
	protected Variable buildVariable(String name, DimensionType dimensionType) {
		Variable variable = variableService.getByName(name);
		if(variable == null) {
			variable = new Variable();
			variable.setName(name);
			variable.setDescription(name);
			variable.setDimensionType(dimensionType);
			variable.setDataSourceType(DataSourceType.UNIVERSE);
			variable = variableService.save(variable, Locale.ENGLISH);
		}
		return variable;
	}

	protected Measure getOrBuildMeasure(String name, Measure parent, HttpSession session, Locale locale) throws Exception {
		Measure measure = initiationService.getMeasureRepository().findByNameIgnoreCase(name);
		if (measure == null) {			
			measure = (Measure)initiationService.createDimension(DimensionType.MEASURE, name, (parent != null ? parent.getId() : null), session, locale);
		}
		return measure;
	}

	protected Period getOrBuildPeriod(String name, Period parent, HttpSession session, Locale locale) throws Exception {
		Period period = initiationService.getPeriodRepository().findByNameIgnoreCase(name);
		if (period == null) {
			period = (Period)initiationService.createDimension(DimensionType.PERIOD, name, (parent != null ? parent.getId() : null), session, locale);
		}
		return period;
	}

	protected Model getOrBuildModel(String name, HttpSession session, Locale locale) throws Exception {
		Model model = initiationService.getModelRepository().findByNameIgnoreCase(name);
		if (model == null) {
			Long id = initiationService.createModel(name, session, locale);
			Optional<Model> response = initiationService.getModelRepository().findById(id);
			model = response.isPresent() ? response.get() : null;
		}
		return model;
	}

	protected Entity getOrBuildEntity(String name, Model model, HttpSession session, Locale locale) throws Exception {
		Entity entity = initiationService.getEntityRepository().findByNameIgnoreCase(name);
		if (entity == null) {
			Long id = initiationService.createEntity(name, (model != null ? model.getId() : null), session, locale);
			Optional<Entity> response = initiationService.getEntityRepository().findById(id);
			entity = response.isPresent() ? response.get() : null;
		}
		return entity;
	}

	protected Attribute getOrBuildAttribute(String name, Entity entity, HttpSession session, Locale locale) throws Exception {
		Attribute attribute = initiationService.getAttributeRepository().findByNameIgnoreCase(name);
		if (attribute == null) {			
			attribute = (Attribute)initiationService.createDimension(DimensionType.ATTRIBUTE, name, (entity != null ? entity.getId() : null), session, locale);
		}
		return attribute;
	}

	protected AttributeValue getOrBuildAttributeValue(String name, Attribute attribute) throws Exception {
		AttributeValue value = initiationService.getAttributeValueRepository().findByNameIgnoreCase(name);
		if (value == null) {
			value = new AttributeValue();
			value.setName(name);
			value.setAttribute(attribute);
			Long position = initiationService.getAttributeValueRepository().count();
			value.setPosition(position.intValue());
			initiationService.getAttributeValueRepository().save(value);
		}
		return value;
	}

	protected IPersistent buildParameterIfNotExist(String name, String code, ParameterType type,
			HashMap<Parameter, IPersistent> parametersMap, IPersistent persistent, String entityName, Model model, HttpSession session, Locale locale)
			throws Exception {
		if (type == ParameterType.ATTRIBUTE) {
			return buildAttributeParameter(name, code, parametersMap, entityName, model, session, locale);
		} else if (type == ParameterType.ATTRIBUTE_VALUE) {
			return buildAttributeValueParameter(name, code, parametersMap, (Attribute) persistent);
		} else if (type == ParameterType.MEASURE) {
			return buildMeasureParameter(name, code, parametersMap, (Measure) persistent, session, locale);
		} else if (type == ParameterType.PERIOD) {
			return buildPeriodParameter(name, code, parametersMap, (Period) persistent, session, locale);
		} else if (type == ParameterType.MODEL) {
			return buildModelParameter(name, code, parametersMap, session, locale);
		} else if (type == ParameterType.ENTITY) {
			return buildEntityParameter(name, code, parametersMap, model, session, locale);
		}
		return null;
	}

	protected Attribute buildAttributeParameter(String name, String code, HashMap<Parameter, IPersistent> parametersMap,
			String entityName, Model model, HttpSession session, Locale locale) throws Exception {
		Entity roleEntity = null;
		Attribute attribute = null;
		ParameterType type = ParameterType.ATTRIBUTE;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code, type);
		if (parameter == null || parameter.getLongValue() == null) {
			attribute = getAttributeFromDb(name);
			if (attribute == null) {
				if (roleEntity == null) {
					roleEntity = getOrBuildEntity(entityName, model, session, locale);
				}
				attribute = getOrBuildAttribute(name, roleEntity, session, locale);
			}
			if (parameter == null) {
				parameter = new Parameter(code, type);
			}
			parameter.setLongValue(attribute.getId());
			parametersMap.put(parameter, attribute);
		} else {
			Optional<Attribute> result = initiationService.getAttributeRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				attribute = result.get();
			}
		}
		return attribute;
	}

	protected AttributeValue buildAttributeValueParameter(String name, String code,
			HashMap<Parameter, IPersistent> parametersMap, Attribute attribute) throws Exception {
		AttributeValue value = null;
		ParameterType type = ParameterType.ATTRIBUTE_VALUE;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code, type);
		if (parameter == null || !StringUtils.hasText(parameter.getStringValue())) {
			value = getAttributeValueFromDb(name, attribute);
			if (value == null) {
				// value = buildAttributeValue(attribute, name);
				value = getOrBuildAttributeValue(name, attribute);
			}
			if (parameter == null) {
				parameter = new Parameter(code, type);
			}
			parameter.setStringValue(name);
			parametersMap.put(parameter, value);
		} else {
			value = new AttributeValue();
			value.setName(parameter.getStringValue());
//			Optional<AttributeValue> result = initiationService.getAttributeValueRepository().findById(parameter.getLongValue());
//			if(result.isPresent()) {
//				value = result.get();
//			}
		}
		return value;
	}

	protected Model buildModelParameter(String name, String code, HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale)
			throws Exception {
		Model model = null;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code,
				ParameterType.MODEL);
		if (parameter == null || parameter.getLongValue() == null) {
			// model = getMeasureFromDb(name);
			if (model == null) {
				model = getOrBuildModel(name, session, locale);
			}
			if (parameter == null) {
				parameter = new Parameter(code, ParameterType.MODEL);
			}
			parameter.setLongValue(model.getId());
			parametersMap.put(parameter, model);
		} else {
			Optional<Model> result = initiationService.getModelRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				model = result.get();
			}
		}
		return model;
	}

	protected Entity buildEntityParameter(String name, String code, HashMap<Parameter, IPersistent> parametersMap,
			Model model, HttpSession session, Locale locale) throws Exception {
		Entity entity = null;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code,
				ParameterType.ENTITY);
		if (parameter == null || parameter.getLongValue() == null) {
			// entity = getMeasureFromDb(name);
			if (entity == null) {
				entity = getOrBuildEntity(name, model, session, locale);
			}
			if (parameter == null) {
				parameter = new Parameter(code, ParameterType.ENTITY);
			}
			parameter.setLongValue(entity.getId());
			parametersMap.put(parameter, entity);
		} else {
			Optional<Entity> result = initiationService.getEntityRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				entity = result.get();
			}
		}
		return entity;
	}

	protected Measure buildMeasureParameter(String name, String code, HashMap<Parameter, IPersistent> parametersMap,
			Measure parent, HttpSession session, Locale locale) throws Exception {
		Measure measure = null;
		ParameterType type = ParameterType.MEASURE;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code, type);
		if (parameter == null || parameter.getLongValue() == null) {
			measure = getMeasureFromDb(name);
			if (measure == null) {
				// measure = buildMeasure(name, 0);
				measure = getOrBuildMeasure(name, parent, session, locale);
			}
			if (parameter == null) {
				parameter = new Parameter(code, type);
			}
			parameter.setLongValue(measure.getId());
			parametersMap.put(parameter, measure);
		} else {
			Optional<Measure> result = initiationService.getMeasureRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				measure = result.get();
			}
		}
		return measure;
	}

	protected Period buildPeriodParameter(String name, String code, HashMap<Parameter, IPersistent> parametersMap,
			Period parent, HttpSession session, Locale locale) throws Exception {
		Period periodName = null;
		ParameterType type = ParameterType.PERIOD;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code, type);
		if (parameter == null || parameter.getLongValue() == null) {
			periodName = getPeriodFromDb(name);
			if (periodName == null) {
				// periodName = buildPeriod(name, 0);
				periodName = getOrBuildPeriod(name, parent, session, locale);
			}
			if (parameter == null) {
				parameter = new Parameter(code, type);
			}
			// parameter.setLongValue(periodName.getId());
			parametersMap.put(parameter, periodName);
		} else {
			Optional<Period> result = initiationService.getPeriodRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				periodName = result.get();
			}
		}
		return periodName;
	}

	protected void buildIncrementalNumber(String name, String code, ParameterType type) throws Exception {
		IncrementalNumber number = null;
		Parameter parameter = parameterService.getParameterRepository().findByCodeAndParameterType(code, type);
		if (parameter == null) {
			parameter = new Parameter(code, type);
		}
		if (parameter.getLongValue() != null) {
			Optional<IncrementalNumber> result = incrementalNumberService.incrementalNumberRepository
					.findById(parameter.getLongValue());
			if (result.isPresent()) {
				number = result.get();
			}
		}
		if (number == null) {
			number = new IncrementalNumber();
			number.setName(name);
			incrementalNumberService.incrementalNumberRepository.save(number);
		}
		parameter.setLongValue(number.getId());
		parameterRepository.save(parameter);
	}

	protected Entity buildEntity(Model model, String name) {
		Entity entity = new Entity();
		entity.setName(name);
		// entity.setPosition(model.getEntities().size());
		entity.setModel(model);
		model.getEntities().add(entity);
		return entity;
	}

	protected Attribute buildAttribute(Entity entity, String name, boolean declared, boolean canUserModifyValues,
			boolean incremental) {
		Attribute attribute = new Attribute();
		attribute.setName(name);
		attribute.setDeclared(declared);
		// attribute.setCanUserModifyValues(canUserModifyValues);
		// attribute.setIncremental(incremental);
		attribute.setPosition(entity.getAttributes().size());
		attribute.setEntity(entity);
		entity.getAttributes().add(attribute);
		return attribute;
	}

	protected AttributeValue buildAttributeValue(Attribute attribute, String name) {
		AttributeValue value = new AttributeValue();
		value.setName(name);
		value.setPosition(attribute.getValues().size());
		value.setAttribute(attribute);
		attribute.getValues().add(value);
		return value;
	}

	protected Measure buildMeasure(String name, int position) {
		Measure measure = new Measure();
		measure.setName(name);
		measure.setPosition(position);
		return measure;
	}

	protected Period buildPeriod(String name, int position) {
		Period periodName = new Period();
		periodName.setName(name);
		periodName.setPosition(position);
		return periodName;
	}

	protected void addColum(Grille grid, String name, String code, ParameterType type, GrilleColumnCategory category,
			boolean mandatory) {
		DimensionType columnType = null;
		String columnValueName = null;
		Long columnValueOid = null;
		if (type == ParameterType.ATTRIBUTE) {
			columnType = DimensionType.ATTRIBUTE;
			Attribute attribute = parameterService.getAttribute(code);
			if (attribute != null) {
				columnValueName = attribute.getName();
				columnValueOid = attribute.getId();
			}
		} else if (type == ParameterType.MEASURE) {
			columnType = DimensionType.MEASURE;
			Measure measure = parameterService.getMeasure(code);
			if (measure != null) {
				columnValueName = measure.getName();
				columnValueOid = measure.getId();
			}
		} else if (type == ParameterType.PERIOD) {
			columnType = DimensionType.PERIOD;
			Period periodName = parameterService.getPeriod(code);
			if (periodName != null) {
				columnValueName = periodName.getName();
				columnValueOid = periodName.getId();
			}
		}
		if (columnType != null && columnValueOid != null) {
			int position = grid.getColumnListChangeHandler().getItems().size();
			GrilleColumn column = buildColumn(name, columnValueOid, columnValueName, columnType, position, category,
					mandatory);
			grid.getColumnListChangeHandler().addNew(column);
		}
	}

	protected GrilleColumn buildColumn(String name, Long dimensionId, String dimensionName, DimensionType type,
			int position, GrilleColumnCategory category, boolean mandatory) {
		GrilleColumn column = new GrilleColumn();
		column.setName(name);
		column.setType(type);
		column.setDimensionId(dimensionId);
		column.setDimensionName(dimensionName);
		column.setPosition(position);
		column.setCategory(category);
		column.setMandatory(mandatory);
		column.getFormat().setUsedSeparator(true);
		return column;
	}

	protected Model getModelFromDb(String name) throws Exception {
		Model result = initiationService.getModelRepository().findByNameIgnoreCase(name);
		return result;
	}

	protected Entity getEntityFromDb(String name) throws Exception {
		Entity result = initiationService.getEntityRepository().findByNameIgnoreCase(name);
		return result;
	}

	protected Attribute getAttributeFromDb(String name) throws Exception {
		Attribute result = initiationService.getAttributeRepository().findByNameIgnoreCase(name);
		return result;
	}

	protected AttributeValue getAttributeValueFromDb(String name, Attribute attribute) throws Exception {
		if (attribute != null && attribute.getId() != null) {
			AttributeValue value = initiationService.getAttributeValueRepository()
					.findByAttributeAndNameIgnoreCase(attribute, name);
			return value;
		}
		return null;
	}

	protected Measure getMeasureFromDb(String name) throws Exception {
		Measure result = initiationService.getMeasureRepository().findByNameIgnoreCase(name);
		return result;
	}

	protected Period getPeriodFromDb(String name) throws Exception {
		Period result = initiationService.getPeriodRepository().findByNameIgnoreCase(name);
		return result;
	}

	protected void saveParameters(HashMap<Parameter, IPersistent> parameters, Locale locale) {
		for (Entry<Parameter, IPersistent> entry : parameters.entrySet()) {
			Parameter parameter = entry.getKey();
			IPersistent persistent = entry.getValue();
			if (parameter.isValue()) {
				parameter.setStringValue(persistent != null ? ((AttributeValue) persistent).getName() : null);
			} else {
				if (parameter.isMeasure()) {
					initiationService.getMeasureRepository().save((Measure) persistent);
				} else if (parameter.isPeriod()) {
					initiationService.getPeriodRepository().save((Period) persistent);
				}
				parameter.setLongValue(persistent != null ? (Long) persistent.getId() : null);
			}
			this.parameterRepository.save(parameter);
		}
	}

}
