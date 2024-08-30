package com.moriset.bcephal.etl.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.moriset.bcephal.etl.domain.BcephalException;
import com.moriset.bcephal.etl.domain.JobDataExecution;
import com.moriset.bcephal.etl.service.JobDataExecutionService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("sourcing-etl/job")
@Slf4j
public class JobDataExecutionController {
	
	

	@Autowired
	private JobDataExecutionService service;
	
	
	@Autowired
	MessageSource messageSource;
	
//	@PostMapping("/search")
//	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
//			@RequestHeader("Accept-Language") java.util.Locale locale,
//			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
//			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
//			JwtAuthenticationToken principal, HttpSession session) {
//
//		log.debug("Call of search");
//		try {
//			filter.setUsername(principal.getName());
//			BrowserDataPage<JobDataExecution> page = service.search(filter, locale, profileId, projectCode);
//			log.debug("Found : {}", page.getCurrentPage());
//			return ResponseEntity.status(HttpStatus.OK).body(page);
//		} catch (BcephalException e) {
//			log.debug(e.getMessage());
//			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//		} catch (Exception e) {
//			log.error("Unexpected error while search entities by filter : {}", filter, e);
//			String message = messageSource.getMessage("unable.to.search.entity.by.filter", new Object[] { filter },
//					locale);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
//		}
//	}
	
	@GetMapping("/first-executions/{count}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getFirstExecutions(@PathVariable("count") int count) {
		log.debug("Call of get execution : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executions = service.getFirstExecutions(count);
			return ResponseEntity.status(HttpStatus.OK).body(executions); 
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
				
	}
	
	@GetMapping("/executions")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getAllExecutions() {
		log.debug("Call of get execution : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executions = service.getAllExecutions();
			return ResponseEntity.status(HttpStatus.OK).body(executions); 
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
				
	}
	
	
	@GetMapping("/execution/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getExecutionsById(@PathVariable("id") long id) {
		log.debug("Call of get execution by id : {}");
		try {
			log.trace("Found : {}", "");
			Optional<JobDataExecution> executionId = service.getExecutionsById(id);
			return ResponseEntity.status(HttpStatus.OK).body(executionId);
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
				
	}
	
	
	
	@GetMapping("/instanceid/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?>  getExecutionsByInstanceId(@PathVariable("id") long id) {	
		log.debug("Call of get execution by instanceId : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executionsWithInstanceId = service.getExecutionsByInstanceId(id);
			return ResponseEntity.status(HttpStatus.OK).body(executionsWithInstanceId);
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
		
	}
	
	
	
	@GetMapping(value ="/executionversion/{version}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?>  getExecutionsByVersion(@PathVariable("version") long version) {
		log.debug("Call of get execution by version : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executionsVersion = service.getExecutionsByVersion(version);
			return ResponseEntity.status(HttpStatus.OK).body(executionsVersion);
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
		 		
	}
	
	@GetMapping(value ="/executionstatus/{status}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?>  getExecutionsByStatus(@PathVariable("status") String status) {
		log.debug("Call of get execution by status : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executionStatus = service.getExecutionsByStatus(status);
			return ResponseEntity.status(HttpStatus.OK).body(executionStatus); 
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
				
	}
	
	@GetMapping(value ="/executionscode/{code}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?>  getExecutionsByExitCode(@PathVariable("code") String code) {
		log.debug("Call of get execution by Exit code : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executionsCode = service.getExecutionsByExitCode(code);
			return ResponseEntity.status(HttpStatus.OK).body(executionsCode); 
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
		 		
	}
	
	
	@GetMapping("/executionsswithcreationdate")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?>  getExecutionsByCreationDate(@PathVariable Date creationDate) {
		log.debug("Call of get execution by creation date : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataExecution> executions = service.getExecutionsByCreationDate(creationDate);
			return ResponseEntity.status(HttpStatus.OK).body(executions); 
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
				
	}
	
	@PostMapping("/execution/save")
	public ResponseEntity<?> saveExecution(@RequestBody JobDataExecution job){
		
		log.debug("Call of Save executions : {}", job);
		
		try {
			
			log.trace("save : {}",job);
			JobDataExecution jobExecution= service.saveDataExecution(job);
			return ResponseEntity.status(HttpStatus.OK).body(jobExecution);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		
	}

//	@Override
//	protected MainObjectService<JobDataExecution, BrowserData> getService() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
	
//	@GetMapping("/executionswithstartdate")
//	@ResponseStatus(HttpStatus.OK)
//	public ResponseEntity<?>  getExecutionsByStartDate() {	
//		List<JobDataExecution> executions = service.getExecutionsByStartDate(@RequestParam Date startDate);
//		return ResponseEntity.status(HttpStatus.OK).body(executions);		
//	}
	
//	@GetMapping("/executions/{endDate}")
//	@ResponseStatus(HttpStatus.OK)
//	public ResponseEntity<?>  getExecutionsByEndDate() {	
//		List<JobDataExecution> executions = service.;
//		return ResponseEntity.status(HttpStatus.OK).body(executions);		
//	}
	
//	@GetMapping("/executions/{modificationDate}")
//	@ResponseStatus(HttpStatus.OK)
//	public ResponseEntity<?> findByModificationDate() {	
//		List<JobDataExecution> executions = service.;
//		return ResponseEntity.status(HttpStatus.OK).body(executions);		
//	}
	
}