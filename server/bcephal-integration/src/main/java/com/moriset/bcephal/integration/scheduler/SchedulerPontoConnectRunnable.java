package com.moriset.bcephal.integration.scheduler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moriset.bcephal.config.messaging.MessengerClient;
import com.moriset.bcephal.domain.AlarmMessage;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.integration.domain.ConnectEntity;
import com.moriset.bcephal.integration.service.SchedulerPontoConnectRunner;
import com.moriset.bcephal.utils.AlarmCategory;
import com.moriset.bcephal.utils.AlarmVariables;

import jakarta.jms.JMSException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Moriset
 *
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPontoConnectRunnable  extends SchedulerRunnable {

	public SchedulerPontoConnectRunnable(ConnectEntity entity) {
		super(entity);
	}
	SchedulerPontoConnectRunner runner;
	BeanCreatingHandlerProvider<SchedulerPontoConnectRunner> provider;
	MessengerClient messengerClient;
	
	@Override
	protected void performAction() throws Exception {
		log.info("Entity : {}. SchedulerPlannerRunner : {}.!", entity.getName(), getEntity().getName());
		
		try {
			provider = new BeanCreatingHandlerProvider<>(SchedulerPontoConnectRunner.class);
			provider.setBeanFactory(getBeanFactory());
			runner = provider.getHandler();
			runner.setEntity(getEntity());
			runner.run();
			log.info("End to Run planner from Entity : {}. SchedulerPlannerRunner : {}.!", getEntity().getName(), getEntity().getName());
		} catch (Exception e) {
			log.debug("unexpected error while running SchedulerPlanner : {}",
					getEntity() != null ? getEntity().getName() : "", e);
			if(messengerClient == null) {
				messengerClient = getContext().getBean(MessengerClient.class);
			}
				if(messengerClient != null && StringUtils.hasText(entity.getEmails())) {
					String[] audience = entity.getEmails().split(";");
					if(entity.getEmails().contains(",")) {
						audience = entity.getEmails().split(",");
					}
					buildAndSendMessage(Arrays.asList(audience));
				}
			
			throw e;
		}finally {
			if(provider != null && runner != null) {
				provider.destroy(runner);
				runner = null;
				provider = null;
			}
		}
	}
	
	private void buildAndSendMessage(List<String> audiences) {
		if (audiences != null && !audiences.isEmpty()) {
			log.debug("Ponto Alarm : {}  - {} audience : {}", entity.getName(), AlarmMessage.MAIL, audiences);
			 String username = "B-CEPHAL";			 
			AlarmMessage msg = buildAlarmMessage(audiences, AlarmMessage.MAIL, username, entity.getProjectCode(), entity.getClientId());
			sendAlertMessage(msg);
		}
		else {
			log.debug("Ponto Alarm : {}  - There is no {} audience!", entity.getName());
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
	
	private AlarmMessage buildAlarmMessage(List<String> audiences, String messageType, String username, String projectCode, Long client) {
		log.debug("Building message to send.");		
		AlarmMessage message = new AlarmMessage();
		message.setUsername(username);
		message.setMode(RunModes.A);
		message.setAutomaticMailModelName(entity.getName());
		message.setCategory(AlarmCategory.FREE.name());
		message.setProjectCode(projectCode);
		message.setClientCode(client != null ? client.toString(): null);
		message.setTitle("Ponto connection Error");
		message.setContent(replaceMessageKeys(entity.getMessages(), username));
		message.setFilesId(null);
		message.setMessageType(messageType);
		message.setContacts(audiences);		
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
