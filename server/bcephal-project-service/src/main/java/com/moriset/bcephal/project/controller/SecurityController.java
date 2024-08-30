package com.moriset.bcephal.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.security.domain.Subscription;
import com.moriset.bcephal.security.domain.SubscriptionCustomBrowserData;
import com.moriset.bcephal.security.service.SubscriptionService;
import com.moriset.bcephal.security.service.UniverseLockerService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/security")
@Slf4j
public class SecurityController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	UniverseLockerService universeLockerService;

	@Autowired
	SubscriptionService subscriptionService;

	@GetMapping("/locked/{fileOid}/{oid}")
	public ResponseEntity<?> locked(@PathVariable("fileOid") Integer fileOid, @PathVariable("oid") Integer oid,
			@RequestHeader("Accept-Language") java.util.Locale locale, @RequestParam("BC-CLIENT") Long subscriptionId) {
		log.debug("Call of locked : {}", fileOid);
		try {
			// universeLockerService.realeasedProject(projectCode);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while locked : {}", fileOid, e);
			String message = messageSource.getMessage("unable.to.locked", new Object[] { fileOid }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/realeased/{projectCode}/{userId}")
	public ResponseEntity<?> realeased(@PathVariable("projectCode") String projectCode,
			@PathVariable("userId") Integer oid, @RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestParam("BC-CLIENT") Long subscriptionId) {
		log.debug("Call of realeased : {}", projectCode);
		try {
			log.debug("Try to unlock project : {} ...", projectCode);
			universeLockerService.realeasedProject(projectCode);
			log.debug("Project : {} unlocked!", projectCode);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while locked : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.unlocked", new Object[] { projectCode }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/client/save")
	public ResponseEntity<?> saveSubscription(@RequestBody Subscription subscription,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of SaveSubscription : {}", subscription);
		try {
			subscription = subscriptionService.save(subscription);
			log.debug("Subscription Saved {} !", subscription);
			return ResponseEntity.status(HttpStatus.OK).body(subscription);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while locked : {}", subscription, e);
			String message = messageSource.getMessage("unable.to.unlocked", new Object[] { subscription }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/client/search")
	public ResponseEntity<?> searchSubscription(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchSubscription : {}", filter);
		try {
			BrowserDataPage<SubscriptionCustomBrowserData> subscription = subscriptionService.search(filter, locale);
			log.debug("Found {} !", subscription.getItems().size());
			return ResponseEntity.status(HttpStatus.OK).body(subscription);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while locked : {}", filter, e);
			String message = messageSource.getMessage("unable.to.unlocked", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/client/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			EditorData<Subscription> data = subscriptionService.getEditorData(filter, locale);
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

}
