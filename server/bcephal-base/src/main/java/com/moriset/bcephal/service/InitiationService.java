/**
 * 
 */
package com.moriset.bcephal.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionApi;
import com.moriset.bcephal.domain.dimension.DimensionApiResponse;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.repository.filters.AttributeRepository;
import com.moriset.bcephal.repository.filters.AttributeValueRepository;
import com.moriset.bcephal.repository.filters.CalendarRepository;
import com.moriset.bcephal.repository.filters.EntityRepository;
import com.moriset.bcephal.repository.filters.MeasureRepository;
import com.moriset.bcephal.repository.filters.ModelRepository;
import com.moriset.bcephal.repository.filters.PeriodRepository;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
public class InitiationService {

	@Autowired
	MessageSource messageSource;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	ModelRepository modelRepository;
	
	@Autowired
	EntityRepository entityRepository;
	
	@Autowired
	AttributeRepository attributeRepository;
	

	@Autowired
	AttributeValueRepository attributeValueRepository;
	
	@Autowired
	MeasureRepository measureRepository;
	
	@Autowired
	CalculatedMeasureRepository calculatedMeasureRepository;
	
	@Autowired
	PeriodRepository periodRepository;
	
	@Autowired
	CalendarRepository calendarRepository;
	
	@Autowired
	SpotRepository spotRepository;
	
	@Autowired
	UniverseGenerator universeGenerator;
	

	public Long createModel(String name, HttpSession session, Locale locale) throws Exception {
		log.debug("Call of createModel");
		try {
			
			Model model = new Model();
			model.setName(name);
			model.setPosition(((Long) modelRepository.count()).intValue());
			model = modelRepository.save(model);
			Long result = model.getId();
			log.debug("Model created : {}", result);
			return result;
			
//			String path = "/initiation/api/create-model";
//			HttpEntity<String> requestEntity = new HttpEntity<>(name, getHttpHeaders(session, locale));
//			ResponseEntity<Long> response = restTemplate.exchange(path, HttpMethod.POST, requestEntity, Long.class);
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to create model : {}. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						name, response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to create model : {}.", name);
//			}
//			Long result = response.getBody();
//			log.debug("Model created : {}", result);
//			return result;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unable to create model : {}", name, e);
			throw new BcephalException("Unable to create model : {}", name);
		}
	}

	public Long createEntity(String name, Long modelId, HttpSession session, Locale locale) throws Exception {
		log.debug("Call of createEntity");
		try {
			
			Entity entity = new Entity();
			entity.setModel( modelRepository.getReferenceById(modelId));
			entity.setName(name);
			entity = entityRepository.save(entity);
			Long result = entity.getId();
			log.debug("Entity created : {}", result);
			return result;
			
//			String path = "/initiation/api/create-entity/" + modelId;
//			HttpEntity<String> requestEntity = new HttpEntity<>(name, getHttpHeaders(session, locale));
//			ResponseEntity<Long> response = restTemplate.exchange(path, HttpMethod.POST, requestEntity, Long.class);
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to create entity : {}. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						name, response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to create entity : {}.", name);
//			}
//			Long result = response.getBody();
//			log.debug("Entity created : {}", result);
//			return result;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unable to create entity : {}", name, e);
			throw new BcephalException("Unable to create entity : {}", name);
		}
	}

//	public Dimension createDimension(DimensionType type, String name, Long defaultEntityId, HttpSession session,
//			Locale locale) throws Exception {
//		if (type.isMeasure()) {
//			return createDimension(new DimensionApi(name, null, defaultEntityId, DimensionType.MEASURE), session,
//					locale).AsDimension(type);
//		}
//		if (type.isPeriod()) {
//			return createDimension(new DimensionApi(name, null, defaultEntityId, DimensionType.PERIOD), session,
//					locale).AsDimension(type);
//		}
//		if (type.isAttribute()) {
//			if (defaultEntityId == null) {
//				throw new BcephalException("Unable to create attribute : {}. Default entity not found!", name);
//			}
//			return createDimension(new DimensionApi(name, null, defaultEntityId, DimensionType.ATTRIBUTE), session,
//					locale).AsDimension(type);
//		}
//		return null;
//	}
	
	
	public Dimension createDimension(DimensionType type, String name, Long defaultEntityId, HttpSession session,
			Locale locale) throws Exception {
		if (type.isMeasure()) {
			return createMeasure(new DimensionApi(name, defaultEntityId, null, DimensionType.MEASURE), locale);
		}
		if (type.isPeriod()) {
			return createPeriod(new DimensionApi(name, defaultEntityId, null, DimensionType.PERIOD), locale);
		}
		if (type.isAttribute()) {
			if (defaultEntityId == null) {
				throw new BcephalException("Unable to create attribute : {}. Default entity not found!", name);
			}
			return createAttribute(new DimensionApi(name, null, defaultEntityId, DimensionType.ATTRIBUTE), locale);
		}
		return null;
	}

