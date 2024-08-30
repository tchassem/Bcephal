/**
 * 
 */
package com.moriset.bcephal.initiation.controller;

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

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.initiation.domain.Measure;
import com.moriset.bcephal.initiation.service.MeasureService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@Slf4j
@RequestMapping("/initiation/measure")
public class MeasureController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	MeasureService measureService;

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			EditorData<Measure> data = measureService.getEditorData(filter, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving measure editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> saveMeasure(@RequestBody Measure measure,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call saveMeasure : {}", measure);
		try {
			measureService.saveRoot(measure, locale);
			measure.setChildren(measureService.getMeasures(locale));
			return ResponseEntity.status(HttpStatus.OK).body(measure);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving measure : {}", measure, e);
			String message = messageSource.getMessage("unable.to.save.measure", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/can-delete/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> canDeleteDimension(
			@PathVariable("id") Long id,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to check if measure is deleteable...");
		try {			
			List<String>result =  measureService.getDimensionDatasources(new Measure(id).getUniverseTableColumnName());
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error when checking if measure is deleteable.", e);
			String message = messageSource.getMessage("unable.to.check.if.measure.is.deletable", new Object[] {id}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
