/**
 * 
 */
package com.moriset.bcephal.utils.socket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;



/**
 * 
 * @author MORISET-004
 *
 */
public class WebSocket {

	private CompletableFuture<WebSocketSession> bcephalServerSession;
	
	protected WebSocketSession clientSession;
	protected WebSocketHandler webSocketHandler;
	
	
	// BCEPHAL SERVER	
	protected WebSocketSession getBcephalServerSession() {
		try {
			return this.bcephalServerSession != null ? this.bcephalServerSession.get() : null;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	protected void disconnectFromBcephalServer() {
		WebSocketSession bcephalSession = getBcephalServerSession();
		if(bcephalSession != null) {
			try {
				bcephalSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public CompletableFuture<WebSocketSession> connectToBcephalServer(String path, HttpHeaders headers, WebSocketSession clientSession, WebSocketHandler webSocketHandler) throws URISyntaxException {
		URI url = new URI(path);
		StandardWebSocketClient client = new StandardWebSocketClient();
		bcephalServerSession = client.execute(webSocketHandler, getNewClientWebSocketHttpHeaders(headers), url);
		this.clientSession = clientSession;
		this.webSocketHandler = webSocketHandler;
		return bcephalServerSession;
    }
	
	WebSocketHttpHeaders getNewClientWebSocketHttpHeaders(HttpHeaders headers) {
		List<String> autorization = headers.get("authorization");
		if(autorization != null) {
			HttpHeaders head = new HttpHeaders();
			headers.forEach((k,v) ->{
				if(v != null) {
					head.add(k, v.toString());
				}
			});
			return new WebSocketHttpHeaders(head);
		}
		return null;
	}
	
	protected WebSocketHandler getNewClientWebSocketHandler(){
		return new WebSocketHandler() {			
			@Override
			public boolean supportsPartialMessages() {
				return false;
			}
			
			@Override
			public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
				disconnectFromClient();
			}
			
			@Override
			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
				if(clientSession != null) {
					try {
						clientSession.sendMessage(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				//logger.debug("Connected to Bcephal server!");
				//System.out.println("Connected..............................");
			}
			
			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
				disconnectFromClient();			
			}

		};
	}
	
	
	
	
	// CLIENT
	
	protected void disconnectFromClient() {
		if(clientSession != null) {
			try {
				clientSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
//	@Override
//	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//		this.clientSession = session;
////		this.clientSession.setTextMessageSizeLimit(bcephalServer.getBufferSize());
////		this.clientSession.setBinaryMessageSizeLimit(bcephalServer.getBufferSize());
//	}
//	
//	@Override
//	public void handleTextMessage(WebSocketSession session, TextMessage message) {
//		WebSocketSession bcephalSession = getBcephalServerSession();
//		if(bcephalSession != null && bcephalSession.isOpen()) {
//			try {
////				bcephalSession.setBinaryMessageSizeLimit(bcephalServer.getBufferSize());
////				bcephalSession.setTextMessageSizeLimit(bcephalServer.getBufferSize());
//				bcephalSession.sendMessage(message);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}	

//	@Override
//	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//		//logger.error("Websocket error", exception);
//		disconnectFromBcephalServer();
//	}
//
//	@Override
//	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//		//logger.debug("Websocket closed!");
//		disconnectFromBcephalServer();
//	}
	

}
