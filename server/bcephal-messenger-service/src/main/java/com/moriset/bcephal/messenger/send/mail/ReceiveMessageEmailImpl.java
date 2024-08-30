package com.moriset.bcephal.messenger.send.mail;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.messenger.model.AlarmMessage;
import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.model.BuildMessage;
import com.moriset.bcephal.messenger.properties.User;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogToSendRepository;

import jakarta.jms.BytesMessage;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@ConfigurationProperties(prefix = "bcephal.notification")
@Slf4j
@Data
public class ReceiveMessageEmailImpl implements BuildMessage {

	private User userMail;

	@Autowired
	@Qualifier(IMailService.iMailService_)
	IMailService mailService;
	
	@Autowired
	AlarmMessageLogToSendRepository alarmMessageLogToSendRepository;

	@Override
	public void work(Message message) throws Exception {

		if (message instanceof TextMessage) {
			TextMessage txtMsg = (TextMessage) message;
			log.debug("Received message from JMS server : {}", txtMsg.getJMSCorrelationID());
			log.trace("Message : {}", txtMsg.getText());

			ObjectMapper mapper = new ObjectMapper();
			AlarmMessage result = mapper.readValue(txtMsg.getText(), AlarmMessage.class);
			
			if(result != null && result.getTitle() != null) {
				log.debug("Saving message log in to send pool...");
				AlarmMessageLogToSend toSend = new AlarmMessageLogToSend(result);
				toSend = alarmMessageLogToSendRepository.save(toSend);
				log.debug("Message saved : {}", toSend.getId());
				log.trace("Saved Message log : {}", toSend);
			}

//			if (result.getTitle() != null) {
//				Email email = new Email(userMail, result.getContacts(), result.getTitle(), result.getContent(), result.getFilesId());
//				mailService.sendMail(email);
//			}

		} 
		else if (message instanceof StreamMessage) {
			StreamMessage smsg = (StreamMessage) message;
			int count = -1;
			OutputStream output = null;
			BufferedOutputStream bos = null;
			try {
				output = new FileOutputStream(smsg.getStringProperty("FILE.NAME"));
				bos = new BufferedOutputStream(output);
				byte[] buffer = new byte[10 * 1024 * 1024];
				while ((count = smsg.readBytes(buffer)) > 0) {
					bos.write(buffer, 0, count);
				}
				bos.flush();
			} finally {
				if (bos != null) {
					bos.close();
				}
				if (output != null) {
					output.close();
				}
			}
		} 
		else if (message instanceof ObjectMessage) {
			ActiveMQObjectMessage smsgo = (ActiveMQObjectMessage) message;
			Object object = smsgo.getObject();
			log.trace("Received message OBJECT3 from Destination : {}", object);

		} 
		else if (message instanceof BytesMessage) {
			BytesMessage smsg = BytesMessage.class.cast(message);
			byte[] resultbytes = new byte[Long.valueOf(smsg.getBodyLength()).intValue()];
			smsg.readBytes(resultbytes);
			String values = new String(resultbytes);
			log.trace("Received message BYTE from Destination : {}", values);
		} 
		else if (message instanceof MapMessage) {
			ActiveMQMapMessage sourceMap = ActiveMQMapMessage.class.cast(message);
			HashMap<String, Object> map = (HashMap<String, Object>) sourceMap.getContentMap();
			log.trace("Received message BYTE from Destination : {}", map);
		}

	}

}
