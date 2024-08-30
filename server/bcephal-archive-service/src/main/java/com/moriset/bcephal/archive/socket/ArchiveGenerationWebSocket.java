/**
 * 
 */
package com.moriset.bcephal.archive.socket;

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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.archive.domain.ArchiveConfiguration;
import com.moriset.bcephal.archive.service.ArchiveConfigurationService;
import com.moriset.bcephal.archive.service.ArchiveGenerationRunner;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;

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
public class ArchiveGenerationWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	ArchiveConfiguration configuration;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ArchiveGenerationRunner runner;

	@Autowired
	ArchiveConfigurationService archiveConfigurationService;
	int value = 1;

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
		} else if (configuration == null) {
			Long configurationId = deserialize(message.getPayload());
			if(configurationId != null) {
				configuration =	archiveConfigurationService.getById(configurationId);
			}
			if (configuration != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.info("Archive generation Web Socket tenantId is {}", tenantId);
				Thread thread = new Thread() {
					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							generate();
						} catch (Exception e) {
							e.printStackTrace();
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

	private void generate() {
		try {
			listener.createInfo(configuration.getId(), configuration.getName());			
			if (configuration != null) {
				log.debug("Archive configuration : {}", configuration.getName());
				listener.createInfo(configuration.getId(), configuration.getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setConfiguration(configuration);
				runner.setListener(listener);
				runner.run();

			} else {
				log.error("Archive configuration is NULL!");
				listener.error("Unknown archive configuration.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while generating archive : {}",
					configuration != null && configuration != null ? configuration.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		configuration = null;
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

	private Long deserialize(String json) {
		try {
			return mapper.readValue(json, Long.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
