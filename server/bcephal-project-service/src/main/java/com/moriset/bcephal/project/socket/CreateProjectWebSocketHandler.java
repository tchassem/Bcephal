package com.moriset.bcephal.project.socket;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.project.service.ProjectManager;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.repository.SimpleArchiveRepository;
import com.moriset.bcephal.security.service.ProjectBackupService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProjectWebSocketHandler extends CustomAbstractWebSocketHandler {

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
	
	Long subscriptionId = null;
	Long profileId = null;
	Locale locale = null;
	
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
					Project project = mapper.reader().readValue(message.getPayload().toString(), Project.class);
					if (listener != null) {
						listener.createInfo(project.getId(),project.getName());
						listener.start(15);
					}
					String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
					if(StringUtils.hasText(subscriptionIds)) {
						subscriptionId = Long.valueOf(subscriptionIds.trim());
					}
					
					String profileIds = (String) session.getAttributes().get(RequestParams.BC_PROFILE);
					if(StringUtils.hasText(profileIds)) {
						profileId = Long.valueOf(profileIds.trim());
					}
					
					String locales = (String) session.getAttributes().get(RequestParams.LANGUAGE);
					if(StringUtils.hasText(locales)) {
						locale = Locale.forLanguageTag(locales.trim());
					}
					String remoteAddress = "remote_address";
					HttpHeaders requestHeaders = new HttpHeaders();
					session.getHandshakeHeaders().forEach((key,value)->{
						if(key.equalsIgnoreCase(HttpHeaders.AUTHORIZATION)) {
							requestHeaders.addAll(HttpHeaders.AUTHORIZATION, value);
						}else
							if(key.equalsIgnoreCase(HttpHeaders.ACCEPT_ENCODING)) {
								requestHeaders.addAll(HttpHeaders.ACCEPT_ENCODING, value);
							}else
							if(key.equalsIgnoreCase(HttpHeaders.COOKIE + "__")) {
								requestHeaders.addAll(HttpHeaders.COOKIE, value);
							}else
								if(key.equalsIgnoreCase(remoteAddress)) {
									requestHeaders.addAll(remoteAddress, value);
								}
					});
					requestHeaders.addAll(RequestParams.BC_CLIENT, Arrays.asList(subscriptionId + ""));
					requestHeaders.addAll(RequestParams.BC_PROFILE, Arrays.asList(profileId + ""));
					listener.nextStep(1);
					project = createProject(subscriptionId, profileId, project, locale, httpSession, requestHeaders);
					listener.nextStep(2);
					session.sendMessage(new TextMessage(mapper.writeValueAsString(project)));
					listener.end();
					
//						String rightLevel = "RUN";							
//						reconciliationModelService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), autoRecoId, reconciliationModelService.getFunctionalityCode(), rightLevel);
						
				}
			}
		} catch (Exception e) {
			if (listener != null) {
				listener.error(e.getMessage(), true);
				listener.end();
			}
			dispose();
			session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), e.getMessage()));
		}
	}

	
	public Project createProject(Long subscriptionId, Long profileId,  Project project,  java.util.Locale locale, HttpSession session, HttpHeaders headers) throws Exception {
		log.trace("Call createProject : {}", project.getName());
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			project.setName(projectManager.normalizeProjectName(project.getName()));
			listener.nextStep(1);
			project = projectManager.createProject(profileId,session, subscriptionId, project, locale,listener);
			listener.nextStep(1);
			projectManager.SaveRightToProject(project, profileId, session, locale);
			return  project;
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while creating the project : {}", project.getName(), e);
			String message = messageSource.getMessage("unable.to.create.project", new String[] { project.getName() },
					locale);
			throw new BcephalException(message);
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
