/**
 * 
 */
package com.moriset.bcephal.scheduler.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;

import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.scheduler.domain.Project;
import com.moriset.bcephal.scheduler.domain.SchedulerLog;
import com.moriset.bcephal.scheduler.domain.SchedulerTypes;
import com.moriset.bcephal.scheduler.repository.SchedulerLogRepository;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Slf4j
public abstract class SchedulerRunnable implements Runnable {

	protected Project project;
	protected Long objectId;
	protected SchedulerTypes type;
	protected SchedulerLog schedulerLog;
	
	private MainObject object;
	private MainObjectRepository<?> repository;
	private SchedulerLogRepository logRepository;
	private RoutineExecutorReopository routineExecutorReopository;
	private BeanFactory beanFactory;
	private WebApplicationContext context;
		
	public SchedulerRunnable(Project project, Long objectId, SchedulerTypes type, MainObjectRepository<?> repository) {
		this.project = project;
		this.objectId = objectId;
		this.type = type;
		this.repository = repository;
	}
	
	@Override
	public void run() {		
		log.info("Project : {}. {} ID : {}. Running scheduler...", project.getName(), type.name(), objectId);
		TenantContext.setCurrentTenant(project.getCode());
		try {
			buildBeans();
			startSchedulerLog();		
			loadObject();
			performAction();
			endSchedulerLog(RunStatus.ENDED, null, null);					
		}
		catch (BcephalException e) {
			log.error("Project : {}. {} ID : {}. Running scheduler...", project.getName(), type.name(), objectId, e);
			endSchedulerLog(RunStatus.ERROR, e.getMessage(), e.getCause());
		}
		catch (Exception e) {
			log.error("Project : {}. {} ID : {}. Running scheduler...", project.getName(), type.name(), objectId, e);
			endSchedulerLog(RunStatus.ERROR, "Unexpected error.", e);
		}
		finally {
			dispose();	
		}
	}
	
	protected void buildBeans() throws Exception{
		MainObjectRepository<?> repo = buildMainObjectRepository();
		if(repo != null) {
			repository = repo;
		}
	}
	protected MainObjectRepository<?> buildMainObjectRepository() throws Exception{
		return null;
	}
	
	protected abstract void performAction() throws Exception;

	protected void loadObject() {
		log.debug("Project : {}. {} ID : {}. Loading object...", project.getName(), type.name(), objectId);
		Optional<?> result = repository.findById(objectId);
		if(result.isPresent()) {
			object = (MainObject)result.get();
			log.debug("Project : {}. {} ID : {}. Object loaded!", project.getName(), type.name(), objectId);
			log.trace("Project : {}. {} name : {}. Object loaded!", project.getName(), type.name(), object.getName());
		}
		else {
			throw new BcephalException("Unable to run " + type.name() + ". Object not found : " + objectId);
		}
	}

	public void dispose() {
		log.debug("Project : {}. {} : {}. Disposing task...", project.getName(), type.name(), object != null ? object.getName() : "");		
		object = null;	
		schedulerLog = null;
		log.debug("Project : {}. {} ID : {}. Task disposed!", project.getName(), type.name(), objectId);
	}
	
	protected String getObjectName() {
		return getObject() != null ? getObject().getName() : null;
	}
	
	private void startSchedulerLog() {
		this.schedulerLog = new SchedulerLog();
		this.schedulerLog.setObjectType(getType());
		this.schedulerLog.setObjectId(objectId);
		this.schedulerLog.setObjectName(getObjectName());
		this.schedulerLog.setProjectCode(getProject().getCode());
		this.schedulerLog = this.logRepository.save(this.schedulerLog);
	}
	
	private void endSchedulerLog(RunStatus status, String message, Throwable e) {
		if(this.schedulerLog == null) {
			this.schedulerLog = new SchedulerLog();
			this.schedulerLog.setObjectType(getType());
			this.schedulerLog.setObjectId(objectId);			
			this.schedulerLog.setProjectCode(getProject().getCode());
		}
		this.schedulerLog.setObjectName(getObjectName());
		this.schedulerLog.setStatus(status);
		this.schedulerLog.setMessage(message);
		if(e != null) {
			this.schedulerLog.setDetails(e.getMessage());
		}
		this.schedulerLog.setEndDate(new Timestamp(System.currentTimeMillis()));
		this.schedulerLog = this.logRepository.save(this.schedulerLog);
	}
	
}
