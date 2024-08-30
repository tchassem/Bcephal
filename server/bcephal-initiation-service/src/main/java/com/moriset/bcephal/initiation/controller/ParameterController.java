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
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.initiation.service.ParameterService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@Slf4j
@RequestMapping("/settings/configuration")
public class ParameterController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ParameterService parameterService;

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			EditorData<Parameter> data = parameterService.getEditorData(filter, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
