/**
 * 
 */
package com.moriset.bcephal.integration.scheduler;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.integration.domain.ConnectEntity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Data
@Slf4j
public class ScheduledFutureRepository {

	private String separator = "-$-";
	
	private Map<String, SchedulerConnectEntityBrowserData> futures;
	
	public ScheduledFutureRepository(){
		this.futures = new HashMap<String, SchedulerConnectEntityBrowserData>();
	}
	
	public void AddFuture(ScheduledFuture<?> future, ConnectEntity entity, String cron) {
		String code = builFutureId(entity.getProjectCode(), entity.getId(), entity.getClientId());		
		SchedulerConnectEntityBrowserData data = new SchedulerConnectEntityBrowserData();
		data.setId(entity.getId());
		data.setObjectId(System.currentTimeMillis());
		data.setName(entity.getName());
		data.setGroup(entity.getClientId() +"");
		data.setCron(cron);
		data.setFuture(future);
		data.setProjectCode(entity.getProjectCode());
		data.setEntityName(entity.getName());
		data.setCode(code);
		this.futures.put(code, data);
	}
	
	
	public SchedulerConnectEntityBrowserData removeFuture(String code) {
		return this.futures.remove(code);
	}
	
	public SchedulerConnectEntityBrowserData getFuture(String code) {
		return this.futures.get(code);
	}
	
	public List<SchedulerConnectEntityBrowserData> getByProjectAndType(String projectCode, Long clientId) {
		List<SchedulerConnectEntityBrowserData> datas = new ArrayList<SchedulerConnectEntityBrowserData>();
		for(String code : futures.keySet()) {
			if(match(code, projectCode, clientId)) {
				SchedulerConnectEntityBrowserData data = futures.get(code);
				updateData(data);
				datas.add(data);
			}
		}
		return datas;
	}
	
	public List<SchedulerConnectEntityBrowserData> getById(Long id) {
		List<SchedulerConnectEntityBrowserData> datas = new ArrayList<SchedulerConnectEntityBrowserData>();
		for(String code : futures.keySet()) {
			SchedulerConnectEntityBrowserData data = futures.get(code);			
			if(match(data, id)) {
				updateData(data);
				datas.add(data);
			}
		}
		return datas;
	}
	
	private void updateData(SchedulerConnectEntityBrowserData data)
	{
		try {
			Field field = data.getFuture().getClass().getDeclaredField("scheduledExecutionTime");
			field.setAccessible(true);
			Date date = (Date)field.get(data.getFuture());
			if(date != null) {
				data.setModificationDate(new Timestamp(date.getTime()));
			}
						
			Field currentFuturefield = data.getFuture().getClass().getDeclaredField("currentFuture");
			currentFuturefield.setAccessible(true);
			
			Field triggerContextField = data.getFuture().getClass().getDeclaredField("triggerContext");
			triggerContextField.setAccessible(true);
			SimpleTriggerContext triggerContext = (SimpleTriggerContext) triggerContextField.get(data.getFuture());
					
			
			Instant instant = triggerContext.lastScheduledExecution();
			if(instant != null) {
				date = Date.from(instant);
			}
			if(date != null) {
				data.setCreationDate(new Timestamp(date.getTime()));
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
	}
	
	private boolean match(String code, String projectCode, Long clientId) {		
		boolean isOk = true;
		String value = "";
		if(StringUtils.hasText(projectCode)) {
			value = projectCode.concat(separator);
			isOk = code.startsWith(value);
		}
//		if(isOk && clientId != null && type != SchedulerTypes.ALL) {
//			if(StringUtils.hasText(projectCode)) {
//				isOk = code.startsWith(projectCode.concat(separator).concat(type.name()).concat(separator));
//			}
//			else {
//				isOk = code.contains(separator.concat(type.name()).concat(separator));
//			}
//		}
		return isOk;
	}
	
	private boolean match(SchedulerConnectEntityBrowserData data, Long id) {		
		boolean isOk = true;
		isOk = id != null && id.equals(data.getObjectId());
		return isOk;
	}

	public BrowserDataPage<SchedulerConnectEntityBrowserData> search(SchedulerConnectEntityBrowserDataFilter filter) {
		BrowserDataPage<SchedulerConnectEntityBrowserData> page = new BrowserDataPage<>();
//		if(filter.getObjectType() == SchedulerTypes.ALL) {
//			page.setItems(new ArrayList<>(futures.values()));
//		}
//		else {
			page.setItems(getByProjectAndType(filter.getProjectCode(), filter.getClientId()));
//		}
		return page;
	}
	
	public String builFutureId(String projectCode, Long objectId, Long clientId) {
		return projectCode.concat("-$-").concat(clientId +"").concat("-$-").concat("" + objectId);
	}

	
	
}
