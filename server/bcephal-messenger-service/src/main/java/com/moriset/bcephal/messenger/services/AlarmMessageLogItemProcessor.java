/**
 * 
 */
package com.moriset.bcephal.messenger.services;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.messenger.controller.BcephalException;
import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.model.AlarmMessageStatus;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogToSendRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Slf4j
public class AlarmMessageLogItemProcessor implements ItemProcessor<AlarmMessageLogToSend, AlarmMessageLogToSend>{
		
	@Autowired
	private EmailService emailService;
	
	@Autowired
	AlarmMessageLogToSendRepository alarmMessageLogToSendRepository;
	
	@Autowired
	ParameterService parameterService;
	
	@Value("${bcephal.messenger.white.list}")
	String whiteList;
	
	@Override
	public AlarmMessageLogToSend process(AlarmMessageLogToSend item) throws Exception {		
		log.debug("Try to process message : {}", item.getId());
		log.trace("Item : {}", item);
		
		log.debug("Save fail message : {}", item.getId());
		boolean isPrensent = alarmMessageLogToSendRepository.findById(item.getId()).isPresent();
		if(isPrensent) {
			if(item.canBeSend()) {
				item.updateSendDate();
				boolean allowInvoices = parameterService.isBypassWhiteList();
				if(allowInvoices) {
					if(item.isMail()) {
						sendEmail(item);
					}
					else if(item.isSms()) {
						sendSms(item);
					}
				}
				else if(validateWhiteList(item)) {					
					if(item.isMail()) {
						log.debug("Emails are authirized...");
						sendEmail(item);
					}
					else if(item.isSms()) {
						log.debug("Phones are authirized...");
						sendSms(item);
					}
				}
				else {
					log.debug("Invoices mail are disable...");
					return null;
				}
			}
			else {
				log.debug("No more posiblility to send this mail : {}", item.getId());
				item.setStatus(AlarmMessageStatus.FAIL);
				item.setErrorMessage("No more posiblility to send this mail!");
			}		
		}
		else {
			log.debug("Message : {} is no longer prensent in pending messages list", item.getId());
		}
		
		log.debug("Message processed! : {}", item.getId());
		log.trace("prcessed item {}", item);
		return item;
				
	}
	
	private boolean validateWhiteList(AlarmMessageLogToSend item) {
		if(StringUtils.hasText(whiteList)) {
			boolean valid = true;
			String[] domains = whiteList.split(";");
			for(String email : item.getContacts()) {
				valid = containsDomain(email, domains);
				log.trace("Email '{}' in white list ? : {}", email, valid);
				if(!valid) {
					break;
				}
			}
			return valid;
		}
		return false;
	}
	
	private boolean containsDomain(String email, String[] domains) {
		for(String domain : domains) {
			if(email.toUpperCase().contains(domain.trim().toUpperCase())) {
				log.trace("Email '{}' in white list domain '{}'!", email, domain);
				return true;
			}
		}
		return false;
	}
		
	private void sendEmail(AlarmMessageLogToSend item) {
		log.trace("Try to send email message : {}", item.getId());
		try {
			item.initContacts();
			if(item.getContacts().size() == 0) {
				throw new BcephalException("List of contacts is empty!");
			}
			emailService.sendSimpleMessage(item);
			item.setStatus(AlarmMessageStatus.SENT);
			item.setErrorMessage(null);
			item.setErrorCode(null);
			log.trace("Email message sent! : {}", item.getId());
		}
		catch (Exception e) {
			String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();    		
			log.error("Unable to send email message : {}", item.getId(), e);
			if(!item.canBeSend()) {
				item.setStatus(AlarmMessageStatus.FAIL);
			}
			item.setErrorMessage(message);
		}
	}
	
	private void sendSms(AlarmMessageLogToSend item) {
		log.debug("Try to send SMS...");
		try {
			
			item.setStatus(AlarmMessageStatus.SENT);
			item.setErrorMessage(null);
			item.setErrorCode(null);
			log.debug("SMS send!");
		}
		catch (Exception e) {
			log.error("Unable to send SMS : {}", item.getId(), e);
			if(item.canBeSend()) {
				item.setStatus(AlarmMessageStatus.FAIL);
			}
			item.setErrorMessage(e.getMessage());
		}
	}

}
