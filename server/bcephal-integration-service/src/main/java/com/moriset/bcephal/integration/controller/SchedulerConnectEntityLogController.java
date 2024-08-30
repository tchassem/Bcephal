/**
 * 
 */
package com.moriset.bcephal.integration.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.integration.scheduler.SchedulerConnectEntityBrowserDataFilter;
import com.moriset.bcephal.integration.scheduler.SchedulerConnectEntityLogBrowserData;
import com.moriset.bcephal.integration.service.SchedulerConnectEntityLogService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@RestController
@RequestMapping("/integration/logs")
@Slf4j
public class SchedulerConnectEntityLogController {
	
	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected ObjectMapper mapper;
	
	@Autowired
	protected SchedulerConnectEntityLogService service;

	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of delete : {}", ids);
		try {
			service.delete(ids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting logs : {}", ids, e);
			String message = messageSource.getMessage("unable.to.delete.logs", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody SchedulerConnectEntityBrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<SchedulerConnectEntityLogBrowserData> page = service.search(filter, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search logs by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.log.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}
