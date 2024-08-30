/**
 * 
 */
package com.moriset.bcephal.project.controller;

import java.lang.reflect.Type;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
public class WSClient {

	public static void main(String[] args) {
		new WSClient();
	}

	public WSClient() {
		WebSocketClient transport = new StandardWebSocketClient();
		WebSocketStompClient stompClient = new WebSocketStompClient(transport);
		stompClient.setMessageConverter(new StringMessageConverter());
		// stompClient.setTaskScheduler(taskScheduler);
		String url = "ws://127.0.0.1:9000/projects/import";
		StompSessionHandler handler = new StompSessionHandlerAdapter() {

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				log.info("handleFrame");
			}

			@Override
			public Type getPayloadType(StompHeaders headers) {
				log.info("getPayloadType");
				return null;
			}

			@Override
			public void handleTransportError(StompSession session, Throwable exception) {
				log.info("handleTransportError");
			}

			@Override
			public void handleException(StompSession session, StompCommand command, StompHeaders headers,
					byte[] payload, Throwable exception) {
				log.info("handleException");
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				log.info("afterConnected");
				session.send("/topic/message", "import project");
			}
		};
		stompClient.connectAsync(url, handler);
	}

}
