package com.moriset.bcephal.messenger.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.messenger.model.AlarmMessageStatus;
import com.moriset.bcephal.messenger.model.BrowserDataFilter;
import com.moriset.bcephal.messenger.model.BrowserDataPage;
import com.moriset.bcephal.messenger.services.AlarmMessageLogService;
import com.moriset.bcephal.messenger.services.ParameterService;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/messenger/logs")
@Slf4j
public class AlarmMessageLogController {

	@Autowired
	protected AlarmMessageLogService alarmMessageLogServices;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	ParameterService parameterService;
	
	@PostMapping("/search")
	public ResponseEntity<?> search(
			@RequestBody BrowserDataFilter filter,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode) {

		log.debug("Call of search");
		try {
			BrowserDataPage<?> page = filter.getStatus() == AlarmMessageStatus.FAIL 
					? alarmMessageLogServices.searchFail(filter, projectCode, locale)
					: (filter.getStatus() == AlarmMessageStatus.SENDED || filter.getStatus() == AlarmMessageStatus.SENT)
					? alarmMessageLogServices.searchSuccess(filter, projectCode, locale)
					: alarmMessageLogServices.searchTosend(filter, projectCode, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search scheduler by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.scheduler.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of delete : {}", oids);
		try {
			alarmMessageLogServices.delete(oids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleteing : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/cancel-items")
	public ResponseEntity<?> cancel(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			Principal principal) {
		log.debug("Call of cancel : {}", oids);
		try {
			String username = principal != null ? principal.getName() : "";
			alarmMessageLogServices.cancel(oids, username, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while canceling : {}", oids, e);
			String message = messageSource.getMessage("unable.to.cancel", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reset-items")
	public ResponseEntity<?> reset(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of reset : {}", oids);
		try {
			alarmMessageLogServices.reset(oids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while reseting : {}", oids, e);
			String message = messageSource.getMessage("unable.to.reset", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/send-items")
	public ResponseEntity<?> send(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of reset : {}", oids);
		try {
			//alarmMessageLogServices.send(oids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while sending : {}", oids, e);
			String message = messageSource.getMessage("unable.to.reset", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/is-bypass-whitelist")
	public ResponseEntity<?> isBypassWhiteList(@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of isBypassWhiteList : {}");
		try {
			boolean allowed = parameterService.isBypassWhiteList();
			return ResponseEntity.status(HttpStatus.OK).body(allowed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while reading : {}", "", e);
			String message = messageSource.getMessage("unable.to.read", new Object[] {  }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/set-bypass-whitelist")
	public ResponseEntity<?> setBypassWhiteList(@RequestBody boolean allowed,	@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of setBypassWhiteList : {}", allowed);
		try {
		    allowed = parameterService.setBypassWhiteList(allowed, locale);
			return ResponseEntity.status(HttpStatus.OK).body(allowed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while setting : {}", allowed, e);
			String message = messageSource.getMessage("unable.to.set", new Object[] { allowed }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}



}
