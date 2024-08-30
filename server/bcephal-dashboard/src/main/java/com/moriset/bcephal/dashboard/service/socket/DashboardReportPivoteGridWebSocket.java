/**
 * 
 */
package com.moriset.bcephal.dashboard.service.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.service.DashboardReportService;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.ExportDataTransfert;
import com.moriset.bcephal.grid.service.ExportDataTransfert.Decision;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.multitenant.jpa.CustomAbstractWebSocketHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardReportPivoteGridWebSocket extends CustomAbstractWebSocketHandler {

	@Autowired
	ObjectMapper mapper;

	TaskProgressListener listener;

	WebSocketSession session;

	GrilleExportData data;
	boolean stopped;

	private Long subscriptionId;

	private String projectCode;
	boolean success = false;

	private Long dataId;
	
	private GrilleType type;

	private Long profileId;
	

	@Autowired
	DashboardReportService dashboardReportService;
	
	
	
	protected String getFunctionalityCode() {
		if(GrilleType.REPORT.equals(type)) {
			return FunctionalityCodes.REPORTING_REPORT_GRID;
		}
		return dashboardReportService.getFunctionalityCode();
	}
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
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
			if (data != null && data.getFilter().getGrid() != null) {
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
								if(GrilleType.DASHBOARD_REPORT.equals(type)) {
									dashboardReportService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), dataId, dashboardReportService.getFunctionalityCode(dataId), rightLevel,profileId);
								}else {
									dashboardReportService.saveUserSessionLog(session.getPrincipal().getName(), subscriptionId, projectCode, session.getId(), dataId, getFunctionalityCode(), rightLevel, profileId);									
								}
								dataId = null;
								type = null;
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
		dispose();
	}

	private void export(java.util.Locale locale) {
		try {
			listener.createInfo(data.getFilter().getUserId(), "Export from User id");
			DashboardReport report = buildGrid();
			if(report != null) {
			 if (report.getDataSourceType().isJoin()) {
			    dataId = ((JoinFilter)data.getFilter()).getJoin().getId();
			 }else {
				 dataId = data.getFilter().getGrid().getId();
			 }
				List<String> paths = dashboardReportService.export(report, data.getType(),null);
				if (listener != null) {
					listener.createInfo(data.getFilter().getUserId(),""/*data.getFilter().getGrid().getName()*/);
					listener.start(paths.size());
				}
				
				for(String path : paths) {
					listener.nextStep(1);
					onLoad(path);
				}
				success = true;	
			 }else {
						log.error("Unknown Join : {}", ""/*data.getFilter().getJoin().getName()*/);
						listener.error("Unknown Join : " /*+data.getFilter().getJoin().getName()*/, false);
					}
			if (listener != null) {
				listener.getInfo().setMessage(null);
				listener.end();
			}			
			dispose();
		} catch (Exception e) {
			log.error("unexpected error while running file loader : {}",
					data.getFilter() != null && data.getFilter().getGrid() != null ? data.getFilter().getGrid().getName() : "", e);
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

	private GrilleExportData deserialize(String json) {
		try {
			return mapper.readValue(json, GrilleExportData.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
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
			sendExportDataTransfert(new ExportDataTransfert(length == buffersize ? data : Arrays.copyOfRange(data, 0, length),decision, this.data.getType()));
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
//			FileSystemUtils.deleteRecursively(new File(path));
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
			log.error("Fail to serialize data", data.getFilter().getGrid().getName());
			listener.error("Fail to serialize  data" + data.getFilter().getGrid().getName(), false);
		}
	}


	
	protected DashboardReport buildGrid() {
		DashboardReport report = dashboardReportService.getById(getData().getFilter().getGrid().getId());
		List<DashboardReportField> fields = report.getFieldListChangeHandler().getItems();
		Collections.sort(fields, new Comparator<DashboardReportField>() {
			@Override
			public int compare(DashboardReportField field1, DashboardReportField field2) {
				return field1.getPosition() - field2.getPosition();
			}
		});
		GrilleDataFilter filter = dashboardReportService.buildGrilleDataFilter(report, fields);
		filter.getGrid().setType(GrilleType.DASHBOARD_REPORT);
		getData().setFilter(filter);
		return report;
	}
	
}
