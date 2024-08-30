package com.moriset.bcephal.scheduler.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogBrowserData;
import com.moriset.bcephal.scheduler.service.SchedulerPlannerLogService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/scheduler-planner/log")
@Slf4j
public class SchedulerPlannerLogController{
	
	@Autowired
	SchedulerPlannerLogService schedulerPlannerLogService;

	@Autowired
	protected MessageSource messageSource;
	
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody SchedulerBrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<SchedulerPlannerLogBrowserData> page = schedulerPlannerLogService.search(filter, locale);
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
		     Map<Long, String> map = new LinkedHashMap<Long, String>();
			if(oids != null) {
				oids.forEach(result ->{
					map.put(result, schedulerPlannerLogService.getBrowserFunctionalityCode());
				});
			}
			schedulerPlannerLogService.delete(oids, locale);
			String rightLevel = "DELETE";
			if(oids != null) {
				map.forEach((result, functionalityCode) ->{
					schedulerPlannerLogService.saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), result, functionalityCode, rightLevel,profileId);
			});
			}			
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
}