	protected DimensionApiResponse createDimension(DimensionApi dimension, HttpSession session, Locale locale)
			throws Exception {
		log.debug("Call of createDimension");
		try {
			String path = dimension.getType().isMeasure() ? "/initiation/api/create-measure"
					: dimension.getType().isPeriod() ? "/initiation/api/create-period"
							: "/initiation/api/create-attribute";
			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(dimension, getHttpHeaders(session, locale));
			ResponseEntity<DimensionApiResponse> response = restTemplate.exchange(path, HttpMethod.POST, requestEntity,
					DimensionApiResponse.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				log.error(
						"Unable to create dimension : {}. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
						dimension.getName(), response.getStatusCode(), response.toString());
				throw new BcephalException("Unable to create dimension : {}.", dimension.getName());
			}
			DimensionApiResponse result = response.getBody();
			log.debug("Dimension created : {}", result);
			return result;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unable to create dimension : {}", dimension.getName(), e);
			throw new BcephalException("Unable to create dimension : {}", dimension.getName());
		}
	}
	
	public Measure createMeasure(DimensionApi measureApi, Locale locale) {
		log.debug("Try to  create measure : {}", measureApi);
		try {
			if (measureApi == null) {
				String message = messageSource.getMessage("unable.to.save.null.measure", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(measureApi.getName())) {
				String message = messageSource.getMessage("unable.to.create.measure.without.name", new Object[] { "" },
						locale);
				throw new BcephalException(message);
			}
			Measure measure = measureRepository.findByName(measureApi.getName());
			if (measure != null) {
				String message = messageSource.getMessage("duplicate.measure.name",
						new Object[] { measureApi.getName() }, locale);
				throw new BcephalException(message);
			}
			measure = new Measure();
			measure.setName(measureApi.getName());
			Measure parent = null;
			if (measureApi.getParent() != null) {
				Optional<Measure> result = measureRepository.findById(measureApi.getParent());
				if (result.isEmpty()) {
//					String message = messageSource.getMessage("measure.not.found",
//							new Object[] { measureApi.getParent() }, locale);
//					throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
					measure.setPosition(measureRepository.findByParentIsNull().size());
				}
				else {
					parent = result.get();
					measure.setParent(parent);
					measure.setPosition(parent.getChildren().size());
				}
			} else {
				measure.setPosition(measureRepository.findByParentIsNull().size());
			}

			boolean isNewDimension = measure.getId() == null;
			measure = measureRepository.save(measure);
			if(parent != null) {
				parent.getChildren().add(measure);
			}
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(measure.getUniverseTableColumnName(), measure.getUniverseTableColumnType());
			}

			log.debug("Measure successfully to created : {} ", measure.getId());
			return measure;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating measure : {}", measureApi, e);
			String message = messageSource.getMessage("unable.to.create.measure", new Object[] { measureApi }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	public Period createPeriod(DimensionApi periodApi, Locale locale) {
		log.debug("Try to  create period : {}", periodApi);
		try {
			if (periodApi == null) {
				String message = messageSource.getMessage("unable.to.save.null.period", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(periodApi.getName())) {
				String message = messageSource.getMessage("unable.to.create.period.without.name", new Object[] { "" },
						locale);
				throw new BcephalException(message);
			}
			Period period = periodRepository.findByName(periodApi.getName());
			if (period != null) {
				String message = messageSource.getMessage("duplicate.period.name", new Object[] { periodApi.getName() },
						locale);
				throw new BcephalException(message);
			}
			period = new Period();
			period.setName(periodApi.getName());
			Period parent = null;
			if (periodApi.getParent() != null) {
				Optional<Period> result = periodRepository.findById(periodApi.getParent());
				if (result.isEmpty()) {
//					String message = messageSource.getMessage("period.not.found",
//							new Object[] { periodApi.getParent() }, locale);
//					throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
					period.setPosition(periodRepository.findByParentIsNull().size());
				}
				else {
					parent = result.get();
					period.setParent(parent);
					period.setPosition(parent.getChildren().size());
				}
			} else {
				period.setPosition(periodRepository.findByParentIsNull().size());
			}			
			boolean isNewDimension = period.getId() == null;
			period = periodRepository.save(period);
			if(parent != null) {
				parent.getChildren().add(period);
			}
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(period.getUniverseTableColumnName(), period.getUniverseTableColumnType());
			}

			log.debug("Period successfully to created : {} ", period.getId());
			return period;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating measure : {}", periodApi, e);
			String message = messageSource.getMessage("unable.to.create.measure", new Object[] { periodApi }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public Attribute createAttribute(DimensionApi attributeApi, Locale locale) {
		log.debug("Try to  create attribute : {}", attributeApi);
		try {
			if (attributeApi == null) {
				String message = messageSource.getMessage("unable.to.save.null.attribute", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(attributeApi.getName())) {
				String message = messageSource.getMessage("unable.to.create.attribute.without.name",
						new Object[] { "" }, locale);
				throw new BcephalException(message);
			}

			Attribute attribute = attributeRepository.findByName(attributeApi.getName());
			if (attribute != null) {
				String message = messageSource.getMessage("duplicate.attribute.name",
						new Object[] { attributeApi.getName() }, locale);
				throw new BcephalException(message);
			}
			attribute = new Attribute();
			attribute.setName(attributeApi.getName());
			Attribute parent = null;
			if (attributeApi.getParent() != null) {
				Optional<Attribute> result = attributeRepository.findById(attributeApi.getParent());
				if (result.isEmpty()) {
					String message = messageSource.getMessage("attribute.not.found",
							new Object[] { attributeApi.getParent() }, locale);
					throw new BcephalException(message);
				}
				parent = result.get();
				attribute.setParent(parent);
				attribute.setPosition(parent.getChildrenListChangeHandler().getItems().size());
				attribute.setEntity(parent.getEntity());
			} else {
				if (attributeApi.getEntity() == null) {
					String message = messageSource.getMessage("unable.to.create.attribute.without.entity",
							new Object[] { "" }, locale);
					throw new BcephalException(message);
				}
				Optional<Entity> result = entityRepository.findById(attributeApi.getEntity());
				if (result.isEmpty()) {
					String message = messageSource.getMessage("entity.not.found",
							new Object[] { attributeApi.getEntity() }, locale);
					throw new BcephalException(message);
				}
				Entity entity = result.get();
				attribute.setEntity(entity);
				attribute.setPosition(entity.getAttributes().size());
			}

			boolean isNewDimension = attribute.getId() == null;
			attribute = attributeRepository.save(attribute);
			if (isNewDimension) {
				universeGenerator.createUniverseColumn(attribute.getUniverseTableColumnName(), attribute.getUniverseTableColumnType());
			}

			log.debug("Attribute successfully to created : {} ", attribute.getId());
			return attribute;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating attribute : {}", attributeApi, e);
			String message = messageSource.getMessage("unable.to.create.attribute", new Object[] { attributeApi },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public List<Model> getModels(HttpSession session, Locale locale) throws Exception {
//		log.debug("Try to  get all models.");
//		try {
//			String path = "/initiation/api/models";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<List<Model>> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					new ParameterizedTypeReference<List<Model>>() {
//					});
//
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to retrieve list of models. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to retrieve list of models.");
//			}
//			List<Model> result = response.getBody();
//			log.debug("Model count : {}", result.size());
//			log.trace("Models : {}", result);
//			return result;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to retrieve list of models", e);
//			throw new BcephalException("Unable to retrieve list of models");
//		}
		return modelRepository.findAll();
	}

	public List<Measure> getMeasures(HttpSession session, Locale locale) throws Exception {
		log.debug("Try to  get all root measures.");
//		try {
//			String path = "/initiation/api/measures";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<List<Measure>> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					new ParameterizedTypeReference<List<Measure>>() {
//					});
//
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to retrieve list of measures. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to retrieve list of measures.");
//			}
//			List<Measure> result = response.getBody();
//			log.debug("Measure count : {}", result.size());
//			log.trace("Measures : {}", result);
//			return result;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to retrieve list of measures", e);
//			throw new BcephalException("Unable to retrieve list of measures");
//		}
		return measureRepository.findByParentIsNull();
	}
	
	public List<Nameable> getCalculatedMeasures(DataSourceType dataSourceType, Long datatSourceId, HttpSession session, Locale locale) throws Exception {
		log.debug("Try to  get calculated measures.");
		
		return dataSourceType != null ? datatSourceId != null ? calculatedMeasureRepository.findAllByDataSourceAsNameable(dataSourceType, datatSourceId) 
				: calculatedMeasureRepository.findAllByDataSourceAsNameable(dataSourceType)
				: calculatedMeasureRepository.findGenericAllAsNameables();
	}

	public List<Period> getPeriods(HttpSession session, Locale locale) throws Exception {
		log.debug("Try to  get all root periods.");
//		try {
//			String path = "/initiation/api/periods";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<List<Period>> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					new ParameterizedTypeReference<List<Period>>() {
//					});
//
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to retrieve list of periods. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to retrieve list of periods.");
//			}
//			List<Period> result = response.getBody();
//			log.debug("Period count : {}", result.size());
//			log.trace("Periods : {}", result);
//			return result;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to retrieve list of periods", e);
//			throw new BcephalException("Unable to retrieve list of periods");
//		}
		return periodRepository.findByParentIsNull();
	}

	public List<Nameable> getCalendarsAsNameable(HttpSession session, Locale locale) throws Exception {
		log.debug("Try to  get all calendars.");
//		try {
//			String path = "/initiation/api/calendars";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);	
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<List<Nameable>> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					new ParameterizedTypeReference<List<Nameable>>() {
//					});
//
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to retrieve list of calendars. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to retrieve list of calendars.");
//			}
//			List<Nameable> result = response.getBody();
//			log.debug("Calendar count : {}", result.size());
//			log.trace("Calendars : {}", result);
//			return result;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to retrieve list of calendars", e);
//			throw new BcephalException("Unable to retrieve list of calendars");
//		}
		return calendarRepository.findAllAsNameables();
	}

	public List<Nameable> getSpotsAsNameable(HttpSession session, Locale locale) throws Exception {
		log.debug("Try to  get all spots.");
//		try {
//			String path = "/initiation/api/spots";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<List<Nameable>> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					new ParameterizedTypeReference<List<Nameable>>() {});
//
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to retrieve list of spots. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to retrieve list of spots.");
//			}
//			List<Nameable> result = response.getBody();
//			log.debug("Spot count : {}", result.size());
//			log.trace("Spots : {}", result);
//			return result;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to retrieve list of spots", e);
//			throw new BcephalException("Unable to retrieve list of spots");
//		}
	return spotRepository.findAllAsNameables();
	}

 private HttpHeaders getHttpHeaders(HttpSession session, Locale locale){
		HttpHeaders requestHeaders = new HttpHeaders();
		if(session instanceof StandardSessionFacade) {
			requestHeaders = (HttpHeaders) session.getAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME);
			if(requestHeaders == null) {
				requestHeaders = new HttpHeaders();
			}
		}
		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());
		return requestHeaders;
	}
	
	public Dimension getDimension(DimensionType type, String name, Boolean ignoreCase, HttpSession session, Locale locale) {
//		log.debug("Call of getDimension");
//		try {
//			String path = "/initiation/api/get-dimension-by-name";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);
//			requestHeaders.set("Dimension-Type", type.name());
//			requestHeaders.set("Dimension-Name", name);
//			requestHeaders.set("Ignore-Case", ignoreCase.toString());		
//			
//			log.info("Dimension request requestHeaders : {}", requestHeaders);
//			
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<DimensionApiResponse> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					DimensionApiResponse.class);
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to read {} : {}. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						type.name(), name, response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to read {} : {}.", type.name(), name);
//			}
//			DimensionApiResponse result = response.getBody();			
//			log.debug("Dimension readed : {}", result);
//			return result != null ? result.AsDimension(type) : null;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to read {} : {}", type.name(), name, e);
//			throw new BcephalException("Unable to read {} : {} : {}", type.name(), name);
//		}	
		
		Dimension dimension = null;
		if(type.isMeasure()) {
			dimension =  ignoreCase ? measureRepository.findByNameIgnoreCase(name) : measureRepository.findByName(name);
		}
		if(type.isPeriod()) {
			dimension =  ignoreCase ? periodRepository.findByNameIgnoreCase(name) : periodRepository.findByName(name);
		}
		if(type.isAttribute()) {
			dimension =  ignoreCase ? attributeRepository.findByNameIgnoreCase(name) : attributeRepository.findByName(name);
		}
		return dimension;
	}

	public Dimension getDimension(DimensionType type, Long id, HttpSession session, Locale locale) {
//		log.debug("Call of getDimension");
//		try {
//			String path = "/initiation/api/get-dimension-by-id";
//			HttpHeaders requestHeaders = getHttpHeaders(session, locale);
//			requestHeaders.set("Dimension-Type", type.name());
//			requestHeaders.set("Dimension-Id", id.toString());						
//			HttpEntity<DimensionApi> requestEntity = new HttpEntity<>(requestHeaders);
//			ResponseEntity<DimensionApiResponse> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
//					DimensionApiResponse.class);
//			if (response.getStatusCode() != HttpStatus.OK) {
//				log.error(
//						"Unable to read {} : {}. Initiation API call faild with :\n- Http status : {}\n- Message : {}",
//						type.name(), id, response.getStatusCode(), response.toString());
//				throw new BcephalException("Unable to read {} : {}.", type.name(), id);
//			}
//			DimensionApiResponse result = response.getBody();			
//			log.debug("Dimension readed : {}", result);
//			return result != null ? result.AsDimension(type) : null;
//		} catch (BcephalException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Unable to read {} : {}", type.name(), id, e);
//			throw new BcephalException("Unable to read {} : {} : {}", type.name(), id);
//		}	
		
		Dimension dimension = null;
		if(type.isMeasure()) {
			Optional<Measure> response =  measureRepository.findById(id);
			if(!response.isEmpty()) {
				dimension = response.get();
			}
		}
		if(type.isPeriod()) {
			Optional<Period> response =  periodRepository.findById(id);
			if(!response.isEmpty()) {
				dimension = response.get();
			}
		}
		if(type.isAttribute()) {
			Optional<Attribute> response =  attributeRepository.findById(id);
			if(!response.isEmpty()) {
				dimension = response.get();
			}
		}
		return dimension;
	}
	
	
	public Entity getEntity(String name, HttpSession session, Locale locale) {	
		List<Entity> entities = entityRepository.findByName(name);
		return entities.size() > 0 ? entities.get(0) : null;
	}
	
	
	
	public Dimension getOrBuildDimension(DimensionType dimensionType, String dimensionName, String modelName, String parentName, HttpSession session, Locale locale) throws Exception {
		if(dimensionType.isMeasure()) {
			return getOrBuildMeasure(dimensionName, parentName, session, locale);
		}
		else if(dimensionType.isPeriod()) {
			return getOrBuildPeriod(dimensionName, parentName, session, locale);
		}
		else {
			return getOrBuildAttribute(dimensionName, modelName, parentName, session, locale);
		}
	}
	
	
	protected Measure getOrBuildMeasure(String name, String parentName, HttpSession session, Locale locale) throws Exception {
		Measure measure = getMeasureRepository().findByNameIgnoreCase(name);
		if (measure == null) {		
			Measure parent = StringUtils.hasText(parentName) ? getOrBuildMeasure(parentName, null, session, locale) : null;
			measure = (Measure)createDimension(DimensionType.MEASURE, name, (parent != null ? parent.getId() : null), session, locale);
		}
		return measure;
	}

	protected Period getOrBuildPeriod(String name, String parentName, HttpSession session, Locale locale) throws Exception {
		Period period = getPeriodRepository().findByNameIgnoreCase(name);
		if (period == null) {
			Period parent = StringUtils.hasText(parentName) ? getOrBuildPeriod(parentName, null, session, locale) : null;
			period = (Period)createDimension(DimensionType.PERIOD, name, (parent != null ? parent.getId() : null), session, locale);
		}
		return period;
	}

	protected Model getOrBuildModel(String name, HttpSession session, Locale locale) throws Exception {
		Model model = getModelRepository().findByNameIgnoreCase(name);
		if (model == null) {
			Long id = createModel(name, session, locale);
			Optional<Model> response = getModelRepository().findById(id);
			model = response.isPresent() ? response.get() : null;
		}
		return model;
	}

	protected Entity getOrBuildEntity(String name, String modelName, HttpSession session, Locale locale) throws Exception {
		Entity entity = getEntityRepository().findByNameIgnoreCase(name);
		if (entity == null) {
			Model model = StringUtils.hasText(modelName) ? getOrBuildModel(modelName, session, locale) : null;
			Long id = createEntity(name, (model != null ? model.getId() : null), session, locale);
			Optional<Entity> response = getEntityRepository().findById(id);
			entity = response.isPresent() ? response.get() : null;
		}
		return entity;
	}

	protected Attribute getOrBuildAttribute(String name, String modelName, String entityName, HttpSession session, Locale locale) throws Exception {
		Attribute attribute = getAttributeRepository().findByNameIgnoreCase(name);
		if (attribute == null) {	
			Entity entity = StringUtils.hasText(entityName) ? getOrBuildEntity(entityName, modelName, session, locale) : null;
			attribute = (Attribute)createDimension(DimensionType.ATTRIBUTE, name, (entity != null ? entity.getId() : null), session, locale);
		}
		return attribute;
	}
	
	
}
