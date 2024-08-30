/**
 * 
 */
package com.moriset.bcephal.utils.socket;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.moriset.bcephal.domain.socket.TaskProgressInfo;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.utils.socket.WebSocketDataTransfert.Decision;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Component
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@EqualsAndHashCode(callSuper = false)
@Data
public class WebSocketDataUpload extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	@Value("${bcephal.project.temp-dir}")
	protected String tempDir;

	TaskProgressListener listener;

	WebSocketSession session;

	boolean stopped;

	String path;

	OutputStreamWriter outStream;

	@Override
	protected synchronized void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
		//log.debug(message.getPayload().array().toString());
		//log.info(" session id " + session.getId());
		refresh(session);
		this.session = session;
		if (listener == null) {
			listener = getNewListener();
		}
		String mes = message.getPayload() != null ? 
				new String(message.getPayload().array(), StandardCharsets.UTF_8)
				: "";
		if ("STOP".equalsIgnoreCase(mes)) {
			stopped = true;
		} 
		else {
			WebSocketDataTransfert data = deserialize(message.getPayload().array());
			if (data != null) {
				String tenantId = TenantContext.getCurrentTenant();
				//log.info("Web Socket Data Upload tenantId is {}", tenantId);
				TenantContext.setCurrentTenant(tenantId);
				readData(data, java.util.Locale.ENGLISH);
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("Connection closed !");
		log.debug(" session id " + session.getId());
		dispose();
	}
	
	private void readData(final WebSocketDataTransfert data, java.util.Locale locale) {		
			try {
				listener.createInfo(data.getUserId(), "Download data from : " + data.getName());
				if (data.getDecision() != null && data.getDecision().isNew()) {
					Path path2 = Paths.get(tempDir,System.currentTimeMillis() + "_", data.getFolder(), data.getName());
					if (!path2.getParent().toFile().exists()) {
						path2.getParent().toFile().mkdirs();
					}
					path = path2.toString();
					log.info(" Download data from {}", path);
				}
				if (data.getData().length > 0) {
						FileUtils.writeByteArrayToFile(new File(path), data.getData(),true);		
				}				
				if (data.getDecision() != null && data.getDecision().isEnd()) {
					WebSocketDataTransfert reponseData = new WebSocketDataTransfert();
					reponseData.setRemotePath(path);
					reponseData.setName(data.getName());
					sendDataTransfert(reponseData, new TaskProgressInfo());
					log.info(" end file {}", path);
				}
				if (data.getDecision() != null && data.getDecision().isClose()) {
					WebSocketDataTransfert reponseData = new WebSocketDataTransfert();
					reponseData.setDecision(Decision.CLOSE);
					sendDataTransfert(reponseData, new TaskProgressInfo());
					log.info(" Close file {}", path);
				}
				if (data.getDecision().isStop() || data.getDecision().isClose()) {
					if (listener != null) {
						listener.getInfo().setMessage(null);
						listener.end();
					}
					dispose();
				}
			} catch (Exception e) {
				log.error("unexpected error while running upload data : {}", data != null ? data.getName() : "", e);
				if (listener != null) {
					listener.error(e.getMessage(), true);
				}
				dispose();
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

	
	
	@SuppressWarnings("unused")
	private String base64Decode(String value) {
		return new String(Base64.getDecoder().decode(value));
	}

	@SuppressWarnings("unused")
	private WebSocketDataTransfert deserialize(String json) {
		try {
			return mapper.readValue(json, WebSocketDataTransfert.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private WebSocketDataTransfert deserialize(byte[] json) throws IOException {
		try {
			return mapper.readValue(new String(json, StandardCharsets.UTF_8), WebSocketDataTransfert.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public void onLoad(String path) throws IOException {
//		FileInputStream in = new FileInputStream(path);
//		byte[] data = new byte[in.available()];
//		while (in.read(data) > 0) {
//			sendDataTransfert(new WebSocketDataTransfert(data, this.data.getType()), new TaskProgressInfo());
//		}
//		in.close();
//		try {
//			FileUtils.forceDelete(new File(path));
//		} catch (Exception e) {
//			log.debug("Unable to delete temp file", e);
//		}
//	}

	public void sendDataTransfert(WebSocketDataTransfert dataTranfert, TaskProgressInfo info) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		try {
			String json = mapper.writeValueAsString(dataTranfert);
			info.setMessage(json);
			listener.setInfo(info);
			listener.SendInfo();
		} catch (Exception e) {
			log.error("Fail to serialize data");
			listener.error("Fail to serialize  data", false);
		}
	}
}
