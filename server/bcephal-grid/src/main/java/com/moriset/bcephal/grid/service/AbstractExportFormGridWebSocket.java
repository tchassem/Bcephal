package com.moriset.bcephal.grid.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.form.FormButtonActionData;
import com.moriset.bcephal.grid.service.ExportDataTransfert.Decision;
import com.moriset.bcephal.grid.service.form.FormModelService;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.utils.RequestParams;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class AbstractExportFormGridWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	FormButtonActionData data;
	boolean stopped;

	@Autowired
	FormModelService formModelService;

	private Long subscriptionId;

	private String projectCode;
	boolean success = false;

	private Long dataId;

	private Long profileId;
	
	protected MainObjectService<? extends MainObject, BrowserData> getService() {
		return  formModelService;
	}
	
	protected String getFunctionalityCode() {
		return getService().getFunctionalityCode();
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
		log.debug(message.getPayload());
		log.info(" session id " + session.getId());
		refresh(session);
		this.session = session;
		if (listener == null) {
			listener = getNewListener();
		}
		if ("STOP".equals(message.getPayload())) {
			stopped = true;
		} else if (data == null) {
			data = deserialize(message.getPayload());
			if (data != null && data.getModel() != null) {
				String tenantId = TenantContext.getCurrentTenant();
				log.info("File Loader Web Socket tenantId is {}", tenantId);
				String subscriptionIds = (String) session.getAttributes().get(RequestParams.BC_CLIENT);
				if(StringUtils.hasText(subscriptionIds)) {
					subscriptionId = Long.valueOf(subscriptionIds.trim());
				}
				
				projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
				profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
				Thread thread = new Thread() {
					public void run() {
						success = false;
						try {
							TenantContext.setCurrentTenant(tenantId);
							//String locale = (String) session.getAttributes().get("Accept-Language");
							export(Locale.ENGLISH);
							success = true;
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							if(success) {
								String rightLevel = "EXPORT";	
								getService().saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), dataId, getFunctionalityCode(), rightLevel, profileId);									
								dataId = null;
							}
						}
					}
				};
				thread.start();
			}
		}
	}

	private TaskProgressListener getNewListener() {
		return new TaskProgressListener() {
			@Override
			public void SendInfo() {
				if (!stopped) {
					try {
						String json = mapper.writeValueAsString(getInfo());
						session.sendMessage(new TextMessage(json));
						Thread.sleep(100);
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

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("Connection closed !");
		log.debug(" session id " + session.getId());
		dispose();
	}

	private void export(java.util.Locale locale) {
		try {
			listener.createInfo(data.getModel().getUserFilter().getId(), "Export from User id");
			if (data.getModelId() != null) {
				buildModel();
				dataId = data.getModelId();
				List<String> paths = List.of("");	// grilleService.export(data, null);
				if (listener != null) {
					// listener.createInfo(data.getFilter().getUserId(), data.getModel().getName());
					listener.start(paths.size());
				}
				
				for(String path : paths) {
					listener.nextStep(1);
					onLoad(path);
				}
				success = true;
			} else {
				log.error("Unknown FileLoader : {}", data.getModel().getName());
				listener.error("Unknown FileLoader : " + data.getModel().getName(), false);
			}
			if (listener != null) {
				listener.getInfo().setMessage(null);
				listener.end();
			}
			
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running file loader : {}",
					data.getModel() != null ? data.getModel().getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose();
		}
	}

	protected void buildModel() {		
		data.setModel(formModelService.getById(data.getModelId()));
	}

	private void dispose() {
		data = null;
		listener = null;
		stopped = false;
	}

	public void onLoad(String path) throws IOException {
		FileInputStream in = new FileInputStream(path);
		int buffersize = 24 * 1024;
		byte[] data = new byte[buffersize];
		int length = 0;
		long Total = in.available() / buffersize + ((in.available() % buffersize) > 0 ? 1 : 0);
		listener.createSubInfo(null, path);
		listener.startSubInfo(Total);
		boolean isInit = false;
		while ((length = in.read(data)) > 0) {
			Decision decision = isInit == false && length == buffersize ? Decision.NEW : length == buffersize ?  Decision.CONTINUE : Decision.END;
			sendExportDataTransfert(new ExportDataTransfert(length == buffersize ? data : Arrays.copyOfRange(data, 0, length), decision, GrilleExportDataType.CSV));
			data = new byte[buffersize];
			isInit = true;
			if(Decision.END.equals(decision)) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		in.close();
		listener.endSubInfo();
		try {
			FileUtils.forceDelete(new File(path));
		} catch (Exception e) {
			log.debug("Unable to delete temp file", e);
		}
	}

	public void sendExportDataTransfert(ExportDataTransfert dataTranfert) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		try {
			String json = mapper.writeValueAsString(dataTranfert);
			listener.getInfo().getCurrentSubTask().setMessage(json);
			listener.nextSubInfoStep(1);
		} catch (Exception e) {
			log.error("Fail to serialize data", data.getModel().getName());
			listener.error("Fail to serialize  data" + data.getModel().getName(), false);
		}
	}

	private FormButtonActionData deserialize(String json) {
		try {
			return mapper.readValue(json, FormButtonActionData.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
