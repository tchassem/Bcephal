/**
 * 
 */
package com.moriset.bcephal.scheduler.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.integration.domain.ConnectEntity;
import com.moriset.bcephal.multitenant.jpa.CurrentTenantIdentifierResolverImpl;
import com.moriset.bcephal.multitenant.jpa.DataBaseUtils;
import com.moriset.bcephal.multitenant.jpa.HikariProperties;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.scheduler.domain.Project;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserData;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserDataFilter;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLog;
import com.moriset.bcephal.scheduler.domain.SchedulerRequest;
import com.moriset.bcephal.scheduler.domain.SchedulerTypes;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerLogRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerRepository;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Component
@Data
@Slf4j
public class SchedulerManager implements BeanFactoryAware{

	@Autowired
	@Qualifier("poolScheduler")
	TaskScheduler taskScheduler;
	
	@Autowired
	List<Project> projects;
	
	@Autowired 
	SchedulerPlannerRepository schedulerPlannerRepository;
		
	@Autowired 
	SchedulerPlannerLogRepository schedulerPlannerLogRepository;
	
			
	@Autowired
	private WebApplicationContext context;
	
	private BeanFactory beanFactory;
	
	
	ScheduledFutureRepository scheduledFutureRepository;
		
	
	public SchedulerManager(){
		scheduledFutureRepository = new ScheduledFutureRepository();
	}
	
	
	public void startSchedulers() {
		log.info("Try to start all scheduled actions...");
		for(Project project : projects) {
			startSchedulers(project);
		}		
		log.info("Scheduled actions started!");
	}
	
