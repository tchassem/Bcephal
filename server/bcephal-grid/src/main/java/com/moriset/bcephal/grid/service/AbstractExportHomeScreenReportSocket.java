package com.moriset.bcephal.grid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.MaterializedExportData;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
public class AbstractExportHomeScreenReportSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	GrilleService grilleService;

	@Autowired
	JoinService joinService;

	@Autowired
	MaterializedGridService materializedGridService;

	TaskProgressListener listener;

	WebSocketSession session;

	GrilleExportData GridData;
	MaterializedExportData Matdata;
	
	boolean stopped;

	private Long subscriptionId;

	private String projectCode;
	
	boolean success = false;
	
	private GrilleType type;

	private Long dataId;

	private Long profileId;


	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("Connection closed !");
		log.debug(" session id " + session.getId());
		dispose();
	}

	private void dispose() {
		Matdata = null;
		GridData = null;
		listener = null;
		stopped = false;
	}

	@SuppressWarnings("unused")
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

}
