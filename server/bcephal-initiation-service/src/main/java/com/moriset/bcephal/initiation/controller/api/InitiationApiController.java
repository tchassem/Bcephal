/**
 * 
 */
package com.moriset.bcephal.initiation.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.CalendarCategory;
import com.moriset.bcephal.initiation.domain.Dimension;
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Measure;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.domain.PeriodName;
import com.moriset.bcephal.initiation.domain.api.AttributeApi;
import com.moriset.bcephal.initiation.domain.api.MeasureApi;
import com.moriset.bcephal.initiation.domain.api.PeriodApi;
import com.moriset.bcephal.initiation.service.AttributeService;
import com.moriset.bcephal.initiation.service.CalendarService;
import com.moriset.bcephal.initiation.service.EntityService;
import com.moriset.bcephal.initiation.service.MeasureService;
import com.moriset.bcephal.initiation.service.ModelService;
import com.moriset.bcephal.initiation.service.PeriodNameService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@RestController
@Slf4j
@RequestMapping("/initiation/api")
public class InitiationApiController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	MeasureService measureService;

	@Autowired
	PeriodNameService periodNameService;

	@Autowired
	AttributeService attributeService;

	@Autowired
	CalendarService calendarService;

	@Autowired
	EntityService entityService;

	@Autowired
	ModelService modelService;

	@PostMapping("/create-measure")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> createMeasure(@RequestBody MeasureApi measureApi,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Try to create measure : {}", measureApi);
		try {
			Measure measure = measureService.createMeasure(measureApi, locale);
			return ResponseEntity.status(HttpStatus.OK).body(measure);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating measure : {}", measureApi, e);
			String message = messageSource.getMessage("unable.to.create.measure", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/create-period")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> createPeriod(@RequestBody PeriodApi periodApi,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Try to create period : {}", periodApi);
		try {
			PeriodName period = periodNameService.createPeriod(periodApi, locale);
			return ResponseEntity.status(HttpStatus.OK).body(period);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating period : {}", periodApi, e);
			String message = messageSource.getMessage("unable.to.create.period", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/create-attribute")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> createAttribute(@RequestBody AttributeApi attributeApi,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Try to create attribute : {}", attributeApi);
		try {
			Attribute attribute = attributeService.createAttribute(attributeApi, locale);
			return ResponseEntity.status(HttpStatus.OK).body(attribute);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating attribute : {}", attributeApi, e);
			String message = messageSource.getMessage("unable.to.create.attribute", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/create-entity/{modelId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> createEntity(@RequestBody String name, @PathVariable("modelId") Long modelId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Try to create entity : {}", name);
		try {
			Entity entity = entityService.createEntity(name, modelId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(entity != null ? entity.getId() : null);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating entity : {}", name, e);
			String message = messageSource.getMessage("unable.to.create.entity", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/create-model")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> createModel(@RequestBody String name,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Try to create model : {}", name);
		try {
			Model model = modelService.createModel(name, locale);
			return ResponseEntity.status(HttpStatus.OK).body(model != null ? model.getId() : null);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating model : {}", name, e);
			String message = messageSource.getMessage("unable.to.create.model", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/models")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getModels(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read all models");
		try {
			List<Model> models = modelService.getModels(locale);
			return ResponseEntity.status(HttpStatus.OK).body(models);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read models.", e);
			String message = messageSource.getMessage("unable.to.read.models", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/measures")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getRootMeasures(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read root measures");
		try {
			List<Measure> measures = measureService.getMeasures(locale);
			return ResponseEntity.status(HttpStatus.OK).body(measures);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read measures.", e);
			String message = messageSource.getMessage("unable.to.read.measures", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/periods")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getRootPeriods(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read root periods");
		try {
			List<PeriodName> periods = periodNameService.getPeriodNames(locale);
			return ResponseEntity.status(HttpStatus.OK).body(periods);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read periods.", e);
			String message = messageSource.getMessage("unable.to.read.periods", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/spots")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getRootSpots(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read root periods");
		try {
			List<PeriodName> periods = periodNameService.getPeriodNames(locale);
			return ResponseEntity.status(HttpStatus.OK).body(periods);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read periods.", e);
			String message = messageSource.getMessage("unable.to.read.periods", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/calendars")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCalendars(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read all calendars");
		try {
			List<CalendarCategory> calendars = calendarService.getCalendars(locale);
			return ResponseEntity.status(HttpStatus.OK).body(calendars);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read calendars.", e);
			String message = messageSource.getMessage("unable.to.read.calendars", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@GetMapping("/get-dimension-by-name")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getDimension(
			@RequestHeader("Dimension-Type") DimensionType type,
			@RequestHeader("Dimension-Name") String name,
			@RequestHeader("Ignore-Case") Boolean ignoreCase,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read dimension");
		try {			
			Dimension dimension = null;
			if(type.isMeasure()) {
				dimension =  ignoreCase ? measureService.getByNameIgnoreCase(name) : measureService.getByName(name);
			}
			if(type.isPeriod()) {
				dimension =  ignoreCase ? periodNameService.getByNameIgnoreCase(name) : periodNameService.getByName(name);
			}
			if(type.isAttribute()) {
				dimension =  ignoreCase ? attributeService.getByNameIgnoreCase(name) : attributeService.getByName(name);
			}			
			return ResponseEntity.status(HttpStatus.OK).body(dimension);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read dimension.", e);
			String message = messageSource.getMessage("unable.to.read.dimension", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/get-dimension-by-id")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getDimension(
			@RequestHeader("Dimension-Type") DimensionType type,
			@RequestHeader("Dimension-Id") Long id,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read dimension");
		try {			
			Dimension dimension = null;
			if(type.isMeasure()) {
				dimension =  measureService.getById(id);
			}
			if(type.isPeriod()) {
				dimension =  periodNameService.getById(id);
			}
			if(type.isAttribute()) {
				dimension =  attributeService.getById(id);
			}			
			return ResponseEntity.status(HttpStatus.OK).body(dimension);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error read dimension.", e);
			String message = messageSource.getMessage("unable.to.read.dimension", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
