package com.moriset.bcephal.project.socket;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.project.archive.ImportProjectData;
import com.moriset.bcephal.project.service.ProjectManager;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.security.repository.SimpleArchiveRepository;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImportProjectWebSocketHandler extends CustomAbstractWebSocketHandler {

	@Autowired
	ProjectManager projectManager;

	@Autowired
	ObjectMapper mapper;

	ImportProjectData importProjectData;
	SimpleArchive archive;

	@Autowired
	SimpleArchiveRepository simpleArchiveRepository;
		
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		String text = message.getPayload().toString();
		if (StringUtils.hasText(text)) {
			try {
				log.info(text);
				log.info(session.getAttributes().toString());
				try {
					SimpleArchive ar = mapper.reader().readValue(text, SimpleArchive.class);
					if(ar.isManualImport()) {
						archive = projectManager.buildManualImportProjectSimpleArchive(ar);
					}else {
						archive = simpleArchiveRepository.findById(ar.getId()).get();
					}					
					try {
						boolean isProjectExist = projectManager.checkIfProjectExists(archive);
						session.sendMessage(new TextMessage(mapper.writeValueAsString(isProjectExist)));
					} catch (Exception e) {
						session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), e.getMessage()));
					}
				} catch (Exception e) {
					try {
						importProjectData = mapper.reader().readValue(text, ImportProjectData.class);
						if (importProjectData != null) {
							importProjectData.setFilePath(archive.getRepository());
							HttpHeaders requestHeaders = new HttpHeaders();
							session.getHandshakeHeaders().forEach((key,value)->{
								if(key.contains("authorization") || key.contains("cookie")
										||key.contains("accept-encoding")
										) {
									requestHeaders.addAll(key, value);
								}
							});
							requestHeaders.add("bc-profile", importProjectData.getProfileId().toString());
							requestHeaders.add("bc-client",  importProjectData.getClientId().toString());
							
							httpSession.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, requestHeaders);
							Project result = projectManager.importProject(importProjectData, importProjectData.getClientId(),
									importProjectData.getProfileId(), httpSession, importProjectData.getLocale());
							projectManager.SaveRightToProject(result, importProjectData.getProfileId(), httpSession, importProjectData.getLocale());
							session.sendMessage(new TextMessage(mapper.writeValueAsString(result)));
							session.close(CloseStatus.NORMAL);
						}
						else {
							throw new BcephalException();
						}
					} catch (Exception e2) {
						session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), e2.getMessage()));
					}
				}
			} catch (Exception e) {
				session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), e.getMessage()));
			}
		}
	}

//	@Override
//	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
//		try {
//			log.info(message.getPayload().toString());
//			if (message.getPayload().limit() > 0) {
//				byte[] byte_ = new byte[message.getPayload().limit()];
//				message.getPayload().get(0, byte_);
//				FileOutputStream out = new FileOutputStream(importProjectData.getFilePath(), true);
//				if (byte_ != null && byte_.length > 0) {
//					out.write(byte_);
//				}
//				out.close();
//			} else {
//				if (importProjectData != null) {
//					// faire un cheking
//					Project result = projectManager.importProject(importProjectData, importProjectData.getClientId(),
//							importProjectData.getProfileId(), httpSession, importProjectData.getLocale());
//					session.sendMessage(new TextMessage(mapper.writeValueAsString(result)));
//					session.close(CloseStatus.NORMAL);
//				}
//			}
//		} catch (Exception e) {
//			session.close(new CloseStatus(CloseStatus.SERVER_ERROR.getCode(), e.getMessage()));
//		}
//	}

}
