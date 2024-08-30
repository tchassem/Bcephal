/**
 * 
 */
package com.moriset.bcephal.planification.websocket;

import java.io.IOException;
import java.util.List;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.repository.TransformationRoutineRepository;
import com.moriset.bcephal.planification.service.RoutineRunner;
import com.moriset.bcephal.planification.service.TransformationRoutineService;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransformationRoutineRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	TransformationRoutine routine;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	RoutineRunner runner;
	
	@Autowired
	TransformationRoutineService transformationRoutineService;
	
	@Autowired
	TransformationRoutineRepository repository;

	int value = 1;
	private boolean success;

	private Long subscriptionId;

	private String projectCode;

	private Long routineId;

	private Long profileId;
	
	private List<Long> routineIds;
	
	
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
		} else if (routine == null) {
			String tenantId = TenantContext.getCurrentTenant();
			Thread th = new Thread() {
				public void run() {
					try {
						TenantContext.setCurrentTenant(tenantId);
						BuilhandleTextMessage(session, message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			th.start();
		}
	}

	
	public void BuilhandleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
			routineIds = deserialize(message.getPayload());
			if (routineIds != null && routineIds.size() > 0) {
				listener.createInfo((long)1, " start Transformation routine");
				listener.start(routineIds.size());
				for (Long id : routineIds) {
					if(id != null) {
						Optional<TransformationRoutine> response = repository.findById(id);
						routine = response.isPresent() ? response.get() : null;
					}
					if (routine != null) {
						String tenantId = TenantContext.getCurrentTenant();						
						String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
						if(StringUtils.hasText(subscriptionIds)) {
							subscriptionId = Long.valueOf(subscriptionIds.trim());
						}
						routineId =  id;
						projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
						profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
						log.info("Archive generation Web Socket tenantId is {}", tenantId);
						Thread thread = new Thread() {
							public void run() {
								try {
									success = false;
									TenantContext.setCurrentTenant(tenantId);
									runRoutine();
									success = true;
								} catch (Exception e) {
									e.printStackTrace();
								}finally {
									if(success) {
										String rightLevel = "RUN";							
										transformationRoutineService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), routineId, transformationRoutineService.getFunctionalityCode(), rightLevel, profileId);
										routineId = null;
									}
								}
							}
						};
						thread.start();
						thread.join();
					}
				}
				if (listener != null) {
					listener.end();
				}
				dispose();
			}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("Connection closed !");
		log.debug(" session id " + session.getId());
		log.debug(" value === " + value);
		dispose();

	}

	private void runRoutine() {
		try {
			listener.createInfo(routine.getId(), routine.getName());			
			if (routine != null) {
				log.debug("Routine : {}", routine.getName());
				listener.createInfo(routine.getId(), routine.getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setRoutine(routine);
				runner.setListener(listener);
				runner.run();
				success = true;
			} else {
				log.error("Routine is NULL!");
				listener.error("Unknown routine.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running routine : {}",
					routine != null && routine != null ? routine.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		routine = null;
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

	private List<Long> deserialize(String json) {
		try {
			return mapper.readValue(json, new TypeReference<List<Long>>() {
			});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
