/**
 * 
 */
package com.moriset.bcephal.scheduler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;
import com.moriset.bcephal.scheduler.websocket.FormButtonRunWebSocket;
import com.moriset.bcephal.scheduler.websocket.SchedulerPlannerRunWebSocket;
import com.moriset.bcephal.scheduler.websocket.UserLoadRunWebSocket;

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
    WebSocketHandler getSchedulerPlannerRunWebSocket() {
		return new PerConnectionWebSocketHandler(SchedulerPlannerRunWebSocket.class);
	}
    
    @Bean
    WebSocketHandler getUserLoadRunWebSocket() {
		return new PerConnectionWebSocketHandler(UserLoadRunWebSocket.class);
	}

    @Bean
    WebSocketHandler getFormButtonRunWebSocket() {
		return new PerConnectionWebSocketHandler(FormButtonRunWebSocket.class);
	}


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getSchedulerPlannerRunWebSocket(), "/ws/scheduler-planner/run").setAllowedOriginPatterns("*")
			.addInterceptors(customHandshakeInterceptor);
		
		registry.addHandler(getUserLoadRunWebSocket(), "/ws/scheduler-planner/user-load/run").setAllowedOriginPatterns("*")
			.addInterceptors(customHandshakeInterceptor);
		
		registry.addHandler(getFormButtonRunWebSocket(), "/ws/scheduler-planner/form-button/run").setAllowedOriginPatterns("*")
			.setAllowedOriginPatterns("*");		
	}

}
