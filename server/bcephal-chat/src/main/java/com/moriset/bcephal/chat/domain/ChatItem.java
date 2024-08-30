package com.moriset.bcephal.chat.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Document;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "ChatItem")
@Table(name = "BCP_CHAT_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatItem extends Persistent {

	private static final long serialVersionUID = 1653687862575627716L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_item_seq")
	@SequenceGenerator(name = "chat_item_seq", sequenceName = "chat_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long chatId;
	
	@Enumerated(EnumType.STRING)
	private ChatItemType type;
	
	@ManyToOne @JoinColumn(name = "document")
	private Document document;
	
	private String message;
	
	private int position;
	
	private Long senderId;
	
	private String senderName;
	
	private Long receiverId;
	
	private Long channelId;
	
	@Enumerated(EnumType.STRING)
	private ChatUserType receiverType;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	
	public ChatItem(){
		type = ChatItemType.MESSAGE;
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
	}
	
	@Override
	public Persistent copy() {
		return null;
	}

}
