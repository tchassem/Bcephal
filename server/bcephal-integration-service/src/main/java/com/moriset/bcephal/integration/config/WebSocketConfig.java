/**
 * 
 */
package com.moriset.bcephal.integration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.integration.socket.PontoConnectWebsocketRun;
import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;
import com.moriset.bcephal.multitenant.jpa.CustomHttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

	@Autowired
	CustomHttpSessionHandshakeInterceptor CustomHttpSessionHandshakeInterceptor;

    @Bean
    WebSocketHandler getPontoConnectWebsocketRun() {
		return new PerConnectionWebSocketHandler(PontoConnectWebsocketRun.class);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getPontoConnectWebsocketRun(), "/ws/integration/ponto/run").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		
	}

}