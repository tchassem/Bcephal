/**
 * 
 */
package com.moriset.bcephal.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.UserSessionLog;
import com.moriset.bcephal.security.domain.UserSessionLogBrowserData;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/users/session-logs")
@RefreshScope
@Slf4j
public class UserSessionLogController extends Controller<UserSessionLog, UserSessionLogBrowserData> {
	
	@Autowired
	UserSessionLogService service;	

	@Override
	protected UserSessionLogService getService() {
		return service;
	}
	
	@PostMapping("/disconnect-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of disconnect users : {}", ids);
		try {
			service.disconnectedUser(ids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while disconnecting : {}", ids, e);
			String message = messageSource.getMessage("unable.to.disconnect", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save-user-sesion-log")
	public ResponseEntity<Object> saveUserSessionLog(@RequestBody UserSessionLog userSessionLog,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of save userSessionLog : {}", userSessionLog);
		try {
			service.getUserForSessionLog(userSessionLog, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save userSessionLog : {}", userSessionLog, e);
			String message = messageSource.getMessage("unable.to.save", new Object[] { userSessionLog }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/update-user-sesion-log/{username}/{clientId}")
	public ResponseEntity<Object> setUserSessionLog(@RequestBody Project project,
			@RequestHeader("Accept-Language") java.util.Locale locale, @PathVariable("username") String username, @PathVariable("clientId") Long clientId) {
		log.debug("Call of update user_session_log with project : {}", project);
		try {
			service.updateUserSessionLog(project, locale, username, clientId);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while update user_session_log : {}", project, e);
			String message = messageSource.getMessage("unable.to.update.user_session_log", new Object[] { project }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/disconnect")
	public ResponseEntity<Object> disconnectAfterSigout(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(name = RequestParams.BC_CLIENT, required = false) Long clientId,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of disconnect user_session_log with username : {}", principal.getName());
		try {
			service.disconnectUserSessionLog(principal.getName(), locale,clientId, projectCode, session.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while disconnect user_session_log : {}", principal.getName(), e);
			String message = messageSource.getMessage("unable.to.disconnect.user_session_log", new Object[] { principal.getName() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/delete-items-by-filter")
	public ResponseEntity<Object> deleteByFilter(@RequestBody BrowserDataFilter filter, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(name = RequestParams.BC_CLIENT, required = false) Long clientId,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of disconnect user_session_log with username : {}", principal.getName());
		try {
			service.deleteByFilter(filter, locale,clientId, projectCode);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while disconnect user_session_log : {}", principal.getName(), e);
			String message = messageSource.getMessage("unable.to.disconnect.user_session_log", new Object[] { principal.getName() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
