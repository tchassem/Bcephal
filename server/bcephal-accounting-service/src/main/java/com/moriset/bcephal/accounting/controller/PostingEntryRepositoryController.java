/**
 * 
 */
package com.moriset.bcephal.accounting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.accounting.service.PostingEntryRepositoryService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/accounting/posting-entry-repository")
@Slf4j
public class PostingEntryRepositoryController extends BaseController<Grille, BrowserData> {

	@Autowired
	PostingEntryRepositoryService postingEntryRepositoryService;
	
	
	@Override
	protected PostingEntryRepositoryService getService() {
		return postingEntryRepositoryService;
	}
	

	@PostMapping("/rows")
	public ResponseEntity<?> searchRows(@RequestBody GrilleDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			BrowserDataPage<Object[]> page = getService().searchRows(filter, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	
}
