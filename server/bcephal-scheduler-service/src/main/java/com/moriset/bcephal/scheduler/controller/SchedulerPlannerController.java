package com.moriset.bcephal.scheduler.controller;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerBrowserData;
import com.moriset.bcephal.scheduler.service.SchedulerPlannerService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/scheduler-planner")
@Slf4j
public class SchedulerPlannerController extends BaseController<SchedulerPlanner, SchedulerPlannerBrowserData>{
	
	@Autowired
	SchedulerPlannerService schedulerPlannerService;
	
	
	
	@Override
	protected SchedulerPlannerService getService() {
		return schedulerPlannerService;
	}
	
	@PostMapping("/download-logs-file/{id}")
	public ResponseEntity<?> downloadLogsFile(@PathVariable("id") Long schedulerId, @RequestHeader(name= RequestParams.BC_PROJECT) String projectCode, HttpServletRequest request) {
		log.debug("Call downloadData");
		try {
		Resource resource = null;
        String contentType = null;
        
        try {
            resource = schedulerPlannerService.downloadFileAsResource(schedulerId,projectCode);
        } catch (Exception e) {
            log.error("Unable to get resource file: {}", e.getMessage());
            throw new BcephalException("Resource not found: {}", e);
        }
        
		try {
            log.debug("Controller try to get the contentType.");
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            log.trace("Path: {}", resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.debug("Couldn't determine contentType of Resource file.");
            throw new BcephalException("Unknow type file: {}", ex);
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        HttpHeaders respHeaders = new HttpHeaders();
		try {
			respHeaders.setContentLength(resource.contentLength());
		} catch (IOException e) {
			e.printStackTrace();
		}
		respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
		
        log.debug("Successful building resource file operation.");
        return ResponseEntity.ok().headers(respHeaders)
        		.contentType(MediaType.parseMediaType(contentType))
        		.body(resource);
		} catch (BcephalException e) {
			log.error("Unexpected error while download files scheduler Id : {} {}", schedulerId, e);
			String message = messageSource.getMessage("unable.to.search.scheduler.by.filter", new Object[] { schedulerId }, Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}

	}

}
