package com.moriset.bcephal.scheduler.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.scheduler.domain.Project;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLog;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogItem;
import com.moriset.bcephal.scheduler.domain.SchedulerTypes;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerLogItemRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerLogRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerRepository;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlannerRunnable implements Runnable {

	protected Project project;
	protected Long objectId;
	protected SchedulerTypes type;

	private MainObject object;

	@Autowired
	private SchedulerManager schedulerManager;

	@Autowired
	private SchedulerPlannerRepository repository;

	@Autowired
	private SchedulerPlannerLogRepository logRepository;

	@Autowired
	private SchedulerPlannerLogItemRepository logItemRepository;

	@Autowired
	private RoutineExecutorReopository routineExecutorReopository;

	@Autowired
	private RestartEndpoint restartEndpoint;
	
	@Autowired
	SchedulerPlannerRunner runner;

	@Value("${bcephal.scheduler.restart.on.error:true}")
	boolean restartAllowed;

	private BeanFactory beanFactory;

	public SchedulerPlannerRunnable() {
		this.type = SchedulerTypes.SCHEDULER_PLANNER;
	}

	@Override
	public void run() {
		log.info("Project : {}. Scheduler ID : {}. Starting...", project.getName(), objectId);
		TenantContext.setCurrentTenant(project.getCode());
		try {
			loadObject();
			performAction();
			dispose();
		} catch (Exception e) {
			log.error("Project : {}. Scheduler ID : {}. Unexpected error!", project.getName(), objectId, e);
			restartService();
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	protected void loadObject() {
		log.debug("Project : {}. Try to read scheduler by ID '{}'...", project.getName(), objectId);
		Optional<?> result = repository.findById(objectId);
		if (result.isPresent()) {
			object = (MainObject) result.get();
			log.debug("Project : {}. Scheduler readed : {}.", project.getName(), getObjectName());
		} else {
			throw new BcephalException("Scheduler not found : " + objectId);
		}
	}

		
	protected void performAction() throws Exception {
		log.debug("Project : {}. Scheduler : {}. running...", project.getName(), getObjectName());
		try {
			runner.setBeanFactory(getBeanFactory());
			runner.setSchedulerPlanner((SchedulerPlanner) getObject());
			runner.setMode(RunModes.A);
			runner.setProjectCode(getProject().getCode());
			runner.setProjectName(getProject().getName());
			runner.setClientId(getProject().getClientId());
			runner.run();
			log.debug("Project : {}. Scheduler : {}. Run ended!", project.getName(), getObjectName());
		} catch (TransactionException e) {
			log.error("Project : {}. Scheduler : {}. TransactionException", project.getName(), getObjectName(), e);
			try {
				SchedulerPlannerLog schedulerLog = runner.getSchedulerLog();
				SchedulerPlannerLogItem logItem = runner.getLogItem();
				if (logItem != null) {
					logItem.setMessage(e.getMessage());
					logItem.setStatus(RunStatus.ERROR);
					buildAndSaveEndLogItem(logItem);
				}
				if (schedulerLog != null) {
					schedulerLog.setMessage(e.getMessage());
					schedulerLog.setStatus(RunStatus.ERROR);
					buildAndSaveEndLog(schedulerLog);
				}
			} catch (Exception ex) {
				log.debug("Project : {}. Scheduler : {}. Unable to write logs after error.", project.getName(),
						getObjectName(), e);
			}
		} catch (Exception e) {
			log.error("Project : {}. Scheduler : {}. Unexpected error", project.getName(), getObjectName(), e);
		}
	}

	public void dispose() {
		String name = getObjectName();
		log.debug("Project : {}. Scheduler : {}. Disposing task...", project.getName(), name);
		object = null;
		log.debug("Project : {}. Scheduler : {}. Task disposed!", project.getName(), name);
	}

	private void restartService() {
		if (restartAllowed) {
			log.info("Project : {}. Try to restart micro service...", project.getName());
			restartEndpoint.restart();
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLog buildAndSaveEndLog(SchedulerPlannerLog schedulerLog) {
		schedulerLog.setEndDate(new Timestamp(System.currentTimeMillis()));
		try {
			schedulerLog = logRepository.save(schedulerLog);
		} catch (Exception e) {
			schedulerLog = logRepository.save(schedulerLog);
		}
		// saveOrUpdateMatGridLog(schedulerLog);
		return schedulerLog;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLogItem buildAndSaveEndLogItem(SchedulerPlannerLogItem logItem) {
		logItem.setEndDate(new Timestamp(System.currentTimeMillis()));
		try {
			logItem = logItemRepository.save(logItem);
		} catch (Exception e) {
			logItem = logItemRepository.save(logItem);
		}
		return logItem;
	}

	protected String getObjectName() {
		return getObject() != null ? getObject().getName() : null;
	}

}
