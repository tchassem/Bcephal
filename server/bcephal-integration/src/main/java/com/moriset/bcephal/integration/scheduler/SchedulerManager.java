/**
 * 
 */
package com.moriset.bcephal.integration.scheduler;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.integration.domain.ConnectEntity;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;
import com.moriset.bcephal.integration.repository.PontoConnectEntityRepository;
import com.moriset.bcephal.integration.repository.SchedulerConnectEntityLogRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Component
@Data
@Slf4j
public class SchedulerManager implements BeanFactoryAware {

	@Autowired
	@Qualifier("poolScheduler")
	TaskScheduler taskScheduler;

	@Autowired
	PontoConnectEntityRepository reConnectEntityRepository;

	@Autowired
	SchedulerConnectEntityLogRepository logRepository;

	@Autowired
	private WebApplicationContext context;

	private BeanFactory beanFactory;

	ScheduledFutureRepository scheduledFutureRepository;
	
	public SchedulerManager() {
		scheduledFutureRepository = new ScheduledFutureRepository();
	}

	public void startSchedulers() {
		log.info("Try to start all scheduled actions...");
		List<PontoConnectEntity> entities = reConnectEntityRepository.findByActiveAndScheduledAndCronExpressionIsNotNull(true, true);
		if (entities.size() > 0) {
			for (PontoConnectEntity entity : entities) {
				startSchedulerPlanners(entity);
			}
		}
		log.info("Scheduled actions started!");
	}

	public boolean start(SchedulerRequest filter) {
		if (filter.getProjectCode() != null && filter.getClientId() != null) {
			for (Long id : filter.getObjectIds()) {
				start(filter.getProjectCode(), id, filter.getClientId());
			}
		}
		return true;
	}

	public boolean start(String projectCode, Long objectId, Long clientId) {
		String code = scheduledFutureRepository.builFutureId(projectCode, objectId, clientId);
		if (scheduledFutureRepository.getFuture(code) != null) {
			@SuppressWarnings("unused")
			boolean response = stop(projectCode, objectId, clientId);
		}
		Optional<PontoConnectEntity> optional = reConnectEntityRepository.findById(objectId);
		PontoConnectEntity entity = optional.get();

		SchedulerPontoConnectRunnable schedulerRunnable = getSchedulerPontoConnectRunnable(entity);
		schedule(schedulerRunnable, entity);
		return true;
	}

	
	private SchedulerPontoConnectRunnable getSchedulerPontoConnectRunnable(PontoConnectEntity entity) {
		SchedulerPontoConnectRunnable schedulerRunnable = new SchedulerPontoConnectRunnable(entity);
		schedulerRunnable.setContext(context);
		schedulerRunnable.setLogRepository(logRepository);
		schedulerRunnable.setBeanFactory(beanFactory);
		return schedulerRunnable;
	}
	
	public boolean restart(SchedulerRequest filter) {
		if (filter.getProjectCode() != null && filter.getClientId() != null) {
			for (Long id : filter.getObjectIds()) {
				restart(filter.getProjectCode(), id, filter.getClientId());
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	public boolean restart(String projectCode, Long objectId, Long clientId) {
		boolean response = stop(projectCode, objectId, clientId);
		return start(projectCode, objectId, clientId);
	}

	public boolean stop(SchedulerRequest filter) {
		if (filter.getProjectCode() != null && filter.getClientId() != null) {
			for (Long id : filter.getObjectIds()) {
				stop(filter.getProjectCode(), id, filter.getClientId());
			}
		}
		return true;
	}

	public boolean stop(String projectCode, Long objectId, Long clientId) {
		boolean response = true;
		String code = scheduledFutureRepository.builFutureId(projectCode, objectId, clientId);
		SchedulerConnectEntityBrowserData data = scheduledFutureRepository.getFuture(code);
		if (data != null) {
			response = data.getFuture().cancel(true);
			scheduledFutureRepository.removeFuture(code);
		}
		return response;
	}

	private void startSchedulerPlanners(ConnectEntity entity) {
		try {
			log.debug("Connect Entity : {}. Try to start all scheduled tasks...", entity);
			SchedulerPontoConnectRunnable schedulerRunnable = getSchedulerPontoConnectRunnable((PontoConnectEntity) entity);
			schedule(schedulerRunnable, entity);
			log.debug("Connect Entity : {}. Scheduled tasks started!", entity);
		} catch (Exception e) {
			log.debug("Connect Entity : {}. Scheduled task error : {}!", entity, e);
		}
	}

	private void schedule(SchedulerRunnable runnable, ConnectEntity entity) {
		log.debug("Connect Entity : {}. Try to schedule {}", runnable.getEntity(), entity.getName());
		try {
			ScheduledFuture<?> future = taskScheduler.schedule(runnable, new CronTrigger(entity.getCronExpression()));
			scheduledFutureRepository.AddFuture(future, entity, entity.getCronExpression());
			log.debug("Connect Entity : {}.  scheduled : {}", runnable.getEntity(), entity.getName());
		} catch (Exception e) {
			log.error("Connect Entity : {}. scheduler : {}", runnable.getEntity(), entity.getName(), e);
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public BrowserDataPage<SchedulerConnectEntityBrowserData> search(SchedulerConnectEntityBrowserDataFilter filter, Locale locale) {
		BrowserDataPage<SchedulerConnectEntityBrowserData> page = scheduledFutureRepository.search(filter);
		return page;
	}
}
