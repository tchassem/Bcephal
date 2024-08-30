/**
 * 
 */
package com.moriset.bcephal.alarm.service;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.alarm.domain.AlarmAttachment;
import com.moriset.bcephal.alarm.domain.AlarmAttachmentType;
import com.moriset.bcephal.alarm.domain.AlarmAudience;
import com.moriset.bcephal.alarm.domain.AlarmAudienceType;
import com.moriset.bcephal.alarm.domain.EmailType;
import com.moriset.bcephal.config.messaging.MessengerClient;
import com.moriset.bcephal.domain.AlarmMessage;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.security.domain.UserData;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.service.condition.ConditionExpressionEvaluator;

import jakarta.jms.JMSException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author EMMENI Emmanuel
 *
 */
@Data
@Slf4j
@Component
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AlarmRunner {
		
	@Autowired(required = false)
	MessengerClient messengerClient;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	ConditionExpressionEvaluator evaluator;

	public AlarmRunner() {
		
	}
	
	
	public String sendAlarm(Alarm alarm, String username, RunModes mode, String projectCode, Long client) {
		log.debug("Try to send an alarm : {}", alarm.getName());
		log.debug("Alarm : {}  - Try to evaluate condition...", alarm.getName());		
		if(evaluator.isValidCondExpr(alarm.getCondition())) {	
			log.debug("Alarm : {}  - Condition is valid!", alarm.getName());
			if(alarm.isSendEmail()) {
				log.debug("Alarm : {}  - Build mail audience...", alarm.getName());
				List<String> audiences = buildAudiences(alarm, true, false);
				String ccAudiences = buildCcAudiences(alarm, true, false);
				buildAndSendMessage(alarm, audiences, ccAudiences, AlarmMessage.MAIL, username, mode, projectCode, client);
			}
			if(alarm.isSendSms()) {
				log.debug("Alarm : {}  - Build SMS audience...", alarm.getName());
				List<String> audiences = buildAudiences(alarm, false, true);
				buildAndSendMessage(alarm, audiences, null, AlarmMessage.SMS, username, mode, projectCode, client);
			}
			return null;
		}
		else {
			log.debug("Alarm : {}  - Condition is not valid!", alarm.getName());
			//throw new BcephalException("Unable to send alarm : Condition is not valid!");
			return "Unable to send alarm : Condition is not valid!";
		}
	}
	
	private List<String> buildAudiences(Alarm alarm, boolean forMail, boolean forSms){
		List<String> audiences = new ArrayList<>();
		for(AlarmAudience audience : alarm.getAudienceListChangeHandler().getItems()) {
			if(audience.getEmailType() != EmailType.CC) {
				List<String> address = getAudienceAddress(audience, forMail, forSms);
				if(address.size() > 0) {
					for(String a : address) {
						if(StringUtils.hasText(a)) {
							audiences.add(a);
						}
					}
				}			
				else {
					log.debug("Wrong audience address : {}", audience);
				}
			}
		}
		return audiences;
	}
	
	private String buildCcAudiences(Alarm alarm, boolean forMail, boolean forSms){
		String audiences = "";
		String coma = "";
		for(AlarmAudience audience : alarm.getAudienceListChangeHandler().getItems()) {
			if(audience.getEmailType() == EmailType.CC) {
				List<String> address = getAudienceAddress(audience, forMail, forSms);
				if(address.size() > 0) {
					for(String a : address) {
						if(StringUtils.hasText(a)) {
							audiences += coma + a;
							coma = ";";
						}
					}
				}
				else {
					log.debug("Wrong audience address : {}", audience);
				}
			}			
		}
		return audiences;
	}
	
	private List<String> getAudienceAddress(AlarmAudience audience, boolean forMail, boolean forSms) {
		List<String> address = new ArrayList<>();
		if(audience.getAudienceType() == AlarmAudienceType.FREE) {
			address.add(forMail ? audience.getEmail() : forSms ? audience.getPhone() : null);
		}
		else if(audience.getAudienceType() == AlarmAudienceType.USER && audience.getUserOrProfilId() != null) {
			UserData user = securityService.getUserById(audience.getUserOrProfilId());
			if(user != null) {
				address.add(user.getEmail());
			}
			else {
				log.debug("User not found : {}", audience.getUserOrProfilId());
			}
		}
		else if(audience.getAudienceType() == AlarmAudienceType.PROFILE && audience.getUserOrProfilId() != null) {
			List<UserData> users = securityService.getUsersByProfileId(audience.getUserOrProfilId());
			if(users.size() > 0) {
				for(UserData user : users) {
					address.add(user.getEmail());
				}
			}
			else {
				log.debug("User not found : {}", audience.getUserOrProfilId());
			}
		}
		return address;
	}
	
	
	private void buildAndSendMessage(Alarm alarm, List<String> audiences, String ccAudiences, String type, String username, RunModes mode, String projectCode, Long client) {
		if (audiences != null && !audiences.isEmpty()) {
			log.debug("Alarm : {}  - {} audience : {}", alarm.getName(), type, audiences);
			AlarmMessage msg = buildAlarmMessage(alarm, audiences, ccAudiences, type, username, mode, projectCode, client);
			sendAlertMessage(msg);
		}
		else {
			log.debug("Alarm : {}  - There is no {} audience!", alarm.getName());
		}
	}

	private void sendAlertMessage(AlarmMessage msg) {
		log.debug("Try to send an alert message");
		
		try {
			if (msg.getMessageType().equalsIgnoreCase(AlarmMessage.MAIL)) {
				messengerClient.SendMail(msg);
			} else {
				messengerClient.SendSms(msg);
			}
		} catch (JsonProcessingException e) {
			log.error("JSON formatting error: {}", e.getMessage());
		} catch (JMSException e) {
			log.error("Message error: {}", e.getMessage());
		}
		
		log.debug("Message has been successfully sent.");
	}
	
	/**
	 * 
	 * @param audiences
	 * @param messageType
	 * @return
	 */
	private AlarmMessage buildAlarmMessage(Alarm alarm, List<String> audiences, String ccAudiences, String messageType, String username, RunModes mode, String projectCode, Long client) {
		log.debug("Building message to send.");		
		AlarmMessage message = new AlarmMessage();
		message.setUsername(username);
		message.setMode(mode);
		message.setAutomaticMailModelName(alarm.getName());
		message.setCategory(alarm.getCategory() != null ? alarm.getCategory().name() : null);
		message.setProjectCode(projectCode);
		message.setClientCode(client != null ? client.toString(): null);
		if (messageType.equalsIgnoreCase(AlarmMessage.MAIL)) {
			message.setTitle(replaceMessageKeys(alarm.getEmailTitle(), username));
			message.setContent(replaceMessageKeys(alarm.getEmail(), username));
			message.setFilesId(null);
			for(AlarmAttachment attachment : alarm.getAttachmentListChangeHandler().getItems()) {
				if(attachment.getAttachmentType() == AlarmAttachmentType.REPORT_GRID) {
//					List<String> files = grilleService.export(null, Locale.ENGLISH);
//					message.setFilesId(files);
				}
			}
		} 
		else if (messageType.equalsIgnoreCase(AlarmMessage.SMS)) {
			message.setContent(replaceMessageKeys(alarm.getSms(), username));
		}
		message.setMessageType(messageType);
		message.setContacts(audiences);	
		message.setCcContacts(ccAudiences);	
		return message;
	}
	
	private String replaceMessageKeys(String message, String username) {	
		if(StringUtils.hasText(message)) {
			AlarmVariables alarmKeys = new AlarmVariables();
			Date date = new Date();
			message = message.replace(alarmKeys.USER, username);
			message = message.replace(alarmKeys.CURRENT_TIME, new SimpleDateFormat("HH:mm:ss").format(date));
			message = message.replace(alarmKeys.CURRENT_DATE, new SimpleDateFormat("yyyy/MM/dd").format(date));
			message = message.replace(alarmKeys.CURRENT_DATE_TIME, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));	
		}
		return message;
	}

	

}
