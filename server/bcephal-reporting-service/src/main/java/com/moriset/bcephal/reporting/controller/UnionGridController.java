/**
 * 
 */
package com.moriset.bcephal.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridBrowserData;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.grid.service.UnionGridService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author MORISET-004
 *
 */
@RestController
@RequestMapping("/reporting/union-grid")
@Slf4j
public class UnionGridController extends BaseController<UnionGrid, UnionGridBrowserData> {

	@Autowired
	UnionGridService unionGridService;

	@Override
	protected UnionGridService getService() {
		return unionGridService;
	}
	
	
	@PostMapping("/rows")
	public ResponseEntity<?> searchRows(@RequestBody UnionGridFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		return searchRows2(filter, locale);
	}
	
	@PostMapping("/rows2")
	public ResponseEntity<?> searchRows2(@RequestBody UnionGridFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			 BrowserDataPage<GridItem> page = getService().searchRows(filter, locale);
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
	
	
	@PostMapping("/column/count")
	public ResponseEntity<?> getColumnCountDetails(@RequestBody UnionGridFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of count rows");
		try {
			GrilleColumnCount gridColumn = getService().getColumnCountDetails(filter, locale);	
			log.debug("Found : {}", gridColumn);
			return ResponseEntity.status(HttpStatus.OK).body(gridColumn);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while count column details by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.count.column.detail.by.filter", new Object[] { },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/rows/duplicate-count")
	public ResponseEntity<?> getColumnDuplicateCount(@RequestBody UnionGridFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of count rows");
		try {
			long duplicateRows = getService().getColumnDuplicateCount(filter, locale);
			log.debug("Found : {}", duplicateRows);
			return ResponseEntity.status(HttpStatus.OK).body(duplicateRows);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while count duplicate rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.count.duplicate.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/rows/duplicate")
	public ResponseEntity<?> getColumnDuplicate(@RequestBody UnionGridFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of count rows");
		try {
			BrowserDataPage<Object[]> page = getService().getColumnDuplicate(filter, locale);
			//log.debug("Found : {}", duplicateRows);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while count duplicate rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.count.duplicate.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
}
