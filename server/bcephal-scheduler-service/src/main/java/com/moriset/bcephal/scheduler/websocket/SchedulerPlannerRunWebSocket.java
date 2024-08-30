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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerRepository;
import com.moriset.bcephal.scheduler.service.SchedulerPlannerRunner;
import com.moriset.bcephal.scheduler.service.SchedulerPlannerService;
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
public class SchedulerPlannerRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	SchedulerPlanner schedulerPlanner;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	SchedulerPlannerRunner runner;
	
	@Autowired
	SchedulerPlannerRepository repository;

	@Autowired
	SchedulerPlannerService schedulerPlannerService;
	
	int value = 1;

	private Long subscriptionId;

	private String projectCode;

	private boolean success;

	private Long schedulerPlannerId;

	private Long profileId;

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
		} else if (schedulerPlanner == null) {
//			schedulerPlanner = deserialize(message.getPayload());
			
			Long id = Long.valueOf(message.getPayload());
			if(id != null) {
				Optional<SchedulerPlanner> response = repository.findById(id);
				schedulerPlanner = response.isPresent() ? response.get() : null;
			}
			if (schedulerPlanner != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.info("SchedulerPlanner generation Web Socket tenantId is {}", tenantId);
				String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
				if(StringUtils.hasText(subscriptionIds)) {
					subscriptionId = Long.valueOf(subscriptionIds.trim());
				}
				schedulerPlannerId = schedulerPlanner.getId();
				projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
				Thread thread = new Thread() {
					public void run() {
						try {
							success = false;
							TenantContext.setCurrentTenant(tenantId);
							runSchedulerPlanner();
							success = true;
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							if(success) {
								String rightLevel = "RUN";							
								schedulerPlannerService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), schedulerPlannerId, schedulerPlannerService.getFunctionalityCode(), rightLevel, profileId);
								schedulerPlannerId = null;
							}
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

	private void runSchedulerPlanner() {
		try {	
			if (schedulerPlanner != null) {
				log.debug("SchedulerPlanner : {}", schedulerPlanner.getName());
				listener.createInfo(schedulerPlanner.getId(), schedulerPlanner.getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setClientId(subscriptionId);
				runner.setSessionId(sessionId);
				runner.setSchedulerPlanner(schedulerPlanner);
				runner.setMode(RunModes.M);
				runner.setProjectCode(projectCode);
				runner.setProjectName(projectCode);
				runner.setSession(httpSession);
				runner.setHttpHeaders(this.session.getHandshakeHeaders());
				runner.setListener(listener);
				runner.setOperationCode(null);
				runner.setStop(false);
				runner.run();
				success = true;
			} else {
				log.error("SchedulerPlanner is NULL!");
				listener.error("Unknown scheduler planner.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running scheduler planner : {}",
					schedulerPlanner != null && schedulerPlanner != null ? schedulerPlanner.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		schedulerPlanner = null;
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

//	private SchedulerPlanner deserialize(String json) {
//		try {
//			return mapper.readValue(json, SchedulerPlanner.class);
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}
