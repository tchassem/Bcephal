package com.moriset.bcephal.chat.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ChatBrowserData extends BrowserData {
	
	private String subjectType;
	private String subjectName;
	private Long channelId;
	private Long subjectId;
	private String owner;	
	private Long receiverId;
	private ChatUserType receiverType;
	
	public ChatBrowserData(Long id, Long subjectId, String subjectType, Long channelId, String subjectName, String owner, String name) {
		setId(id);
		setName(name);
		setSubjectId(subjectId);
		setChannelId(channelId);
		setSubjectType(subjectType);
		setSubjectName(subjectName);
		setOwner(owner);
	}
	
	public ChatBrowserData(Long id, Long subjectId, String subjectType, Long channelId, String subjectName, String owner, String name, Long receiverId, String receiverType) {
		setId(id);
		setName(name);
		setSubjectId(subjectId);
		setChannelId(channelId);
		setSubjectType(subjectType);
		setSubjectName(subjectName);
		setOwner(owner);
		setReceiverId(receiverId);
		setReceiverType(ChatUserType.forValue(receiverType));
	}
	
	public ChatBrowserData(Long id, Long subjectId, String subjectType, Long channelId, String subjectName, String owner, String name, Long receiverId, ChatUserType receiverType) {
		setId(id);
		setName(name);
		setSubjectId(subjectId);
		setChannelId(channelId);
		setSubjectType(subjectType);
		setSubjectName(subjectName);
		setOwner(owner);
		setReceiverId(receiverId);
		setReceiverType(receiverType);
	}
}
