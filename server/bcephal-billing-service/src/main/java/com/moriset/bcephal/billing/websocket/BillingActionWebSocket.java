/**
 * 
 */
package com.moriset.bcephal.billing.websocket;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.billing.service.action.BillingActionRunner;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author EMMENI Emmanuel
 *
 */
@Slf4j
public abstract class BillingActionWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	BillingRequest billingRequest;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;	
	
	BillingActionRunner runner;


	protected void runBilling() {
		try {	
			if (billingRequest != null) {
				log.debug("Billing model Id : {}", billingRequest);
							
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner = getActionRunner();
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setBillingRequest(billingRequest);
				runner.setMode(RunModes.M);
				runner.setHttpHeaders(this.session.getHandshakeHeaders());
				runner.setListener(listener);
				
				String projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				Long profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
				String locale = (String) session.getAttributes().get(RequestParams.LANGUAGE);
				Long client = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_CLIENT));
				runner.setProfileId(profileId);
				runner.setProjectCode(projectCode);
				runner.setClient(client);
				runner.setLanguage(locale);
				if(StringUtils.hasText(locale)) {
					runner.setLocale(Locale.forLanguageTag(locale));
				}				
				int count = runner.run();
				if (listener != null) {
					listener.getInfo().setMessage("" + count);
				}

			} else {
				log.error("Billing request is NULL!");
				listener.error("Unknown Billing request.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running billing : {}", billingRequest, e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}
	


	protected abstract BillingActionRunner getActionRunner();



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
		else if (billingRequest == null) {
			billingRequest = deserialize(message.getPayload());
			if (billingRequest != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.info("Billing Web Socket tenantId is {}", tenantId);
				Thread thread = new Thread() {
					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							runBilling();
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
	
	protected void stop() {
		stopped = true;
	}

	protected void dispose() {
		billingRequest = null;
		listener = null;
		stopped = false;
	}

	protected TaskProgressListener getNewListener() {
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

	private BillingRequest deserialize(String json) {
		try {
			return mapper.readValue(json, BillingRequest.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
