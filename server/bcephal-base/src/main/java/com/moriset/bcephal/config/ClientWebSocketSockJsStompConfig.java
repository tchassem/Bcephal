//package com.moriset.bcephal.config;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.CertificateException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.net.ssl.KeyManager;
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.TrustManagerFactory;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.tomcat.websocket.WsWebSocketContainer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.messaging.converter.StringMessageConverter;
//import org.springframework.messaging.simp.stomp.StompSessionHandler;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.Transport;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
//
//@Configuration
//@Import(SchedulerConfiguration.class)
//public class ClientWebSocketSockJsStompConfig {
//
//	@Value("${gateway-host}")
//	private String gatewayHost;
//	@Value("${gateway-port}")
//	private String gatewayPort;
//
//	@Value("${gateway-protocol}")
//	private String gatewayProtocol;
//
//	@Value("${activemq.enable:false}")
//	protected Boolean activeEnable;
//	
//	
//	@Value("${server.ssl.key-store:classpath:bcephalsecurity.jks}")
//	private String keyStore;
//	
//	@Value("${server.ssl.key-store-password:_bcphlD_.541}")
//	private String keyStorePwd;
//	
//	@Value("${server.ssl.keyStoreType:JKS}")
//	private String keyStoreType;
//	
//	@Value("${server.ssl.key-alias:bcephal-alias}")
//	private String keyAlias;
//	
//
//	private String gatewayUrl() {
//		String proto = "ws";
//		if (!StringUtils.isBlank(gatewayProtocol) && gatewayProtocol.toLowerCase().equals("https")) {
//			proto = "wss";
//		}
//		return proto + "://" + gatewayHost + ":" + gatewayPort;
//	}
//
//	@Bean
//	public WebSocketClient webSocketClient() {
//		return new StandardWebSocketClient();
//	}
//
//	@Autowired
//	@Qualifier("poolScheduler")
//	TaskScheduler taskScheduler;
//
//	
//	@Autowired
//	RestTemplate restTemplate;
//	
//	@Autowired
//	ResourceLoader ResourceLoader;
//	
//	@Bean
//	@ConditionalOnProperty(prefix = "", name = "gateway-protocol", havingValue = "http")
//	public StompSessionHandler stompSessionHandler(WebSocketClient webSocketClient) {
//		return new ClientStompSessionHandler(gatewayUrl(), webSocketClient, taskScheduler, activeEnable);
//	}
//	
//	@Bean
//	@ConditionalOnProperty(prefix = "", name = "gateway-protocol", havingValue = "https")
//	public ClientStompSessionHandler client(WebSocketClient webSocketClient) {
//		Map<String, Object> userProperties = new HashMap<String, Object>();
//		try {
//			userProperties.put("org.apache.tomcat.websocket.SSL_CONTEXT", getDefaulSSLContext());
//			((StandardWebSocketClient)webSocketClient).setUserProperties(userProperties);
//		} catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException | CertificateException | KeyStoreException | IOException e) {
//			e.printStackTrace();
//		}	
////		try {
////	        SSLContext sslContext = SSLContext.getDefault();
////	        ((StandardWebSocketClient)webSocketClient).setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));
////	    } catch (NoSuchAlgorithmException e) {
////	        e.printStackTrace();
////	    }
////	    List<Transport> webSocketTransports = Arrays.asList(new WebSocketTransport(webSocketClient)/*,  new RestTemplateXhrTransport(restTemplate)*/);
////	    SockJsClient sockJsClient = new SockJsClient(webSocketTransports);
////	    sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
////	    
////	    WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
////	    //stompClient.setAutoStartup(true);
////	    //stompClient.setMessageConverter(new MappingJackson2MessageConverter());
////	    stompClient.setMessageConverter(new StringMessageConverter());
//	   
//	    return new ClientStompSessionHandler(gatewayUrl(), webSocketClient, taskScheduler, activeEnable);
//	}
//
//	private SSLContext getDefaulSSLContext() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
//		InputStream keystoreLocation = ResourceLoader.getResource(keyStore).getInputStream();
//	    char [] keystorePassword = keyStorePwd.toCharArray();
//	    char [] keyPassword = keyStorePwd.toCharArray();
//
//	    KeyStore keystore = KeyStore.getInstance(keyStoreType);
//	    keystore.load(keystoreLocation, keystorePassword);
//	    KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//	    kmfactory.init(keystore, keyPassword);
//
////	    InputStream truststoreLocation = new FileInputStream("src/main/resources/aaa.jks");
////	    char [] truststorePassword = "zzz".toCharArray();
////	    String truststoreType = "JKS";
////
////	    KeyStore truststore = KeyStore.getInstance(truststoreType);
////	    truststore.load(truststoreLocation, truststorePassword);
////	    TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
////	    tmfactory.init(truststore);
//
//	    KeyManager[] keymanagers = kmfactory.getKeyManagers();
//	    //TrustManager[] trustmanagers =  tmfactory.getTrustManagers();
//
//	    SSLContext sslContext = SSLContext.getInstance("TLS");
//	    sslContext.init(keymanagers, null, new SecureRandom());
//	    return sslContext;
//	}
////	@Bean
////	public WebSocketClient webSocketClient2() {
////		List<Transport> transports = new ArrayList<>();
////		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
////		transports.add(new RestTemplateXhrTransport());
////		return new SockJsClient(transports);
////	}
//}
