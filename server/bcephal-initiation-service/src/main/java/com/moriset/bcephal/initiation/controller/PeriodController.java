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
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.initiation.domain.PeriodName;
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
@RequestMapping("/initiation/period")
public class PeriodController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PeriodNameService periodNameService;

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			EditorData<PeriodName> data = periodNameService.getEditorData(filter, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving period editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> savePeriod(@RequestBody PeriodName periodName,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call savePeriod : {}", periodName);
		try {
			periodName = periodNameService.saveRoot(periodName, locale);
			periodName.setChildren(periodNameService.getPeriodNames(locale));
			return ResponseEntity.status(HttpStatus.OK).body(periodName);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving period : {}", periodName, e);
			String message = messageSource.getMessage("unable.to.save.period", new Object[] { periodName }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/can-delete/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> canDeleteDimension(
			@PathVariable("id") Long id,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Try to check if period is deleteable...");
		try {			
			List<String> result = periodNameService.getDimensionDatasources(new Period(id).getUniverseTableColumnName());
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error when checking if period is deleteable.", e);
			String message = messageSource.getMessage("unable.to.check.if.period.is.deletable", new Object[] {id}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	

}
