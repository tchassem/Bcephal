/**
 * 
 */
package com.moriset.bcephal.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class AlarmMessage {

	public static final int MAX_SEND_ATTEMPS = 3;
	public static final String MAIL = "MAIL";
	public static final String SMS = "SMS";
	
	private String reference1;	
	private String reference2;	
	private String category;
	private String clientCode;
	private String automaticMailModelName;
		
	private String projectCode;
	
	private String username;
	
	private Integer maxSendCount;

	private String messageType;
	
	private RunModes mode;

	private String title;

	private String content;
	
	private String ccContacts;

	private List<String> contacts;
	
	private List<String> filesId;
	

	public AlarmMessage() {
		super();
		this.contacts = new ArrayList<>(0);
		this.filesId = new ArrayList<>(0);
		this.maxSendCount = MAX_SEND_ATTEMPS;
	}

	
}
