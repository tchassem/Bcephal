package com.moriset.bcephal.scheduler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.loader.domain.UserLoad;
import com.moriset.bcephal.loader.domain.UserLoadBrowserData;
import com.moriset.bcephal.loader.domain.UserLoaderMenu;
import com.moriset.bcephal.scheduler.service.UserLoadService;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/scheduler/user-load")
@Slf4j
public class UserLoadController extends BaseController<UserLoad, UserLoadBrowserData>{
	
	@Autowired
	UserLoadService userLoadService;
	

	@Override
	protected UserLoadService getService() {
		return userLoadService;
	}


	@GetMapping("/menus")
	public ResponseEntity<?> getMenus(@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get menus");
		try {
			List<UserLoaderMenu> menus = getService().getActiveMenus();
			log.debug("Found : {}", menus.size());
			return ResponseEntity.status(HttpStatus.OK).body(menus);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving active menus", e);
			String message = messageSource.getMessage("unable.to.retieve.active.form.menus", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/code-access-rights")
	public ResponseEntity<?> CodeAccessRights(@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of Code Access Rights project : {}", projectCode);
		try {
			List<ClientFunctionality> items =  userLoadService.getFunctionalityRight(client, projectCode);
			return ResponseEntity.status(HttpStatus.OK).body(items);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while Code Access Rights project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.get.code.access.datas", new Object[] { projectCode }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
