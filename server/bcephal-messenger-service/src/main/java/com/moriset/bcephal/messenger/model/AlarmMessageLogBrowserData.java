package com.moriset.bcephal.messenger.model;


import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmMessageLogBrowserData  {
	
	private Long id;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp modificationDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp firstSendDate;
	
	private String reference1;	
	
	private String reference2;	
	
	private String messageType;
	
	private String automaticMailModelName;

	private AlarmMessageStatus messageLogStatus;
	
	private String category;
    
	private String subject;

	private String content;

	private String audience;
	
	private String ccAudience;

	private Long maxSendAttempts;

	private Long sendAttempts;

	private String log;
	
	private String mode;
	
	private String username;
	
	private int fileCount;
	
	public AlarmMessageLogBrowserData(AlarmMessageLog message) {
		this.id = message.getId();
		this.creationDate = message.getCreationDate();
		this.modificationDate = message.getLastSendDate();
		this.firstSendDate = message.getFirstSendDate();
		this.messageType = message.getMessageType();
		this.messageLogStatus = message.getStatus();
		this.subject = message.getTitle();
		this.content = message.getContent();
		this.audience = message.getContactsAsString();
		this.ccAudience = message.getCcContacts();
		this.maxSendAttempts = message.getMaxSendCount();
		this.sendAttempts = message.getSendCount();
		this.log = message.getErrorMessage();
		this.username = message.getUsername();
		this.mode = message.getMode();
		this.reference1 = message.getReference1();
		this.reference2 = message.getReference2();
		this.category = message.getCategory();
		this.automaticMailModelName = message.getAutomaticMailModelName();
		this.fileCount = message.getFilesId() != null ? message.getFilesId().size() : 0;
	}

}
