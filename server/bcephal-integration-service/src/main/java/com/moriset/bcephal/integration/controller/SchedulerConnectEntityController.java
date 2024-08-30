/**
 * 
 */
package com.moriset.bcephal.integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.integration.scheduler.SchedulerConnectEntityBrowserData;
import com.moriset.bcephal.integration.scheduler.SchedulerConnectEntityBrowserDataFilter;
import com.moriset.bcephal.integration.scheduler.SchedulerManager;
import com.moriset.bcephal.integration.scheduler.SchedulerRequest;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@RestController
@RequestMapping("/integration/scheduler")
@Slf4j
public class SchedulerConnectEntityController {

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected SchedulerManager manager;
	
	@PostMapping("/search")
	public ResponseEntity<?> search(
			@RequestBody SchedulerConnectEntityBrowserDataFilter filter,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<SchedulerConnectEntityBrowserData> page = manager.search(filter, locale);
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
	
	
	@PostMapping("/start")
	public ResponseEntity<?> start( 
			@RequestBody SchedulerRequest filter,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of start : {} ", filter);
		try {
			boolean response = manager.start(filter);
			log.debug("Response : {}", response);				
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while starting scheduler for : {}", filter, e);
			String message = messageSource.getMessage("unable.to.start.scheduler", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/restart")
	public ResponseEntity<?> restart( 
			@RequestBody SchedulerRequest filter, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of restart : {} ", filter);
		try {
			boolean response = manager.restart(filter);
			log.debug("Response : {}", response);				
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while restarting scheduler for : {}", filter, e);
			String message = messageSource.getMessage("unable.to.restart.scheduler", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/stop")
	public ResponseEntity<?> stop( 
			@RequestBody SchedulerRequest filter,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of stop : {}", filter);
		try {
			boolean response = manager.stop(filter);
			log.debug("Response : {}", response);				
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while stoping scheduler for : {}", filter, e);
			String message = messageSource.getMessage("unable.to.stop.scheduler", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
}
