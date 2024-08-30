//package com.moriset.bcephal.utils.socket;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//@Data
//@Slf4j
//public abstract class WebSocketProxy implements WebSocketHandler{	
//	
//	protected abstract WebSocketHandler getNewWebSocketHandler();
//	
//	private final Map<WebSocketSession, WebSocketHandler> handlers = new ConcurrentHashMap<>();
//		
//	private WebSocketHandler getHandler(WebSocketSession session) {
//		WebSocketHandler handler = this.handlers.get(session);
//		if (handler == null) {
//			throw new IllegalStateException("WebSocketHandler not found for " + session);
//		}
//		return handler;
//	}
//	
//	@Override
//	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//		WebSocketHandler WebSocketHandler =  getNewWebSocketHandler();	
//		this.handlers.put(session, WebSocketHandler);
//		WebSocketHandler.afterConnectionEstablished(session);
//	}
//
//	@Override
//	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//		getHandler(session).handleMessage(session, message);
//	}
//
//	@Override
//	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//		getHandler(session).handleTransportError(session, exception);
//	}
//
//	@Override
//	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//		try {
//			getHandler(session).afterConnectionClosed(session, closeStatus);
//		}
//		finally {
//			destroyHandler(session);
//		}
//	}
//
//	@Override
//	public boolean supportsPartialMessages() {
//		return false;
//	}
//
//	private void destroyHandler(WebSocketSession session) {
//		WebSocketHandler handler = this.handlers.remove(session);
//		try {
////			if (handler != null) {
////				this.provider.destroy(handler);
////			}
//		}
//		catch (Throwable ex) {
//			if (log.isWarnEnabled()) {
//				log.warn("Error while destroying " + handler, ex);
//			}
//		}
//	}
//}
//
