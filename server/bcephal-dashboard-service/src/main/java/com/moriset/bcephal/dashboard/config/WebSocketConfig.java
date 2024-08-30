/**
 * 
 */
package com.moriset.bcephal.dashboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.dashboard.service.socket.DashboardReportPivoteGridWebSocket;
import com.moriset.bcephal.dashboard.service.socket.DashboardReportWebSocket;
import com.moriset.bcephal.dashboard.service.socket.HomeScreenReportSocket;
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
    WebSocketHandler getDashboardReportWebSocket() {
		return new PerConnectionWebSocketHandler(DashboardReportWebSocket.class);
	}

    @Bean
    WebSocketHandler getHomeScreenReportSocket() {
		return new PerConnectionWebSocketHandler(HomeScreenReportSocket.class);
	}
	
	@Bean
	WebSocketHandler getDashboardReportPivoteGridWebSocket() {
		return new PerConnectionWebSocketHandler(DashboardReportPivoteGridWebSocket.class);
	}


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getDashboardReportWebSocket(), "/ws/dashboarding/export-dashboard-report").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getHomeScreenReportSocket(), "/ws/dashboarding/export-home-screen-report").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
		registry.addHandler(getDashboardReportPivoteGridWebSocket(), "/ws/dashboarding/export-dashboard-report-pivot-gird").setAllowedOriginPatterns("*")
		.addInterceptors(customHandshakeInterceptor);
	}

}