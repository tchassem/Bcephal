/**
 * 
 */
package com.moriset.bcephal.initiation.controller;

import java.math.BigDecimal;
import java.util.Date;
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

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.domain.ModelEditorData;
import com.moriset.bcephal.initiation.service.AttributeService;
import com.moriset.bcephal.initiation.service.MeasureService;
import com.moriset.bcephal.initiation.service.ModelService;
import com.moriset.bcephal.initiation.service.PeriodNameService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@Slf4j
@RequestMapping("/initiation/model")
public class ModelController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ModelService modelService;

	@Autowired
	AttributeService attributeService;

	@Autowired
	MeasureService measureService;

	@Autowired
	PeriodNameService periodService;

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			ModelEditorData data = modelService.getEditorData(filter, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving model editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/all-models")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getModels(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call getModels :");
		try {
			List<Model> models = modelService.getModels(locale);
			return ResponseEntity.status(HttpStatus.OK).body(models);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving models.", e);
			String message = messageSource.getMessage("unable.to.retreive.models", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> saveModel(@RequestBody Model model,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call saveModel : {}", model);
		try {
			modelService.save(model, locale);
			model = modelService.getById(model.getId());
			return ResponseEntity.status(HttpStatus.OK).body(model);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving model : {}", model, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { model }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save-all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> saveAllModel(@RequestBody ListChangeHandler<Model> allModels,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call saveAllModel : {}", allModels);
		try {
			modelService.saveAll(allModels, locale);
			List<Model> models = modelService.getModels(locale);
			ListChangeHandler<Model> modelSaveds = new ListChangeHandler<Model>();
			modelSaveds.setOriginalList(models);
			return ResponseEntity.status(HttpStatus.OK).body(modelSaveds);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving models : {}", allModels, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { allModels }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search-attribute-values")
	public ResponseEntity<?> searchAttributeValues(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchAttributeValues");
		try {
			BrowserDataPage<String> page = attributeService.searchAttributeValues(filter, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getting attribute Values...", e);
			String message = messageSource.getMessage("unable.to.load.attribute.values", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search-measure-values")
	public ResponseEntity<?> searchMeasureValues(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchMeasureValues");
		try {
			BrowserDataPage<BigDecimal> page = measureService.searchMeasureValues(filter, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getting measure Values...", e);
			String message = messageSource.getMessage("unable.to.load.measure.values", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search-period-values")
	public ResponseEntity<?> searchPeriodValues(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchPeriodValues");
		try {
			BrowserDataPage<Date> page = periodService.searchPeriodValues(filter, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getting period Values...", e);
			String message = messageSource.getMessage("unable.to.load.period.values", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/can-delete/{type}/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> canDeleteDimension(
			@PathVariable("type") String type,
			@PathVariable("id") Long id,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to read dimension");
		try {			
			boolean result = false;
			if("ATTRIBUTE".equalsIgnoreCase(type)) {
				result = modelService.canDeleteAttribute(id);	
			}
			else if("ENTITY".equalsIgnoreCase(type)) {
				result = modelService.canDeleteEntity(id);	
			}
			else if("MODEL".equalsIgnoreCase(type)) {
				result = modelService.canDeleteModel(id);	
			}
			return ResponseEntity.status(HttpStatus.OK).body(result);
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