	public boolean start(SchedulerRequest filter) {
		if(filter.getProjectCode() != null && filter.getObjectType() != null) {
			for(Long id : filter.getObjectIds()) {
				start(filter.getProjectCode(), id, filter.getObjectType());
			}
		}
		return true;
	}
	
	
	public boolean start(String projectCode, Long objectId, SchedulerTypes type) {
		String code = scheduledFutureRepository.builFutureId(projectCode, objectId, type.name());
		if(scheduledFutureRepository.getFuture(code) != null) {
			@SuppressWarnings("unused")
			boolean response = stop(projectCode, objectId, type);
		}
		Project project = getProjectByCode(projectCode);
		if(project == null) {			
			try {
				project = findByCode(projectCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(type == SchedulerTypes.SCHEDULER_PLANNER) {
			Optional<SchedulerPlanner> optional = schedulerPlannerRepository.findById(objectId);
			if(optional.isPresent()) {
				SchedulerPlanner planner = optional.get();
				schedule(planner, project);	
			}
		}
		return true;
	}
	
	public boolean restart(SchedulerRequest filter) {
		if(filter.getProjectCode() != null && filter.getObjectType() != null) {
			for(Long id : filter.getObjectIds()) {
				restart(filter.getProjectCode(), id, filter.getObjectType());
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	public boolean restart(String projectCode, Long objectId, SchedulerTypes type) {
		boolean response = stop(projectCode, objectId, type);
		return start(projectCode, objectId, type);
	}
	
	public boolean stop(SchedulerRequest filter) {
		if(filter.getProjectCode() != null && filter.getObjectType() != null) {
			for(Long id : filter.getObjectIds()) {
				stop(filter.getProjectCode(), id, filter.getObjectType());
			}
		}
		return true;
	}
	
	public boolean stop(String projectCode, Long objectId, SchedulerTypes type) {
		boolean response = true;
		if(SchedulerTypes.ALL == type) {
			List<SchedulerBrowserData> datas = scheduledFutureRepository.getById(objectId);
			for(SchedulerBrowserData data : datas) {
				response = data.getFuture().cancel(true);
				scheduledFutureRepository.removeFuture(data.getCode());
			}
		}
		else {
			String code = scheduledFutureRepository.builFutureId(projectCode, objectId, type.name());
			SchedulerBrowserData data = scheduledFutureRepository.getFuture(code);
			if(data != null) {
				response = data.getFuture().cancel(true);
				scheduledFutureRepository.removeFuture(code);
			}
		}		
		return response;
	}
	
	

	private void startSchedulers(Project project) {
		log.debug("Try to start all schedulers for project : {}", project);
		TenantContext.setCurrentTenant(project.getCode());
		startSchedulerPlanners(project);
		startArchives(project);	
		log.debug("Scheduler started for project : {}", project);
	}
	
	private void startSchedulerPlanners(Project project) {
		try {
			log.debug("Project : {}. Try to start all scheduled tasks...", project);
			List<SchedulerPlanner> planners = schedulerPlannerRepository.findByActiveAndScheduledAndCronExpressionIsNotNull(true, true);
			for(SchedulerPlanner planner : planners) {
				schedule(planner, project);
			}
			log.debug("Project : {}. Scheduled tasks started!", project);		
		}catch (Exception e) {
			log.debug("Project : {}. Scheduled task error : {}!", project,e);
		}
	}
	
	private void startArchives(Project project) {
		
	}
	
	private void schedule(SchedulerPlanner planner, Project project) {
		BeanCreatingHandlerProvider<SchedulerPlannerRunnable> provider = new BeanCreatingHandlerProvider<>(
				SchedulerPlannerRunnable.class);
		provider.setBeanFactory(getBeanFactory());
		SchedulerPlannerRunnable schedulerRunnable = provider.getHandler();	
//		SchedulerPlannerRunnable schedulerRunnable = context.getBean(SchedulerPlannerRunnable.class);
		schedulerRunnable.setProject(project);
		schedulerRunnable.setObjectId(planner.getId());		
		schedulerRunnable.setBeanFactory(beanFactory);
		schedule(schedulerRunnable, planner.getCronExpression(), planner);
	}
	
	
	private void schedule(SchedulerPlannerRunnable runnable, String cron, MainObject object) {
		log.debug("Project : {}. Try to schedule {} : {}", runnable.getProject(), runnable.getType().name(), object.getName());		
		try {
			try {
				org.springframework.scheduling.support.CronExpression.parse(cron);
			}
			catch (Exception e) {
				throw new BcephalException("Wrong cron expression. " + e.getMessage());
			}			
			ScheduledFuture<?> future = taskScheduler.schedule(runnable, new CronTrigger(cron));	
			scheduledFutureRepository.AddFuture(future, runnable.getProject().getCode(), runnable.getProject().getName(), object, runnable.getType().name(), cron);
			log.debug("Project : {}. {} scheduled : {}", runnable.getProject(), runnable.getType().name(), object.getName());	
		}
		catch (Exception e) {
			log.error("Project : {}. {} scheduler : {}", runnable.getProject(), runnable.getType().name(), object.getName(), e);	
			buildAndSaveErrorLog(runnable, object, e.getMessage());
		}
	}
	
	
	
	
	protected SchedulerPlannerLog buildAndSaveErrorLog(SchedulerPlannerRunnable runnable, MainObject object, String message) {
		log.trace("Scheduler : {} : Build Log...!", object.getName());
		SchedulerPlannerLog schedulerLog = new SchedulerPlannerLog();
		schedulerLog.setSchedulerId(object.getId());
		schedulerLog.setSchedulerName(object.getName());
		//schedulerLog.mode = RunModes.A;	
		schedulerLog.setStatus(RunStatus.ERROR);
		schedulerLog.setMessage(message);
		//schedulerLog.username = username;
		schedulerLog.setEndDate(new Timestamp(System.currentTimeMillis()));
		schedulerLog.setSetps(((SchedulerPlanner)object).getItemListChangeHandler().getItems().size());
		schedulerLog = schedulerPlannerLogRepository.save(schedulerLog);		
		log.trace("Scheduler : {} : Log builded!", object.getName());
		return schedulerLog;
	}
	
	protected SchedulerPlannerLog buildAndSaveErrorLog(SchedulerPlannerRunnable runnable,ConnectEntity entity, String message) {
		log.trace("Scheduler : {} : Build Log...!", entity.getName());
		SchedulerPlannerLog schedulerLog = new SchedulerPlannerLog();
		schedulerLog.setSchedulerId(entity.getId());
		schedulerLog.setSchedulerName(entity.getName());
		//schedulerLog.mode = RunModes.A;	
		schedulerLog.setStatus(RunStatus.ERROR);
		schedulerLog.setMessage(message);
		//schedulerLog.username = username;
		schedulerLog.setEndDate(new Timestamp(System.currentTimeMillis()));
		//schedulerLog.setSetps(((SchedulerPlanner)object).getItemListChangeHandler().getItems().size());
		schedulerLog = schedulerPlannerLogRepository.save(schedulerLog);		
		log.trace("Scheduler : {} : Log builded!", entity.getName());
		return schedulerLog;
	}
	
	private Project getProjectByCode(String projectCode) {
		Optional<Project> project = this.projects.stream().filter(proj -> proj.getCode().equals(projectCode) ).findFirst();
		return project.isPresent() ? project.get() : null;
	}
	
	@Autowired
	Map<String, DataSource> bcephalDataSources;
	
	@Autowired
	DataSourceProperties properties_;
	
	@Autowired
	HikariProperties hikari_;
	
	public Project findByCode(String projectCode) throws Exception {
		DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
		if(Defalutsource == null) {
			throw new BcephalException("The default connection is null!");
		}
		DataBaseUtils utils = new DataBaseUtils(properties_, Defalutsource, hikari_);
		DataSource securityDataSources = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.SECURITY_TENANT);
		Connection conn = securityDataSources.getConnection();
		if (conn != null && !conn.isClosed()) {
			String sql =String.format("SELECT name,subscriptionId FROM BCP_SEC_PROJECT WHERE code='%s'", projectCode);
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				try {
					do {
						String projectName = result.getString("name");
						Long clientId = result.getLong("subscriptionId");
						DataSource source = utils.buildDataSource(projectCode);
						bcephalDataSources.put(projectCode, source);
						Project  project = new Project(projectCode, projectName, clientId);
						projects.add(project);
						return project;
					} while (result.next());
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		return null;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}


	public BrowserDataPage<SchedulerBrowserData> search(SchedulerBrowserDataFilter filter, Locale locale) {
		BrowserDataPage<SchedulerBrowserData> page = scheduledFutureRepository.search(filter);		
		return page;
	}
		
	
}
