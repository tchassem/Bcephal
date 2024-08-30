/**
 * 
 */
package com.moriset.bcephal.sourcing.grid.socket;

import java.io.IOException;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.loader.repository.FileLoaderRepository;
import com.moriset.bcephal.loader.service.FileLoaderProperties;
import com.moriset.bcephal.loader.service.FileLoaderRunData;
import com.moriset.bcephal.loader.service.FileLoaderRunnerForGrid;
import com.moriset.bcephal.loader.service.FileLoaderRunnerForMaterializedGrid;
import com.moriset.bcephal.loader.service.FileLoaderService;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Data
@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@EqualsAndHashCode(callSuper=false)
public class FileLoaderWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	FileLoaderRunData data;

	boolean stopped;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	FileLoaderProperties properties;

	@Autowired
	FileLoaderRepository fileLoaderRepository;

	@Autowired
	GrilleService grilleService;
	
	@Autowired
	FileLoaderService fileLoaderService;

	@Autowired
	FileLoaderRunnerForGrid runnerForGrid;
	
	@Autowired
	FileLoaderRunnerForMaterializedGrid runnerForMaterializedGrid;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;
	
	int value = 1;

	private Long subscriptionId;

	private String projectCode;

	private boolean success;

	private Long dataId;

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
		} else if (data == null) {
			data = deserialize(message.getPayload());
			if (data != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.info("File Loader Web Socket tenantId is {}", tenantId);
				String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
				if(StringUtils.hasText(subscriptionIds)) {
					subscriptionId = Long.valueOf(subscriptionIds.trim());
				}
				
				projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
				dataId = data.getId();
				Thread thread = new Thread() {
					public void run() {
						try {
							success = false;
							TenantContext.setCurrentTenant(tenantId);
							load();
							success = true;
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							if(success) {
								String rightLevel = "LOAD";							
								fileLoaderService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), dataId, fileLoaderService.getFunctionalityCode(), rightLevel, profileId);
								dataId = null;
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

	private void load() {
		try {

			listener.createInfo(data.getId(), "FileLoader Run");

			if (data.getId() != null) {
				log.debug("Read FileLoader by ID : {}", data.getId());
				Optional<FileLoader> result = fileLoaderRepository.findById(data.getId());
				if (result.isPresent()) {
					data.setLoader(result.get());
					data.getLoader().getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(data.getLoader().getId(), FileLoader.class.getName()));
					data.getLoader().sortRoutines();
				}
			}
			if (data.getLoader() != null) {
				log.debug("FileLoader : {}", data.getLoader().getName());
				listener.createInfo(data.getId(), data.getLoader().getName());
//				this.data.setFiles(new ArrayList<>(0));		
//				if (data.getRepositories() != null && !data.getRepositories().isEmpty()) {
//					if(data.getLoader().getSource() == FileLoaderSource.LOCAL) {
//						String repository = data.getRepositories().get(0);
//						repository = FilenameUtils.separatorsToSystem(repository);					
//						for(File file : new ArrayList<>(data.getFiles())) {
//							String path = FilenameUtils.concat(repository, file.getName());
//							data.getFiles().remove(file);
//							data.getFiles().add(new File(path));
//						}	
//					}	
//				} else {
//					throw new BcephalException("Repository is empty!");
//				}
				
//				if (StringUtils.hasLength(data.getRepository())) {
//					data.setRepository(FilenameUtils.separatorsToSystem(data.getRepository()));
//					if (new File(data.getRepository()).isFile()) {
//						data.setRepository(new File(data.getRepository()).getParent());
//					}
//				} else {
//					throw new BcephalException("Repository is empty!");
//				}
				
				if (data.getLoader().getUploadMethod() == FileLoaderMethod.DIRECT_TO_MATERIALIZED_GRID
						|| data.getLoader().getUploadMethod() == FileLoaderMethod.NEW_MATERIALIZED_GRID) {
					runnerForMaterializedGrid.setUsername(username);
					runnerForMaterializedGrid.setSession(httpSession);
					runnerForMaterializedGrid.setData(data);
					runnerForMaterializedGrid.setProperties(properties);
					runnerForMaterializedGrid.setListener(listener);
					runnerForMaterializedGrid.setDeleteTmpDir(true);
					runnerForMaterializedGrid.run();
				}				
				else if (data.getLoader().getUploadMethod() == FileLoaderMethod.DIRECT_TO_GRID) {
					runnerForGrid.setUsername(username);
					runnerForGrid.setSession(httpSession);
					runnerForGrid.setData(data);
					runnerForGrid.setProperties(properties);
					runnerForGrid.setListener(listener);
					runnerForGrid.setDeleteTmpDir(true);
					runnerForGrid.run();
				} else {
					runnerForGrid.setUsername(username);
					runnerForGrid.setSession(httpSession);
					runnerForGrid.setData(data);
					runnerForGrid.setProperties(properties);
					runnerForGrid.setListener(listener);
					runnerForGrid.setDeleteTmpDir(true);
					runnerForGrid.run();
				}
				success = true;
			} else {
				log.error("Unknown FileLoader : {}", data.getId());
				listener.error("Unknown FileLoader : " + data.getId(), false);
			}
			if (listener != null) {
				listener.end();
			}
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running file loader : {}",
					data != null && data.getLoader() != null ? data.getLoader().getName() : "", e);
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

	private FileLoaderRunData deserialize(String json) {
		try {
			return mapper.readValue(json, FileLoaderRunData.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
