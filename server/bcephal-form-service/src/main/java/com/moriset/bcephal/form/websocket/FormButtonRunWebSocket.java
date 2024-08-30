package com.moriset.bcephal.form.websocket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.form.FormButtonActionData;
import com.moriset.bcephal.grid.service.form.FormButtonRunner;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FormButtonRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;
	
	@Autowired
	FormButtonRunner runner;
	
	FormButtonActionData data;
	
	boolean stopped;
	
	
	
	protected void runAction() {
		try {	
			if (data != null) {
				log.debug("Button runner : {}", data);
							
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				String projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				Long clientId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_CLIENT));
				Long profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
								
				runner.setStop(false);
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setData(data);
				runner.setMode(RunModes.M);
				runner.setListener(listener);
				runner.setProjectCode(projectCode);
				runner.setClientId(clientId);
				runner.setProfileId(profileId);
				runner.run();

			} else {
				log.error("Billing request is NULL!");
				listener.error("Unknown Billing request.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running form button actions : {}", data, e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}
	
	
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
		else if (data == null) {
			data = deserialize(message.getPayload());
			if (data != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.trace("TenantId is {}", tenantId);
				Thread thread = new Thread() {
					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							runAction();
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
		if(runner != null) {
			runner.setStop(true);
		}
	}

	protected void dispose() {
		data = null;
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

	private FormButtonActionData deserialize(String json) {
		try {
			return mapper.readValue(json, FormButtonActionData.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}


}
