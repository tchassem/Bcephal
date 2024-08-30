/**
 * 
 */
package com.moriset.bcephal.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.alarm.service.AlarmService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@RestController
@RequestMapping("/dashboarding/alarm")
@Slf4j
public class AlarmController extends BaseController<Alarm, BrowserData> {

	@Autowired
	AlarmService alarmService;

	@Override
	protected AlarmService getService() {
		return alarmService;
	}
	
	@PostMapping("/send-message")
	public ResponseEntity<?> runAlertMessage(@RequestBody Alarm alarm, 
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of send alarm message: {}", alarm);
		try {			
			alarmService.sendMessage(alarm, principal.getName(), RunModes.M, projectCode, client);
			String rightLevel = "RUN";
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), alarm.getId(),getService().getFunctionalityCode() , rightLevel, profileId);			
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while send alarm message : {}", alarm, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alarm);
		}
	}
}
