package com.moriset.bcephal.messenger.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.messenger.model.AlarmMessage;
import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.model.AlarmMessageStatus;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class StatisticsService {

	@Autowired
	private EmailService emailService;
	
	@Value("${bcephal.messenger.administrator.emails}")
	private String adminContacts;
	
	
	public void sendStat() {
		log.debug("Try to send stat...");
		try {
			if(adminContacts != null || adminContacts.isEmpty()) {
				log.error("Unable to send stat. Administrator contacts is undefined!");
				return;
			}
			AlarmMessageLogToSend message = new AlarmMessageLogToSend();
			//message.setContacts(adminContacts);
			message.setTitle("B-cephal messenger : Stat");
			message.setContent("Stat");;
			message.setUsername("B-CEPHAL");			
			message.setMessageType(AlarmMessage.MAIL);			
			message.setContactsImpl(adminContacts);
			message.setStatus(AlarmMessageStatus.IN_PROCESS);
			message.setMaxSendCount(1L);
			message.setSendCount(0L);
			message.setMode("A");
			emailService.sendSimpleMessage(message);
			log.debug("Stat sent!");
		}
		catch (Exception e) {  		
			log.error("Unable to send stat.", e);
		}
	}
	
	public void sendError() {
		sendError("B-cephal messenger : Error notification", "Error notification");
	}
	
	public void sendError(String title, String content) {
		log.debug("Try to send error notification...");
		try {
			if(adminContacts == null || adminContacts.isEmpty()) {
				log.error("Unable to send error notification. Administrator contacts are undefined!");
				return;
			}
			log.debug("Administrator contacts : {}", adminContacts);
			AlarmMessageLogToSend message = new AlarmMessageLogToSend();
			//message.setContacts(adminContacts);
			message.setTitle(title);
			message.setContent(content);
			message.setUsername("B-CEPHAL");			
			message.setMessageType(AlarmMessage.MAIL);			
			message.setContactsImpl(adminContacts);
			message.initContacts();
			message.setStatus(AlarmMessageStatus.IN_PROCESS);
			message.setMaxSendCount(1L);
			message.setSendCount(0L);
			message.setMode("A");
			emailService.sendSimpleMessage(message);
			log.debug("Error notification sent!");
		}
		catch (Exception e) {  		
			log.error("Unable to send error notification.", e);
		}
	}
	
}