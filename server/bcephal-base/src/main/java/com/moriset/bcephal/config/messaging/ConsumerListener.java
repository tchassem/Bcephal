package com.moriset.bcephal.config.messaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.config.messaging.CustomReceiverConfig.ITopicSession;

import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TopicSubscriber;

@ConditionalOnBean(value = MessagingHandler.class)
@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Component
public class ConsumerListener {

	Set<MessageConsumer> listners = new HashSet<MessageConsumer>();

	public void add(MessageConsumer consumer, MessageListener listener) {
		if (consumer != null && listener != null) {
			try {
				if(!listners.contains(consumer)) {
					consumer.setMessageListener(listener);
					listners.add(consumer);
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
	
//	public ConsumerListener() {
//		 startListeners();
//	}

	@Autowired(required = false)
	ITopicSession iSession;

	@Autowired(required = false)
	MessengerClient receiverClient;
	
	@Autowired(required = false)
	MessagingHandler projectHandler;
	
	private void startListener(String destinations_) throws JMSException {
			String destinations = PREFIX_TOPIC + destinations_;
			Session session = iSession.getSession();
			TopicSubscriber consumer = session.createDurableSubscriber(session.createTopic(destinations), destinations);
			add(consumer, receiverClient);
	}

	public static final String PREFIX_TOPIC = "bcephal_messenger.topic.";
	public static final String CLOSE_PROJECT = "/bcephal_messenger/close-project";

	private List<Timer> timers = new ArrayList<>();
	
	@ConditionalOnBean(value = MessagingHandler.class)
	@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
	@Autowired
	public void startListeners() {
		if(projectHandler != null && listners.size() == 0) {
			projectHandler.Destination().forEach(dest -> startListenersItem(dest));
		}
	}
	
	public void startListenersItem(String dest) {
		timers.add(new Timer());
		int index = timers.size();
		timers.get(index - 1).scheduleAtFixedRate(new TimerTask() {
			public void run() {
					try {						
						startListener(dest);
						timers.get(index - 1).cancel();
						timers.remove(index - 1);
					} catch (Exception e) {
						
					}
			}
		}, Calendar.getInstance().getTime(), (1 * 60 * 1000));
	}
	
}
