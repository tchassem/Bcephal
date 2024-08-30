package com.moriset.bcephal.scheduler.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.scheduler.domain.Presentation;
import com.moriset.bcephal.scheduler.domain.PresentationBrowserData;
import com.moriset.bcephal.scheduler.service.PresentationService;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/scheduler/presentation")
@Slf4j
public class PresentationController extends BaseController<Presentation, PresentationBrowserData> {

	@Autowired
	PresentationService presentationService;
	
	@Override
	protected PresentationService getService() {
		return presentationService;
	}
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Override
	public ResponseEntity<?> downloadData(@RequestBody String path, HttpServletRequest request) throws Exception {
		log.debug("Call downloadData");
        String contentType = null;
        path = objectMapper.readValue(path, String.class);
        Resource resource = null;
		Path filePath = Paths.get(path);
		try {
            resource = new UrlResource(filePath.toUri());
            if(!resource.exists()) {
                throw new BcephalException("File not found: {}", FilenameUtils.getName(filePath.toString()));
            }
        } catch (MalformedURLException ex) {
            throw new BcephalException("Request url malformed: {}", FilenameUtils.getName(filePath.toString()), ex);
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
	}
}
