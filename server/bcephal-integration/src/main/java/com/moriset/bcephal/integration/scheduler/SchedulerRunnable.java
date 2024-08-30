/**
 * 
 */
package com.moriset.bcephal.integration.scheduler;

import java.sql.Timestamp;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;

import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.integration.domain.ConnectEntity;
import com.moriset.bcephal.integration.domain.SchedulerConnectEntityLog;
import com.moriset.bcephal.integration.repository.SchedulerConnectEntityLogRepository;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
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

	protected ConnectEntity entity;
	protected SchedulerConnectEntityLog schedulerLog;
	
	private SchedulerConnectEntityLogRepository logRepository;
	private BeanFactory beanFactory;
	private WebApplicationContext context;
	
	public SchedulerRunnable(ConnectEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public void run() {		
		log.info("Connect Entity : {}. ID : {}. Running scheduler...", entity.getName(), entity.getId());
		TenantContext.setCurrentTenant(entity.getProjectCode());
		startSchedulerLog();
		try {
			performAction();
			endSchedulerLog(RunStatus.ENDED, null, null);					
		}
		catch (BcephalException e) {
			log.error("Project : {}.  ID : {}. Running scheduler...", entity.getName(), entity.getId(), e);
			endSchedulerLog(RunStatus.ERROR, e.getMessage(), e.getCause());
		}
		catch (Exception e) {
			log.error("Project : {}.  ID : {}. Running scheduler...", entity.getName(), entity.getId(), e);
			endSchedulerLog(RunStatus.ERROR, "Unexpected error.", e);
		}
		finally {
			dispose();	
		}
	}
	
	protected abstract void performAction() throws Exception;

	

	public void dispose() {
		log.debug("Project : {}.  : {}. Disposing task...", entity.getName(), entity.getName());	
		schedulerLog = null;
		log.debug("Project : {}.  ID : {}. Task disposed!", entity.getName(), entity.getId());
	}
	
	private void startSchedulerLog() {
		this.schedulerLog = new SchedulerConnectEntityLog();
		this.schedulerLog.setObjectId(entity.getId());
		this.schedulerLog.setClientId(entity.getClientId());;
		this.schedulerLog.setObjectName(entity != null ? entity.getName() : null);
		this.schedulerLog.setProjectCode(getEntity().getProjectCode());
		this.schedulerLog = this.logRepository.save(this.schedulerLog);
	}
	
	private void endSchedulerLog(RunStatus status, String message, Throwable e) {
		if(this.schedulerLog == null) {
			this.schedulerLog = new SchedulerConnectEntityLog();
			this.schedulerLog.setObjectId(entity.getId());	
			this.schedulerLog.setClientId(entity.getClientId());
			this.schedulerLog.setProjectCode(getEntity().getProjectCode());
		}
		this.schedulerLog.setObjectName(getEntity() != null ? getEntity().getName() : null);
		this.schedulerLog.setStatus(status);
		this.schedulerLog.setMessage(message);
		if(e != null) {
			this.schedulerLog.setDetails(e.getMessage());
		}
		this.schedulerLog.setEndDate(new Timestamp(System.currentTimeMillis()));
		this.schedulerLog = this.logRepository.save(this.schedulerLog);
	}
	
}
