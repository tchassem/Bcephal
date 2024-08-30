package com.moriset.bcephal.messenger.client;

import java.io.IOException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.moriset.bcephal.messenger.client.AbstractKeyStore.CustomConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TopicConnection;


@Configuration
public class ReceiverConfig {
	public static final String SESSION_TOPIC = "SESSION_TOPIC";
	public static final String CONNECTION_TOPIC = "CONNECTION_TOPIC";
	public static final String SESSION = "SESSION";
	public static final String CONNECTION = "CONNECTION";
	public static final String ClientID = "BCEPHAL_MAIL_JMS";

	@Autowired
	@Qualifier(value = ProducerConfig.RECEIVER_ACTIVEMQ_CONNECTION_FACTORY)
	ActiveMQConnectionFactory activeMQConnectionFactory;

	@Bean(name = Infos.MAIL)
	@Primary
	ReceiverClient receiverMail() {
		return new ReceiverClient();
	}

	@Bean(name = Infos.SMS)
	ReceiverClient receiverSms() {
		return new ReceiverClient();
	}

	TopicConnection topicConnection;
	TopicConnection topicConnectionSms;

	Connection connection;

	interface ITopicConnection {
		TopicConnection getConnection();
	}

	interface IConnection {
		Connection getConnection();
	}

	public interface ITopicSession {
		Session getSession();
	}

	interface ISession {
		Session getSession();
	}

	@Bean(name = CONNECTION_TOPIC)
	@Primary
	ITopicConnection getConnectionTopic() {
		return () -> {
			if (topicConnection != null) {
				return topicConnection;
			}
			try {
				topicConnection = activeMQConnectionFactory.createTopicConnection();
				topicConnection.setClientID(ClientID);
				topicConnection.start();
			} catch (Exception e) {
				return null;
			}
			return topicConnection;
		};
	}

	@Bean(name = CONNECTION_TOPIC + Infos.SMS)
	ITopicConnection getConnectionTopicSms() {
		return () -> {
			if (topicConnectionSms != null) {
				return topicConnectionSms;
			}
			try {
				topicConnectionSms = activeMQConnectionFactory.createTopicConnection();
				topicConnectionSms.setClientID(ClientID + Infos.SMS);
				topicConnectionSms.start();
			} catch (Exception e) {
				return null;
			}
			return topicConnectionSms;
		};
	}

	@Bean(name = CONNECTION)
	IConnection getConnection() {
		return () -> {
			if (connection != null) {
				return connection;
			}
			try {
				connection = activeMQConnectionFactory.createConnection();
			} catch (JMSException e) {
				e.printStackTrace();
			}
			return connection;
		};
	}

	private boolean isAvailableServer() throws JMSException, IOException, Exception {
		return ((CustomConnectionFactory) activeMQConnectionFactory).isAvailableServer();
	}

	@Bean(name = SESSION_TOPIC + Infos.SMS)
	ITopicSession getSessionTopicSms() {
		return () -> {
			try {
				if (isAvailableServer()) {
					try {
						return ((ActiveMQConnection) getConnectionTopicSms().getConnection()).createTopicSession(true,
								Session.SESSION_TRANSACTED);
					} catch (JMSException e) {
						return null;
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}

		};
	}

	@Bean(name = SESSION_TOPIC)
	@Primary
	ITopicSession getSessionTopic() {
		return () -> {
			try {
				if (isAvailableServer()) {
					try {
						return ((ActiveMQConnection) getConnectionTopic().getConnection()).createTopicSession(true,
								Session.SESSION_TRANSACTED);
					} catch (JMSException e) {
						return null;
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		};
	}

	@Bean(name = SESSION)
	ISession getSession() {
		return () -> {
			try {
				if (isAvailableServer()) {
					try {
						return getConnection().getConnection().createSession(true, Session.SESSION_TRANSACTED);
					} catch (JMSException e) {
						return null;
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		};
	}
}