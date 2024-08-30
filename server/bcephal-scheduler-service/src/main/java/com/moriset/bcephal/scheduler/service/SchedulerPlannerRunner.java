package com.moriset.bcephal.scheduler.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.alarm.domain.AlarmCategory;
import com.moriset.bcephal.alarm.repository.AlarmRepository;
import com.moriset.bcephal.alarm.service.AlarmRunner;
import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.repository.BillingModelRepository;
import com.moriset.bcephal.billing.service.action.BillingSendMailRunner;
import com.moriset.bcephal.billing.service.batch.BillingRunner;
import com.moriset.bcephal.billing.websocket.BillingRequest;
import com.moriset.bcephal.billing.websocket.BillingRequest.BillingRequestType;
import com.moriset.bcephal.dashboard.service.PresentationChartService;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.repository.JoinRepository;
import com.moriset.bcephal.grid.service.GridPublicationManager;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.grid.service.SpotService;
import com.moriset.bcephal.grid.service.VariableReferenceService;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;
import com.moriset.bcephal.integration.repository.PontoConnectEntityRepository;
import com.moriset.bcephal.integration.service.SchedulerPontoConnectRunner;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.loader.repository.FileLoaderRepository;
import com.moriset.bcephal.loader.service.FileLoaderProperties;
import com.moriset.bcephal.loader.service.FileLoaderRunData;
import com.moriset.bcephal.loader.service.FileLoaderRunner;
import com.moriset.bcephal.loader.service.FileLoaderRunnerForGrid;
import com.moriset.bcephal.loader.service.FileLoaderRunnerForMaterializedGrid;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.repository.TransformationRoutineRepository;
import com.moriset.bcephal.planification.service.JoinRunner;
import com.moriset.bcephal.planification.service.RoutineRunner;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.repository.AutoRecoRepository;
import com.moriset.bcephal.reconciliation.service.AutoRecoBothCumulatedRunner;
import com.moriset.bcephal.reconciliation.service.AutoRecoCumulatedRunner;
import com.moriset.bcephal.reconciliation.service.AutoRecoMethodRunner;
import com.moriset.bcephal.reconciliation.service.AutoRecoNeutralizationRunner;
import com.moriset.bcephal.reconciliation.service.AutoRecoOneOnOneRunner;
import com.moriset.bcephal.reconciliation.service.AutoRecoOneSideRunner;
import com.moriset.bcephal.reconciliation.service.AutoRecoZeroAmountRunner;
import com.moriset.bcephal.reconciliation.service.PartialAutoRecoCumulatedRunner;
import com.moriset.bcephal.reconciliation.service.PartialAutoRecoOneOnOneRunner;
import com.moriset.bcephal.reconciliation.service.ReconciliationModelService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.scheduler.domain.PresentationTemplate;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItem;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemDecision;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemLoop;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemTemporisationUnit;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemType;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLog;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogItem;
import com.moriset.bcephal.scheduler.repository.PresentationRepository;
import com.moriset.bcephal.scheduler.repository.PresentationTemplateRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerLogItemRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerLogRepository;
import com.moriset.bcephal.service.condition.ConditionExpressionEvaluator;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.service.TaskService;
import com.moriset.bcephal.utils.ApiCodeGenerator;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FileUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;

