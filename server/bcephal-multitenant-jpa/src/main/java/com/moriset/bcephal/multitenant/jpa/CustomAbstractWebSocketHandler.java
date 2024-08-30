package com.moriset.bcephal.multitenant.jpa;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CustomAbstractWebSocketHandler extends AbstractWebSocketHandler {
	
	@Autowired
	MultiTenantInterceptor Interceptor;	
	protected String username;
	protected HttpSession httpSession;
	protected String headers;
	
	protected String headers2;
	
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {		
		refresh(session);
		username = session.getPrincipal() != null ? session.getPrincipal().getName() : null;
		log.debug ("User = {}", username);
		if(session.getAttributes().containsKey(MultiTenantInterceptor.CUSTOM_SESSION_NAME)) {
			httpSession = (HttpSession) session.getAttributes().get(MultiTenantInterceptor.CUSTOM_SESSION_NAME);			
		}
		if(session.getAttributes().containsKey(MultiTenantInterceptor.httpHeaderName)) {
			String header = (String) session.getAttributes().get(MultiTenantInterceptor.httpHeaderName);
			if(StringUtils.hasText(header)) {
				byte[] bytesEncoded = Base64.getDecoder().decode(header.getBytes());
				headers = new String(bytesEncoded);
			}
		}
		super.afterConnectionEstablished(session);
	}
	
	public void refresh(WebSocketSession session){		
		String tenantId = (String) session.getAttributes().get(MultiTenantInterceptor.TENANT_HEADER_NAME);
		log.debug("After Connection Established Custom tenant Id {}",tenantId);
		if(TenantContext.getCurrentTenant() == null 
				&& Interceptor.bcephalDataSources.containsKey(tenantId)
				&& tenantId != null && !tenantId.trim().isEmpty()) {
			TenantContext.setCurrentTenant(tenantId);
		}
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.debug("Transport error",exception.getMessage());
	}
}
