package com.moriset.bcephal.config.messaging;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.QueueConnection;
import jakarta.jms.TopicConnection;

public abstract class AbstractKeyStore {

	public static String TCP_URL_TEMPLATE = "failover:(mock://(tcp://%s:%s))?timeout=3000&updateURIsSupported=false";
	public static String CHECK_CONNECTION_UNMOCK_TCP_URL_TEMPLATE = "failover:(tcp://%s:%s)?timeout=3000&updateURIsSupported=false&startupMaxReconnectAttempts=3";

	@Autowired
	public HostAddress hostAddress;
	
	@Autowired
	KeystoreClient keystore;

	@Autowired
	public SharedUser sharedUser;

	protected String getBrockerUrl() {
		return String.format(TCP_URL_TEMPLATE, hostAddress.getIp(), hostAddress.getPort());
	}
	
	protected String getBrockerUrlCheckTemplate() {
		return String.format(CHECK_CONNECTION_UNMOCK_TCP_URL_TEMPLATE, hostAddress.getIp(), hostAddress.getPort());
	}

	protected ActiveMQConnectionFactory getActiveMQSslConnectionFactory() throws Exception {
		ActiveMQSslConnectionFactory factory = new CustomActiveMQSslConnectionFactory(getBrockerUrl());
		factory.setTrustAllPackages(true);
		URL keyStore = getClass().getResource("/com/moriset/bcephal/activemq/client/properties/client.keystore");
		URL trustStore = getClass().getResource("/com/moriset/bcephal/activemq/client/properties/client.ts");
		factory.setKeyStore(keyStore.toExternalForm());
		factory.setKeyStoreKeyPassword(keystore.getKeyStorePassword());
		factory.setKeyStorePassword(keystore.getKeyStorePassword());
		factory.setTrustStore(trustStore.toExternalForm());
		factory.setTrustStorePassword(keystore.getTrustPassword());
		return factory;
	}

	protected void setUser(ActiveMQConnectionFactory factory) {
		factory.setPassword(sharedUser.getPassword());
		factory.setUserName(sharedUser.getName());
	}

	
	protected class CustomActiveMQConnectionFactory extends ActiveMQConnectionFactory implements CustomConnectionFactory {
		public CustomActiveMQConnectionFactory() {
			super();
		}

		public CustomActiveMQConnectionFactory(String brokerURL) {
			super(brokerURL);
		}

		public CustomActiveMQConnectionFactory(URI brokerURL) {
			super(brokerURL);
		}

		@Override
		public Connection createConnection() throws JMSException {
			return super.createConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public Connection createConnection(String userName, String password) throws JMSException {
			return super.createConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
			return super.createTopicConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public TopicConnection createTopicConnection() throws JMSException {
			return super.createTopicConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
			return super.createQueueConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public QueueConnection createQueueConnection() throws JMSException {
			return super.createQueueConnection(sharedUser.getName(), sharedUser.getPassword());
		}
		
		@Override
		public ActiveMQConnectionFactory CustomCheckConnectionFactory() {
			return new CustomActiveMQConnectionFactory(getBrockerUrlCheckTemplate());
		}
	}
	
	public interface CustomConnectionFactory{
		/**
		 * 
		 * @return
		 * @throws JMSException
		 * @throws IOException
		 * @throws Exception
		 */
		default public boolean isAvailableServer()
				throws JMSException, IOException, Exception {
			boolean status = false;			
			 Connection connection = (Connection) createCheckConnectionFactory().createConnection();
			((ActiveMQConnection) connection).start();
			status = ((ActiveMQConnection) connection).isStarted();
			((Connection) connection).close();
			return status;
		}
		
		
		ActiveMQConnectionFactory CustomCheckConnectionFactory();
		
		default  ActiveMQConnectionFactory createCheckConnectionFactory() throws Exception {
			ActiveMQConnectionFactory factory = CustomCheckConnectionFactory();
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
	}

	protected class CustomActiveMQSslConnectionFactory extends ActiveMQSslConnectionFactory implements CustomConnectionFactory {

		public CustomActiveMQSslConnectionFactory() {
			super();
		}

		public CustomActiveMQSslConnectionFactory(String brokerURL) {
			super(brokerURL);
		}

		public CustomActiveMQSslConnectionFactory(URI brokerURL) {
			super(brokerURL);
		}

		@Override
		public Connection createConnection() throws JMSException {
			return super.createConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public Connection createConnection(String userName, String password) throws JMSException {
			return super.createConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
			return super.createTopicConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public TopicConnection createTopicConnection() throws JMSException {
			return super.createTopicConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
			return super.createQueueConnection(sharedUser.getName(), sharedUser.getPassword());
		}

		@Override
		public QueueConnection createQueueConnection() throws JMSException {
			return super.createQueueConnection(sharedUser.getName(), sharedUser.getPassword());
		}
		
		@Override
		public ActiveMQConnectionFactory CustomCheckConnectionFactory() {
			return new CustomActiveMQSslConnectionFactory(getBrockerUrlCheckTemplate());
		}
	}

}
