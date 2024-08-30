package com.moriset.bcephal.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.dashboard.domain.Dashboard;
import com.moriset.bcephal.dashboard.domain.DashboardBrowserData;
import com.moriset.bcephal.dashboard.domain.ProfileDashboard;
import com.moriset.bcephal.dashboard.domain.ProfileDashboardEditorData;
import com.moriset.bcephal.dashboard.service.DashboardService;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/dashboarding")
@Slf4j
public class DashboardController extends BaseController<Dashboard, DashboardBrowserData> {

	@Autowired
	DashboardService dashboardService;

	@Override
	protected DashboardService getService() {
		return dashboardService;
	}
	
	
	@GetMapping("/profile-dashboard-editor-data/{profileId}")
	public ResponseEntity<?> getProfileDashboardEditorData(@PathVariable("profileId") Long profileId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get profile dashboard editor data");
		try {
			ProfileDashboardEditorData data = getService().getProfileDashboardEditorData(profileId, locale);
			log.debug("Found : {}", data.getItemListChangeHandler().getOriginalList());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", profileId, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { profileId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save-profile-dashboards/{profileId}")
	public ResponseEntity<?> save(@RequestBody ListChangeHandler<ProfileDashboard> dashboards, 
			@PathVariable("profileId") Long profileId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of save profile dashboards : {}", dashboards);
		try {
			getService().save(dashboards, profileId, locale);
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
