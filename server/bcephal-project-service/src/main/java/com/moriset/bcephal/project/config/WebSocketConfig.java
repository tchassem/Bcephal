/**
 * 
 */
package com.moriset.bcephal.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;
import com.moriset.bcephal.project.socket.BackupProjectWebSocketHandler;
import com.moriset.bcephal.project.socket.CreateProjectWebSocketHandler;
import com.moriset.bcephal.project.socket.ImportProjectWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Bean
    WebSocketHandler getImportProjectWebSocketHandler() {
		return new PerConnectionWebSocketHandler(ImportProjectWebSocketHandler.class);
	}

    @Bean
    WebSocketHandler getBackupProjectWebSocketHandler() {
		return new PerConnectionWebSocketHandler(BackupProjectWebSocketHandler.class);
	}

    @Bean
    WebSocketHandler getCreateProjectWebSocketHandler() {
		return new PerConnectionWebSocketHandler(CreateProjectWebSocketHandler.class);
	}

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getImportProjectWebSocketHandler(), "/ws/project/import-project")
				.setAllowedOriginPatterns("*").addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getBackupProjectWebSocketHandler(), "/ws/project/backup-project")
				.setAllowedOriginPatterns("*").addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getCreateProjectWebSocketHandler(), "/ws/project/create-project")
		.setAllowedOriginPatterns("*").addInterceptors(customHandshakeInterceptor);
	}
}
