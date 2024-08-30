package com.moriset.bcephal.config.messaging;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.config.messaging.CustomReceiverConfig.ITopicSession;

import jakarta.jms.BytesMessage;
import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import jakarta.jms.TopicSubscriber;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnBean(value = MessagingHandler.class)
@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Slf4j
@Component
public class MessengerClient implements MessageListener {

	@Autowired(required = false)
	ITopicSession isSession;
	
	@Autowired(required = false)
	MessagingHandler messagingHandler;
	
	@Autowired
	ObjectMapper Mapper;
	

	private static final long TIME_WAI = 500;
	
	private static final Object lock = new Object();
	private static final Object lockListner = new Object();
	private static final Object lockStartListner = new Object();
	
	public boolean SendMail(Object mail) throws JMSException, JsonProcessingException {
		String json = Mapper.writeValueAsString(mail);
		return SendPersitentMessage(new Infos(json, Infos.MAIL));
	}
	
	public boolean SendSms(Object mail) throws JMSException, JsonProcessingException {
		String json = Mapper.writeValueAsString(mail);
		return SendPersitentMessage(new Infos(json, Infos.SMS));
	}

	@SuppressWarnings("unchecked")
	public boolean SendMessage(Infos infos) throws JMSException {
		synchronized (lock) {
			Session ses = isSession.getNewSession();
			try {
				MessageProducer producer = ses.createProducer(ses.createTopic(ConsumerListener.PREFIX_TOPIC + infos.getDestination()));
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
					Map map = (Map) infos.getMessage();
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
				msg.setStringProperty("JMSDeliveryMode", "" + DeliveryMode.NON_PERSISTENT + "");
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
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
	
	@SuppressWarnings("unchecked")
	public boolean SendPersitentMessage(Infos infos) throws JMSException {
		synchronized (lock) {
			Session ses = isSession.getNewSession();
			try {
				MessageProducer producer = ses.createProducer(ses.createTopic(ConsumerListener.PREFIX_TOPIC + infos.getDestination()));
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
					Map map = (Map) infos.getMessage();
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
	

	@Override
	public void onMessage(Message message) {
		synchronized (lockListner) {
			try {
				if (message instanceof TextMessage) {
					TextMessage txtMsg = (TextMessage) message;
					log.trace("Received message TEXT from Destination : {}", txtMsg.getText());
					if (messagingHandler != null) {
						messagingHandler.doWork(txtMsg.getText(), message.getJMSDestination());
					}
				} else if (message instanceof StreamMessage) {
					ActiveMQStreamMessage smsg = (ActiveMQStreamMessage) message;
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
					if (messagingHandler != null) {
						messagingHandler.doWork(smsg.getStringProperty("FILE.NAME"), message.getJMSDestination());
					}
				} else if (message instanceof ObjectMessage) {
					ActiveMQObjectMessage smsgo = (ActiveMQObjectMessage) message;
					Object object = smsgo.getObject();
					log.info("Received message OBJECT3 from Destination : {}", object);
				} else if (message instanceof BytesMessage) {
					BytesMessage smsg = BytesMessage.class.cast(message);
					byte[] resultbytes = new byte[Long.valueOf(smsg.getBodyLength()).intValue()];
					smsg.readBytes(resultbytes);
					if (messagingHandler != null) {
						messagingHandler.doWork(resultbytes, message.getJMSDestination());
					}
					String values = new String(resultbytes);
					log.info("Received message BYTE from Destination : {}", values);
				} else if (message instanceof MapMessage) {
					ActiveMQMapMessage sourceMap = ActiveMQMapMessage.class.cast(message);
					HashMap<String, Object> map = (HashMap<String, Object>) sourceMap.getContentMap();
					if (messagingHandler != null) {
						messagingHandler.doWork(map, message.getJMSDestination());
					}
					log.info("Received message MAP from Destination : {}", map);
				}
				message.acknowledge();
				session.commit();
			} catch (Exception e) {
				log.debug(e.getMessage());
			} finally {
				try {
					message.acknowledge();
				} catch (JMSException e) {
					log.debug(e.getMessage());
				}
			}
		}
	}

	private TopicSubscriber consumer;
	Session session;
	
    void startListener(ITopicSession isSession) throws JMSException{
		synchronized (lockStartListner) {
			if(consumer == null) {
				String destinations = ConsumerListener.PREFIX_TOPIC + "received";
				session = isSession.getSession();
				consumer = session.createDurableSubscriber(session.createTopic(destinations), destinations);
				consumer.setMessageListener(this);
			 }
		}
	 }
	
	private Timer timer = null;

	@ConditionalOnBean(value = MessagingHandler.class)
	@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
	@Autowired
	void startListeners(ITopicSession isSession) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
					try {
						startListener(isSession);
						timer.cancel();
					} catch (Exception e) {						
					}
			}
		}, Calendar.getInstance().getTime(), (1 * 60 * 1000));
	}

}