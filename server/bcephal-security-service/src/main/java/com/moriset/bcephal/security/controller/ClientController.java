/**
 * 
 */
package com.moriset.bcephal.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
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

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.ClientSecurityBrowserData;
import com.moriset.bcephal.security.service.ClientService;
import com.moriset.bcephal.security.service.PrivilegeObserver;
import com.moriset.bcephal.security.service.SubPrivilegeObserver;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/clients")
@RefreshScope
@Slf4j
public class ClientController extends Controller<Client, ClientSecurityBrowserData> {

	@Autowired
	ClientService service;

	@Override
	protected ClientService getService() {
		return service;
	}
	
	@GetMapping("/privileges")
	public ResponseEntity<?> getConnectedUserPrivileges(
			@RequestHeader(name = RequestParams.BC_CLIENT, required = false) Long clientId,
			@RequestHeader(name = RequestParams.BC_PROFILE, required = false) Long profileId,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, 
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {		
		log.debug("Try to retreive connected user privileges : {}", principal.getName());		
		try {
			log.trace("Username : {}", principal.getName());
			log.trace("Client : {}", clientId);
			log.trace("Profiles : {}", profileId);
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);			
			PrivilegeObserver observer = service.getPrivilegeObserver(clientId, profileId, principal.getName(),projectCode,session);
			log.trace("Connected user privileges : {}", observer);
			return ResponseEntity.status(HttpStatus.OK).body(observer);
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error during retreiving connected user privileges",  e);
			String message = messageSource.getMessage("unable.to.retreive.user.privileges", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}			
	}
	
	@GetMapping("/sub-privileges/{functionalityCode}/{objectId}")
	public ResponseEntity<?> getConnectedUserSubPrivileges(
			@PathVariable("functionalityCode") String functionalityCode,
			@PathVariable("objectId") Long objectId,
			@RequestHeader(name = RequestParams.BC_CLIENT) Long clientId,
			@RequestHeader(name = RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name = RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, 
			JwtAuthenticationToken principal) {		
		log.debug("Try to retreive connected user privileges : {}", principal.getName());		
		try {
			log.trace("Username : {}", principal.getName());
			log.trace("Client : {}", clientId);
			log.trace("Profiles : {}", profileId);
			SubPrivilegeObserver observer = service.getSubPrivilegeObserver(functionalityCode,objectId, clientId, profileId, principal.getName(),projectCode);
			log.trace("Connected user privileges : {}", observer);
			return ResponseEntity.status(HttpStatus.OK).body(observer);
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error during retreiving connected user privileges",  e);
			String message = messageSource.getMessage("unable.to.retreive.user.privileges", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}			
	}
	
	@PostMapping("/shortcut-by-profile")
	public ResponseEntity<?> getShortcutByProfile(
			@RequestHeader(name = RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, 
			JwtAuthenticationToken principal) {		
		log.debug("Try to retreive get Shortcut By Profile : {}, user : {}", profileId,principal.getName());		
		try {
			log.trace("Username : {}", principal.getName());
			log.trace("Profile : {}", profileId);
			List<String> observer = service.getShortcutByProfile(profileId, principal.getName());
			log.trace("get Shortcut By Profile : {}", observer);
			return ResponseEntity.status(HttpStatus.OK).body(observer);
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error during retreiving get Shortcut By Profile",  e);
			String message = messageSource.getMessage("unable.to.retreive.shortcut.by.Profile", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}			
	}
		
	
	@PostMapping("/save")
	@Override
	public ResponseEntity<?> save(@RequestBody Client client, @RequestHeader(name = RequestParams.BC_CLIENT, required = false) Long clientId, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode) {
		log.debug("Call of save client : {}", client);
		try {
			getService().save(client, locale);
			client = getService().getById((Long) client.getId(), clientId,projectCode);
			log.debug("Client : {}", client);
			return ResponseEntity.status(HttpStatus.OK).body(client);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save client : {}", client, e);
			String message = messageSource.getMessage("unable.to.save.client", new Object[] { client }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/connected-user-clients")
	public ResponseEntity<?> getConnectedUserClients(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, JwtAuthenticationToken principal) {		
		log.debug("Try to retreive clients for connected user : {}", principal.getName());		
		try {
			List<Nameable> profiles = service.getUserClients(principal.getName());
			log.debug("Connected user clients : {}", profiles);
			return ResponseEntity.status(HttpStatus.OK).body(profiles);
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error during retreiving connected user clients",  e);
			String message = messageSource.getMessage("unable.to.retreive.user.clients", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}	
		
	}
	
	@GetMapping("/connected-user-profiles")
	public ResponseEntity<?> getConnectedUserProfiles(@RequestHeader(RequestParams.BC_CLIENT) Long client, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, JwtAuthenticationToken principal) {
		
		log.debug("Try to retreive profiles for connected user : {}", principal.getName());
		
		try {
			List<Nameable> profiles = service.getUserProfiles(client, principal.getName());
			log.debug("Connected user profiles : {}", profiles);
			return ResponseEntity.status(HttpStatus.OK).body(profiles);
		}
		catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error during retreiving connected user profiles",  e);
			String message = messageSource.getMessage("unable.to.retreive.user.profiles", new Object[] { }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}			
	}
	
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter, @RequestHeader(RequestParams.BC_CLIENT) Long clientId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<ClientSecurityBrowserData> page = getService().search(filter, null, locale);
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
	
	@Override
	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter, @RequestHeader(name = RequestParams.BC_CLIENT,required = false) Long clientId, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, JwtAuthenticationToken principal,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode, HttpSession session,@RequestHeader HttpHeaders headers) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			EditorData<Client> data = getService().getEditorData(filter, clientId,projectCode, principal.getName(),session, locale);
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
