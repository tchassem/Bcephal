package com.moriset.bcephal.sourcing.grid.controller;

import java.math.BigDecimal;

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
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.grid.service.SpotService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sourcing/spot")
@Slf4j
public class SpotController extends BaseController<Spot, BrowserData> {

	@Autowired
	SpotService spotService;

	@Override
	protected SpotService getService() {
		return spotService;
	}
	
	@GetMapping("/evaluate/{id}")
	public ResponseEntity<?> evaluate(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of evaluate : {}", id);
		try {
			BigDecimal amount = getService().evaluate(id);
			log.debug("Result : {}", amount);
			return ResponseEntity.status(HttpStatus.OK).body(amount);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while evaluating spot with id : {}", id, e);
			String message = messageSource.getMessage("unable.to.evaluate.spot.by.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/evaluate-spot")
	public ResponseEntity<?> evaluate(@RequestBody Spot spot,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of evaluate : {}", spot);
		try {
			BigDecimal amount = getService().evaluate(spot);
			log.debug("Result : {}", amount);
			return ResponseEntity.status(HttpStatus.OK).body(amount);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while evaluating spot : {}", spot, e);
			String message = messageSource.getMessage("unable.to.evaluate.spot", new Object[] { spot }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}
