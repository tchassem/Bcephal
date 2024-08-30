package com.moriset.bcephal.loader.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.service.ExportDataTransfert;
import com.moriset.bcephal.grid.service.ExportDataTransfert.Decision;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.multitenant.jpa.TenantContext;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileLoaderApiService {

	@Autowired
	FileLoaderRunnerForGrid runnerForGrid;
	
	@Autowired
	FileLoaderRunnerForMaterializedGrid runnerForMaterializedGrid;
	
	@Autowired
	FileLoaderProperties properties;

	@Autowired
	GrilleService grilleService;
	
	public void load(FileLoaderRunData data, String username, HttpSession session, String tenantId, TaskProgressListener listener) {		
		Thread thread = new Thread() {
			public void run() {
				try {
					TenantContext.setCurrentTenant(tenantId);
					performAction(data, username, session, listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					
				}
			}
		};
		thread.start();
	}
	
	
	private void performAction(FileLoaderRunData data, String username, HttpSession session, TaskProgressListener listener) {
		try {
			listener.createInfo(data.getId(), "FileLoader Run");			
			if (data.getLoader() != null) {
				log.debug("FileLoader : {}", data.getLoader().getName());
				listener.createInfo(data.getId(), data.getLoader().getName());
				if (data.getLoader().getUploadMethod() == FileLoaderMethod.DIRECT_TO_MATERIALIZED_GRID
						|| data.getLoader().getUploadMethod() == FileLoaderMethod.NEW_MATERIALIZED_GRID) {
					runnerForMaterializedGrid.setUsername(StringUtils.hasText(username) ? username : "B-CEPHAL");
					runnerForMaterializedGrid.setSession(session);
					runnerForMaterializedGrid.setData(data);
					runnerForMaterializedGrid.setProperties(properties);
					runnerForMaterializedGrid.setListener(listener);
					runnerForMaterializedGrid.setDeleteTmpDir(true);
					runnerForMaterializedGrid.run();
				}				
				else {
					runnerForGrid.setUsername(StringUtils.hasText(username) ? username : "B-CEPHAL");
					runnerForGrid.setSession(session);
					runnerForGrid.setData(data);
					runnerForGrid.setProperties(properties);
					runnerForGrid.setListener(listener);
					runnerForGrid.setDeleteTmpDir(true);
					runnerForGrid.run();
				} 
			} else {
				log.error("Unknown FileLoader : {}", data.getId());
				listener.error("Unknown FileLoader : " + data.getId(), false);
			}
			if (listener != null) {
				listener.end();
			}
		} catch (Exception e) {
			log.error("unexpected error while running file loader : {}",
					data != null && data.getLoader() != null ? data.getLoader().getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
		}
	}

	public void export(GrilleExportData data, String username, HttpSession session, Locale locale, TaskProgressListener listener) {
		try {
			listener.createInfo(data.getFilter().getUserId(), "Export from User id");
			if (data.getFilter().getGrid() != null) {
				buildGrid(data);
				List<String> paths = grilleService.export(data, null);
				if (listener != null) {
					listener.createInfo(data.getFilter().getUserId(), data.getFilter().getGrid().getName());
					listener.start(paths.size());
				}
				
				for(String path : paths) {
					listener.nextStep(1);
					onLoad(path, listener, data);
				}
			} else {
				log.error("Unknown FileLoader : {}", data.getFilter().getGrid().getName());
				listener.error("Unknown FileLoader : " + data.getFilter().getGrid().getName(), false);
			}
			if (listener != null) {
				listener.getInfo().setMessage(null);
				listener.end();
			}
			
			dispose(data, listener);
		} catch (Exception e) {
			log.error("unexpected error while running file loader : {}", data.getFilter() != null && data.getFilter().getGrid() != null ? data.getFilter().getGrid().getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
			dispose(data, listener);
		}
	}

	private void onLoad(String path, TaskProgressListener listener, GrilleExportData data_) throws IOException {
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
			sendExportDataTransfert(new ExportDataTransfert(length == buffersize ? data : Arrays.copyOfRange(data, 0, length),decision, data_.getType()), listener, data_);
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

	private void sendExportDataTransfert(ExportDataTransfert dataTranfert, TaskProgressListener listener, GrilleExportData data) {
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

	private void buildGrid(GrilleExportData data) {		
		data.getFilter().setGrid(grilleService.getById(data.getFilter().getGrid().getId()));
	}

	private void dispose(GrilleExportData data, TaskProgressListener listener) {
		data = null;
		listener = null;
	}
	
	
}
