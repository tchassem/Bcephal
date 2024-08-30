/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.moriset.bcephal.billing.service.BaseInvoiceRepositoryService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseInvoiceRepositoryController extends BaseController<Grille, BrowserData> {

	
	protected abstract BaseInvoiceRepositoryService getInvoiceService();
	
	@Override
	protected BaseInvoiceRepositoryService getService() {
		return getInvoiceService();
	}

	@PostMapping("/rows2")
	public ResponseEntity<?> searchRows(@RequestBody GrilleDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			BrowserDataPage<GridItem> page = getService().searchRows2(filter, locale);
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
	
	@GetMapping("file-id/{id}")
	public ResponseEntity<?> getFileCodeByUniverseId(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get invoice file id : {}", id);
		try {
			String code = getService().getFileCodeByUniverseId(id);
			log.debug("Found : {}", code);
			return ResponseEntity.status(HttpStatus.OK).body(code);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving invoice file id : {}", id, e);
			String message = messageSource.getMessage("unable.to.retieve.invoice.file.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/invoice-object-id/{universeId}")
	public ResponseEntity<?> getInvoiceObjectId(@PathVariable("universeId") Long universeId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of getInvoiceObjectId");
		try {
			Long objectId = getInvoiceService().getInvoiceObjectId(universeId, locale);
			log.debug("Found : {}", objectId);
			return ResponseEntity.status(HttpStatus.OK).body(objectId);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retreiving object ID for universe ID : {}", universeId, e);
			String message = messageSource.getMessage("unable.to.search.rows.by.id", new Object[] { universeId },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}
