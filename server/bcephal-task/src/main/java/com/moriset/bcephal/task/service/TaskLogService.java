/**
 * 
 */
package com.moriset.bcephal.task.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.task.domain.TaskLog;
import com.moriset.bcephal.task.domain.TaskLogBrowserData;
import com.moriset.bcephal.task.domain.TaskLogType;
import com.moriset.bcephal.task.repository.TaskLogRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class TaskLogService extends MainObjectService<TaskLog, TaskLogBrowserData> {
	
	@Autowired
	TaskLogRepository taskLogRepository;
		
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	UserDataRepository userRepository;
	
	@Autowired
	ProfileDataRepository profileRepository;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.TASK_LOG;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}

	@Override
	public TaskLogRepository getRepository() {
		return taskLogRepository;
	}
	
	
	@Override
	public EditorData<TaskLog> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {		
		return null;
	}

	@Override
	protected TaskLog getNewItem() {
		TaskLog task = new TaskLog();
		String baseName = "Task log ";
		int i = 1;
		task.setName(baseName + i);
		while(getByName(task.getName()) != null) {
			i++;
			task.setName(baseName + i);
		}
		return task;
	}
	
		
	@Override
	@Transactional
	public TaskLog save(TaskLog taskLog, Locale locale) {
		log.debug("Try to  Save TaskLog : {}", taskLog);		
		try {	
			if(taskLog == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.task.log", new Object[]{taskLog} , locale);
				throw new BcephalException(message);
			}
//			if(!StringUtils.hasLength(taskLog.getName())) {
//				String message = getMessageSource().getMessage("unable.to.save.task.with.empty.name", new String[]{taskLog.getName()} , locale);
//				throw new BcephalException(message);
//			}
					
			taskLog.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(taskLog, locale);
			taskLog = getRepository().save(taskLog);						
			log.debug("TaskLog saved : {} ", taskLog.getId());
	        return taskLog;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save TaskLog : {}", taskLog, e);
			String message = getMessageSource().getMessage("unable.to.save.task.log", new Object[]{taskLog} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	protected void validateBeforeSave(TaskLog entity, Locale locale) {
		
	}
	
	
	@Override
	public void delete(TaskLog taskLog) {
		log.debug("Try to delete TaskLog : {}", taskLog);	
		if(taskLog == null || taskLog.getId() == null) {
			return;
		}
		
		getRepository().deleteById(taskLog.getId());
		log.debug("TaskLog successfully to delete : {} ", taskLog);
	    return;	
	}

	@Override
	protected TaskLogBrowserData getNewBrowserData(TaskLog item) {
		return new TaskLogBrowserData(item);
	}

	@Override
	protected Specification<TaskLog> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<TaskLog> qBuilder = new RequestQueryBuilder<TaskLog>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("creationDate"), root.get("modificationDate"));
		    if(filter.getGroupId() != null) {
		    	Predicate predicate = qBuilder.getCriteriaBuilder().equal(qBuilder.getRoot().get("taskId"), filter.getGroupId());	
		    	qBuilder.add(predicate);
		    }		    
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    
		    if(filter.getColumnFilters() != null) {
		    	build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
	        return qBuilder.build();
		};
	}
	
	protected void build(ColumnFilter columnFilter) {
		if ("type".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("type");
			columnFilter.setType(TaskLogType.class);
		} else
			if ("username".equalsIgnoreCase(columnFilter.getName())) {
				columnFilter.setName("username");
				columnFilter.setType(String.class);
			} 
		super.build(columnFilter);
	}

}
