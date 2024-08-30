package com.moriset.bcephal.chat.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.chat.domain.Chat;
import com.moriset.bcephal.chat.repository.ChatRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.GroupBrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ChatService extends MainObjectService<Chat, BrowserData> {
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Override
	public ChatRepository getRepository() {
		return chatRepository;
	}

	@Override
	protected Chat getNewItem() {
		Chat chat = new Chat();
		String baseName = "Chat ";
		int i = 1;
		chat.setName(baseName + i);
		while (getByName(chat.getName()) != null) {
			i++;
			chat.setName(baseName + i);
		}
		return chat;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.CHAT;
	}


	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return new ArrayList<>();
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		
	}

	@Override
	protected BrowserData getNewBrowserData(Chat item) {
		return new BrowserData(item);
	}

	@Override
	protected Specification<Chat> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Chat> qBuilder = new RequestQueryBuilder<Chat>(root, query, cb);
			qBuilder.select(GroupBrowserData.class, root.get("id"), root.get("name"), root.get("visibleInShortcut"),
					root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter != null && StringUtils.hasText(filter.getSubjectType())) {
				qBuilder.addEquals("subjectType", filter.getSubjectType());
				if(filter.getSubjectId() != null) {
					qBuilder.addEqualsCriteria("subjectId", filter.getSubjectId());
				}
			}
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}
	
	@Override 
	public Chat save(Chat chat, Locale locale) {
		log.debug("Try to Save chat : {}", chat);
		if (getRepository() == null) {
			return chat;
		}
		try {
			if (chat == null) {
				String message = messageSource.getMessage("unable.to.save.null.chat", new Object[] { chat },
						locale);
				throw new BcephalException(message);
			}
			Chat existingChat = findBySubjectTypeAndSubjectId(chat.getSubjectType(), chat.getSubjectId(), locale);
			if(existingChat != null) {
				return existingChat;
			}
			chat.setName(getNewItem().getName());
			chat.setSubjectName(chat.getSubjectType() + "_" + chat.getName());
			chat.setCreationDate(new Timestamp(System.currentTimeMillis()));
			chat.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(chat, locale);
			chat = getRepository().save(chat);
			log.debug("Chat successfully saved : {}", chat);
			return chat;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save chat : {}", chat, e);
			String message = messageSource.getMessage("unable.to.save.chat", new Object[] { chat }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public Chat findBySubjectTypeAndSubjectId(String subjectType, Long subjectId, Locale locale) {
		log.debug("Try to find chat by subjectType and subjectId");
		try {
			if (subjectType == null) {
				String message = getMessageSource().getMessage("unable.to.find.chatItem.with.null.subjectType", new Object[] {},
						locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if (subjectId == null || subjectId.longValue() == 0) {
				String message = getMessageSource().getMessage("unable.to.find.chatItem.with.null.subjectId", new Object[] {},
						locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			Chat chat = chatRepository.findBySubjectTypeAndSubjectId(subjectType, subjectId);
			log.debug("Chat found by subjectType and subjectId completed");
			return chat;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save chatItem ", e);
			String message = getMessageSource().getMessage("unable.to.save.chatItem", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

}
