package com.moriset.bcephal.chat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.MainObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "Chat")
@Table(name = "BCP_CHAT")
@Data
@EqualsAndHashCode(callSuper = false)
public class Chat extends MainObject {

	private static final long serialVersionUID = 6918238719062964826L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq")
	@SequenceGenerator(name = "chat_seq", sequenceName = "chat_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
			
	private String subjectType;
	
	private String subjectName;
	
	private Long subjectId;
	
	private String owner;
	
	@Enumerated(EnumType.STRING)
	private ChatStatus status;
	
	
	public Chat() {
		super();
		status = ChatStatus.OPENED;
	}
	
	@JsonIgnore
	public Chat copy() {		
		return null;
	}
}
