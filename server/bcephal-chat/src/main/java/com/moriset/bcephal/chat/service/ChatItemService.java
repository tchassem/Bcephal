package com.moriset.bcephal.chat.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.chat.domain.ChatBrowserData;
import com.moriset.bcephal.chat.domain.ChatBrowserDataFilter;
import com.moriset.bcephal.chat.domain.ChatItem;
import com.moriset.bcephal.chat.domain.ChatItemPersistence;
import com.moriset.bcephal.chat.domain.ChatItemType;
import com.moriset.bcephal.chat.domain.ChatItemUser;
import com.moriset.bcephal.chat.domain.ChatUserType;
import com.moriset.bcephal.chat.repository.ChatItemRepository;
import com.moriset.bcephal.chat.repository.ChatItemUserRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.GroupBrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.service.DocumentService;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.HttpHeadersUtility;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatItemService extends PersistentService<ChatItem, BrowserData> {

	@Autowired
	private ChatItemRepository chatItemRepository;

	@Autowired
	private ChatItemUserRepository chatItemUserRepository;
	
	@Autowired
	DocumentService documentService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	protected ObjectMapper mapper;
	
	@Override
	public ChatItemRepository getRepository() {
		return chatItemRepository;
	}
	
	public boolean canDelete(Long id, Locale locale) {
		Optional<ChatItem> optItem = getRepository().findById(id);
		return optItem.isPresent() ? true : false;
	}

	public void delete(ChatItem item, HttpHeaders httpHeaders, Locale locale) {
		log.debug("Try to delete : {}", item);
		if (item == null || item.getId() == null) {
			return;
		}
		
		if (item.getType().equals(ChatItemType.DOCUMENT) && item.getDocument() != null) {
			
			HttpHeaders headers = HttpHeadersUtility.getHttpHeaders(restTemplate, httpHeaders, locale);
			HttpEntity<List<String>> requestEntity = new HttpEntity<List<String>>(List.of(item.getDocument().getCode()), headers);
			ResponseEntity<?> response = restTemplate.exchange("/file-manager/delete-items", HttpMethod.POST, requestEntity, Boolean.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				log.error("Unable to delete file of chat document : {}.", item.getDocument().getName());
				throw new BcephalException("Unable to delete file of chat document : {}.", item.getDocument().getName());
			}
			documentService.deleteById(item.getDocument().getId());
		}
		getRepository().deleteById((Long) item.getId());
		log.debug("Entity successfully deleted : {}", item);
	}

	@Transactional
	public void delete(List<Long> ids, HttpHeaders httpHeaders, Locale locale) {
		log.debug("Try to delete : {} chatItems", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.chatItem.list", new Object[] { ids }, locale);
				throw new BcephalException(message);
			}
			deleteByIds(ids, httpHeaders, locale);
			log.debug("{} chatItems successfully deleted", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete chatItems : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete.chatItems", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private void deleteByIds(List<Long> ids, HttpHeaders httpHeaders, Locale locale) {
		log.debug("Try to delete by ids : {}", ids);
		if (getRepository() == null || ids == null) {
			return;
		}
		ids.forEach(item -> {
			deleteById(item, httpHeaders, locale);
		});
	}
	
	private void deleteById(Long id, HttpHeaders httpHeaders, Locale locale) {
		log.debug("Try to delete by id : {}", id);
		if (getRepository() == null || id == null) {
			return;
		}
		ChatItem item = getRepository().getReferenceById(id);
		delete(item, httpHeaders, locale);
		log.debug("ChatItem successfully deleted : {}", id);
	}
	
	@Transactional
	public BrowserDataPage<ChatItem> save(ChatItemPersistence itemPersistence, HttpHeaders httpHeaders, Locale locale) {
		log.debug("Try to Save chatItem");
		try {
			ChatItem item = itemPersistence.getItem();
			if (item == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.chatItem", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if (item.getChatId() == null) {
				String message = getMessageSource().getMessage("unable.to.save.chatItem.without.chatId", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			
			List<ChatItem> items = getRepository().findByChatIdAndSenderIdAndReceiverIdAndReceiverType(item.getChatId(), item.getSenderId(), item.getReceiverId(), item.getReceiverType().name());
			
			ChatItem itemMaxPos = items.stream().max(Comparator.comparing(ChatItem::getPosition)).orElse(item);
			item.setPosition(itemMaxPos.getPosition() + 1);			
			if (items.size() == 0) {
				items = getRepository().findByChatIdAndChannelDesc(item.getChatId());
				itemMaxPos = items.stream().max(Comparator.comparing(ChatItem::getChannelId)).orElse(item);
				item.setChannelId(itemMaxPos.getChannelId() == null ? 1L : itemMaxPos.getChannelId() + 1);				
				saveChatItemUser(item, httpHeaders, locale);
			} else {
				item.setChannelId(itemMaxPos.getChannelId());
				saveChatItemUser(item, httpHeaders, locale);
			}
			item.setCreationDate(new Timestamp(System.currentTimeMillis()));
			item.setModificationDate(new Timestamp(System.currentTimeMillis()));
			if (item.getDocument() != null && item.getDocument().getId() == null) {
				item.getDocument().setCreationDate(new Timestamp(System.currentTimeMillis()));
				item.getDocument().setModificationDate(new Timestamp(System.currentTimeMillis()));
				item.setDocument(documentService.save(item.getDocument(), locale));
			}
			itemPersistence.setItem(getRepository().save(item));
			BrowserDataPage<ChatItem> page = search(itemPersistence.getFilter(), locale);
			log.debug("ChatItem saved");
			return page;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save chatItem ", e);
			String message = getMessageSource().getMessage("unable.to.save.chatItem", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private void saveChatItemUser(ChatItem item, HttpHeaders httpHeaders, Locale locale) {
		Long receiverId = item.getReceiverId();
		if (item.getReceiverType().equals(ChatUserType.PROFILE)) {					
			HttpHeaders headers = HttpHeadersUtility.getHttpHeaders(restTemplate, httpHeaders, locale);
			HttpEntity<EditorDataFilter> requestEntity = new HttpEntity<EditorDataFilter>(new EditorDataFilter(receiverId), headers);
			ResponseEntity<?> response = restTemplate.exchange("/profiles/editor-data", HttpMethod.POST, requestEntity, EditorData.class);
			try {
				LinkedHashMap<String, Object> editorDataItem = (LinkedHashMap<String, Object>) ((EditorData<?>)response.getBody()).getItem();
				LinkedHashMap<String, List<LinkedHashMap<?,?>>> userListChangeHandler = (LinkedHashMap<String, List<LinkedHashMap<?,?>>>) editorDataItem.get("userListChangeHandler");						
				List<LinkedHashMap<?,?>> originalList = userListChangeHandler.get("originalList");
				originalList.forEach(user -> {
					Long userId = Long.parseLong(user.get("id").toString());
					if (userId != item.getSenderId()) {
				    	ChatItemUser itemUser = new ChatItemUser(item.getChatId(), receiverId, userId, item.getChannelId());
				    	chatItemUserRepository.save(itemUser);
					}
			    });
			} catch (Exception e) {
				throw new BcephalException(e.getMessage());
			}
		} else {
			ChatItemUser itemUser = new ChatItemUser(item.getChatId(), receiverId, receiverId, item.getChannelId());
			chatItemUserRepository.save(itemUser);
		}
	}
	
	protected Specification<ChatItem> getBrowserDatasSpecification(ChatBrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		
		return (root, query, cb) -> {
			RequestQueryBuilder<ChatItem> qBuilder = new RequestQueryBuilder<ChatItem>(root, query, cb);
			qBuilder.select(GroupBrowserData.class, root.get("id"), root.get("chatId"), root.get("position"), root.get("receiverId"), root.get("channelId"), root.get("receiverType"),
					root.get("type"), root.get("message"), root.get("senderId"), root.get("senderName"), root.get("creationDate"), root.get("modificationDate"));

			if (filter != null && filter.getChatId() != null) {
				qBuilder.addEqualsCriteria("chatId", filter.getChatId());
				if (filter != null && filter.getReceiverType() != null) {
					qBuilder.add(qBuilder.getCriteriaBuilder().equal(root.get("receiverType"), filter.getReceiverType()));
				}
			}
			if (filter.getReceiverType().equals(ChatUserType.USER)) {
				if (filter != null && filter.getConnectedUserId() != null) {
					List<Predicate> predicates_ = new ArrayList<>();
					predicates_.add(qBuilder.EqualsCriteria( filter.getConnectedUserId(), "senderId"));
					predicates_.add(qBuilder.EqualsCriteria(filter.getReceiverId(), "senderId"));
					Predicate[] predicat = predicates_.toArray(new Predicate[predicates_.size()]);
					qBuilder.add(cb.or(predicat));
				}
				if (filter != null && filter.getReceiverId() != null) {
					List<Predicate> predicates_ = new ArrayList<>();
					predicates_.add(qBuilder.EqualsCriteria( filter.getReceiverId(), "receiverId"));
					predicates_.add(qBuilder.EqualsCriteria(filter.getConnectedUserId(), "receiverId"));
					Predicate[] predicat = predicates_.toArray(new Predicate[predicates_.size()]);
					qBuilder.add(cb.or(predicat));
				}
			} else {
				qBuilder.addEqualsCriteria("receiverId", filter.getReceiverId());
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
	
	
	public BrowserDataPage<ChatItem> search(ChatBrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<ChatItem> page = new BrowserDataPage<ChatItem>();
		page.setPageSize(filter.getPageSize());
		
		Specification<ChatItem> specification = getBrowserDatasSpecification(filter, locale, new ArrayList<>());
		if (filter.isShowAll()) {
			List<ChatItem> items = getRepository().findAll(specification, getBrowserDatasSort(filter, locale));
			page.setItems(items);

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<ChatItem> oPage = getRepository().findAll(specification, getPageable(filter, getBrowserDatasSort(filter, locale)));
			page.setItems(oPage.getContent());

			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageCount(oPage.getTotalPages());
			page.setTotalItemCount(Long.valueOf(oPage.getTotalElements()).intValue());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		}

		return page;
	}

	private Pageable getPageable(ChatBrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}
	
	protected Sort getBrowserDatasSort(ChatBrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			
			if(filter.getColumnFilters().isSortFilter()) {
	    		build(filter.getColumnFilters());
	    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
	    			
	    			if(!filter.getColumnFilters().isJoin()) {
	    				return Sort.by(Order.desc(filter.getColumnFilters().getName()));
	    			} else {
	    				String name = filter.getColumnFilters().getJoinName() + "_" + filter.getColumnFilters().getName();
	    				return Sort.by(Order.desc(name));
	    			}
	    		} else {
	    			if(!filter.getColumnFilters().isJoin()) {
	    				return Sort.by(Order.asc(filter.getColumnFilters().getName()));
	    			} else {
	    				String name = filter.getColumnFilters().getJoinName() + "_" + filter.getColumnFilters().getName();
	    				return Sort.by(Order.asc(name));
	    			}
	    		}
	    	} else {
	    		if(filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
	    			for(ColumnFilter columnFilter : filter.getColumnFilters().getItems()){
	    				if(columnFilter.isSortFilter()) {
	    		    		build(columnFilter);
	    		    		if(columnFilter.getLink() != null && columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
	    		    			if(!columnFilter.isJoin()) {
	    		    				return Sort.by(Order.desc(columnFilter.getName()));
	    		    			} else {
	    		    				String name = columnFilter.getJoinName() + "_" + columnFilter.getName();	    		    				
	    		    				return Sort.by(Order.desc(name));
	    		    			}
	    		    		} else {
	    		    			if(!columnFilter.isJoin()) {
	    		    				return Sort.by(Order.asc(columnFilter.getName()));
	    		    			} else {
	    		    				String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
	    		    				return Sort.by(Order.asc(name));
	    		    			}
	    		    		}
	    		    	}
	    			}
	    		}
	    	}
    	}
		return Sort.by(Order.desc("position"));
	}
	
	protected void build(ColumnFilter columnFilter) {
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		} 
		else if ("description".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("description");
			columnFilter.setType(String.class);
		} 
		else if ("group".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
			columnFilter.setJoin(true);
			columnFilter.setJoinName("group");
		} 
		else if ("visibleInShortcut".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("visibleInShortcut");
			columnFilter.setType(Boolean.class);
		} 
		else if ("creationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} 
		else if ("modificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		}
	}

	public List<ChatItem> findUserNotifications(Long userId, HttpHeaders httpHeaders, java.util.Locale locale) {		
		List<ChatItem> items = chatItemRepository.findUserDashboardMsgNotifs(userId);
		return items;
	}

	public BrowserDataPage<ChatBrowserData> searchNotificationsItems(String username, ChatBrowserDataFilter filter, HttpHeaders httpHeaders, Locale locale) {
		
		HttpHeaders headers = HttpHeadersUtility.getHttpHeaders(restTemplate, httpHeaders, locale);
		Pageable pageable = PageRequest.of(filter.getPage(), filter.getPageSize(), Sort.by(Order.desc("id")));
		BrowserDataPage<ChatBrowserData> page = new BrowserDataPage<ChatBrowserData>();
		try {
			HttpEntity<EditorDataFilter> request = new HttpEntity<EditorDataFilter>(null, headers);
			ResponseEntity<String> response = restTemplate.exchange("/users/by-name/{name}", HttpMethod.GET, request, String.class, Map.of("name", username));			
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode userId = root.path("id");
			Page<ChatBrowserData> pageItems = chatItemRepository.searchNotificationsItems(userId.asLong(), pageable);
			page.setPageSize(pageItems.getPageable().getPageSize());		
			page.setItems(pageItems.getContent());
			page.setCurrentPage(pageItems.getPageable().getPageNumber());
			page.setPageCount(pageItems.getTotalPages());
			page.setTotalItemCount(pageItems.getNumberOfElements());			
			return page;			
		} catch (RestClientException | JsonProcessingException e1) {
			throw new BcephalException(e1.getMessage());
		}
	}

	public void updateUserNotifications(Long chatId, Long receiverId, Long itemReceiverId, ChatUserType userType, Long channelId) {
		/*List<ChatItem> items = chatItemRepository.findUserChatMsgNotifs(userId, channelId);
		if (items.size() > 0) {
			List<ChatItem> items_ = new ArrayList<ChatItem>();
			items.stream().collect(Collectors.groupingBy(ChatItem::getChatId)).entrySet().forEach(
			entry -> {
				items_.addAll(entry.getValue());
			});
			items_.forEach(res -> {
				List<ChatItem> chatItems = chatItemRepository.findByChatIdAndChannelDesc(res.getChatId());
				ChatItem itemMax = chatItems.stream().max(Comparator.comparing(ChatItem::getId)).get();
				ChatItemUser itemUser = chatItemUserRepository.findByChatIdAndUserIdAndChannelId(itemMax.getChatId(), userId, itemMax.getChannelId());
				itemUser.setLastItemId(itemMax.getId());
				chatItemUserRepository.save(itemUser);
			});
		}*/
		ChatItem itemMax = null;
		List<ChatItem> chatItems = chatItemRepository.findByChatIdAndReceiverIdAndReceiverTypeAndChannelId(chatId, receiverId, userType.name(), channelId);
		if (chatItems.size() > 0) {
			itemMax = chatItems.stream().max(Comparator.comparing(ChatItem::getId)).get();
			chatItemUserRepository.updateItemsUser(itemMax.getChatId(), receiverId, itemReceiverId, itemMax.getChannelId(), itemMax.getId());
//			ChatItemUser itemUser = chatItemUserRepository.findByChatIdAndReceiverIdAndItemReceiverIdAndChannelId(itemMax.getChatId(), receiverId, itemReceiverId, itemMax.getChannelId());
//			if (itemUser != null) {
//				itemUser.setLastItemId(itemMax.getId());
//				chatItemUserRepository.save(itemUser);
//			}
		}
//		if (channelId == null) {
//			channelId = 1L;
//		}
	}

}
