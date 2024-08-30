/**
 * 
 */
package com.moriset.bcephal.settings.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.settings.service.ParameterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

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
	
	@Autowired
	ObjectMapper mapper;

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,@RequestHeader HttpHeaders headers) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			
			EditorData<Parameter> data = parameterService.getEditorData(filter, session, locale);
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

	@PostMapping("/build-automatically")
	public ResponseEntity<?> buildAutomatically(@RequestBody String code,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,@RequestHeader HttpHeaders headers) {
		log.debug("Call of buildAutomatically with code : {}", code);
		try {
			if(StringUtils.hasText(code) && code.contains("\"")) {
				code = mapper.readValue(code, String.class);
			}
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			EditorData<Parameter> data = parameterService.buildAutomatically(code, session, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while builf configuration : {}", code, e);
			String message = messageSource.getMessage("unable.to.build.configuration", new Object[] { code }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> save(@RequestBody Parameter parameter,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, HttpSession session,@RequestHeader HttpHeaders headers) {
		log.trace("Call Save parameters");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);			
			parameter = parameterService.saveRoot(parameter, locale);
			EditorData<Parameter> data = parameterService.getEditorData(null, session, locale);
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving parameters", e);
			String message = messageSource.getMessage("unable.to.save.parameters", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
