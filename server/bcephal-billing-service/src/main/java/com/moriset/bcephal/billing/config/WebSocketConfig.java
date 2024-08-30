/**
 * 
 */
package com.moriset.bcephal.billing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.moriset.bcephal.billing.websocket.BillingCreateCreditNoteWebSocket;
import com.moriset.bcephal.billing.websocket.BillingResetValidationWebSocket;
import com.moriset.bcephal.billing.websocket.BillingResetWebSocket;
import com.moriset.bcephal.billing.websocket.BillingRunWebSocket;
import com.moriset.bcephal.billing.websocket.BillingSendMailWebSocket;
import com.moriset.bcephal.billing.websocket.BillingValidationWebSocket;
import com.moriset.bcephal.multitenant.jpa.CustomHandshakeInterceptor;

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
    WebSocketHandler getBillingRunWebSocket() {
		return new PerConnectionWebSocketHandler(BillingRunWebSocket.class);
	}

    @Bean
    WebSocketHandler getBillingResetWebSocket() {
		return new PerConnectionWebSocketHandler(BillingResetWebSocket.class);
	}

    @Bean
    WebSocketHandler getBillingResetValidationWebSocket() {
		return new PerConnectionWebSocketHandler(BillingResetValidationWebSocket.class);
	}

    @Bean
    WebSocketHandler getBillingValidationWebSocket() {
		return new PerConnectionWebSocketHandler(BillingValidationWebSocket.class);
	}

    @Bean
    WebSocketHandler getBillingSendMailWebSocket() {
		return new PerConnectionWebSocketHandler(BillingSendMailWebSocket.class);
	}
    
    @Bean
    WebSocketHandler getBillingCreateCreditNoteWebSocket() {
		return new PerConnectionWebSocketHandler(BillingCreateCreditNoteWebSocket.class);
	}


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getBillingRunWebSocket(), "/ws/billing/run").setAllowedOriginPatterns("*")
				.addHandler(getBillingResetWebSocket(), "/ws/billing/reset").setAllowedOriginPatterns("*")
				.addHandler(getBillingResetValidationWebSocket(), "/ws/billing/reset-validation").setAllowedOriginPatterns("*")
				.addHandler(getBillingValidationWebSocket(), "/ws/billing/validation").setAllowedOriginPatterns("*")
				.addHandler(getBillingSendMailWebSocket(), "/ws/billing/send-mail").setAllowedOriginPatterns("*")
				.addHandler(getBillingCreateCreditNoteWebSocket(), "/ws/billing/create-credit-note").setAllowedOriginPatterns("*")
				.addInterceptors(customHandshakeInterceptor);
	}

}
