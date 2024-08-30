package com.moriset.bcephal.settings.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.chat.domain.Chat;
import com.moriset.bcephal.chat.domain.ChatBrowserData;
import com.moriset.bcephal.chat.domain.ChatBrowserDataFilter;
import com.moriset.bcephal.chat.domain.ChatItem;
import com.moriset.bcephal.chat.domain.ChatItemPersistence;
import com.moriset.bcephal.chat.service.ChatItemService;
import com.moriset.bcephal.chat.service.ChatService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/settings/chat")
public class ChatController extends BaseController<Chat, BrowserData>{
	
	@Autowired
	ChatService chatService;
	
	@Autowired
	private ChatItemService chatItemService;
	
	@Override
	protected ChatService getService() {
		return chatService;
	}

	@GetMapping("/subject/details/{subjectType}/{subjectId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> GetBySubjectTypeAndSubjectId(@PathVariable("subjectType") String subjectType, @PathVariable("subjectId") Long subjectId, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call find chat by subjectType and subjectId");
		try {
			Chat chat = getService().findBySubjectTypeAndSubjectId(subjectType, subjectId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(chat);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while finding chat by subjectType and subjectId", e);
			String message = messageSource.getMessage("unable.to.find.chat.by.subject.details", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/items/save")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> saveItem(@RequestBody ChatItemPersistence itemPersistence, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, HttpSession session, @RequestHeader HttpHeaders headers) {
		log.trace("Call Save chatItem");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);			
			BrowserDataPage<ChatItem> page = chatItemService.save(itemPersistence, headers, locale);
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while saving chatItem", e);
			String message = messageSource.getMessage("unable.to.save.chatItem", new Object[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/items/can-delete")
	public ResponseEntity<?> canDeleteItems(@RequestBody List<Long> oids, @RequestHeader("Accept-Language") java.util.Locale locale, @RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode, @RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId, JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of can delete item : {}", oids);
		try {			
			final boolean res[] = new boolean[] {true};
			if(oids != null) {
				oids.forEach(oid ->{
					res[0] = chatItemService.canDelete(oid, locale);
				});
			}		
			return ResponseEntity.status(HttpStatus.OK).body(res[0]);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while checking can delete items : {}", oids, e);
			String message = messageSource.getMessage("unable.to.check.deletion.item", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/items/delete-items")
	public ResponseEntity<?> deleteItems(@RequestBody List<Long> oids, @RequestHeader("Accept-Language") java.util.Locale locale, @RequestHeader HttpHeaders headers) {
		log.debug("Call of delete chatItems : {}", oids);
		try {			
			chatItemService.delete(oids, headers, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting chatItems : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete.chatItems", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/items/search")
	public ResponseEntity<?> search(@RequestBody ChatBrowserDataFilter filter, @RequestHeader("Accept-Language") java.util.Locale locale, JwtAuthenticationToken principal, HttpSession session) {

		log.debug("Call of search chatItems");
		try {
			filter.setUsername(principal.getName());
			BrowserDataPage<ChatItem> page = chatItemService.search(filter, locale);
			if (page.getItems().size() > 0) {
				chatItemService.updateUserNotifications(filter.getChatId(), filter.getReceiverId(), filter.getConnectedUserId(),  filter.getReceiverType(), filter.getChannelId());
			}
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search chatItems by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.chatItems.by.filter", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/notifications/{userId}")
	public ResponseEntity<?> searchNotifications(@PathVariable("userId") Long userId, @RequestHeader HttpHeaders headers, @RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of user chat notifications");
		try {
			List<ChatItem> items = chatItemService.findUserNotifications(userId, headers, locale);
			log.debug("Notifications found : {}", items.size());
			return ResponseEntity.status(HttpStatus.OK).body(items);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search chat notifications : {}", e);
			String message = messageSource.getMessage("unable.to.search.user.chat.notifications", null, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/notifications/items/{username}")
	public ResponseEntity<?> searchNotificationsItems(@PathVariable("username") String username, @RequestBody ChatBrowserDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, @RequestHeader HttpHeaders headers) {

		log.debug("Call of notifications items");
		try {
			filter.setUsername(username);
			BrowserDataPage<ChatBrowserData> page = chatItemService.searchNotificationsItems(username, filter, headers, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search chatItems by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.chatItems.by.filter", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

}

