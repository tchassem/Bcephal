/**
 * 
 */
package com.moriset.bcephal.initiation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.initiation.service.IncrementalNumberService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@Slf4j
@RequestMapping("/settings/incremental-number")
public class IncrementalNumberController {

	@Autowired
    MessageSource messageSource;
	
	@Autowired
	IncrementalNumberService incrementalNumberService;
	
	
	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			EditorData<IncrementalNumber> data = incrementalNumberService.getEditorData(filter, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving incremental number editor data : {}", filter,  e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> saveIncrementalNumber(@RequestBody IncrementalNumber sequence, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {	
		log.trace("Call saveIncrementalNumber : {}", sequence);		
		try {	
			incrementalNumberService.save(sequence, locale);
	        return ResponseEntity.status(HttpStatus.OK).body(sequence);	
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error while saving incremental number : {}", sequence, e);
			String message = messageSource.getMessage("unable.to.save.incremental.number", new String[]{} , locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
    }
	
}
