package com.moriset.bcephal.chat.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "ChatItemUser")
@Table(name = "BCP_CHAT_ITEM_USER")
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatItemUser extends Persistent {

	private static final long serialVersionUID = -7835847164598914771L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_item_user_seq")
	@SequenceGenerator(name = "chat_item_user_seq", sequenceName = "chat_item_user_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long chatId;
	
	private Long receiverId;
	
	private Long itemReceiverId;
	
	private Long lastItemId;
	
	private Long channelId;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	
	public ChatItemUser(){
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
	}
	
	public ChatItemUser(Long chatId, Long receiverId, Long itemReceiverId, Long channelId){
		this.chatId = chatId;
		this.receiverId = receiverId;
		this.itemReceiverId = itemReceiverId;
		this.channelId = channelId;
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public Persistent copy() {
		return null;
	}

}
