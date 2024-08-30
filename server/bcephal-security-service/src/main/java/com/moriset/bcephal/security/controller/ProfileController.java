/**
 * 
 */
package com.moriset.bcephal.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.security.domain.AccessRightEditorData;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileBrowserData;
import com.moriset.bcephal.security.domain.ProfileDashboard;
import com.moriset.bcephal.security.domain.ProfileDashboardEditorData;
import com.moriset.bcephal.security.domain.ProfileProject;
import com.moriset.bcephal.security.service.ProfileService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@RestController
@RequestMapping("/profiles")
@Slf4j
public class ProfileController extends Controller<Profile, ProfileBrowserData> {

	@Autowired
	ProfileService service;

	@Override
	protected ProfileService getService() {
		return service;
	}
	
	@GetMapping("/access-right-editor-data/{project}")
	public ResponseEntity<?> getAccessRightEditorData(@RequestHeader(RequestParams.BC_CLIENT) Long clientId, 
			@PathVariable("project") Long project,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getAccessRightEditorData : {}", project);
		try {
			AccessRightEditorData item = getService().getAccessRightEditorData(clientId, project);
			log.debug("Found : {}", item);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving entity with id : {}", project, e);
			String message = messageSource.getMessage("unable.to.retieve.entity.by.id", new Object[] { project }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save-access-rights/{project}")
	public ResponseEntity<?> save(@RequestBody ListChangeHandler<ProfileProject> profiles, 
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId, 
			@PathVariable("project") Long project,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of save profile projects : {}", profiles);
		try {
			getService().save(profiles, clientId, project, locale);
			AccessRightEditorData item = getService().getAccessRightEditorData(clientId, project);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save profile projects : {}", profiles, e);
			String message = messageSource.getMessage("unable.to.save.profile.projects", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/sub-privilege-editor-data")
	public ResponseEntity<?> getSubPrivilegeEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, 
			JwtAuthenticationToken principal) {

		log.debug("Call of get sub privilege editor data");
		try {
			EditorData<Profile> data = getService().getSubPrivilegeEditorData(filter, clientId, principal.getName(), projectCode, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@GetMapping("/profile-dashboard-editor-data/{dashboardId}")
	public ResponseEntity<?> getProfileDashboardEditorData(@PathVariable("dashboardId") Long dashboardId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {

		log.debug("Call of get profile dashboard editor data");
		try {
			ProfileDashboardEditorData data = getService().getProfileDashboardEditorData(dashboardId, locale);
			log.debug("Found : {}", data.getItemListChangeHandler().getOriginalList());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", dashboardId, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { dashboardId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save-profile-dashboards/{dashboardId}")
	public ResponseEntity<?> save(@RequestBody ListChangeHandler<ProfileDashboard> dashboards, 
			@PathVariable("profileId") Long dashboardId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of save profile dashboards : {}", dashboards);
		try {
			getService().save(dashboards, dashboardId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save profile dashboards : {}", dashboards, e);
			String message = messageSource.getMessage("unable.to.save.profile.dashboards", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
