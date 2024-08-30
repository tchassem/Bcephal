package com.moriset.bcephal.scheduler.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.domain.api.SchedulerLogResponse;
import com.moriset.bcephal.scheduler.repository.api.SchedulerLogResponseRepository;
import com.moriset.bcephal.scheduler.service.SchedulerPlannerService;
import com.moriset.bcephal.scheduler.service.api.SchedulerApiService;
import com.moriset.bcephal.utils.ApiCodeGenerator;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/scheduler-api")
@RefreshScope
@Slf4j
public class SchedulerApiController {

	@Autowired
	SchedulerPlannerService schedulerPlannerService;
	
	@Autowired
	SchedulerApiService schedulerApiService;
	
	@Autowired
	SchedulerLogResponseRepository logRepository;
	
	@GetMapping("/run")
	public ResponseEntity<?> runScheduler(
			HttpSession session,
    		JwtAuthenticationToken principal,
    		@RequestHeader(RequestParams.AUTHORIZATION) String auth,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
    		@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
    		@RequestHeader(RequestParams.OPERATION_NAME) String operationName
    		) {
		
		log.debug("Begining to run scheduler planner {}...", operationName);
		
		try {
			List<SchedulerPlanner> schedulerPlanners = schedulerPlannerService.getAllByName(operationName);
			if(schedulerPlanners.size() == 0) {
				throw new BcephalException(String.format("Scheduler planner not found : %s", operationName));
			}
			if(schedulerPlanners.size() > 1) {
				throw new BcephalException(String.format("There are more than one scheduler planner named : %s", operationName));
			}
			SchedulerPlanner schedulerPlanner = schedulerPlanners.get(0);
			log.debug("Scheduler planner found : {}", schedulerPlanner.getName());
			
					
			log.trace("API code generation...");
			String code = ApiCodeGenerator.generate("API_" + ApiCodeGenerator.SCHEDULER_PREFIX);
			log.debug("API code generated : {}", code);
			
			String username = principal.getName();
			schedulerApiService.RunSchedulerPlanner(schedulerPlanner, code, username, clientId, projectCode, session, TenantContext.getCurrentTenant(), new TaskProgressListener() {				
				@Override
				public void SendInfo() { }
			});
			
			return ResponseEntity.ok(code);

		}catch (HttpClientErrorException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (BcephalException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}
	
	
	
	@GetMapping("/status")
	public ResponseEntity<?> status(
			HttpSession session,
    		JwtAuthenticationToken principal,
    		@RequestHeader(RequestParams.AUTHORIZATION) String auth,
    		@RequestHeader(RequestParams.LANGUAGE) String locale,
    		@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
    		@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
    		@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
    		@RequestHeader(RequestParams.OPERATION_CODE) String operationCode
    		) {
		
		log.debug("Try to get status for scheduler by operation code : {}", operationCode);
		
		try {
			if(!StringUtils.hasText(operationCode)) {
				throw new BcephalException(String.format("Operation code is not provided!"));
			}
			List<SchedulerLogResponse> logs = logRepository.findByOperationCode(operationCode);			
			log.debug("Log found : {}", logs.size());						
			return ResponseEntity.ok(logs);

		}catch (HttpClientErrorException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (BcephalException e) {
			log.error("Error!", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}
	

}
