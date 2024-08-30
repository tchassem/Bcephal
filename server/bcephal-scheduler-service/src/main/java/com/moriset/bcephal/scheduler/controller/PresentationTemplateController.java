package com.moriset.bcephal.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.scheduler.domain.PresentationTemplate;
import com.moriset.bcephal.scheduler.domain.PresentationTemplateBrowserData;
import com.moriset.bcephal.scheduler.service.PresentationTemplateService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/scheduler/presentation-template")
public class PresentationTemplateController extends BaseController<PresentationTemplate, PresentationTemplateBrowserData> {

	@Autowired
	PresentationTemplateService presentationTemplateService;
	
	@Override
	protected PresentationTemplateService getService() {
		return presentationTemplateService;
	}
	
	@PostMapping("/save-template")
	public ResponseEntity<?> save(
			@RequestBody PresentationTemplate template, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, 
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of save entity : {}", template);
		try {
			String projectName = headers.getFirst(RequestParams.BC_PROJECT_NAME);
			if(!StringUtils.hasText(projectName)) {
				throw new BcephalException("Undefined project name!");
			}
			getService().save(template, locale, projectName);
			template = getService().getById((Long) template.getId());
			log.debug("entity : {}", template);
			return ResponseEntity.status(HttpStatus.OK).body(template);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", template, e);
			String message = messageSource.getMessage("unable.to.save.entity", new Object[] { template }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}
