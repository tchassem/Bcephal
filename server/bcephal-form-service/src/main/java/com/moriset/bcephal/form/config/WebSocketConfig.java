/**
 * 
 */
package com.moriset.bcephal.form.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.form.websocket.ExportFormGridWebSocket;
import com.moriset.bcephal.form.websocket.FormButtonRunWebSocket;
import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Bean
    WebSocketHandler getFormButtonRunWebSocket() {
		return new PerConnectionWebSocketHandler(FormButtonRunWebSocket.class);
	}

    @Bean
    WebSocketHandler getExportFormGridWebSocket() {
		return new PerConnectionWebSocketHandler(ExportFormGridWebSocket.class);
	}
	

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getFormButtonRunWebSocket(), "/ws/form/button/run").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getFormButtonRunWebSocket(), "/ws/form/export-form-grid").setAllowedOriginPatterns("*")
		.addInterceptors(customHandshakeInterceptor);
	}

}
