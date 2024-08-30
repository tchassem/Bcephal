/**
 * 
 */
package com.moriset.bcephal.reporting.controller;

import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinBrowserData;
import com.moriset.bcephal.grid.domain.JoinEditorData;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author MORISET-004
 *
 */
@RestController
@RequestMapping("/join")
@Slf4j
public class JoinController extends BaseController<Join, JoinBrowserData> {

	@Autowired
	JoinService joinService;

	@Override
	protected JoinService getService() {
		return joinService;
	}
	
	
	@PostMapping("/rows")
	public ResponseEntity<?> searchRows(@RequestBody JoinFilter filter,
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
	public ResponseEntity<?> searchRows2(@RequestBody JoinFilter filter,
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
	
	
	@GetMapping("/materialize/{id}")
	public ResponseEntity<?> materialize(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale, 
			HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode) {
		log.debug("Call of materialize");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			JoinEditorData join = getService().materialize(id, session, locale);
			log.debug("Materialized!");
			return ResponseEntity.status(HttpStatus.OK).body(join);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while materializing join : {}", id, e);
			String message = messageSource.getMessage("unable.to.materialize.join.by.filter", new Object[] { id },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/materialize")
	public ResponseEntity<?> materialize(@RequestBody List<Long> ids, 
			@RequestHeader("Accept-Language") java.util.Locale locale, 
			HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode) {
		log.debug("Call of materialize joins");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			var ed = getService().materialize(ids, locale);
			log.debug("Materialize : {}", ed);
			return ResponseEntity.status(HttpStatus.OK).body(ed);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while materialize joins.", e);
			String message = messageSource.getMessage("unable.to.materialize.joins", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}		
	
	@GetMapping("/refresh-materialization/{id}")
	public ResponseEntity<?> refreshMaterialization(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale, 
			HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode) {
		log.debug("Call of refreshMaterialization");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			boolean res = getService().refreshMaterialization(id, locale);
			log.debug("Materialization refreshed!");
			return ResponseEntity.status(HttpStatus.OK).body(res);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while refresh join materialization : {}", id, e);
			String message = messageSource.getMessage("unable.to.refresh.materialization.join.by.filter", new Object[] { id },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/reset-materialization/{id}")
	public ResponseEntity<?> resetMaterialization(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode) {
		log.debug("Call of resetMaterialization");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			JoinEditorData join = getService().resetMaterialization(id, session, locale);
			log.debug("Materialization reseted!");
			return ResponseEntity.status(HttpStatus.OK).body(join);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while reset join materialization : {}", id, e);
			String message = messageSource.getMessage("unable.to.reset.materialization.join.by.filter", new Object[] { id },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/column/count")
	public ResponseEntity<?> getColumnCountDetails(@RequestBody JoinFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {
		
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
	public ResponseEntity<?> getColumnDuplicateCount(@RequestBody JoinFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

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
	public ResponseEntity<?> getColumnDuplicate(@RequestBody JoinFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale) {

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
	
	@PostMapping("/search-attribute-values")
	public ResponseEntity<?> searchColumnValues(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of searchColumnValues");
		try {
			BrowserDataPage<String> page = getService().searchColumnValues(filter, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while getting column values...", e);
			String message = messageSource.getMessage("unable.to.load.column.values", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	public Long copy(Long id,String joinName, Locale locale) {
		log.debug("Try to copy entity : {}", id);
		Long copyId = getService().copy(id, joinName, locale);
		Join result = getService().getById(copyId);
		return result.getId();
	}
	
	
}
