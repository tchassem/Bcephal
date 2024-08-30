/**
 * 
 */
package com.moriset.bcephal.sourcing.grid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;
import com.moriset.bcephal.multitenant.jpa.CustomHttpSessionHandshakeInterceptor;
import com.moriset.bcephal.sourcing.grid.socket.ExportGrilleWebSocket;
import com.moriset.bcephal.sourcing.grid.socket.ExportMaterializedGridWebSocket;
import com.moriset.bcephal.sourcing.grid.socket.FileLoaderWebSocket;
import com.moriset.bcephal.utils.socket.WebSocketDataUpload;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private CustomHandshakeInterceptor customHandshakeInterceptor;

	@Autowired
	CustomHttpSessionHandshakeInterceptor CustomHttpSessionHandshakeInterceptor;

    @Bean
    WebSocketHandler getFileLoaderWebSocket() {
		return new PerConnectionWebSocketHandler(FileLoaderWebSocket.class);
	}

    @Bean
    WebSocketHandler getExportGrilleWebSocket() {
		return new PerConnectionWebSocketHandler(ExportGrilleWebSocket.class);
	}

    @Bean
    WebSocketHandler getExportMaterializedGridWebSocket() {
		return new PerConnectionWebSocketHandler(ExportMaterializedGridWebSocket.class);
	}

    @Bean
    WebSocketHandler getWebSocketDataUpload() {
		return new PerConnectionWebSocketHandler(WebSocketDataUpload.class);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getFileLoaderWebSocket(), "/ws/sourcing/file-loader/load").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getWebSocketDataUpload(), "/ws/sourcing/file-loader/upload-data-file")
				.setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getExportGrilleWebSocket(), "/ws/sourcing/export-grille").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getExportMaterializedGridWebSocket(), "/ws/sourcing/export-materialized-grid").setAllowedOriginPatterns("*")
		.addInterceptors(customHandshakeInterceptor);
	}

}