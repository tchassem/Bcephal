package com.moriset.bcephal.config.messaging;

import java.io.IOException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.util.JMSExceptionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moriset.bcephal.config.messaging.AbstractKeyStore.CustomConnectionFactory;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TopicConnection;

@ConditionalOnBean(value = MessagingHandler.class)
@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Configuration
public class CustomReceiverConfig {
	public static final String SESSION_TOPIC = "SESSION_TOPIC";
	public static final String CONNECTION_TOPIC = "CONNECTION_TOPIC";
	public static final String SESSION = "SESSION";
	public static final String CONNECTION = "CONNECTION";
	public static final String ClientID = "BCEPHAL_MAIL_JMS_SERVEUR";
	
	
	@Autowired(required=false)
	@Qualifier(MessagingConfig.RECEIVER_ACTIVEMQ_CONNECTION_FACTORY)
	ActiveMQConnectionFactory activeMQConnectionFactory;

	@Autowired
	public HostAddress hostAddress;
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	private String getClientId() {
		long pid = ProcessHandle.current().pid();
		return applicationName + "___"+ hostAddress.getIpAddress().getCanonicalHostName() + "__" + hostAddress.getIp() + "__" + hostAddress.getPort() + "__" + pid;
	}
	
	class ITopicSession {
		TopicConnection topicConnection;
		Session session;
		
		synchronized public ActiveMQConnection getConnection() {
			if (topicConnection == null) {
				try {
					topicConnection = activeMQConnectionFactory.createTopicConnection();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
				if (topicConnection != null && !ActiveMQConnection.class.cast(topicConnection).isStarted()) {
					try {
					    topicConnection.setClientID(ClientID + getClientId());
						topicConnection.start();
					} catch (Exception e) {
						return null;
					}
				}
				return (ActiveMQConnection) topicConnection;
			
		}

		synchronized public Session getSession() throws JMSException {
			try {
				if(session != null) {
					return session;
				}
				if (isAvailableServer()) {
					ActiveMQConnection con = ((ActiveMQConnection) getConnection());
					if (con != null && con.isStarted()) {						
						session = con.createTopicSession(true, Session.SESSION_TRANSACTED);
						return session;
					}
				}
				throw JMSExceptionSupport.create(new Exception("Session not found"));
			} catch (Exception e) {
				throw JMSExceptionSupport.create(e);
			}
		}
		
		public Session getNewSession() throws JMSException {
			try {
				
				if (isAvailableServer()) {
					ActiveMQConnection con = ((ActiveMQConnection) getConnection());
					if (con != null && con.isStarted()) {
						return con.createTopicSession(true, Session.SESSION_TRANSACTED);
					}
				}
				throw JMSExceptionSupport.create(new Exception("Session not found"));
			} catch (Exception e) {
				throw JMSExceptionSupport.create(e);
			}
		}
	}

	private boolean isAvailableServer() throws JMSException, IOException, Exception {
		try {
			return ((CustomConnectionFactory) activeMQConnectionFactory).isAvailableServer();
		}
		catch (Exception e) {
			throw new BcephalException("Unable to connect to Mail server.");
		}
	}

	class ISession {
		Connection connection;
		Session session;
		
		public ActiveMQConnection getConnection() {
			if (connection != null) {
				return (ActiveMQConnection) connection;
			}
			try {
				connection = activeMQConnectionFactory.createConnection();
			} catch (JMSException e) {
				e.printStackTrace();
			}
			return (ActiveMQConnection) connection;
		}

		public Session getSession() throws JMSException {
			try {
				if(session != null) {
					return session;
				}
				if (isAvailableServer()) {
					ActiveMQConnection con = getConnection();
					if (con != null && con.isStarted()) {						
						return con.createSession(true, Session.SESSION_TRANSACTED);
					}
				}
				throw JMSExceptionSupport.create(new Exception("Session not found"));
			} catch (Exception e) {
				throw JMSExceptionSupport.create(e);
			}
		}
	}

	@ConditionalOnBean(value = MessagingHandler.class)
	@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
	@Bean(name = SESSION_TOPIC)
	ITopicSession getSessionTopic() {
		return new ITopicSession();
	}

	@ConditionalOnBean(value = MessagingHandler.class)
	@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
	@Bean(name = SESSION)
	ISession getSession() {
		return new ISession();
	}
}