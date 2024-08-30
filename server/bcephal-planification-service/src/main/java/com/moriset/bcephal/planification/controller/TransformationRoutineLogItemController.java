package com.moriset.bcephal.planification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineLogItem;
import com.moriset.bcephal.planification.service.TransformationRoutineLogItemService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/planification/routine-log-item")
@Slf4j
public class TransformationRoutineLogItemController {
	
	@Autowired
	TransformationRoutineLogItemService logItemService;

	@Autowired
	protected MessageSource messageSource;

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<TransformationRoutineLogItem> page = logItemService.search(filter, locale);
			log.debug("Found : {}", page.getItems().size());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search entities by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.entity.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
