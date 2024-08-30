/**
 * 
 */
package com.moriset.bcephal.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.security.domain.MainObject;
import com.moriset.bcephal.security.service.MainObjectService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
public abstract class Controller<P extends MainObject, B> {

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected ObjectMapper mapper;

	protected abstract MainObjectService<P, B> getService();

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable("id") Long id,
			@RequestHeader(name = RequestParams.BC_CLIENT, required = false) Long clientId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode) {
		log.debug("Call of get by ID : {}", id);
		try {
			P item = getService().getById(id, clientId, projectCode);
			log.debug("Found : {}", item);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving entity with id : {}", id, e);
			String message = messageSource.getMessage("unable.to.retieve.entity.by.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/by-name/{name}")
	public ResponseEntity<?> getByName(@PathVariable("name") String name,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get by ID : {}", name);
		try {
			P item = getService().getByName(name);
			log.debug("Found : {}", item);
			return ResponseEntity.status(HttpStatus.OK).body(item);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving entity with id : {}", name, e);
			String message = messageSource.getMessage("unable.to.retieve.entity.by.id", new Object[] { name }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody P entity, 
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode) {
		log.debug("Call of save entity : {}", entity);
		try {
			getService().save(entity, clientId, locale, projectCode);
			entity = getService().getById((Long) entity.getId(), clientId,projectCode);
			log.debug("entity : {}", entity);
			return ResponseEntity.status(HttpStatus.OK).body(entity);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.entity", new Object[] { entity }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of delete : {}", ids);
		try {
			getService().delete(ids, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleteing : {}", ids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of search");
		log.trace("Filter : {}", filter);
		log.trace("Client ID : {}", clientId);
		try {
			BrowserDataPage<B> page = getService().search(filter, clientId, locale);
			log.debug("Found : {}", page.getCurrentPage());
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

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId, 			
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, JwtAuthenticationToken principal,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode, HttpSession session,
			@RequestHeader HttpHeaders headers) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			EditorData<P> data = getService().getEditorData(filter, clientId, projectCode, principal.getName(), session,locale);
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
