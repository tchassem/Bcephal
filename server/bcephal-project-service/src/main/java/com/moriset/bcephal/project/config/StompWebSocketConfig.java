//package com.moriset.bcephal.project.config;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//	@Value("${activemq.address.ip:localhost}")
//	protected String activeServerHost;
//	@Value("${activemq.address.tcp-port:61627}")
//	protected Integer activeserverTcpPort;
//	@Value("${activemq.address.stomp-port:61628}")
//	protected Integer activeserverStompPort;
//
//	@Value("${activemq.enable:false}")
//	protected Boolean activeEnable;
//
//	@Value("${activemq.user.name:mksaf32ariouser45pok}")
//	private String userName;
//	@Value("${activemq.user.password:mk+/sD(f4782*SrTo?User45kWq}")
//	private String userPassword;
//
//	@Override
//	public void configureMessageBroker(MessageBrokerRegistry config) {
//		if (Boolean.FALSE.equals(activeEnable)) {
//			config.enableStompBrokerRelay("/topic")
//					.setRelayHost(replaceSpecialValues(activeServerHost))
//					.setRelayPort(activeserverStompPort)
//					.setAutoStartup(true)
//					.setSystemLogin(userName)
//					.setSystemPasscode(userPassword)
//					.setClientLogin(userName)
//					.setClientPasscode(userPassword);
//			config.setApplicationDestinationPrefixes("/app");
//		}
//	}
//
//	public InetAddress getIpAddress() {
//		try {
//			return InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private final String LOCALHOST = "localhost";
//
//	public String replaceSpecialValues(String property) {
//		if (!StringUtils.isBlank(property)) {
//			if (property.contains(LOCALHOST)) {
//				InetAddress address = getIpAddress();
//				if (address != null) {
//					property = StringUtils.replace(property, LOCALHOST, address.getHostAddress());
//				}
//			}
//		}
//		return property;
//	}
//}
