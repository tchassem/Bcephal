/**
 * 
 */
package com.moriset.bcephal.security.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.security.Permission.AdminRole;
import com.moriset.bcephal.security.Permission.checkRole;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.security.domain.UserBrowserData;
import com.moriset.bcephal.security.domain.UserInfo;
import com.moriset.bcephal.security.repository.UserInfoRepository;
import com.moriset.bcephal.security.service.UserService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/users")
@RefreshScope
@Slf4j
public class UserController extends Controller<User, UserBrowserData> {
	
	@Autowired
	UserService service;
	
	@Autowired
	UserInfoRepository userInfoRepository;

	@Override
	protected UserService getService() {
		return service;
	}
	 
	 
	@checkRole
 	@AdminRole
	@PostMapping("/current")
 	@ResponseBody
	public String  getCurrentUser(Authentication auth) {		
		return auth.getName();
	}
	
	@GetMapping(value = "/connected-user-info")
	public ResponseEntity<?> getConnectedUserInfo(JwtAuthenticationToken principal) {
		log.debug("Try to retreive connected user info : {}", "/security/connected-user-info");
		try {
			log.debug("User : {}", principal.getName());
			Optional<UserInfo> response = userInfoRepository.findByUsernameIgnoreCase(principal.getName());
			UserInfo user = response.isPresent() ? response.get() : null;
			log.debug("Connected user info : {}", user);
			return ResponseEntity.status(HttpStatus.OK).body(user);
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error during retreiving connected user info",  e);
			String message = messageSource.getMessage("unable.to.retreive.user.info", new Object[] { }, Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}