@Component
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SchedulerPlannerRunner  {
	
	private SchedulerPlanner schedulerPlanner;
	private RunModes mode;
	private String projectCode;
	private String projectName;
	private TaskProgressListener listener;
	
	String operationCode;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	SchedulerPlannerService schedulerPlannerService;
	
	@Autowired
	SpotService spotService;
	
	@Autowired
	SchedulerPlannerLogRepository logRepository;
	
	@Autowired
	SchedulerPlannerLogItemRepository logItemRepository;
	
	@Autowired
	GridPublicationManager gridPublicationManager;
	
	@Autowired
	AlarmRepository alarmRepository;
	
	@Autowired
	AlarmRunner alarmRunner;
	
	@Autowired
	AutoRecoRepository autoRecoRepository;
	
	AutoRecoMethodRunner autoRecoMethodRunner;
	
	@Autowired
	ReconciliationModelService recoService;
	
	@Autowired
	JoinRepository joinRepository;
	
	@Autowired
	JoinRunner joinRunner;
	
	@Autowired
	PresentationTemplateRepository presentationTemplateRepository;
	
	@Autowired
	PresentationRepository presentationRepository;
	
	@Autowired
	PresentationChartService presentationChartService;
	
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	MaterializedGridService materializedGridService;
	
	@Autowired
	JoinService joinService;
	
	@Value("${bcephal.project.data-dir}")
	String projectDataDir;
	
	@Value("${bcephal.presentations-dir:${bcephal.project.data-dir}}")
	String presentationsDir;
		
	String logFilePath;
	
	private BeanFactory beanFactory;
	
	
	@Autowired
	FileLoaderRunnerForGrid runnerForGrid;	
	@Autowired
	FileLoaderRunnerForMaterializedGrid runnerForMaterializedGrid;
	@Autowired
	FileLoaderProperties properties;
	
	@Autowired
	RoutineRunner routineRunner;
	
	@Autowired
	TransformationRoutineRepository routineRepository;
	
	@Autowired
	TaskService taskService;
	
	@Autowired AutoRecoOneOnOneRunner autoRecoOneOnOneRunner;
	@Autowired AutoRecoZeroAmountRunner autoRecoZeroAmountRunner;
	@Autowired AutoRecoCumulatedRunner autoRecoCumulatedRunner;
	@Autowired AutoRecoBothCumulatedRunner autoRecoBothCumulatedRunner;
	@Autowired PartialAutoRecoOneOnOneRunner partialAutoRecoOneOnOneRunner;
	@Autowired PartialAutoRecoCumulatedRunner partialAutoRecoCumulatedRunner;
	@Autowired AutoRecoOneSideRunner autoRecoOneSideRunner;
	@Autowired AutoRecoNeutralizationRunner autoRecoNeutralizationRunner;
		
	
	@Autowired
	BillingModelRepository billingModelRepository;
	
	@Autowired
	BillingRunner billingRunner;
	
	@Autowired
	BillingSendMailRunner billingSendMailRunner;
	
	@Autowired
	ProjectBackupRunner projectBackupRunner;
	
	
	@Autowired
	ConditionExpressionEvaluator expressionEvaluator;
	
	@Autowired
	FileLoaderRepository fileLoaderRepository;
	
	@Autowired
	PontoConnectEntityRepository pontoConnectEntityRepository;
	
	@Autowired
	SchedulerPontoConnectRunner schedulerPontoConnectRunner; 
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;
	
	@Autowired 
	ParameterRepository parameterRepository;
	
	@Autowired
	VariableReferenceService variableReferenceService;
	
	
	@Value("${bcephal.export.report.repository}")
	String exportReportDir;
	
	String sessionId;

	String username = "B-CEPHAL";
	Long clientId;
	
	HttpSession session;
	HttpHeaders httpHeaders;
	
	SchedulerPlannerLog schedulerLog;
	SchedulerPlannerLogItem logItem;
	
	MaterializedGrid logMatGrid;
	Map<String, Object> parameters;
	
	boolean stop;
	
	int tabulationCount = 1;
	
	@Autowired
    private TransactionTemplate template;
	

	public SchedulerPlannerRunner() {
		
	}

	
	/**
	 * Run scheduler
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void runOld() {			
		schedulerLog = buildAndSaveStartLog();
		try {			
			log.debug("Project : {}. Scheduler : {}", projectName, this.schedulerPlanner.getName());
			checkUserStop();
			int count = schedulerPlanner.getItemListChangeHandler().getItems().size();
			if(getListener() != null) {				
				this.getListener().start(count+1);
			}			
			loadClosures(schedulerPlanner);				
			int current = 0;
			while(current < count) {
				checkUserStop();
				if(getListener() != null) {	
					this.getListener().nextStep(1);
				}
				SchedulerPlannerItem item = schedulerPlanner.getItems().get(current++);
				if(getListener() != null) {	
					this.getListener().setSendError(item.isStopIfError());
				}
				
//				ScdulerPlannerRunnerItemAction action = template.execute(status -> {
//					try {
//						return run(item, true);
//					} catch (Exception e) {
//						e.printStackTrace();
//						return null;
//					}
//				});
				
				ScdulerPlannerRunnerItemAction action = run(item, true);								
				schedulerLog.setRunnedSetps(schedulerLog.getRunnedSetps() + 1);	
				checkUserStop();
				if(action.decision == SchedulerPlannerItemDecision.CONTINUE) {
					log.debug("Scheduler : {}  -  Continue...", this.schedulerPlanner.getName());
					continue;
				}
				else if(action.decision == SchedulerPlannerItemDecision.STOP) {
					log.debug("Scheduler : {}  -  Stop...", this.schedulerPlanner.getName());
					break;
				}
				else if(action.decision == SchedulerPlannerItemDecision.RESTART) {
					log.debug("Scheduler : {}  -  Restart...", this.schedulerPlanner.getName());
					current = 0;
				}
				else if(action.decision == SchedulerPlannerItemDecision.SKIP_NEXT) {
					log.debug("Scheduler : {}  -  Skip next step...", this.schedulerPlanner.getName());
					current++;
				}
				else if(action.decision == SchedulerPlannerItemDecision.GOTO) {
					log.debug("Scheduler : {}  -  Go to '{}'...", this.schedulerPlanner.getName(), action.gotoCode);
					if(!StringUtils.hasText(action.gotoCode)) {
						throw new BcephalException("Wrong instruction at position : " + (item.getPosition() + 1) + ". Missing go to code" );
					}
					SchedulerPlannerItem gotoItem = schedulerPlanner.getItemByCode(action.gotoCode);
					if(gotoItem != null) {
						current = gotoItem.getPosition();
						log.debug("Scheduler : {}  -  Go to instruction at position : {}", this.schedulerPlanner.getName(), current + 1);
					}
					else {
						throw new BcephalException("Instruction not found : " + action.gotoCode);
					}
				}
				else if(action.decision == SchedulerPlannerItemDecision.MESSAGE_WITH_CONFIRM 
						|| action.decision == SchedulerPlannerItemDecision.MESSAGE_WITHOUT_CONFIRM) {
					SchedulerPlannerLogItem logItem = buildAndSaveStartLogItem(item, action);
					log.debug("Scheduler : {}  -  Message...", this.schedulerPlanner.getName());
					try {	
						String message = runAlarm(action.objectId);
						logItem.setMessage(message);
						logItem.setStatus(RunStatus.ENDED);
					}
					catch (Exception e) {
						logItem.setMessage(e.getMessage());
						logItem.setStatus(RunStatus.ERROR);	
						throw e;
					}
					finally {
						buildAndSaveEndLogItem(logItem);
					}
				}
				else if(action.decision == SchedulerPlannerItemDecision.GENERATE_TASK) {
					SchedulerPlannerLogItem logItem = buildAndSaveStartLogItem(item, action);
					log.debug("Scheduler : {}  -  Generate task...", this.schedulerPlanner.getName());
					try {	
						runGenerateTask(action.objectId);
						logItem.setStatus(RunStatus.ENDED);
					}
					catch (Exception e) {
						logItem.setMessage(e.getMessage());
						logItem.setStatus(RunStatus.ERROR);	
						throw e;
					}
					finally {
						buildAndSaveEndLogItem(logItem);
					}
				}
			}		
			schedulerLog.setStatus(RunStatus.ENDED);
			checkUserStop();			
		}
		catch (Exception e) {
			schedulerLog.setMessage(e.getMessage());
			schedulerLog.setStatus(RunStatus.ERROR);	
			log.error("Scheduler : {} : unexpected error", schedulerPlanner.getName(), e);
		}
		finally {
			buildAndSaveEndLog(schedulerLog);
			if(getListener() != null) {	
				if(schedulerLog.getStatus() == RunStatus.ERROR) {
					this.getListener().error(schedulerLog.getMessage(), true);
				}
				else {
					this.getListener().end();
				}
			}
		}
	}
	
	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void run() {	
		logFilePath = buildLogFilePath();
		schedulerLog = buildAndSaveStartLog();
		try {
			log.debug("Project : {}. Scheduler : {}", projectName, this.schedulerPlanner.getName());
			tabulationCount = 0;
			printLog(schedulerLog);
			checkUserStop();
			schedulerPlanner.sortItems();
			run(schedulerPlanner.getItems(), true, getListener());			
			schedulerLog.setStatus(RunStatus.ENDED);
			checkUserStop();			
		}
		catch (Exception e) {
			schedulerLog.setMessage(e.getMessage());
			schedulerLog.setStatus(RunStatus.ERROR);	
			log.error("Scheduler : {} : unexpected error", schedulerPlanner.getName(), e);
		}
		finally {
			buildAndSaveEndLog(schedulerLog);
			printLogResult(schedulerLog);
			if(getListener() != null) {	
				if(schedulerLog.getStatus() == RunStatus.ERROR) {
					this.getListener().error(schedulerLog.getMessage(), true);
				}
				else {
					this.getListener().end();
				}
			}
		}
	}
	
	
	protected void run(List<SchedulerPlannerItem> items, boolean newLoop, TaskProgressListener listener) throws Exception {		
				
		log.debug("Project : {}. Scheduler : {}. Item count : {}", projectName, this.schedulerPlanner.getName(), items.size());
		checkUserStop();
		int count = items.size();
		if(listener != null) {				
			listener.start(count+1);
		}			
		int current = 0;
		while(current < count) {
			checkUserStop();
			if(listener != null) {	
				listener.nextStep(1);
			}
			SchedulerPlannerItem item = items.get(current++);
			if(listener != null) {	
				listener.setSendError(item.isStopIfError());
			}
			ScdulerPlannerRunnerItemAction action = run(item, newLoop);		
			if(newLoop) {
				schedulerLog.setRunnedSetps(schedulerLog.getRunnedSetps() + 1);	
			}
			checkUserStop();
			if(action.decision == SchedulerPlannerItemDecision.CONTINUE) {
				log.debug("Scheduler : {}  -  Continue...", this.schedulerPlanner.getName());
				continue;
			}
			else if(action.decision == SchedulerPlannerItemDecision.STOP) {
				log.debug("Scheduler : {}  -  Stop...", this.schedulerPlanner.getName());
				break;
			}
			else if(action.decision == SchedulerPlannerItemDecision.RESTART) {
				log.debug("Scheduler : {}  -  Restart...", this.schedulerPlanner.getName());
				current = 0;
			}
			else if(action.decision == SchedulerPlannerItemDecision.SKIP_NEXT) {
				log.debug("Scheduler : {}  -  Skip next step...", this.schedulerPlanner.getName());
				current++;
			}
			else if(action.decision == SchedulerPlannerItemDecision.GOTO) {
				log.debug("Scheduler : {}  -  Go to '{}'...", this.schedulerPlanner.getName(), action.gotoCode);
				if(!StringUtils.hasText(action.gotoCode)) {
					throw new BcephalException("Wrong instruction at position : " + (item.getPosition() + 1) + ". Missing go to code" );
				}
				SchedulerPlannerItem gotoItem = schedulerPlanner.getItemByCode(action.gotoCode);
				if(gotoItem != null) {
					current = gotoItem.getPosition();
					log.debug("Scheduler : {}  -  Go to instruction at position : {}", this.schedulerPlanner.getName(), current + 1);
				}
				else {
					throw new BcephalException("Instruction not found : " + action.gotoCode);
				}
			}
			else if(action.decision == SchedulerPlannerItemDecision.MESSAGE_WITH_CONFIRM 
					|| action.decision == SchedulerPlannerItemDecision.MESSAGE_WITHOUT_CONFIRM) {
				SchedulerPlannerLogItem logItem = buildAndSaveStartLogItem(item, action);
				log.debug("Scheduler : {}  -  Message...", this.schedulerPlanner.getName());
				try {	
					String message = runAlarm(action.objectId);
					logItem.setMessage(message);
					logItem.setStatus(RunStatus.ENDED);
				}
				catch (Exception e) {
					logItem.setMessage(e.getMessage());
					logItem.setStatus(RunStatus.ERROR);	
					throw e;
				}
				finally {
					buildAndSaveEndLogItem(logItem);
				}
			}
			else if(action.decision == SchedulerPlannerItemDecision.GENERATE_TASK) {
				SchedulerPlannerLogItem logItem = buildAndSaveStartLogItem(item, action);
				log.debug("Scheduler : {}  -  Generate task...", this.schedulerPlanner.getName());
				try {	
					runGenerateTask(action.objectId);
					logItem.setStatus(RunStatus.ENDED);
				}
				catch (Exception e) {
					logItem.setMessage(e.getMessage());
					logItem.setStatus(RunStatus.ERROR);	
					throw e;
				}
				finally {
					buildAndSaveEndLogItem(logItem);
				}
			}
		}
	}
	
	public void loadClosures(SchedulerPlanner scheduler) throws Exception {
		log.trace("Project : {}. Scheduler : {}  -  Loding closures...", projectName, this.schedulerPlanner.getName());
		if (scheduler != null) {
			scheduler.sortItems();
//			List<SchedulerPlannerItem> items = scheduler.getItemListChangeHandler().getItems();			
//			Collections.sort(items, new Comparator<SchedulerPlannerItem>() {
//				@Override
//				public int compare(SchedulerPlannerItem value1, SchedulerPlannerItem value2) {
//					return value1.getPosition() - value2.getPosition();
//				}
//			});
//			scheduler.setItems(items);
			
			if(scheduler.getItems().size() == 0) {
				throw new BcephalException("The list of item is empty!");
			}					
		}
		log.trace("Project : {}. Scheduler : {}  -  Closures loaded!", projectName, this.schedulerPlanner.getName());
	}
		
	private ScdulerPlannerRunnerItemAction run(SchedulerPlannerItem item, boolean newLoop) throws Exception {
		checkUserStop();
		log.debug("Scheduler : {}  -  Item : {} - {} - ({})", this.schedulerPlanner.getName(), item.getPosition() + 1, item.getType(), item.getObjectName());
		ScdulerPlannerRunnerItemAction action = new ScdulerPlannerRunnerItemAction(SchedulerPlannerItemDecision.CONTINUE);
		if(!item.isActive()) {
			log.debug("Scheduler : {}  -  Item not active : {}", this.schedulerPlanner.getName(), item.getPosition() + 1);
			return action;
		}
		logItem = buildAndSaveStartLogItem(item);		
		try {	
			printLog(logItem);
			if(item.getType() == SchedulerPlannerItemType.TEMPORISATION) {
				if(item.getTemporisationUnit() == null) {
					item.setTemporisationUnit(SchedulerPlannerItemTemporisationUnit.SECONDE);
				}
				log.debug("Scheduler : {}  -  Sleep {} {}", this.schedulerPlanner.getName(), item.getTemporisationValue(), item.getTemporisationUnit());
				long duration = item.getTemporisationUnit().buildDuration(item.getTemporisationValue());
				checkUserStop();
				Thread.sleep(duration);
			}
			else if(item.getType() == SchedulerPlannerItemType.CHECK) {
				log.debug("Scheduler : {}  -  Check condition : {} {} {}", this.schedulerPlanner.getName(), item.getObjectName(), item.getComparator(), item.getDecimalValue().toPlainString());				
				if(item.getObjectId() == null) {
					throw new BcephalException("Wrong instruction at position : " + (item.getPosition() + 1) + ". Spot not defined" );
				}
				if(!StringUtils.hasText(item.getComparator())) {
					throw new BcephalException("Wrong instruction at position : " + (item.getPosition() + 1) + ". Comparator not defined" );
				}	
				checkUserStop();
				BigDecimal amount = spotService.evaluate(item.getObjectId());
				String expression = amount.toPlainString() +  item.getComparator() + item.getDecimalValue().toPlainString();
				log.debug("Scheduler : {}  -  Check condition : {}", this.schedulerPlanner.getName(), expression);
				boolean response = evaluateExpression(expression,getOperator(item.getComparator()));
				log.debug("Scheduler : {}  -  Check condition : {} : {}", this.schedulerPlanner.getName(), expression, response);
				logItem.setStatus(RunStatus.ENDED);
				if(response) {
					return new ScdulerPlannerRunnerItemAction(item.getAction1().getDecision(), item.getAction1().getAlarmId(), item.getAction1().getGotoCode());
				}
				else {
					return new ScdulerPlannerRunnerItemAction(item.getAction2().getDecision(), item.getAction2().getAlarmId(), item.getAction2().getGotoCode());
				}
			}
			else if(item.getType() == SchedulerPlannerItemType.ACTION) {
				if(item.getAction1() == null || item.getAction1().getDecision() == null) {
					throw new BcephalException("Wrong instruction at position : " + (item.getPosition() + 1) + ". Action not defined" );
				}
				if(item.getAction1().getDecision() == SchedulerPlannerItemDecision.MESSAGE_WITH_CONFIRM 
						|| item.getAction1().getDecision() == SchedulerPlannerItemDecision.MESSAGE_WITHOUT_CONFIRM ) {
					log.debug("Scheduler : {}  -  Message...", this.schedulerPlanner.getName());
					String message = runAlarm(item.getAction1().getAlarmId());
					logItem.setMessage(message);
					logItem.setStatus(RunStatus.ENDED);
					return action;
				}	
				if(item.getAction1().getDecision() == SchedulerPlannerItemDecision.GENERATE_TASK) {
					log.debug("Scheduler : {}  -  Generate task...", this.schedulerPlanner.getName());
					runGenerateTask(item.getAction1().getAlarmId());
					logItem.setStatus(RunStatus.ENDED);
					return action;
				}	
				logItem.setStatus(RunStatus.ENDED);
				return new ScdulerPlannerRunnerItemAction(item.getAction1().getDecision(), item.getAction1().getAlarmId(), item.getAction1().getGotoCode());
			}
			else if(item.getType() == SchedulerPlannerItemType.INTEGRATION) {
				runIntegration(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.GENERATE_TASK) {
				runGenerateTask(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.BILLING) {
				runBilling(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.BILLING_MAIL) {
				runBillingMail(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.LOOP) {
				if(newLoop) {
					variableValues = new HashMap<>();
				}
				tabulationCount++;
				runLoop(item.getItemLoop(), newLoop);
				tabulationCount--;
			}
			else if(item.getType() == SchedulerPlannerItemType.PRESENTATION) {
				runPresentation(item);
			}
			else if(item.getType() == SchedulerPlannerItemType.JOIN) {
				runJoin(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.RECO) {
				runReco(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.ROUTINE) {
				runRoutine(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.TRANSFORMATION_TREE) {
				runTree(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.REFRESH_PUBLICATIONS) {
				runRefreshPublications();
			}
			else if(item.getType() == SchedulerPlannerItemType.FILE_LOADER) {
				FileLoaderRunner runner = runFileLoader(item.getObjectId());
//				runner.backupFiles(runnerForMaterializedGrid.getFilesToBackup(), true);
			}
			else if(item.getType() == SchedulerPlannerItemType.BOOKING) {
				runBooking(item.getObjectId());
			}
			else if(item.getType() == SchedulerPlannerItemType.EXPORT_REPORT_CSV 
					|| item.getType() == SchedulerPlannerItemType.EXPORT_REPORT_XLS
					|| item.getType() == SchedulerPlannerItemType.EXPORT_REPORT_JSON ) {
				runExportReport(item);
			}
			else if(item.getType() == SchedulerPlannerItemType.PROJECT_BACKUP) {
				runProjectBackup(item);
			}
			if(logItem.getStatus() == RunStatus.ERROR) {
				if(item.isStopIfError()) {
					throw new BcephalException("{} : {}", item.asString(), logItem.getMessage());
				}
				logItem.setStatus(RunStatus.ERROR);
			}
			else {
				logItem.setStatus(RunStatus.ENDED);
			}
			return action;
		}
		catch (Exception e) {
			logItem.setMessage(e.getMessage());
			logItem.setStatus(RunStatus.ERROR);	
			if(e instanceof BcephalException) {
				throw e;
			}
			log.error("Scheduler  -  Item : {} - {} - ({})", schedulerPlanner.getName(), item.getPosition() + 1, item.getType(), item.getObjectName(), e);
			if(item.isStopIfError()) {
				throw new BcephalException("Unexpected error... Scheduler  -  Item : {} - {} - ({}) ", schedulerPlanner.getName(), item.getPosition() + 1, item.getType(), item.getObjectName(), e);
			}
			return new ScdulerPlannerRunnerItemAction(SchedulerPlannerItemDecision.CONTINUE);
		}
		finally {
			buildAndSaveEndLogItem(logItem);
			printLogResult(logItem);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)	
	private void runIntegration(Long objectId) throws CertificateException, IOException, OperatorCreationException, PKCSException {
		checkUserStop();
		log.trace("Try to read Integration : {}", objectId);
		if(objectId != null) {
			Optional<PontoConnectEntity> response = pontoConnectEntityRepository.findById(objectId);
			if(response.isPresent()) {
				schedulerPontoConnectRunner.setEntity(response.get());
				schedulerPontoConnectRunner.run();
			}
		}
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runExportReport(SchedulerPlannerItem item) {
		checkUserStop();
		GrilleExportDataType dataType = item.getType() == SchedulerPlannerItemType.EXPORT_REPORT_XLS 
				? GrilleExportDataType.EXCEL 
						: item.getType() == SchedulerPlannerItemType.EXPORT_REPORT_JSON 
						? GrilleExportDataType.JSON : GrilleExportDataType.CSV;
		
		if(item.getGridType() == JoinGridType.MATERIALIZED_GRID) {
			MaterializedGrid grid = materializedGridService.getById(item.getObjectId());
			if (grid == null) {
				log.debug("Unknown materialized grid : " + item.getObjectId());
				throw new BcephalException("Unknown materialized grid : " + item.getObjectId());
			}
			log.debug("Try to export materialized grid : " + grid.getName());	
			try {
				MaterializedGridDataFilter filter = new MaterializedGridDataFilter();
				filter.setGrid(grid);
				List<String> paths = materializedGridService.performExport(filter, dataType, item.getMaxRowCountPerFile());				
				String destination = copyFile(paths, item.getRepository(), grid.getName());
				log.trace("Materialized grid exported : {}", destination);
			} catch (Exception e) {
				log.error("Unable to export materializedgrid", e);
				throw new BcephalException("Unable to export materialized grid : {}", grid.getName(), e);
			}			
		}
		else if(item.getGridType() == JoinGridType.REPORT_GRID 
				|| item.getGridType() == JoinGridType.GRID) {
			Grille grid = grilleService.getById(item.getObjectId());
			if (grid == null) {
				log.debug("Unknown grid : " + item.getObjectId());
				throw new BcephalException("Unknown grid : " + item.getObjectId());
			}
			log.debug("Try to export grid : " + grid.getName());
			try {
				GrilleDataFilter filter = new GrilleDataFilter();	
				filter.setDataSourceType(grid.getDataSourceType());	
				filter.setDataSourceId(grid.getDataSourceId());
				filter.setGrid(grid);
				List<VariableValue> values = buildVariableValuesForFilter();
				if(grid.getAdminFilter() != null) {
					grid.getAdminFilter().setVariableValues(values);
				}
				if(grid.getUserFilter() != null) {
					grid.getUserFilter().setVariableValues(values);
				}
				if(filter.getFilter() != null) {
					filter.getFilter().setVariableValues(values);
				}
				
				List<String> paths = grilleService.performExport(filter, dataType, item.getMaxRowCountPerFile());				
				String destination = copyFile(paths, item.getRepository(), grid.getName());
				log.trace("Grid exported : {}", destination);
			} catch (Exception e) {
				log.error("Unable to export grid", e);
				throw new BcephalException("Unable to export grid : {}", grid.getName(), e);
			}			
		}
		else if(item.getGridType() == JoinGridType.JOIN) {
			Join grid = joinService.getById(item.getObjectId());
			if (grid == null) {
				log.debug("Unknown join : " + item.getObjectId());
				throw new BcephalException("Unknown join : " + item.getObjectId());
			}
			log.debug("Try to export join : " + grid.getName());
			try {
				JoinFilter filter = new JoinFilter();
				if(variableValues == null) {
					variableValues = new HashMap<>();
				}
				filter.setVariableValues(variableValues);
				filter.setJoin(grid);
				List<String> paths = joinService.performExport(filter, dataType, item.getMaxRowCountPerFile());				
				String destination = copyFile(paths, item.getRepository(), grid.getName());
				log.trace("Join exported : {}", destination);
			} catch (Exception e) {
				log.error("Unable to export join", e);
				throw new BcephalException("Unable to export join : {}", grid.getName(), e);
			}	
		}
	}
	
	private String copyFile(List<String> paths, String repo, String name) throws Exception {
		String destination = this.exportReportDir;
		if(StringUtils.hasText(repo)) {
			destination = destination.concat(File.separator).concat(repo);
		}
		if(!new File(destination).exists()) {
			new File(destination).mkdirs();
		}
		name = name.replaceAll("[^a-zA-Z0-9]", "");  
		name = name.concat("_").concat(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		
		int count = paths.size();
		int nbr = 0;
		for (String source : paths) {
			String extension = FilenameUtils.EXTENSION_SEPARATOR_STR + FilenameUtils.getExtension(source);
			String fileName = name;
			if(count>1) {
				fileName += "_" + ++nbr;
			}
			String target = destination.concat(File.separator).concat(fileName).concat(extension);
			FileUtil.copy(new File(source), new File(target));				
			log.debug("Grid exported : {}", target);
			try {
				FileUtil.delete(new File(source));
			}
			catch (Exception e) {
				log.debug("Unable to delete file: {}", source, e);
			}	
		}		
		return destination;				
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected String runAlarm(Long id) throws Exception {	
		checkUserStop();
		Optional<Alarm> response = alarmRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown alarm : " + id);
			throw new BcephalException("Unknown alarm : " + id);
		}
		Alarm alarm = response.get();
		log.debug("Try to run alarm : " + alarm);		
		String message = alarmRunner.sendAlarm(alarm, username, mode, projectCode, null);
		log.debug("Alarm return message : {}", message);
		return message;
	}
		

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runJoin(Long id) throws Exception {	
		log.trace("Try to read join : {}", id);
		Optional<Join> response = joinRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown join : {}", id);
			throw new BcephalException("Unknown join : " + id);
		}
		Join join = response.get();
		log.trace("Join readed : {}", join.getName());
		log.debug("Try to run join : {}", join.getName());
		
		join.getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(join.getId(), Join.class.getName()));
		join.sortRoutines();
		checkUserStop();					
		joinRunner.setListener(getSubListener());
		joinRunner.setJoin(join);
		joinRunner.setUsername(username);
		joinRunner.setMode(mode);
		joinRunner.setSessionId(sessionId);
		joinRunner.setSession(session);
		joinRunner.setStopped(false);
		joinRunner.setVariableValues(variableValues);
		joinRunner.run();
	}
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runBilling(Long id) throws Exception {
		log.trace("Try to read billing model : {}", id);
		Optional<BillingModel> response = billingModelRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown billing model : {}", id);
			throw new BcephalException("Unknown billing model : " + id);
		}
		BillingModel model = response.get();
		log.trace("Billing model readed : {}", model.getName());
		log.debug("Try to run billing model : {}", model.getName());
		checkUserStop();
		billingRunner.setUsername(username);
		billingRunner.setSessionId(sessionId);
		billingRunner.setHttpHeaders(this.httpHeaders);
		billingRunner.setBillingModelId(id);
		billingRunner.setMode(mode);
		billingRunner.setListener(getSubListener());
		billingRunner.setStopped(false);
		billingRunner.run();					
	}
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runBillingMail(Long id) throws Exception {
		log.trace("Try to read invoice alarm : {}", id);
		if (id == null) {
			log.debug("The invoice alarm is not setted!");
			throw new BcephalException("The invoice alarm is not setted!");
		}		
		Optional<Alarm> response = alarmRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown alarm : {}", id);
			throw new BcephalException("Unknown alarm : " + id);
		}
		Alarm alarm = response.get();
		if(alarm.getCategory() != AlarmCategory.INVOICE) {
			log.debug("Alarm '{}' is not an INVOICE alarm : {}", alarm.getName());
			throw new BcephalException("Alarm '" + alarm.getName() + "' is not an INVOICE alarm");
		}
		log.trace("Alarm readed : {}", alarm.getName());
		log.debug("Try to run send invoices : {}", alarm.getName());
		checkUserStop();
		
		BillingRequest billingRequest = new BillingRequest();
		billingRequest.setModelId(alarm.getId());
//		billingRequest.getIds().add(1L);
		billingRequest.setType(BillingRequestType.INVOICE);
		
		billingSendMailRunner.setUsername(username);
		billingSendMailRunner.setSessionId(sessionId);
		billingSendMailRunner.setHttpHeaders(this.httpHeaders);
		billingSendMailRunner.setBillingRequest(billingRequest);;
		billingSendMailRunner.setMode(mode);
		billingSendMailRunner.setListener(getSubListener());
		
//		String projectCode = (String) session.getAttributes().get(RequestParams.BC_PROJECT);
//		Long profileId = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_PROFILE));
//		String locale = (String) session.getAttributes().get(RequestParams.LANGUAGE);
//		Long client = Long.valueOf((String) session.getAttributes().get(RequestParams.BC_CLIENT));
//		billingSendMailRunner.setProfileId(profileId);
//		billingSendMailRunner.setProjectCode(projectCode);
//		billingSendMailRunner.setClient(client);
//		billingSendMailRunner.setLanguage(locale);
//		if(StringUtils.hasText(locale)) {
//			billingSendMailRunner.setLocale(Locale.forLanguageTag(locale));
//		}				
		
		int count = billingSendMailRunner.run();
		log.debug("{} invoice(s) sent!", count);
		logItem.setMessage("" + count + " invoice(s) sent!");;
	}
	
	protected Map<String, Object> variableValues;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runLoop(SchedulerPlannerItemLoop loop, boolean newLoop) throws Exception {
		log.debug("Try to run loop : ({}) {}", loop.getId(), loop.getVariableName());
		SchedulerPlannerLogItem loopLogItem = this.logItem;
		try {
			List<?> values = buildVariableValues(loop);
			log.debug("Loop : {}  - Values count : {}", loop.getVariableName(), values.size());
			int n = 0;
			for(Object val : values) {
				log.trace("{}- {} = Value {}", ++n, loop.getVariableName(), val);
				printVariableValue(loop.getVariableName(), val);
				variableValues.put(loop.getVariableName(), val);
				TaskProgressListener sublistener = getSubListener();
				sublistener.createInfo(loop.getId(), loop.getVariableName());
				run(loop.getItems(), false, sublistener);
			}
			loopLogItem.setStatus(RunStatus.ENDED);	
		}
		catch (Exception e) {
			loopLogItem.setMessage(e.getMessage());
			loopLogItem.setStatus(RunStatus.ERROR);	
			throw e;
		}
		finally {
			buildAndSaveEndLogItem(loopLogItem);
			if(newLoop) {
				variableValues = new HashMap<>();
			}
		}		
	}
	
	
	protected Map<Long, PresentationRunner> presentationRunners;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runPresentation(SchedulerPlannerItem item) throws Exception {
		log.trace("Try to read presentation template : {}", item.getObjectId());
		if(presentationRunners == null) {
			presentationRunners = new HashMap<>();
		}
		Optional<PresentationTemplate> response = presentationTemplateRepository.findById(item.getObjectId());
		if (response.isEmpty()) {
			log.debug("Unknown presentation template : {}", item.getObjectId());
			throw new BcephalException("Unknown presentation template : " + item.getObjectId());
		}
		PresentationTemplate template = response.get();
		log.trace("Presentation Template readed : {}", template.getName());
		log.debug("Try to run Presentation Template : {}", template.getName());
		checkUserStop();
		
		PresentationRunner runner = presentationRunners.get(item.getObjectId());
		boolean first = runner == null;
		if(runner == null) {
			runner = new PresentationRunner();
			runner.item = item;
			runner.template = template;
			runner.operationCode = operationCode;
			runner.presentationsDir = presentationsDir;	
			runner.projectCode = projectCode;	
			runner.variableValues = variableValues;	
			runner.presentationChartService = presentationChartService;
			runner.listener = getSubListener();
			presentationRunners.put(item.getObjectId(), runner);
		}
		runner.run(first);	
		if(runner.presentation != null) {
			presentationRepository.save(runner.presentation);
		}
	}

	protected List<?> buildVariableValues(SchedulerPlannerItemLoop loop) throws Exception {
		if(loop.getVariableType().isPeriod() && loop.getVariableInterval() != null) {
			return loop.getVariableInterval().buildVariableValues(variableValues);
		}
		else if(loop.getVariableReference() != null) {
			return variableReferenceService.getValues(loop.getVariableReference(), loop.getVariableType(), variableValues);
		}
		return new ArrayList<>();
	}
	
	protected List<VariableValue> buildVariableValuesForFilter() throws Exception {
		List<VariableValue> values = new ArrayList<>();
		if(variableValues != null) {
			for(String name : variableValues.keySet()) {
				Object obj = variableValues.get(name);
				if(obj != null) {
					if(obj instanceof Number) {
						values.add(VariableValue.builder().name(name).decimalValue(BigDecimal.valueOf(((Number)obj).doubleValue())).build());
					}
					else if(obj instanceof BigDecimal) {
						values.add(VariableValue.builder().name(name).decimalValue((BigDecimal)obj).build());
					}
					else if(obj instanceof String) {
						values.add(VariableValue.builder().name(name).stringValue((String)obj).build());
					}
					else if(obj instanceof Date) {
						values.add(VariableValue.builder().name(name).periodValue((Date)obj).build());
					}
					else if(obj instanceof VariableIntervalPeriod) {
						values.add(VariableValue.builder()
								.name(name)
								.periodValue(((VariableIntervalPeriod)obj).getStart())
								.end(((VariableIntervalPeriod)obj).getEnd())
								.build());
					}
				}
			}
		}
		return values;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)		
	protected void runRoutine(Long id) throws Exception {	
		checkUserStop();
		log.trace("Try to read routine : {}", id);
		Optional<TransformationRoutine> response = routineRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown routine : {}", id);
			throw new BcephalException("Unknown routine : " + id);
		}
		TransformationRoutine routine = response.get();
		TaskProgressListener sublistener = getSubListener();
		sublistener.createInfo(routine.getId(), routine.getName());				
		routineRunner.setUsername(username);
		routineRunner.setRoutine(routine);
		routineRunner.setListener(sublistener);
		routineRunner.run();
		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)		
	protected void runGenerateTask(Long id) throws Exception {	
		checkUserStop();
		log.trace("Try to generate task...");		
		Task task = taskService.generateTask(id);
		log.trace("Task generated : {}", task.getId());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runTree(Long id) throws Exception {	
		checkUserStop();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected FileLoaderRunner runFileLoader(Long id) throws Exception {
		TaskProgressListener listener = getSubListener();
		log.trace("Try to read file loader : {}", id);
		Optional<FileLoader> response = fileLoaderRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown file loader : {}", id);
			throw new BcephalException("Unknown file loader : " + id);
		}
		FileLoaderRunData data = new FileLoaderRunData();
		data.setLoader(response.get());
		data.setMode(RunModes.A);
		
		data.getLoader().getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(data.getLoader().getId(), FileLoader.class.getName()));
		data.getLoader().sortRoutines();
		
		log.trace("File loader readed : {}", data.getLoader().getName());
		log.debug("Try to run file loader : {}", data.getLoader().getName());
		
		data.setRepositories(new ArrayList<>());
		for(com.moriset.bcephal.loader.domain.FileLoaderRepository repo : data.getLoader().getRepositories()) {
			if (StringUtils.hasText(repo.getRepositoryOnServer())) {
				Path path = Paths.get(properties.getInDir(), repo.getRepositoryOnServer());
				data.getRepositories().add(path.toString());
			}
		}
		if(data.getRepositories().isEmpty()) {
			if (StringUtils.hasText(properties.getInDir())) {
				Path path = Paths.get(properties.getInDir());
				data.getRepositories().add(path.toString());
			}
		}		
		
		
		listener.createInfo(data.getId(), data.getLoader().getName());
		checkUserStop();
		data.setOperationCode(operationCode);
		if (data.getLoader().getUploadMethod() == FileLoaderMethod.DIRECT_TO_MATERIALIZED_GRID 
				|| data.getLoader().getUploadMethod() == FileLoaderMethod.NEW_MATERIALIZED_GRID) {	
			
			runnerForMaterializedGrid.setUsername(username);
			runnerForMaterializedGrid.setSession(session);
			runnerForMaterializedGrid.setData(data);
			runnerForMaterializedGrid.setProperties(properties);
			runnerForMaterializedGrid.setListener(getSubListener());
			runnerForMaterializedGrid.setMoveFiles(false);
			runnerForMaterializedGrid.run();
			runnerForMaterializedGrid.backupFiles(runnerForMaterializedGrid.getFilesToBackup(), true);
			return runnerForMaterializedGrid;
		}
		else {
			runnerForGrid.setUsername(username);
			runnerForGrid.setSession(session);
			runnerForGrid.setData(data);
			runnerForGrid.setProperties(properties);
			runnerForGrid.setListener(getSubListener());
			runnerForGrid.setMoveFiles(false);
			runnerForGrid.run();
			runnerForGrid.backupFiles(runnerForGrid.getFilesToBackup(), true);
			return runnerForGrid;
		}		
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runBooking(Long id) throws Exception {	
		checkUserStop();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runProjectBackup(SchedulerPlannerItem item) throws Exception {
		checkUserStop();
		log.debug("Try to bacup project...");
		projectBackupRunner.setUsername(username);
		projectBackupRunner.setListener(getSubListener());
		projectBackupRunner.setProjectCode(projectCode);
		projectBackupRunner.setClientId(clientId);
		projectBackupRunner.setMaxArchiveCount(item.getMaxRowCountPerFile() > 0 ? item.getMaxRowCountPerFile() : null);
		projectBackupRunner.backupProject(Locale.ENGLISH);
		log.debug("Project backuped!");
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runRefreshPublications() throws Exception {	
		checkUserStop();
		log.debug("Try to refresh grid and join publications");
		try {
			log.debug("Try to read publisned grids and joins...");
			String sql = "SELECT id from BCP_GRID WHERE published = true";
			Query query = entityManager.createNativeQuery(sql);
			if(stop) {
				throw new BcephalException("Stopped by user.");
			}
			@SuppressWarnings("unchecked")
			List<Long> ids = query.getResultList();
			int count = ids.size();
			log.debug("Published grid count : {}", count);
			entityManager.clear();
			
			sql = "SELECT id from BCP_JOIN WHERE published = true";
			query = entityManager.createNativeQuery(sql);
			if(stop) {
				throw new BcephalException("Stopped by user.");
			}
			@SuppressWarnings("unchecked")
			List<Long> joinIds = query.getResultList();
			int joinCount = ids.size();
			log.debug("Published join count : {}", joinCount);
			entityManager.clear();
			
			TaskProgressListener listener = getSubListener();
			listener.createInfo(null, "Refresh publications");
			listener.start(count + joinCount);
			
			for (Number id : ids) {
				if(stop) {
					throw new BcephalException("Stopped by user.");
				}
				log.debug("Try to refresh grid : {}", id);
				Grille grid = new Grille();
				grid.setId(id.longValue());
				gridPublicationManager.refresh(grid);
				log.debug("Grid : {} refreshed!", id);
				listener.nextStep(1);
				entityManager.clear();
			}				
//			log.debug("Grid publications refreshed!");
//			listener.end();
			
			for (Number id : joinIds) {
				if(stop) {
					throw new BcephalException("Stopped by user.");
				}
				log.debug("Try to refresh join : {}", id);
				joinService.refreshMaterialization(id.longValue(), Locale.ENGLISH);
				log.debug("Join : {} refreshed!", id);
				listener.nextStep(1);
				entityManager.clear();
			}				
			
			log.debug("Join publications refreshed!");
			listener.end();
		}
		catch (Exception e) {
			if(e instanceof BcephalException) {
				throw (BcephalException)e;
			}
			else {
				log.error("Unable to refresh grid publications", e);
				throw new BcephalException("Unable to refresh grid publications", e);
			}
		}
	}
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void runReco(Long id) throws Exception {	
		Optional<AutoReco> response = autoRecoRepository.findById(id);
		if (response.isEmpty()) {
			log.debug("Unknown AutoReco : " + id);
			throw new BcephalException("Unknown auto reco : " + id);
		}
		AutoReco reco = response.get();
		log.debug("Try to run AutoReco : " + reco);
		
		reco.setModel(recoService.getById(reco.getRecoId()));
		boolean isPartial = reco.isPartialReco();
//		if (reco.isOneOnOne()) {
//			autoRecoMethodRunner = isPartial ? partialAutoRecoOneOnOneRunnerProvider.getHandler() : autoRecoOneOnOneRunnerProvider.getHandler();
//		} else if (reco.isZeroAount()) {
//			autoRecoMethodRunner = autoRecoZeroAmountRunnerProvider.getHandler();
//		} else if (reco.isCumulatedLeft() || reco.isCumulatedRight()) {
//			autoRecoMethodRunner = isPartial ? partialAutoRecoCumulatedRunnerProvider.getHandler() : autoRecoCumulatedRunnerProvider.getHandler();
//		} else if (reco.isBothCumulated()) {
//			autoRecoMethodRunner = autoRecoBothCumulatedRunnerProvider.getHandler();
//		} else if (reco.isLeftSide() || reco.isRightSide()) {
//			autoRecoMethodRunner = autoRecoOneSideRunnerProvider.getHandler();
//		} else if (reco.isNeutralization()) {
//			autoRecoMethodRunner = autoRecoNeutralizationRunnerProvider.getHandler();
//		}
		
		if (reco.isOneOnOne()) {
			autoRecoMethodRunner = isPartial ? partialAutoRecoOneOnOneRunner : autoRecoOneOnOneRunner;
		} else if (reco.isZeroAount()) {
			autoRecoMethodRunner = autoRecoZeroAmountRunner;
		} else if (reco.isCumulatedLeft() || reco.isCumulatedRight()) {
			autoRecoMethodRunner = isPartial ? partialAutoRecoCumulatedRunner : autoRecoCumulatedRunner;
		} else if (reco.isBothCumulated()) {
			autoRecoMethodRunner = autoRecoBothCumulatedRunner;
		} else if (reco.isLeftSide() || reco.isRightSide()) {
			autoRecoMethodRunner = autoRecoOneSideRunner;
		} else if (reco.isNeutralization()) {
			autoRecoMethodRunner = autoRecoNeutralizationRunner;
		}
				
		TaskProgressListener listener = getSubListener();
		listener.createInfo(reco.getId(), reco.getName());
		listener.createSubInfo(reco.getId(), reco.getName());
		
		autoRecoMethodRunner.setReco(reco);
		autoRecoMethodRunner.setListener(listener);
		autoRecoMethodRunner.setStop(false);
		autoRecoMethodRunner.run();							
	}
		
	
	protected TaskProgressListener getSubListener() {
		return new TaskProgressListener() {
			@Override
			public void createInfo(Long id, String name) {
				super.createInfo(id, name);
				if(getListener() != null) {	 
					getListener().createSubInfo(id, name);
				}
			}

			@Override
			public void start(long stepCount) {
				super.start(stepCount);	
				if(getListener() != null) {	 
					getListener().startSubInfo(stepCount);
				}			
			}

			@Override
			public void end() {
				super.end();	
				if(getListener() != null) {	 
					getListener().endSubInfo();
				}
			}

			@Override
			public void error(String message, boolean ended) {
				//super.error(message, ended);	
				if(logItem != null) {
					logItem.setMessage(message);
					logItem.setStatus(RunStatus.ERROR);	
				}
				if(getListener() != null) {	 
					if(getListener().isSendError()) {
						super.error(message, ended);
					}
					getListener().error(message, ended);
					//getListener().setSendError(true);
				}
				else {
					super.error(message, ended);
				}
			}

			@Override
			public void nextStep(long step) {
				super.nextStep(step);	
				if(getListener() != null) {	 
					getListener().nextSubInfoStep(step);
				}
			}

			@Override
			public void OnChange() {
				if(getListener() != null) {	 
					getListener().OnChange();
				}
			}

			public void SendInfo() {
				
			};
		};
	}
	
	Operator getOperator(String operator) {
		switch (operator) {
		case "=": {
			return new Operator("=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] == values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		case ">=": {
			return new Operator(">=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] >= values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		case "<=": {
			return new Operator("<=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] <= values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		case "!=": {
			return new Operator("!=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] != values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		case "<>": {
			return new Operator("<>", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] != values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		case ">": {
			return new Operator(">", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] > values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		case "<": {
			return new Operator("<", 2, true, Operator.PRECEDENCE_ADDITION - 1) {	
		        @Override
		        public double apply(double... values) {
		            if (values[0] < values[1]) {
		                return 0d;
		            } else {
		                return 1d;
		            }
		        }
		    };
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + operator);
		}
		
	}
    
	private boolean evaluateExpression(String expression, Operator operator) throws Exception {						
		if(expression == null || expression.trim().isEmpty()) return false;
		expression = expression.trim();
		try {			
			Expression exp = new ExpressionBuilder(expression).operator(operator).build();
			return exp.evaluate() == 0;
		} catch (Exception e ) {
			log.error("Scheduler : {} : Unable to evaluate expression : {}", schedulerPlanner.getName(), expression);
			throw new BcephalException("Unable to evaluate expression : " + expression);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLog buildAndSaveStartLog() {
		log.trace("Scheduler : {} : Build Log...!", schedulerPlanner.getName());
		//String publicationNbr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		SchedulerPlannerLog schedulerLog = new SchedulerPlannerLog();
		schedulerLog.setSchedulerId(this.schedulerPlanner.getId());
		schedulerLog.setSchedulerName(this.schedulerPlanner.getName());
		schedulerLog.mode = mode;	
		schedulerLog.setStatus(RunStatus.IN_PROGRESS);
		schedulerLog.username = username;
		schedulerLog.setSetps(schedulerPlanner.getItemListChangeHandler().getItems().size());
		
		schedulerLog.setOperationCode(operationCode);
		
		schedulerLog = logRepository.save(schedulerLog);
		
		Parameter parameter = parameterRepository.findByCodeAndParameterType(InitiationParameterCodes.LOGS_SCHEDULER_MAT_GRID, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = materializedGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				logMatGrid = result.get();
			}
		}
				
		log.trace("Scheduler : {} : Log builded!", schedulerPlanner.getName());
		return schedulerLog;
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLog buildAndSaveEndLog(SchedulerPlannerLog schedulerLog) {
		log.trace("Scheduler : {} : Build Log...!", schedulerPlanner.getName());
		schedulerLog.setEndDate(new Timestamp(System.currentTimeMillis()));
		try {
			schedulerLog = logRepository.save(schedulerLog);
		} catch (Exception e) {
			schedulerLog = logRepository.save(schedulerLog);
		}
		saveOrUpdateMatGridLog(schedulerLog);
		log.trace("Scheduler : {} : Log builded!", schedulerPlanner.getName());
		return schedulerLog;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLogItem buildAndSaveStartLogItem(SchedulerPlannerItem item) {
		log.trace("Scheduler : {} : Build Log item...!", schedulerPlanner.getName());
		SchedulerPlannerLogItem logItem = new SchedulerPlannerLogItem();
		logItem.setLogId(this.schedulerLog.getId());
		logItem.setType(item.getType());
		logItem.setObjectId(item.getObjectId());
		logItem.setObjectName(item.asString());
		logItem.setPosition(item.getPosition() + 1);
		logItem.setStatus(RunStatus.IN_PROGRESS);
		logItem.setSchedulerItemId(item.getId());
		logItem = logItemRepository.save(logItem);		
		log.trace("Scheduler : {} : Log item builded!", schedulerPlanner.getName());
		return logItem;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLogItem buildAndSaveStartLogItem(SchedulerPlannerItem item, ScdulerPlannerRunnerItemAction action) {
		log.trace("Scheduler : {} : Build Log item...!", schedulerPlanner.getName());
		SchedulerPlannerLogItem logItem = new SchedulerPlannerLogItem();
		logItem.setLogId(this.schedulerLog.getId());
		logItem.setType(item.getType());
		logItem.setObjectId(action.objectId);
		logItem.setObjectName(action.decision.name());
		logItem.setPosition(item.getPosition() + 1);
		logItem.setStatus(RunStatus.IN_PROGRESS);
		logItem.setSchedulerItemId(item.getId());
		logItem = logItemRepository.save(logItem);		
		log.trace("Scheduler : {} : Log item builded!", schedulerPlanner.getName());
		return logItem;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected SchedulerPlannerLogItem buildAndSaveEndLogItem(SchedulerPlannerLogItem logItem) {
		log.trace("Scheduler : {} : Build Log item...!", schedulerPlanner.getName());
		logItem.setEndDate(new Timestamp(System.currentTimeMillis()));
		try {
			logItem = logItemRepository.save(logItem);
		} catch (Exception e) {
			logItem = logItemRepository.save(logItem);
		}
		log.trace("Scheduler : {} : Log builded!", schedulerPlanner.getName());
		return logItem;
	}

	public void cancel() {
		this.stop = true;
		if(autoRecoMethodRunner != null) {
			autoRecoMethodRunner.setStop(true);
		}
		if(joinRunner != null) {
			joinRunner.setStopped(true);
		}
		if(alarmRunner != null) {
			
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveOrUpdateMatGridLog(SchedulerPlannerLog schedulerLog) {
		if(logMatGrid != null) {
			Object obj = null;
			Query query = null;
			try {
				query = entityManager.createNativeQuery(buildSelectQuery(schedulerLog.getId()));
				obj = (Object[])query.getSingleResult();	
			}
			catch (NoResultException e) {
				
			}
			String sql = obj == null ? buildInsertQuery(schedulerLog) : buildUpdateQuery(schedulerLog);
			log.trace("Log query: {}", sql);
			if(sql != null) {				
				query = entityManager.createNativeQuery(sql);
				for(String key : parameters.keySet()) {
					query.setParameter(key, parameters.get(key));
				}
				query.executeUpdate();
			} 
		}
	}
	
	private String buildSelectQuery(Long schedulerLogId) {
		MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
		String sql = "SELECT * FROM " + logMatGrid.getMaterializationTableName() + " WHERE " + matgGridColumn.getDbColumnName() + " = '" + schedulerLogId + "' LIMIT 1";
		return sql;
	}
	
	private String buildInsertQuery(SchedulerPlannerLog schedulerLog) {
		if(logMatGrid != null) {
			String sql = "INSERT INTO " + logMatGrid.getMaterializationTableName()
					+ " (";	
			String values = " VALUES(";
			String coma = "";
			parameters = new HashedMap<>();
			
			MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "" + schedulerLog.getId(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NAME);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "" + schedulerLog.getSchedulerName(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_AM);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = RunModes.A.name(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_USER);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = username; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			Timestamp logDate = Timestamp.from(Instant.now());
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_DATE);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = logDate;
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_TIME);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = new SimpleDateFormat("HH:mm:ss").format(logDate); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_CATEGORY);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "Scheduler"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ACTION);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "Run"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = schedulerLog.getStatus().name(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = schedulerLog.getSetps(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = schedulerLog.getStatus() == RunStatus.ERROR ? 1L : 0L; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				String value = schedulerLog.getMessage(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			
			sql += ") ";
			values += ")";		
			sql += values;		
			return sql;
		}
		return null;
	}
	
	private String buildUpdateQuery(SchedulerPlannerLog schedulerLog) {
		if(logMatGrid != null) {
			parameters = new HashedMap<>();
			String coma = ",";
			String sql = "UPDATE " + logMatGrid.getMaterializationTableName();
			sql += " SET " + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT).getDbColumnName() + " = :linecount ";
			parameters.put("linecount", schedulerLog.getSetps());
			if(schedulerLog.getStatus() == RunStatus.ERROR) {
				sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT).getDbColumnName() + " = :errorcount ";
				parameters.put("errorcount", 1L);
			}
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS).getDbColumnName() + " = :status ";
			parameters.put("status", schedulerLog.getStatus().name());
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE).getDbColumnName() + " = :message ";
			parameters.put("message", schedulerLog.getMessage());
			
			MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
			sql += " WHERE " + matgGridColumn.getDbColumnName() + " = '" + schedulerLog.getId() + "'";
			return sql;
		}
		return null;
	}
	
	private String getParameterName(String col) {		
		return ":" + col;
	}
	
	private void checkUserStop() {
		if(stop) {
			throw new BcephalException("Stopped by user.");
		}
	}
	
	private void printLog(SchedulerPlannerLog schedulerPlannerLog) throws IOException {
		FileWriter fileWriter = new FileWriter(logFilePath);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.printf("Project : %s", projectName);
	    printWriter.println();
	    printWriter.printf("User : %s", username);
	    printWriter.println();
	    printWriter.printf("Mode : %s", mode.toString());
	    printWriter.println();
	    printWriter.printf("Scheduler : %s", schedulerPlanner.getName());
	    printWriter.println();
	    printWriter.printf("Item count : %d", schedulerPlanner.getItems().size());
	    printWriter.println();
	    printWriter.printf("Operation code : %s", getOperationCode());
	    printWriter.println();
	    printWriter.printf("Start date : %s", schedulerPlannerLog.getStartDate());
	    printWriter.println();
	    printWriter.println("************************************************************");
	    printWriter.close();
	}
	
	private void printLogResult(SchedulerPlannerLog schedulerPlannerLog) {
		try {
			FileWriter fileWriter = new FileWriter(logFilePath, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println("************************************************************");
		    printWriter.printf("Status : %s", schedulerPlannerLog.getStatus().toString());
		    printWriter.println();
		    printWriter.printf("Message : %s", schedulerPlannerLog.getMessage());
		    printWriter.println();
		    printWriter.printf("End date : %s", schedulerPlannerLog.getEndDate());
		    printWriter.close();
		} catch (IOException e) {
			log.error("Unable to write result in log file.", e);
		}	    
	}
	
	private void printLog(SchedulerPlannerLogItem schedulerPlannerLogItem) throws IOException {
		FileWriter fileWriter = new FileWriter(logFilePath, true);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
		String tabs = buildTabulations();
	    printWriter.println();
	    printWriter.println(tabs + "-------------------------------------------------------------------");
	    printWriter.printf(tabs + "(%d) : %s", schedulerPlannerLogItem.getPosition(), schedulerPlannerLogItem.getType().toString());
	    printWriter.println();
	    printWriter.printf(tabs + "\tObject name : %s", schedulerPlannerLogItem.getObjectName());
	    printWriter.println();	 
	    printWriter.printf(tabs + "\tStart date : %s", schedulerPlannerLogItem.getStartDate());
	    printWriter.println();
	    printWriter.close();	    
	    printVariableValues();
	}
	
	private void printVariableValues() throws IOException {
		if(variableValues != null) {
			FileWriter fileWriter = new FileWriter(logFilePath, true);
		    PrintWriter printWriter = new PrintWriter(fileWriter);
			String tabs = buildTabulations();
		    printWriter.print(tabs + "\tVariables values : ");
		    int n = 0;
		    for(String name : variableValues.keySet()) {
			    printWriter.println();
		    	Object ob = variableValues.get(name);
		    	if(ob == null) {
		    		printWriter.printf(tabs + "\t\t%d- %s = ", ++n, name);
		    	}
		    	else if(ob instanceof Number) {
		    		printWriter.printf(tabs + "\t\t%d- %s = %d", ++n, name, ob);
		    	}
		    	else  {
		    		printWriter.printf(tabs + "\t\t%d- %s = %s", ++n, name, ob.toString());
		    	}
		    }		    
		    printWriter.close();
		}
	}
	
	
	private void printLogResult(SchedulerPlannerLogItem schedulerPlannerLogItem) {
		try {
			FileWriter fileWriter = new FileWriter(logFilePath, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);	
			String tabs = buildTabulations();	
			printWriter.println();
		    printWriter.printf(tabs + "Status : %s", schedulerPlannerLogItem.getStatus().toString());
		    printWriter.println();
		    printWriter.printf(tabs + "Message : %s", schedulerPlannerLogItem.getMessage());
		    printWriter.println();
		    printWriter.printf(tabs + "End date : %s", schedulerPlannerLogItem.getEndDate());
		    printWriter.println();
		    printWriter.close();
		} catch (IOException e) {
			log.error("Unable to write result in log file.", e);
		}	    
	}
	
	private void printVariableValue(String variablename, Object value) throws IOException {
		FileWriter fileWriter = new FileWriter(logFilePath, true);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
		String tabs = buildTabulations();
	    printWriter.println();
	    if(value == null) {
    		printWriter.printf(tabs + "Set variable : %s = ", variablename);
    	}
    	else if(value instanceof Number) {
    		printWriter.printf(tabs + "Set variable : %s = %d", variablename, value);
    	}
    	else  {
    		printWriter.printf(tabs + "Set variable : %s = %s", variablename, value.toString());
    	}
	    printWriter.close();
	}
	
	private String buildTabulations() {
		String tabs = "";
		for(int i = 1; i <= tabulationCount; i++) {
			tabs += "\t";
		}
		return tabs;
	}
	
	private String buildLogFilePath() {
		if(!StringUtils.hasText(operationCode)) {
			operationCode = ApiCodeGenerator.generate(ApiCodeGenerator.SCHEDULER_PREFIX);
		}
		Path path = schedulerPlanner.buildLogFilePath(projectDataDir, projectCode);	
		File file = path.toFile();
		if(file.exists()) {
			
		}
		else if(!file.getParentFile().exists()) {			
			file.getParentFile().mkdirs();
		}
		return path.toString();
	}

}
