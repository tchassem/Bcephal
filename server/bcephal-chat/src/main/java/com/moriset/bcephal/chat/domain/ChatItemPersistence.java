package com.moriset.bcephal.chat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChatItemPersistence {
	
	private ChatItem item;

	private ChatBrowserDataFilter filter;
}
