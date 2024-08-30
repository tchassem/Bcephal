/**
 * 
 */
package com.moriset.bcephal.billing.websocket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.billing.service.batch.BillingRunner;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author EMMENI Emmanuel
 *
 */
@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BillingRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	Long billingModelId;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	BillingRunner runner;


	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		log.debug(message.getPayload());
		log.info(" session id " + session.getId());
		refresh(session);
		this.session = session;
		if (listener == null) {
			listener = getNewListener();
		}
		if ("STOP".equals(message.getPayload())) {
			stop();
		} 
		else if (billingModelId == null) {
			billingModelId = Long.valueOf(message.getPayload());
			if (billingModelId != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.info("Join generation Web Socket tenantId is {}", tenantId);
				Thread thread = new Thread() {
					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							runJoin();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				thread.start();
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("Connection closed !");
		log.debug(" session id " + session.getId());
		dispose();

	}

	private void runJoin() {
		try {	
			if (billingModelId != null) {
				log.debug("Billing model Id : {}", billingModelId);
							
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setBillingModelId(billingModelId);
				runner.setMode(RunModes.M);
				runner.setHttpHeaders(this.session.getHandshakeHeaders());
				runner.setListener(listener);
				runner.run();

			} else {
				log.error("Join is NULL!");
				listener.error("Unknown join.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running billing : {}", billingModelId, e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}
	
	private void stop() {
		stopped = true;
		if(runner != null) {
			runner.stop();
		}
	}

	private void dispose() {
		billingModelId = null;
		listener = null;
		stopped = false;
	}

	private TaskProgressListener getNewListener() {
		return new TaskProgressListener() {
			@Override
			public void SendInfo() {
				if (!stopped) {
					try {
						String json = mapper.writeValueAsString(getInfo());
						session.sendMessage(new TextMessage(json));
						if (getInfo().isTaskEnded()) {
							session.close(CloseStatus.NORMAL);
						} else if (getInfo().isTaskInError()) {
							session.close(CloseStatus.SERVER_ERROR);
						}
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}


}
