package com.moriset.bcephal.messenger.client;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.messenger.client.ReceiverConfig.ISession;
import com.moriset.bcephal.messenger.client.ReceiverConfig.ITopicSession;

import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Producer {


	private final long TIME_WAI = 10000;

	public static String PREFIX_TOPIC = "bcephal_messenger.topic.";

	@Autowired
	ISession isession;

	@Autowired
	ITopicSession isSession;

	public boolean SendMessage(Infos infos) throws JMSException {
		synchronized (this) {
			Session ses = isSession.getSession();
			try {
				MessageProducer producer = ses.createProducer(ses.createTopic(PREFIX_TOPIC + infos.getDestination()));
				Message msg = null;
				if (infos.getMessage() instanceof String) {
					ActiveMQTextMessage msge = new ActiveMQTextMessage();
					msge.setText((String) infos.getMessage());
					msg = msge;
				} else if (infos.getMessage() instanceof byte[]) {
					ActiveMQBytesMessage streamMessage = new ActiveMQBytesMessage();
					msg = streamMessage;
					streamMessage.writeBytes((byte[]) infos.getMessage());
					streamMessage.storeContent();
				} else if (infos.getMessage() instanceof Map) {
					@SuppressWarnings("rawtypes")
					Map<?, ?> map = (Map) infos.getMessage();
					ActiveMQMapMessage mapObject = new ActiveMQMapMessage();
					msg = mapObject;
					map.keySet().forEach(key -> {
						try {
							mapObject.setObject((String) key, map.get(key));
						} catch (JMSException e) {
							e.printStackTrace();
						}
					});
					mapObject.storeContent();
				} else if (infos.getMessage() instanceof InputStream) {
					ActiveMQStreamMessage streamMessage = new ActiveMQStreamMessage();
					streamMessage.writeBytes((byte[]) infos.getMessage());
					streamMessage.storeContent();
					msg = streamMessage;
				} else {
					ActiveMQObjectMessage msge = new ActiveMQObjectMessage();
					msge.setObject((Serializable) infos.getMessage());
					msge.storeContent();
					msg = msge;
				}
				msg.setStringProperty("JMSDeliveryMode", "" + DeliveryMode.PERSISTENT + "");
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				producer.send(msg);
				producer.close();
				ses.commit();
				Thread.sleep(TIME_WAI);
				return true;
			} catch (Exception e) {
				log.error("--sendFile fail--", e);
			} finally {
				try {
					if (ses != null) {
						ses.close();
					}
				} catch (JMSException e) {
				}
			}
			return false;
		}
	}
}