package com.moriset.bcephal.etl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.etl.domain.BcephalException;
import com.moriset.bcephal.etl.domain.Email;
import com.moriset.bcephal.etl.domain.EmailAccount;
import com.moriset.bcephal.etl.service.EmailAccountService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("sourcing-etl/email-account")
@Slf4j
public class EmailAccountController  {
	
	@Autowired
	private EmailAccountService emailAccountService;
	
	@Autowired
	MessageSource messageSource;
	
	private EmailAccountService getService() {
		
		return emailAccountService;
	}
	
  
//	String host = "imap.gmail.com";
//	String username = "pascalyakouyami@gmail.com";
//    String password = "hbtmdeponvpiduxx";
  public  String downloadDir = "C:\\Users\\Moriset-5\\Documents\\workspace-spring-tool-suite-4-4.19.0.RELEASE\\java-mail\\src\\main\\resources\\backup";
    
    @PostMapping("/download-attachment")
    public  ResponseEntity<?> getmail(@RequestBody Long id,
    		@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
			JwtAuthenticationToken principal, HttpSession session
			){
    	log.debug("Call of download");
    	try {
    		EmailAccount emailAccount = getService().getById(id, projectCode);
    	 List<Email> emails =getService().downloadPop3( emailAccount, downloadDir);
    	
    	 return ResponseEntity.status(HttpStatus.OK).body(emails);
    	} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while download attachments by filter : {}", e);
			String message = messageSource.getMessage(null, null, downloadDir, null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
    }
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
			JwtAuthenticationToken principal, HttpSession session) {

		log.debug("Call of search");
		try {
			filter.setUsername(principal.getName());
			BrowserDataPage<EmailAccount> page = getService().search(filter, locale, profileId, projectCode);
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
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
			@RequestHeader(name= RequestParams.BC_CLIENT,required = false ) Long clientId) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			session.setAttribute(RequestParams.BC_CLIENT, clientId);
			EditorData<EmailAccount> data = getService().getEditorData(filter, session, locale);
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
	
	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody EmailAccount entity, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of save entity : {}", entity);
		try {
//			boolean isNew = entity.getId() == null;
			getService().save(entity, locale,projectCode);
//			String rightLevel = isNew ? "CREATE" : "EDIT";
			log.debug("entity : {}", entity != null ? entity.getUserName() : null);
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
	protected String getFunctionalityCode(Long entityId) {
		return getService().getFunctionalityCode();
	}
	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> ids, @RequestHeader("Accept-Language") java.util.Locale locale) {
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
//	@DeleteMapping("/delete/{oid}")
//	public ResponseEntity<String> delete(@PathVariable("oid") Long id) throws IOException {
//		return baseService.delete(id);
//	}

}
