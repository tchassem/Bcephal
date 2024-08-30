package com.moriset.bcephal.accounting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.accounting.domain.Posting;
import com.moriset.bcephal.accounting.domain.PostingBrowserData;
import com.moriset.bcephal.accounting.service.PostingService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/accounting/posting")
public class PostingController extends BaseController<Posting, PostingBrowserData> {

	@Autowired
	PostingService postingService;
	
	@Override
	protected PostingService getService() {
		return postingService;
	}
	
	@PostMapping("/validate")
	public ResponseEntity<?> validate(@RequestBody Posting posting,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of validate posting : {}", posting);
		try {
			posting = getService().validate(posting, locale);
			posting = getService().getById(posting.getId());
			return ResponseEntity.status(HttpStatus.OK).body(posting);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while validating posting : {}", posting, e);
			String message = messageSource.getMessage("unable.to.validate.posting", new Object[] { posting }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/validate-postings")
	public ResponseEntity<?> validate(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of validate postings : {}", ids);
		try {
			getService().validate(ids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while validating postings : {}", ids, e);
			String message = messageSource.getMessage("unable.to.validate.postings", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reset")
	public ResponseEntity<?> resetValidation(@RequestBody Posting posting,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of validate posting : {}", posting);
		try {
			posting = getService().resetValidation(posting, locale);
			posting = getService().getById(posting.getId());
			return ResponseEntity.status(HttpStatus.OK).body(posting);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while reseting validation posting : {}", posting, e);
			String message = messageSource.getMessage("unable.to.reset.posting.validation", new Object[] { posting }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reset-postings")
	public ResponseEntity<?> resetValidation(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of validate postings : {}", ids);
		try {
			getService().resetValidation(ids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while reseting validation postings : {}", ids, e);
			String message = messageSource.getMessage("unable.to.reset.postings.validation", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
}
