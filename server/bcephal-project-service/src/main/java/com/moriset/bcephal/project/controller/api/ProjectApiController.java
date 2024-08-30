package com.moriset.bcephal.project.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.repository.ProjectBrowserDataRepository;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/project-api")
@RefreshScope
@Slf4j
public class ProjectApiController {

	@Autowired
	ProjectBrowserDataRepository projectBrowserDataRepository;
	
	@GetMapping("/connect-to-project")
	public ResponseEntity<?> connectToProject(JwtAuthenticationToken principal,
			@RequestHeader(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(RequestParams.BC_PROJECT_NAME) String projectName,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of connectToProject...");
		
		List<ProjectBrowserData> projects = projectBrowserDataRepository.getBySubscriptionIdAndProfileIdAndProjectName(subscriptionId, profileId, projectName);
		
		if(projects.size() == 0) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body("User not allowed to access project: " + projectName);
		}
		
		return ResponseEntity.ok().body(projects.get(0).getCode());
	}
	
}
