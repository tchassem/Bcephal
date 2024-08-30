/**
 * 
 */
package com.moriset.bcephal.reporting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;
import com.moriset.bcephal.reporting.websocket.ExportJoinWebSocket;
import com.moriset.bcephal.reporting.websocket.ExportUnionGridWebSocket;
import com.moriset.bcephal.reporting.websocket.JoinRunWebSocket;

/**
 * @author EMMENI Emmanuel
 *
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Bean
    WebSocketHandler getRoutineRunWebSocket() {
		return new PerConnectionWebSocketHandler(JoinRunWebSocket.class);
	}

    @Bean
    WebSocketHandler getExportJoinWebSocket() {
		return new PerConnectionWebSocketHandler(ExportJoinWebSocket.class);
	}

    @Bean
    WebSocketHandler getExportUnionGridWebSocket() {
		return new PerConnectionWebSocketHandler(ExportUnionGridWebSocket.class);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getRoutineRunWebSocket(), "/ws/join/run-join").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getExportJoinWebSocket(), "/ws/join/export-join").setAllowedOriginPatterns("*")
			.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getExportUnionGridWebSocket(), "/ws/reporting/export-union-grid").setAllowedOriginPatterns("*")
			.addInterceptors(customHandshakeInterceptor);
	}

}
