/**
 * 
 */
package com.moriset.bcephal.scheduler.websocket;

import java.io.IOException;
import java.util.Optional;

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
import com.moriset.bcephal.grid.domain.form.FormButtonActionData;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelButton;
import com.moriset.bcephal.grid.repository.form.FormModelButtonRepository;
import com.moriset.bcephal.grid.repository.form.FormModelRepository;
import com.moriset.bcephal.grid.service.form.FormButtonRunner;
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
@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormButtonRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	FormButtonActionData data;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	FormButtonRunner runner;
	
	@Autowired
	FormModelRepository formModelRepository;
	
	@Autowired
	FormModelButtonRepository formModelButtonRepository;
	
	int value = 1;

	Long subscriptionId;

	private String projectCode;

	boolean success;

	Long profileId;
	Long clientId;

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
				runner.cancel();
			}
		} else if (data == null) {
			data = deserialize(message.getPayload());
			Optional<FormModel> modelOption = formModelRepository.findById(data.getModelId());
			data.setModel(modelOption.isPresent() ? modelOption.get() : null);
			Optional<FormModelButton> buttonOption = formModelButtonRepository.findById(data.getButtonId());
			data.setButton(buttonOption.isPresent() ? buttonOption.get() : null);
						
			if (data.getModel() != null && data.getButton() != null) {
				data.getButton().sortActions();				
				String tenantId = TenantContext.getCurrentTenant();
				log.info("SchedulerPlanner generation Web Socket tenantId is {}", tenantId);
				String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
				if(StringUtils.hasText(subscriptionIds)) {
					subscriptionId = Long.valueOf(subscriptionIds.trim());
				}
				projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
				clientId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_CLIENT));
				Thread thread = new Thread() {
					public void run() {
						try {
							success = false;
							TenantContext.setCurrentTenant(tenantId);
							runButtonActions();
							success = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							
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

	private void runButtonActions() {
		try {	
			if (data != null) {
				log.debug("Button : {}", data.getButton().getName());
				listener.createInfo(data.getButton().getId(), data.getButton().getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setData(data);
				runner.setMode(RunModes.M);
				runner.setProjectCode(projectCode);
				runner.setClientId(clientId);
				runner.setProfileId(profileId);
				runner.setSession(httpSession);
				runner.setListener(listener);
				runner.setStop(false);
				runner.run();
				success = true;
			} else {
				log.error("Button action data is NULL!");
				listener.error("Unknown Button action data.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running button actions : {}",
					data != null && data.getButton() != null ? data.getButton().getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		data = null;
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
