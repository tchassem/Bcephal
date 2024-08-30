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
import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.archive.service.ArchiveRestorationRunner;
import com.moriset.bcephal.archive.service.ArchiveService;
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
public class ArchiveRestorationWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	Archive archive;	

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ArchiveRestorationRunner runner;
	
	@Autowired
	ArchiveService archiveService;

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
		} else if (archive == null) {
			Long archiveId = deserialize(message.getPayload());
			if (archiveId != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.debug("Archive restoration Web Socket tenantId is {}", tenantId);
				Thread thread = new Thread() {
					public void run() {
						try {
							TenantContext.setCurrentTenant(tenantId);
							restore(archiveId);
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

	private void restore(Long archiveId) {
		try {
			log.debug("Try to read archive by Id : {}", archiveId);
			listener.createInfo(archiveId, "");	
			archive = archiveService.getById(archiveId);
					
			if (archive != null) {
				log.debug("Archive : {}", archive.getName());				
				listener.createInfo(archive.getId(), archive.getName());				
				String sessionId = this.session.getHandshakeHeaders().getFirst(HttpHeaders.COOKIE).split("=")[1];
				
				runner.setUsername(username);
				runner.setSessionId(sessionId);
				runner.setArchive(archive);
				runner.setListener(listener);
				runner.run();

			} else {
				log.error("Archive is NULL!");
				listener.error("Unknown archive.", false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while restore archive : {}",
					archive != null && archive != null ? archive.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	private void dispose() {
		archive = null;
		listener = null;
		stopped = false;
		runner = null;
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
