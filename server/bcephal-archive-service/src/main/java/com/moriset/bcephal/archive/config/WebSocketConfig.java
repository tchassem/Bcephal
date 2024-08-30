/**
 * 
 */
package com.moriset.bcephal.archive.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.archive.socket.ArchiveGenerationWebSocket;
import com.moriset.bcephal.archive.socket.ArchiveRestorationWebSocket;
import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Bean
    WebSocketHandler getArchiveGenerationWebSocket() {
		return new PerConnectionWebSocketHandler(ArchiveGenerationWebSocket.class);
	}

    @Bean
    WebSocketHandler getArchiveRestorationWebSocket() {
		return new PerConnectionWebSocketHandler(ArchiveRestorationWebSocket.class);
	}


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getArchiveGenerationWebSocket(), "/ws/archive/generation").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getArchiveRestorationWebSocket(), "/ws/archive/restoration").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
	}

	@Autowired
	Map<String, DataSource> bcephalDataSources;
}