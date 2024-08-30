/**
 * 
 */
package com.moriset.bcephal.planification.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;
import com.moriset.bcephal.planification.websocket.ScriptRunWebSocket;
import com.moriset.bcephal.planification.websocket.TransformationRoutineRunWebSocket;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Bean
    WebSocketHandler getRoutineRunWebSocket() {
		return new PerConnectionWebSocketHandler(TransformationRoutineRunWebSocket.class);
	}

    @Bean
    WebSocketHandler getScriptRunWebSocket() {
		return new PerConnectionWebSocketHandler(ScriptRunWebSocket.class);
	}
	


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getRoutineRunWebSocket(), "/ws/planification/run-routine")
				.setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		
		registry.addHandler(getScriptRunWebSocket(), "/ws/planification/run-script")
				.setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
	}

}