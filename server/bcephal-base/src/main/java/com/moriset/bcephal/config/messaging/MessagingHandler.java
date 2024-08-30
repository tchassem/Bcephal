package com.moriset.bcephal.config.messaging;

import java.util.Arrays;
import java.util.List;

import org.apache.activemq.command.ActiveMQTopic;

public interface MessagingHandler {

	default public void doWork(Object message, Object destination) {
		boolean isCloseProject = false;
		boolean isSms = false;
		boolean isMail = false;
		if (destination != null && destination instanceof ActiveMQTopic) {
			isCloseProject = ((ActiveMQTopic) destination).getPhysicalName()
					.endsWith(ConsumerListener.PREFIX_TOPIC + ConsumerListener.CLOSE_PROJECT);
			isMail = ((ActiveMQTopic) destination).getPhysicalName()
					.endsWith(ConsumerListener.PREFIX_TOPIC + Infos.MAIL);
			isSms = ((ActiveMQTopic) destination).getPhysicalName().endsWith(ConsumerListener.PREFIX_TOPIC + Infos.SMS);
		}
		if (isCloseProject) {
			closeProject((String) message);
		} else if (isMail) {
			receiveMail((String) message);
		} else if (isSms) {
			receiveSms((String) message);
		}
	}

	default public List<String> Destination() {
		return Arrays.asList(ConsumerListener.CLOSE_PROJECT, Infos.MAIL, Infos.SMS);
	}

	void closeProject(String message);

	default public void receiveMail(Object message) {

	}

	default public void receiveSms(Object message) {

	}

}
