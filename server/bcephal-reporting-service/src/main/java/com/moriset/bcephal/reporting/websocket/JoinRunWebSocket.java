/**
 * 
 */
package com.moriset.bcephal.reporting.websocket;

import java.io.IOException;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.repository.JoinRepository;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.planification.service.JoinRunner;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author EMMENI Emmanuel
 *
 */
@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JoinRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	Join join;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	JoinRunner runner;
	
	@Autowired
	JoinRepository repository;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;

	@Autowired
	JoinService joinService;
	int value = 1;

	private Long subscriptionId;

	private String projectCode;

	private Long dataId;
	private boolean success;

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
			// stop();
		} else if (join == null) {
//			join = deserialize(message.getPayload());
			
			Long id = Long.valueOf(message.getPayload());
			if(id != null) {
				Optional<Join> response = repository.findById(id);
				join = response.isPresent() ? response.get() : null;
			}
			
			if (join != null) {
				
				join.getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(join.getId(), Join.class.getName()));
				join.sortRoutines();
				
				String tenantId = TenantContext.getCurrentTenant();
				log.info("Join generation Web Socket tenantId is {}", tenantId);
				String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
				if(StringUtils.hasText(subscriptionIds)) {
					subscriptionId = Long.valueOf(subscriptionIds.trim());
				}
				dataId = id;
				projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
				Thread thread = new Thread() {
					

					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							success = false;
							runJoin();
							success = true;
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							if(success) {
								String rightLevel = "RUN";							
								joinService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), dataId, joinService.getFunctionalityCode(), rightLevel, profileId);
								dataId = null;
							}
						}
					}
				};
				thread.start();
				// load();
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

	private void runJoin() {
		try {	
			if (join != null) {
				log.debug("Join : {}", join.getName());
				listener.createInfo(join.getId(), join.getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setJoin(join);
				runner.setMode(RunModes.M);
				runner.setSession(httpSession);
				runner.setListener(listener);
				runner.run();
				success = true;
			} else {
				log.error("Join is NULL!");
				listener.error("Unknown join.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running join : {}",
					join != null && join != null ? join.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		join = null;
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

//	private Join deserialize(String json) {
//		try {
//			return mapper.readValue(json, Join.class);
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}
