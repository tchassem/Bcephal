package com.moriset.bcephal.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.moriset.bcephal.api.domain.LoginResponse;
import com.moriset.bcephal.api.domain.RequestParams;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Security", description = "Signin and signout")
@RestController
@RequestMapping( value = "/api/security", produces = MediaType.APPLICATION_JSON_VALUE )
@Slf4j
public class SecurityController {
	
	@Autowired 
	private RestTemplate loadBalancedRestTemplate;
	
	@Operation(
			operationId = "open-project",
			summary = "Connect to a project", 
			description = "Open a project",
			responses = {
					@ApiResponse(description="OK", responseCode = "200"),
					@ApiResponse(description="Unauthorized", responseCode = "403")
			}			
	)
	@GetMapping(value = "/open-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> openProject(
    		HttpSession session,
    		@RequestHeader HttpHeaders httpHeaders,
    		@Parameter(name = "Client-Name", description = "The client to which the user belongs") @RequestParam("Client-Name") String clientName,
    		@Parameter(name = "Project-Name", description = "The project to connect to") @RequestParam("Project-Name") String projectName,
    		@Parameter(name = "Profile-Name", description = "The user profile to use", required = false) @RequestParam(value = "Profile-Name", required = false) String profileName    		    		
		) throws Exception {
		
		log.info("Enter to openProject");
								
		try {	
			
			String auth = httpHeaders.getFirst("Authorization");
			if(!StringUtils.hasText(auth)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing access token");
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			headers.set(RequestParams.LANGUAGE, "en");
			headers.set(RequestParams.AUTHORIZATION, auth);
					
			headers.set(RequestParams.BC_CLIENT, clientName);
			headers.set(RequestParams.BC_PROJECT_NAME, projectName);
			headers.set(RequestParams.BC_PROFILE, profileName);
			
			HttpEntity<?> request = new HttpEntity<>(headers);
			ResponseEntity<LoginResponse> result = loadBalancedRestTemplate.exchange("lb://security-service/security-api/login", HttpMethod.GET, request, LoginResponse.class);
			var response = result.getBody();
			
			session.setAttribute(RequestParams.BC_CLIENT, response.getClientId());
			session.setAttribute(RequestParams.BC_PROFILE, response.getProfileId());
			session.setAttribute(RequestParams.BC_PROJECT, response.getProjectCode());
			
			return ResponseEntity.ok("Conneted");
		}
		catch (Exception e) {
			return handleExeption(e);
		}
			
	}
	
	@Hidden
	@Operation(
			operationId = "logout",
			summary = "Logout", 
			description = "Logout endpoint",
			responses = {
					@ApiResponse(description="OK", responseCode = "200"),
					@ApiResponse(description="Unauthorized", responseCode = "403")
			}			
	)
	@GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(
    		HttpSession session,
    		@RequestHeader HttpHeaders httpHeaders,
    		@Parameter(name = "token") @RequestParam("token") String token
		) throws Exception {
		
		log.info("Log out...");	
		
		session.invalidate();
		LoginResponse response = new LoginResponse(token);
		return ResponseEntity.ok(response);
	}
	
	

	private ResponseEntity<?> handleExeption(Exception e) {
		if (e instanceof HttpStatusCodeException) {
			log.error("Error!", e);
			return ResponseEntity.status(((HttpStatusCodeException) e).getStatusCode()).body(e.getMessage());
		} 
		else if (e instanceof ResourceAccessException) {
			log.error("Error!", e);
			return ResponseEntity.internalServerError().body("Service unavailable!");
		}
		else if (e instanceof IllegalStateException) {
			log.error("Error!", e);
			return ResponseEntity.internalServerError().body("Service unavailable : " + e.getMessage());
		}
		else {
			log.error("Unexpected error!", e);
			return ResponseEntity.internalServerError().build();
		}
	}
	
}
