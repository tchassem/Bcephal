package com.moriset.bcephal.planification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.planification.domain.script.ScriptLog;
import com.moriset.bcephal.planification.service.ScriptLogService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/planification/script-log")
@Slf4j
public class ScriptLogController {

	@Autowired
	ScriptLogService logService;

	@Autowired
	protected MessageSource messageSource;

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<ScriptLog> page = logService.search(filter, locale);
			log.debug("Found : {}", page.getItems().size());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search entities by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.entity.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of delete : {}", oids);
		try {	
			logService.delete(oids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/delete-all")
	public ResponseEntity<?> deleteAll(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of delete all ");
		try {
			logService.deleteAll(filter, locale);					
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting", e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}
