/**
 * 
 */
package com.moriset.bcephal.reporting.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

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

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.reporting.service.ReportGridService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/reporting/grid")
@Slf4j
public class ReportGridController extends BaseController<Grille, BrowserData> {

	@Autowired
	ReportGridService reportGridService;

	@Override
	protected ReportGridService getService() {
		return reportGridService;
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
	
	@PostMapping("/rows2")
	public ResponseEntity<?> searchRows2(@RequestBody GrilleDataFilter filter,
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

	@GetMapping("/columns/{gridId}")
	public ResponseEntity<?> getGridColumns(JwtAuthenticationToken principal, @PathVariable("gridId") Long gridId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of getGridColumns by given gridId : {} ", gridId);
		try {
			List<GrilleColumn> columns = reportGridService.getColumns(gridId);
			return ResponseEntity.status(HttpStatus.OK).body(columns);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getGridColumns ...", e);
			String message = messageSource.getMessage("unable.to.load.columns", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/publish/{gridId}")
	public ResponseEntity<?> publish(@PathVariable("gridId") Long gridId, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		log.debug("Call of Publish Grid");
		try {
			var ed = getService().publish(gridId, locale, session);
			log.debug("Publish : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while publishing grid.", e);
			String message = messageSource.getMessage("unable.to.publish.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/publish")
	public ResponseEntity<?> publish(@RequestBody List<Long> gridIds, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		log.debug("Call of Publish Grids");
		try {
			var ed = getService().publish(gridIds, locale, session);
			log.debug("Publish : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while publishing grids.", e);
			String message = messageSource.getMessage("unable.to.publish.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/reset-publication/{gridId}")
	public ResponseEntity<?> resetPublication(@PathVariable("gridId") Long gridId, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		
		log.debug("Call of Reset Grid");
		try {
			var ed = getService().resetPublication(gridId, locale, session);
			log.debug("ResetPublication : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while resetting grid.", e);
			String message = messageSource.getMessage("unable.to.reset.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/reset-publication")
	public ResponseEntity<?> resetPublication(@RequestBody List<Long> gridIds, 
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session) {
		
		log.debug("Call of Reset Grids");
		try {
			var ed = getService().resetPublication(gridIds, locale, session);
			log.debug("ResetPublication : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while resetting grids.", e);
			String message = messageSource.getMessage("unable.to.reset.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/refresh-publication/{gridId}")
	public ResponseEntity<?> refreshPublication(@PathVariable("gridId") Long gridId, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of refreshPublication Grid");
		try {
			boolean result = getService().refreshPublication(gridId, locale);
			log.debug("RefreshPublication : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while refreshing grid.", e);
			String message = messageSource.getMessage("unable.to.refresh.grid", new Object[] { gridId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/republish")
	public ResponseEntity<?> republish(@RequestBody List<Long> gridIds, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of refreshPublication Grids");
		try {
			boolean result = getService().republish(gridIds, locale);
			log.debug("Republish : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while republish grids.", e);
			String message = messageSource.getMessage("unable.to.republish.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/refresh-publication")
	public ResponseEntity<?> refreshPublication(@RequestBody List<Long> gridIds, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
		log.debug("Call of refreshPublication Grids");
		try {
			boolean result = getService().refreshPublication(gridIds, locale);
			log.debug("RefreshPublication : {}", result);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while refreshing grids.", e);
			String message = messageSource.getMessage("unable.to.refresh.grids", new Object[] { gridIds }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
