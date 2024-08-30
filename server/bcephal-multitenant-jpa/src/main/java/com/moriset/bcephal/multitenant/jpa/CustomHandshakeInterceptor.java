package com.moriset.bcephal.multitenant.jpa;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor{  

	@Autowired
	MultiTenantInterceptor Interceptor;
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		Interceptor.setTenant(request);
		attributes.put(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());		
		log.debug("Set tenant from before Handshake {} ",TenantContext.getCurrentTenant());
		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest req = (ServletServerHttpRequest)request;
			String httpHeader = req.getServletRequest().getParameter(MultiTenantInterceptor.httpHeaderName);
			if(httpHeader != null) {
				attributes.put(MultiTenantInterceptor.httpHeaderName, httpHeader);
			}
			String httpHeaderClient = req.getServletRequest().getParameter(MultiTenantInterceptor.BC_CLIENT);
			if(httpHeaderClient != null) {
				attributes.put(MultiTenantInterceptor.BC_CLIENT, httpHeaderClient);
			}
			String httpHeaderProfile = req.getServletRequest().getParameter(MultiTenantInterceptor.BC_PROFILE);
			if(httpHeaderProfile != null) {
				attributes.put(MultiTenantInterceptor.BC_PROFILE, httpHeaderProfile);
			}
			String httpHeaderLanguage = req.getServletRequest().getParameter(MultiTenantInterceptor.LANGUAGE);
			if(httpHeaderLanguage != null) {
				attributes.put(MultiTenantInterceptor.LANGUAGE, httpHeaderLanguage);
			}
			HttpSession sessionS = req.getServletRequest().getSession();
			if(sessionS != null) {
				attributes.put(MultiTenantInterceptor.CUSTOM_SESSION_NAME, sessionS);	
			}
			
			
			
			HttpSession session = req.getServletRequest().getSession(false);
			   if (session != null) {
				log.info("Set HTTPSESSIONID from before Handshake {} ",session.getId());
			    attributes.put("HTTPSESSIONID", session.getId());
			   }
		}
		
		
		
			   
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		log.debug("Data base {} : data source successfully builded!");
		if(TenantContext.getCurrentTenant() == null) {				
					try {
						Interceptor.setTenant(request);
					} catch (Exception e) {
						log.error("Error:{}",e);
					}
		}
	}
}
