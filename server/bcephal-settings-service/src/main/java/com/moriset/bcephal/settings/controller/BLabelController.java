package com.moriset.bcephal.settings.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.BLabel;
import com.moriset.bcephal.domain.BLabels;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.settings.domain.BLabelEditorData;
import com.moriset.bcephal.settings.service.BLabelService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/settings/label")
public class BLabelController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	BLabelService bLabelService;

	protected BLabelService getService() {
		return bLabelService;
	}

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session, @RequestHeader HttpHeaders headers) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			
			BLabelEditorData data = getService().getEditorData(filter, session, locale);
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

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> save(@RequestBody BLabel bLabel,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, HttpSession session, @RequestHeader HttpHeaders headers) {
		log.trace("Call Save parameters");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);			
			bLabel = getService().save(bLabel, locale);
			return ResponseEntity.status(HttpStatus.OK).body(bLabel);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving label", e);
			String message = messageSource.getMessage("unable.to.save.label", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save-list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> saveList(@RequestBody BLabels labels,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, HttpSession session, @RequestHeader HttpHeaders headers) {
		log.trace("Call Save parameters");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);			
			labels = getService().save(labels, locale);
			BLabelEditorData data = getService().getEditorData(new EditorDataFilter(), session, locale);
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving list labels", e);
			String message = messageSource.getMessage("unable.to.save.labels", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> retrieveLabels(@RequestBody String lang,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, HttpSession session, @RequestHeader HttpHeaders headers) {
		log.trace("Call Save parameters");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);			
			ObjectMapper mapper = new ObjectMapper();
		    String curLang = mapper.readValue(lang, String.class);		       
			BLabels labels = getService().getLabels(curLang, locale);
			return ResponseEntity.status(HttpStatus.OK).body(labels);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving list labels", e);
			String message = messageSource.getMessage("unable.to.retrieve.labels", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
