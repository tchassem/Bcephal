/**
 * 
 */
package com.moriset.bcephal.scheduler.websocket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.loader.domain.UserLoad;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.scheduler.service.UserLoadRunner;
import com.moriset.bcephal.utils.ConfirmationRequest;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserLoadRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	UserLoad userLoad;

	boolean stopped;

	@Autowired
	UserLoadRunner runner;
		
	int value = 1;
	
	Thread thread;
	
	ConfirmationRequest confirmationRequest;
	
	private Long subscriptionId;
	private String projectCode;

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		log.debug(message.getPayload());
		log.info(" session id " + session.getId());
		log.info(" value === " + value);
		value++;
		refresh(session);
		this.session = session;
		if (listener == null) {
			listener = getNewListener();
		}
		if ("STOP".equals(message.getPayload())) {
			stopped = true;
			if(runner != null) {
				runner.setStop(true);
			}
		} 
		else if (userLoad != null) {
			ConfirmationRequest request = deserializeConfirmationRequest(message.getPayload());
			if(request != null) {
				confirmationRequest = request;				
				synchronized (thread) {
					thread.notify();
				}
			}
		}
		else if (userLoad == null) {
			userLoad = deserialize(message.getPayload());			
			if (userLoad != null) {
				String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
				if(StringUtils.hasText(subscriptionIds)) {
					subscriptionId = Long.valueOf(subscriptionIds.trim());
				}
				projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				String tenantId = TenantContext.getCurrentTenant();
				log.trace("UserLoad generation Web Socket tenantId is {}", tenantId);				
				thread = new Thread() {
					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							runUserLoad();
						} 
						catch (Exception e) {
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
		log.debug(" value === " + value);
		dispose();

	}

	private void runUserLoad() {
		try {	
			if (userLoad != null) {
				log.debug("UserLoad : {}", userLoad.getName());
				listener.createInfo(userLoad.getId(), userLoad.getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setUserLoad(userLoad);
				runner.setMode(RunModes.M);
				runner.setSession(httpSession);
				runner.setHttpHeaders(this.session.getHandshakeHeaders());
				runner.setListener(listener);
				runner.setStop(false);
				runner.setSubscriptionId(subscriptionId);
				runner.setProjectCode(projectCode);
				runner.run();				
				//sendUserLoad();				
			} else {
				log.error("SchedulerPlanner is NULL!");
				listener.error("Unknown scheduler planner.", false);
			}
			
			if (listener != null) {				
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running user load : {}",
					userLoad != null && userLoad != null ? userLoad.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		userLoad = null;
		listener = null;
		stopped = false;
		thread = null;
	}

	private TaskProgressListener getNewListener() {
		return new TaskProgressListener() {
			
			@Override
			public ConfirmationRequest sendConfirmation(ConfirmationRequest request) {		
				confirmationRequest = null;				
				try {
					String json = mapper.writeValueAsString(request);
					session.sendMessage(new TextMessage(json));					
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					synchronized (thread) {
						thread.wait();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return confirmationRequest;
			}
			
			@Override
			public void sendMessage(Object request) {		
				try {					
					String json = (request instanceof String) ? (String)request : mapper.writeValueAsString(request);
					session.sendMessage(new TextMessage(json));					
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
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
	
	private UserLoad deserialize(String json) {
		try {
			return mapper.readValue(json, UserLoad.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ConfirmationRequest deserializeConfirmationRequest(String json) {
		try {
			ConfirmationRequest request = mapper.readValue(json, ConfirmationRequest.class);
			if(request != null && StringUtils.hasText(request.getResponse())) {
				return request;
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
