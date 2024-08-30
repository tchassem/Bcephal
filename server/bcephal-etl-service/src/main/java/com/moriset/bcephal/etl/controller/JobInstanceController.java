package com.moriset.bcephal.etl.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.etl.domain.BcephalException;
import com.moriset.bcephal.etl.domain.JobDataInstance;
import com.moriset.bcephal.etl.service.JobDataInstanceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("sourcing-etl/job")
@Slf4j
public class JobInstanceController {
	
	@Autowired
	private JobDataInstanceService instanceService;
	
	@Autowired
	protected MessageSource messageSource;
	
	@GetMapping("/instances")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getAllInstances(){
		
		log.debug("Call of get instances : {}");
		try {
			log.trace("Found : {}", "");
			List<JobDataInstance> listInstances = instanceService.getAllInstances();
			return ResponseEntity.status(HttpStatus.OK).body(listInstances);
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
		
	}
	
	@GetMapping("/instance/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getInstanceById(@PathVariable("id") long id){
		log.debug("Call of get instance by ID : {}", id);
		try {
			log.trace("Found : {}", "");
			 Optional<JobDataInstance> instance = instanceService.getInstanceById(id);
			 return ResponseEntity.status(HttpStatus.OK).body(instance);
		}catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		
		
		
		
	}
	

	@GetMapping("/instancename")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getAllInstancesByName(@RequestParam String name){
		
		log.debug("Call of get instance by Name : {}", name);
		try {
			
			log.trace("Found : {}", "");
			List<JobDataInstance> listInstances = instanceService.getAllInstancesByName(name);
			return ResponseEntity.status(HttpStatus.OK).body(listInstances);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		
	}
	

	@GetMapping("/instanceversion")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getAllInstancesByVersion(@RequestParam long version){
		
		log.debug("Call of get instance by Version : {}", version);
		try {
			
			log.trace("Found : {}", "");
			List<JobDataInstance> listInstances = instanceService.getInstanceByVersion(version);
			return ResponseEntity.status(HttpStatus.OK).body(listInstances);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		
		
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> saveInstance(@RequestBody JobDataInstance job){
		
		log.debug("Call of Save instance : {}", job);
		
		try {
			
			log.trace("save : {}",job);
			JobDataInstance jobInstance= instanceService.saveInstance(job);
			return ResponseEntity.status(HttpStatus.OK).body(jobInstance);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		
	}
	

}
