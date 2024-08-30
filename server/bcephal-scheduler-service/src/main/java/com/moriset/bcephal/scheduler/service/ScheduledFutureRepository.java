/**
 * 
 */
package com.moriset.bcephal.scheduler.service;

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
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserData;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerTypes;

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
	
	private Map<String, SchedulerBrowserData> futures;
	
	public ScheduledFutureRepository(){
		this.futures = new HashMap<String, SchedulerBrowserData>();
	}
	
	public void AddFuture(ScheduledFuture<?> future, String projectCode, String projectName, MainObject object, String objectClass, String cron) {
		String code = builFutureId(projectCode, object.getId(), objectClass);		
		SchedulerBrowserData data = new SchedulerBrowserData();
		data.setId(object.getId());
		data.setObjectId(System.currentTimeMillis());
		data.setName(object.getName());
		data.setGroup(objectClass);
		data.setCron(cron);
		data.setFuture(future);
		data.setProjectCode(projectCode);
		data.setProjectName(projectName);
		data.setCode(code);
		this.futures.put(code, data);
	}
	
	public void AddFuture(ScheduledFuture<?> future, String projectCode, String projectName, Long objectId, String objectName, String objectClass, String cron) {
		String code = builFutureId(projectCode, objectId, objectClass);		
		SchedulerBrowserData data = new SchedulerBrowserData();
		data.setId(objectId);
		data.setObjectId(System.currentTimeMillis());
		data.setName(objectName);
		data.setGroup(objectClass);
		data.setCron(cron);
		data.setFuture(future);
		data.setProjectCode(projectCode);
		data.setProjectName(projectName);
		data.setCode(code);
		this.futures.put(code, data);
	}
	
	
	public SchedulerBrowserData removeFuture(String code) {
		return this.futures.remove(code);
	}
	
	public SchedulerBrowserData getFuture(String code) {
		return this.futures.get(code);
	}
	
	public List<SchedulerBrowserData> getByProjectAndType(String projectCode, SchedulerTypes type) {
		List<SchedulerBrowserData> datas = new ArrayList<SchedulerBrowserData>();
		for(String code : futures.keySet()) {
			if(match(code, projectCode, type)) {
				SchedulerBrowserData data = futures.get(code);
				updateData(data);
				datas.add(data);
			}
		}
		return datas;
	}
	
	public SchedulerBrowserData getByProjectAndId(String projectCode, Long id) {
		for(String code : futures.keySet()) {
			if(match(code, projectCode, id)) {
				SchedulerBrowserData data = futures.get(code);
				updateData(data);
				return data;
			}
		}
		return null;
	}
	
	public List<SchedulerBrowserData> getById(Long id) {
		List<SchedulerBrowserData> datas = new ArrayList<SchedulerBrowserData>();
		for(String code : futures.keySet()) {
			SchedulerBrowserData data = futures.get(code);			
			if(match(data, id)) {
				updateData(data);
				datas.add(data);
			}
		}
		return datas;
	}
	
	private void updateData(SchedulerBrowserData data)
	{
		try {
			
//			CronExpression cronTrigger = CronExpression.parse(data.getCron());
//	        LocalDateTime next = cronTrigger.next(LocalDateTime.now());
//	        
//	        data.setModificationDate(next);
			
			Field field = data.getFuture().getClass().getDeclaredField("scheduledExecutionTime");
			field.setAccessible(true);
			Object value = field.get(data.getFuture());
			Date date = null;
			if(value instanceof Date) {
				date = (Date)value;
			}
			else if(value instanceof Instant) {
				date = Date.from((Instant)value);
			}
			
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
	
	private boolean match(String code, String projectCode, SchedulerTypes type) {		
		boolean isOk = true;
		String value = "";
		if(StringUtils.hasText(projectCode)) {
			value = projectCode.concat(separator);
			isOk = code.startsWith(value);
		}
		if(isOk && type != null && type != SchedulerTypes.ALL) {
			if(StringUtils.hasText(projectCode)) {
				isOk = code.startsWith(projectCode.concat(separator).concat(type.name()).concat(separator));
			}
			else {
				isOk = code.contains(separator.concat(type.name()).concat(separator));
			}
		}
		return isOk;
	}
	
	private boolean match(String code, String projectCode, Long id) {		
		boolean isOk = true;
		String value = "";
		if(StringUtils.hasText(projectCode)) {
			value = projectCode.concat(separator);
			isOk = code.startsWith(value);
		}
		if(isOk && id != null ) {
			value = separator.concat(id.toString());
			isOk = code.endsWith(value);
		}
		return isOk;
	}
	
	private boolean match(SchedulerBrowserData data, Long id) {		
		boolean isOk = true;
		isOk = id != null && id.equals(data.getObjectId());
		return isOk;
	}

	public BrowserDataPage<SchedulerBrowserData> search(SchedulerBrowserDataFilter filter) {
		BrowserDataPage<SchedulerBrowserData> page = new BrowserDataPage<>();
		if(filter.getObjectType() == SchedulerTypes.ALL) {
			page.setItems(new ArrayList<>(futures.values()));
		}
		else {
			page.setItems(getByProjectAndType(filter.getProjectCode(), filter.getObjectType()));
		}
		return page;
	}
	
	public String builFutureId(String projectCode, Long objectId, String objectClass) {
		return projectCode.concat("-$-").concat(objectClass).concat("-$-").concat("" + objectId);
	}

	
	
}
