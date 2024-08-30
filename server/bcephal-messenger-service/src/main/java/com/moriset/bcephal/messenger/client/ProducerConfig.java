package com.moriset.bcephal.messenger.client;

import java.util.Properties;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;


@Configuration
public class ProducerConfig extends AbstractKeyStore {

	public static final String RECEIVER_ACTIVEMQ_CONNECTION_FACTORY = "receiverActiveMQConnectionFactory";
	public static final String SENDER_ACTIVEMQ_CONNECTION_FACTORY = "senderActiveMQConnectionFactory";

	@Bean(name = SENDER_ACTIVEMQ_CONNECTION_FACTORY)
	ActiveMQConnectionFactory senderActiveMQConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory();
		return activeMQConnectionFactory;
	}

	private ActiveMQConnectionFactory getActiveMQConnectionFactory() {
		ActiveMQConnectionFactory factory = null;
		if (hostAddress.isSecure()) {
			try {
				factory = getActiveMQSslConnectionFactory();
			} catch (Exception e) {
			}
		}
		if (factory == null) {
			factory = new CustomActiveMQConnectionFactory(getBrockerUrl());
		}
		factory.setTrustAllPackages(true);
		factory.setUseCompression(true);
		factory.setUseAsyncSend(true);
		factory.setUseRetroactiveConsumer(true);
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(5);
		redeliveryPolicy.setBackOffMultiplier(3000);
		redeliveryPolicy.setRedeliveryDelay(3000);
		factory.setRedeliveryPolicy(redeliveryPolicy);
		return factory;
	}

	@Bean(name = RECEIVER_ACTIVEMQ_CONNECTION_FACTORY)
	ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory();
		Properties props = activeMQConnectionFactory.getProperties();
		if (props == null) {
			props = new Properties();
		}
		props.setProperty("deliveryMode", "2");
		props.setProperty("explicitQosEnabled", "true");
		return activeMQConnectionFactory;
	}

	@Bean
	CachingConnectionFactory cachingConnectionFactory() {
		return new CachingConnectionFactory(senderActiveMQConnectionFactory());
	}

	@Bean
	JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
		jmsTemplate.setDeliveryPersistent(true);
		jmsTemplate.setPubSubDomain(true);
		return jmsTemplate;
	}

	@Bean
	Producer sender() {
		return new Producer();
	}
}
