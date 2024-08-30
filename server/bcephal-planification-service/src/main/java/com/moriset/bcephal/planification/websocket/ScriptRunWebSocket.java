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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.planification.domain.script.Script;
import com.moriset.bcephal.planification.repository.ScriptRepository;
import com.moriset.bcephal.planification.service.ScriptRunner;
import com.moriset.bcephal.planification.service.ScriptService;
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
public class ScriptRunWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	Script script;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ScriptRunner runner;
	
	@Autowired
	ScriptService scriptService;
	
	@Autowired
	ScriptRepository repository;

	int value = 1;
	private boolean success;

	private Long subscriptionId;

	private String projectCode;

	private Long scriptId;

	private Long profileId;
	
	private List<Long> scriptIds;
	
	
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
		} else if (script == null) {
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
			scriptIds = deserialize(message.getPayload());
			if (scriptIds != null && scriptIds.size() > 0) {
				listener.createInfo((long)1, " start script");
				listener.start(scriptIds.size());
				for (Long id : scriptIds) {
					if(id != null) {
						Optional<Script> response = repository.findById(id);
						script = response.isPresent() ? response.get() : null;
					}
					if (script != null) {
						String tenantId = TenantContext.getCurrentTenant();						
						String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
						if(StringUtils.hasText(subscriptionIds)) {
							subscriptionId = Long.valueOf(subscriptionIds.trim());
						}
						scriptId =  id;
						projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
						profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
						log.debug("Script Web Socket tenantId is {}", tenantId);
						Thread thread = new Thread() {
							public void run() {
								try {
									success = false;
									TenantContext.setCurrentTenant(tenantId);
									runScript();
									success = true;
								} catch (Exception e) {
									e.printStackTrace();
								}finally {
									if(success) {
										String rightLevel = "RUN";							
										scriptService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), scriptId, scriptService.getFunctionalityCode(), rightLevel, profileId);
										scriptId = null;
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

	private void runScript() {
		try {
			listener.createInfo(script.getId(), script.getName());			
			if (script != null) {
				log.debug("Script : {}", script.getName());
				listener.createInfo(script.getId(), script.getName());				
				runner.setUsername(username);
				runner.setMode(RunModes.M);
				runner.setScript(script);
				runner.setListener(listener);
				runner.run();
				success = true;
			} else {
				log.error("Script is NULL!");
				listener.error("Unknown script.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running script : {}",
					script != null && script != null ? script.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		script = null;
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
