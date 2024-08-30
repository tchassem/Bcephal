/**
 * 
 */
package com.moriset.bcephal.security.controller.api;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.security.repository.ClientRepository;
import com.moriset.bcephal.security.repository.ProfileRepository;
import com.moriset.bcephal.security.repository.ProfileUserRepository;
import com.moriset.bcephal.security.repository.UserRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/security-api")
@RefreshScope
@Slf4j
public class ApiController  {
	
	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	ProfileUserRepository profileUserRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	private RestTemplate loadBalancedRestTemplate;

	
	@GetMapping("/login")
	public ResponseEntity<?> login(
			JwtAuthenticationToken principal,
			@RequestHeader(RequestParams.AUTHORIZATION) String auth,
			@RequestHeader(RequestParams.BC_CLIENT) String clientName,
			@RequestHeader(RequestParams.BC_PROJECT_NAME) String projectName,
			@RequestHeader(value = RequestParams.BC_PROFILE, required = false) String profileName
    		) {
		log.debug("Call of login...");
		try {	
			LoginResponse response = new LoginResponse();
			String username = principal.getName();
			
			if(!StringUtils.hasText(clientName)) {
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), "Client name is NULL");
			}
			var clients = clientRepository.findByName(clientName);
			if(clients.size() == 0) {
				throw new BcephalException(HttpStatus.UNAUTHORIZED.value(), "Client not found : {0}", clientName);
			}
			var client = clients.get(0);
			response.setClientId(client.getId());
			
			
			if(StringUtils.hasText(profileName)) {
				var profiles = profileRepository.findByClientIdAndName(client.getId(), profileName);
				if(profiles.size() == 0) {
					throw new BcephalException(HttpStatus.UNAUTHORIZED.value(), "Profile not found : {0}", profileName);
				}
				response.setProfileId(profiles.get(0).getId());
			}
			else {
				Optional<User> result = userRepository.findByUsernameIgnoreCase(username);
				User user = result.isPresent() ? result.get() : null;
				boolean isAdmin = user != null && user.isAdministrator();
				if(!isAdmin) {
					result = userRepository.findByUsernameAndClientId(username, client.getId());
					if(result.isEmpty()) {
						throw new BcephalException(HttpStatus.UNAUTHORIZED.value(), "Unknown user");
					}
					user = result.get();
				}
				if(user != null) {
					var profiles = profileUserRepository.getProfilesByClientIdAndUserId(client.getId(), user.getId());
					if(profiles.size() == 0) {
						throw new BcephalException(HttpStatus.UNAUTHORIZED.value(), "User without profile");
					}
					response.setProfileId(profiles.get(0).getId());
				}
			}
			
			if(!StringUtils.hasText(projectName)) {
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), "Project name is NULL");
			}
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			headers.set(RequestParams.LANGUAGE, "en");
			headers.set(RequestParams.AUTHORIZATION, auth);					
			headers.set(RequestParams.BC_CLIENT, ""+ response.getClientId());
			headers.set(RequestParams.BC_PROJECT_NAME, projectName);
			headers.set(RequestParams.BC_PROFILE, ""+ response.getProfileId());
						
			HttpEntity<?> request = new HttpEntity<>(headers);
			ResponseEntity<String> result = loadBalancedRestTemplate.exchange("lb://project-service/project-api/connect-to-project", HttpMethod.GET, request, String.class);
			response.setProjectCode(result.getBody());
			
			return ResponseEntity.ok().body(response);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} 
		catch (HttpClientErrorException e) {
			log.error("Unexpected error while api sigin : {}", "", e);
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		}
		catch (Exception e) {
			log.error("Unexpected error while api sigin : {}", "", e);
			String message = messageSource.getMessage("unable.to.sigin.in.api", new Object[] { "" }, Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
