/**
 * 
 */
package com.moriset.bcephal.etl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

//	@Autowired
//	CustomHandshakeInterceptor customHandshakeInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
	}


}
