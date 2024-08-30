package com.moriset.bcephal.project.socket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.project.service.ProjectManager;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.security.repository.SimpleArchiveRepository;
import com.moriset.bcephal.security.service.ProjectBackupService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BackupProjectWebSocketHandler extends CustomAbstractWebSocketHandler {

	@Autowired
	ProjectManager projectManager;
	@Autowired
	ResourceLoader resourceLoader;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	MessageSource messageSource;

	TaskProgressListener listener;
	WebSocketSession session;
	boolean stopped;

	@Autowired
	ProjectBackupService projectBackupService;

	@Autowired
	SimpleArchiveRepository simpleArchiveRepository;
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		try {
			this.session = session;
			if (listener == null) {
				listener = getNewListener();
			}
			if ("STOP".equals(message.getPayload())) {
				stopped = true;
			} else {
				String text = message.getPayload().toString();
				if (StringUtils.hasText(text)) {
					log.info(text);
					log.info(session.getAttributes().toString());
					SimpleArchive archive = mapper.reader().readValue(message.getPayload().toString(),
							SimpleArchive.class);
					archive.setUserName(session.getPrincipal().getName());
					if (listener != null) {
						listener.createInfo(archive.getId(),archive.getName());
						listener.start(2);
					}
					
					String archiveName = archive.getName();
					if(!archiveName.endsWith(".bcp")) {
						archiveName = archiveName + ".bcp";
					}
					boolean isNameUsed= simpleArchiveRepository.existsByName(archiveName);
					session.sendMessage(new TextMessage(mapper.writeValueAsString(isNameUsed)));
					if (!isNameUsed) {
						if (archive != null) {
							listener.nextStep(1);
							String path = projectManager.backupProject(archive, archive.getLocale());
							archive.setRepository(path);
							listener.nextStep(1);
							projectBackupService.create(archive, archive.getLocale());
							
							listener.end();
						}
					}
					else {
						return;
					}
				}
			}
		} catch (Exception e) {
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
			session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), e.getMessage()));
		}
	}

	private void dispose() {
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
}
