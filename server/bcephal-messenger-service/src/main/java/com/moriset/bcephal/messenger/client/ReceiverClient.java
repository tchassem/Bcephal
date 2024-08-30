package com.moriset.bcephal.messenger.client;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.moriset.bcephal.messenger.client.ReceiverConfig.ITopicSession;
import com.moriset.bcephal.messenger.model.BuildMessage;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TopicSubscriber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceiverClient implements MessageListener {

	private TopicSubscriber consumer;
	public String sourceTopic = "bcephal_messenger.topic.";

	ITopicSession iSession;
	Session session;
	BuildMessage buildMessage;

	public void onMessage(Message message) {
		synchronized (this) {
			try {
				if (buildMessage != null) {
					buildMessage.work(message);
				}
				try {
					message.acknowledge();
					session.commit();
				} catch (JMSException e) {
					log.error(e.getMessage());
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	Timer timer;

	public void start(String destination, ITopicSession iSession, BuildMessage buildMessage) {
		sourceTopic += destination;
		this.iSession = iSession;
		this.buildMessage = buildMessage;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					startListener();
					timer.cancel();
				} catch (Exception e) {
				}
			}
		}, Calendar.getInstance().getTime(), (1 * 60 * 1000));
	}

	private void startListener() throws JMSException {
		session = iSession.getSession();
		consumer = session.createDurableSubscriber(session.createTopic(sourceTopic), sourceTopic);
		consumer.setMessageListener(this);
	}
}