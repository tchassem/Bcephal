//package com.moriset.bcephal.config;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.messaging.converter.StringMessageConverter;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.web.socket.WebSocketHttpHeaders;
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//
//import com.moriset.bcephal.utils.ProjectHandler;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class ClientStompSessionHandler extends StompSessionHandlerAdapter {
//
//	public static final String SOCKET_STOMP_PATH_JS = "/ws-js/project/close";
//
//	private String gatewayUrl;
//	private WebSocketStompClient webSocketStompClient;
//	private List<String> protocols;
//	private WebSocketHttpHeaders handSahkeHeaders;
//	private StompHeaders stompHeaders;
//	private TaskScheduler taskScheduler;
//	private ScheduledFuture<?> Scheduled;
//
//	private AtomicBoolean isConnected;
//	private Boolean activeEnable;
//
//	public ClientStompSessionHandler(String gatewayUrl, WebSocketClient webSocketClient, TaskScheduler taskScheduler,
//			Boolean activeEnable) {
//		this.gatewayUrl = gatewayUrl;
//		this.taskScheduler = taskScheduler;
//		webSocketStompClient = new WebSocketStompClient(webSocketClient);
//		webSocketStompClient.setMessageConverter(new StringMessageConverter());
//		webSocketStompClient.setTaskScheduler(new ConcurrentTaskScheduler());
//		protocols = new ArrayList<>();
//		protocols.add("v10.stomp");
//		protocols.add("v11.stomp");
//		handSahkeHeaders = new WebSocketHttpHeaders();
//		handSahkeHeaders.setUpgrade("websocket");
//		handSahkeHeaders.setConnection("Upgrade");
//		// handSahkeHeaders.setSecWebSocketProtocol(protocols);
//		stompHeaders = new StompHeaders();
//		isConnected = new AtomicBoolean(false);
//		this.activeEnable = activeEnable;
//	}
//	
//	public ClientStompSessionHandler(String gatewayUrl, WebSocketStompClient webSocketStompClient, TaskScheduler taskScheduler,
//			Boolean activeEnable) {
//		this.gatewayUrl = gatewayUrl;
//		this.taskScheduler = taskScheduler;
//		this.webSocketStompClient = webSocketStompClient;
//		webSocketStompClient.setTaskScheduler(new ConcurrentTaskScheduler());
//		protocols = new ArrayList<>();
//		protocols.add("v10.stomp");
//		protocols.add("v11.stomp");
//		handSahkeHeaders = new WebSocketHttpHeaders();
//		handSahkeHeaders.setUpgrade("websocket");
//		handSahkeHeaders.setConnection("Upgrade");
//		// handSahkeHeaders.setSecWebSocketProtocol(protocols);
//		stompHeaders = new StompHeaders();
//		isConnected = new AtomicBoolean(false);
//		this.activeEnable = activeEnable;
//	}
//
//	public boolean IsEmptyProjectHandler() {
//		return this.projectHandler == null;
//	}
//
//	public void start(ProjectHandler projectHandler) {
//		this.projectHandler = projectHandler;
//		if (!StringUtils.isBlank(gatewayUrl)) {
//			connect();
//		}
//	}
//
//	private void connect() {
//		webSocketStompClient.connect(URI.create(gatewayUrl.concat(SOCKET_STOMP_PATH_JS)), handSahkeHeaders,
//				stompHeaders, this);
//	}
//
//	private void scheduleConnect() {
//		Scheduled = this.taskScheduler.schedule(() -> {
//			try {
//				connect();
//				Scheduled.cancel(true);
//				Scheduled = null;
//			} catch (Exception e1) {
//
//			}
//		}, new CronTrigger("0 */1 * ? * *"));
//	}
//
//	StompSession session;
//
//	@Override
//	public void afterConnected(StompSession session, StompHeaders headers) {
//		log.debug("Client connected: headers {}", headers);
//		this.isConnected.set(true);
//		this.session = session;
//		session.subscribe("/topic/close-project", this);
//		// String message = "one-time message from client";
//		// log.info("Client sends: {}", message);
//		// session.send("/app/request", message);
//	}
//
//	private ProjectHandler projectHandler;
//
//	@Override
//	public void handleFrame(StompHeaders headers, Object payload) {
//		log.debug("Client received: payload {}, headers {}", payload, headers);
//		if (projectHandler != null) {
//			projectHandler.closeProject((String) payload);
//		}
//		if (!this.activeEnable) {
//			if (this.session != null && this.session.isConnected()) {
//				this.session.acknowledge(headers.getMessageId(), true);
//			}
//		}
//	}
//
//	@Override
//	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
//			Throwable exception) {
//		log.error("Client error: exception {}, command {}, payload {}, headers {}", exception.getMessage(), command,
//				payload, headers);
//	}
//
//	@Override
//	public void handleTransportError(StompSession session, Throwable exception) {
//		log.error("Client transport error: error {}", exception.getMessage());
//		if (!this.isConnected.get()) {
//			this.isConnected.set(false);
//			session = null;
//			scheduleConnect();
//		}
//
//	}
//
//}