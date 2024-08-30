package com.moriset.bcephal.chat.domain;

import com.moriset.bcephal.domain.filters.BrowserDataFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChatBrowserDataFilter extends BrowserDataFilter {

	private Long chatId;
	
	private Long channelId;
	
	private Long connectedUserId;
	
	private Long receiverId;
    
	private ChatUserType receiverType;
}
