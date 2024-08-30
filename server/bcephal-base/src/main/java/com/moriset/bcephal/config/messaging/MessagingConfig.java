package com.moriset.bcephal.config.messaging;

import java.util.Properties;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBean(value = MessagingHandler.class)
@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Configuration
public class MessagingConfig extends AbstractKeyStore{
	
	public static final String RECEIVER_ACTIVEMQ_CONNECTION_FACTORY = "receiverActiveMQConnectionFactory";
	public static final String SENDER_ACTIVEMQ_CONNECTION_FACTORY = "senderActiveMQConnectionFactory";

	
	@ConditionalOnBean(value = MessagingHandler.class)
	@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
	@Bean(name = SENDER_ACTIVEMQ_CONNECTION_FACTORY)
	ActiveMQConnectionFactory senderActiveMQConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory();
		return activeMQConnectionFactory;
	}
	
	private ActiveMQConnectionFactory getActiveMQConnectionFactory(){
		ActiveMQConnectionFactory factory = null;
			if(hostAddress.isSecure()) {
				try {
					factory = getActiveMQSslConnectionFactory();
				} catch (Exception e) {}
			}
			if(factory == null) {
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
	
	@ConditionalOnBean(value = MessagingHandler.class)
	@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
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
}
