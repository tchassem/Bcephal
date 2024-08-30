package com.moriset.bcephal.scheduler.service;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderLog;
import com.moriset.bcephal.loader.domain.FileLoaderLogItem;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.loader.domain.UserLoad;
import com.moriset.bcephal.loader.domain.UserLoadLog;
import com.moriset.bcephal.loader.domain.UserLoader;
import com.moriset.bcephal.loader.domain.UserLoaderCondition;
import com.moriset.bcephal.loader.domain.UserLoaderController;
import com.moriset.bcephal.loader.domain.UserLoaderItemType;
import com.moriset.bcephal.loader.domain.UserLoaderScheduler;
import com.moriset.bcephal.loader.domain.UserLoaderSchedulerType;
import com.moriset.bcephal.loader.domain.UserLoaderTreatment;
import com.moriset.bcephal.loader.repository.FileLoaderLogItemRepository;
import com.moriset.bcephal.loader.repository.FileLoaderLogRepository;
import com.moriset.bcephal.loader.repository.FileLoaderRepository;
import com.moriset.bcephal.loader.repository.UserLoadLogRepository;
import com.moriset.bcephal.loader.repository.UserLoadRepository;
import com.moriset.bcephal.loader.service.FileLoaderProperties;
import com.moriset.bcephal.loader.service.FileLoaderRunData;
import com.moriset.bcephal.loader.service.FileLoaderRunnerForGrid;
import com.moriset.bcephal.loader.service.FileLoaderRunnerForMaterializedGrid;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerRepository;
import com.moriset.bcephal.service.SpotEvaluator;
import com.moriset.bcephal.utils.ApiCodeGenerator;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.ConfirmationRequest;
import com.moriset.bcephal.utils.FileUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserLoadRunner {
	
	private static String FILE_VAR = "$FILE$";

	private UserLoad userLoad;
	private UserLoader userLoader;
	private String fileDirs;
	private UserLoadLog userLoadLog;
	
	private Long subscriptionId;
	private String username;
	private RunModes mode;
	private String projectCode;
	private String projectName;
	private TaskProgressListener listener;
	private boolean stop;
	
	HttpSession session;
	HttpHeaders httpHeaders;
	String sessionId;
	
	String operationCode;
			
	@Autowired
	EntityManager entityManager;
	
	@Autowired
	UserLoaderService userLoaderService;
	
	@Autowired
	UserLoadRepository userLoadRepository;
		
	@Autowired
	UserLoadLogRepository userLoadLogRepository;
	
	@Autowired
	FileLoaderRepository fileLoaderRepository;
	
	@Autowired
	FileLoaderLogRepository fileLoaderLogRepository;
	
	@Autowired
	FileLoaderLogItemRepository fileLoaderLogItemRepository;
	
	@Autowired
	FileLoaderRunnerForGrid runnerForGrid;	
	@Autowired
	FileLoaderRunnerForMaterializedGrid runnerForMaterializedGrid;
	@Autowired
	FileLoaderProperties properties;
	
	@Autowired
	SchedulerPlannerRepository schedulerPlannerRepository;
	
	@Autowired
	SchedulerPlannerRunner schedulerPlannerRunner;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;
	
	@Autowired
	SpotEvaluator spotEvaluator;
	
	@Autowired
	SpotRepository spotRepository;
	
	
	public void run() {
		log.trace("User load : {} : loading...!", userLoad.getName());
		if(userLoad.isError()) {
			this.getListener().start(5);
		}
		initializations();
		if(userLoad.isError()) {
			if(userLoad.isError()) {
				this.getListener().error(userLoad.getMessage(), true);
			}
			return;
		}
		if(getListener() != null) {	
			this.getListener().nextStep(1);
		}
		
		try {			
			checkUserStop();			
			File source = new File(fileDirs);
			List<File> files = FileUtil.listFiles(fileDirs);
			log.debug("Source files count :  {}", files.size());
			userLoad.setFileCount(files.size());
			if(getListener() != null) {	
				this.getListener().nextStep(1);
			}
			
			for(UserLoaderScheduler userLoaderScheduler : userLoader.getSchedulers()) {
				if(userLoaderScheduler.getType() == UserLoaderSchedulerType.BEFORE) {
					runScheduler(userLoaderScheduler);
				}
			}
				
			log.debug("Treatment ty :  {}", userLoader.getTreatment());
			
			if(userLoader.getTreatment() == UserLoaderTreatment.REPOSITORY) {
				copyFiles(source, files);				
			}
			else if(userLoader.getTreatment() == UserLoaderTreatment.LOADER) {
				Optional<FileLoader> response = fileLoaderRepository.findById(userLoader.getFileLoaderId());
				if(response.isPresent()) {
					runFileLoader(response.get(), files);
				}
				else {
					throw new BcephalException("File loader not found : " + userLoader.getFileLoaderId());
				}
			}
			
			for(UserLoaderScheduler userLoaderScheduler : userLoader.getSchedulers()) {
				if(userLoaderScheduler.getType() == UserLoaderSchedulerType.AFTER) {
					runScheduler(userLoaderScheduler);
				}
			}
			
			userLoad.setStatus(RunStatus.ENDED);
			if(getListener() != null) {	
				this.getListener().nextStep(1);
			}
			checkUserStop();			
		}
		catch (Exception e) {
			userLoad.setMessage(e.getMessage());
			userLoad.setStatus(RunStatus.ERROR);
			userLoad.setError(true);
			log.error("User load : {} : Error...", userLoad.getName(), e);
		}
		finally {
			buildAndSaveEndLog();
			if(getListener() != null) {	
				if(userLoad.isError()) {
					this.getListener().error(userLoad.getMessage(), true);
					this.getListener().sendMessage(userLoad);
				}
				else {
					this.getListener().sendMessage(userLoad);
					this.getListener().end();
				}
			}
		}
	}
	
	
	
	private void runScheduler(UserLoaderScheduler userLoaderScheduler) {
		SchedulerPlanner schedulerPlanner = null;
		if(userLoaderScheduler != null && userLoaderScheduler.getSchedulerId() != null) {
			Optional<SchedulerPlanner> response = schedulerPlannerRepository.findById(userLoaderScheduler.getSchedulerId());
			schedulerPlanner = response.isPresent() ? response.get() : null;
		}
		if (schedulerPlanner != null) {						
			log.debug("SchedulerPlanner : {}", schedulerPlanner.getName());
			
			userLoadLog = buildUserLoadLog(new File("SCHEDULER - " + schedulerPlanner.getName()));	
						
			schedulerPlannerRunner.setOperationCode(operationCode);			
			schedulerPlannerRunner.setUsername(username);
			schedulerPlannerRunner.setSchedulerPlanner(schedulerPlanner);
			schedulerPlannerRunner.setMode(RunModes.M);
			schedulerPlannerRunner.setProjectCode(projectCode);
			schedulerPlannerRunner.setProjectName(projectCode);
			schedulerPlannerRunner.setStop(false);			
			schedulerPlannerRunner.setClientId(getSubscriptionId());
			schedulerPlannerRunner.setSessionId(getSessionId());
			schedulerPlannerRunner.setSession(getSession());
			schedulerPlannerRunner.setHttpHeaders(getHttpHeaders());
			schedulerPlannerRunner.setListener(getNewschedulerListener());
			schedulerPlannerRunner.getListener().createInfo(schedulerPlanner.getId(), schedulerPlanner.getName());	
			schedulerPlannerRunner.run();
		}		
	}
	

	private TaskProgressListener getNewschedulerListener() {
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
				if(userLoadLog != null) {
					userLoadLog.setLoaded(false);
					userLoadLog.setMessage(message);
					userLoadLog.setError(true);
					saveUserLoadLog(userLoadLog);
				}
				if(getListener() != null) {	 
					if(getListener().isSendError()) {
						super.error(message, ended);
					}
					getListener().error(message, ended);
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
	



	private void copyFiles(File source, List<File> files) {
		log.debug("Try to copy files...");
		if(getListener() != null) {	 
			getListener().createSubInfo(1L, "Copy files");
			getListener().startSubInfo(files.size() + 1);
		}		
		
		File destination = new File(userLoader.getRepository());
		if(!destination.exists()) {
			destination.mkdirs();
		}
		
		for(File file : files) {
			log.debug("Try to treat file : {}", file.toPath());
			userLoadLog = buildUserLoadLog(file);
			boolean isValid = ValidateFile(file, userLoadLog);
			if(isValid) {
				try {
					Path destinationPath = Paths.get(destination.toPath().toString(), file.getName());
					Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
					log.debug("File copied from : {} to {}", file.toPath(), destinationPath);
					userLoadLog.setLoaded(true);
					saveUserLoadLog(userLoadLog);
					userLoad.setLoadedFileCount(userLoad.getLoadedFileCount() + 1);
					try {
						Files.deleteIfExists(file.toPath());
					}
					catch(Exception e) {
						log.debug("Unable to delete file : {}", file.toPath(), e);
					}
				}
				catch(Exception e) {
					log.error("Unable to copy file : {}", file.toPath(), e);
					saveUserLoadLogError(userLoadLog, e.getMessage());
				}
				if(getListener() != null) {	
					this.getListener().nextStep(1);
				}
				log.debug("Files copied!");
			}
			userLoadLog = null;
		}
		
		if(getListener() != null) {	 
			getListener().endSubInfo();
		}
		
		
	}



	



	protected void runFileLoader(FileLoader loader, List<File> files) throws Exception {
		TaskProgressListener listener = getSubListener();
		log.trace("Try to read file loader : {}", loader.getName());		
		FileLoaderRunData data = new FileLoaderRunData();
		data.setLoader(loader);
		data.setMode(RunModes.M);
		data.setOperationCode(operationCode);
		
		data.getLoader().getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(data.getLoader().getId(), FileLoader.class.getName()));
		data.getLoader().sortRoutines();
		
		log.trace("File loader readed : {}", data.getLoader().getName());
		log.debug("Try to run file loader : {}", data.getLoader().getName());
		
		data.setRepositories(new ArrayList<>());
		data.setFiles(new ArrayList<>());
				
		data.getRepositories().add(fileDirs);
		
		for(File file : files) {
			log.debug("Try to treat file : {}", file.toPath());
			userLoadLog = buildUserLoadLog(file);
			boolean isValid = ValidateFile(file, userLoadLog);
			if(isValid) {				
				data.getFiles().add(file.getName());
			}
			userLoadLog = null;
		}
		
		
//		for(com.moriset.bcephal.loader.domain.FileLoaderRepository repo : data.getLoader().getRepositories()) {
//			if (StringUtils.hasText(repo.getRepositoryOnServer())) {
//				Path path = Paths.get(properties.getInDir(), repo.getRepositoryOnServer());
//				data.getRepositories().add(path.toString());
//			}
//		}
//		if(data.getRepositories().isEmpty()) {
//			if (StringUtils.hasText(properties.getInDir())) {
//				Path path = Paths.get(properties.getInDir());
//				data.getRepositories().add(path.toString());
//			}
//		}		
		
//		if(userLoad.isError()) {
//			this.getListener().start(data.getRepositories().size() + 1);
//		}
		listener.createInfo(data.getId(), data.getLoader().getName());
		checkUserStop();
		
		if (data.getLoader().getUploadMethod() == FileLoaderMethod.DIRECT_TO_MATERIALIZED_GRID 
				|| data.getLoader().getUploadMethod() == FileLoaderMethod.NEW_MATERIALIZED_GRID) {	
			
			runnerForMaterializedGrid.setUsername(username);
			runnerForMaterializedGrid.setSession(session);
			runnerForMaterializedGrid.setData(data);
			runnerForMaterializedGrid.setProperties(properties);
			runnerForMaterializedGrid.setListener(getSubListener());	
			runnerForMaterializedGrid.setRebuildRepositories(false);
			runnerForMaterializedGrid.setOperationCode(operationCode);
			runnerForMaterializedGrid.run();
		}
		else {
			runnerForGrid.setUsername(username);
			runnerForGrid.setSession(session);
			runnerForGrid.setData(data);
			runnerForGrid.setProperties(properties);
			runnerForGrid.setListener(getSubListener());
			runnerForGrid.setRebuildRepositories(false);
			runnerForGrid.setOperationCode(operationCode);
			runnerForGrid.run();
		}
		
		List<FileLoaderLog> fileLoaderLogs = fileLoaderLogRepository.findAllByOperationCode(operationCode);
		FileLoaderLog fileLoaderLog = fileLoaderLogs.size() > 0 ? fileLoaderLogs.get(0) : null;
		if(fileLoaderLog != null) {
			userLoad.setLoadedFileCount(fileLoaderLog.getLoadedFileCount());
			userLoad.setEmptyFileCount(fileLoaderLog.getEmptyFileCount());
			userLoad.setErrorFileCount(userLoad.getErrorFileCount() + fileLoaderLog.getErrorFileCount());
			userLoad = userLoadRepository.save(userLoad);
			List<UserLoadLog> logs = userLoadLogRepository.findAllByLoadId(userLoad.getId());
			for(UserLoadLog userLoadLog : logs) {
				Optional<FileLoaderLogItem> item = fileLoaderLogItemRepository.findByLogAndFile(fileLoaderLog.getId(), userLoadLog.getFile());
				if(item.isPresent()) {
					userLoadLog.setEmpty(item.get().isEmpty());
					userLoadLog.setLineCount(item.get().getLineCount());
					userLoadLog.setLoaded(item.get().isLoaded());
					userLoadLog.setError(item.get().isError());
					userLoadLog.setMessage(item.get().getMessage());
					userLoadLogRepository.save(userLoadLog);
				}
			}
		}
		
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
				super.error(message, ended);
				
//				if(userLoad != null) {
//					userLoad.setMessage(message);
//					userLoad.setStatus(RunStatus.ERROR);	
//					userLoad.setError(true);
//				}
//				
//				if(getListener() != null) {	 
//					if(getListener().isSendError()) {
//						super.error(message, ended);
//					}
//					getListener().error(message, ended);
//				}
//				else {
//					super.error(message, ended);
//				}
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
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void initializations() {
		log.trace("Initializations...");
		
		operationCode = ApiCodeGenerator.generate(ApiCodeGenerator.USER_LOAD_PREFIX);
		userLoad.setOperationCode(operationCode);
		fileDirs = userLoad.getFilesDir();
		userLoad.setMode(mode);	
		userLoad.setUsername(username);
		userLoad.setStatus(RunStatus.IN_PROGRESS);
		userLoad.setStartDate(new Date());
		
		String message = null;
		if(userLoad.getLoaderId() == null) {
			message = "User loader not setted!";
		}
		else {
			userLoader = userLoaderService.getById(userLoad.getLoaderId());
		}
		
		if(userLoader == null) {
			message = "User loader not found :" + userLoad.getLoaderId();
		}
		else if(!StringUtils.hasText(fileDirs)) {
			message = "Source dir is not setted!";
		}
		else if(!new File(fileDirs).exists()) {
			message = "Source dir not found : " + fileDirs;
		}
		else if(userLoader.getTreatment() == UserLoaderTreatment.REPOSITORY && !StringUtils.hasText(userLoader.getRepository())) {
			message = "Target repository is not setted!";
		}
		else if(userLoader.getTreatment() == UserLoaderTreatment.LOADER && userLoader.getFileLoaderId() == null) {
			message = "File loader is not setted!";
		}
				
		if(StringUtils.hasText(message)) {
			userLoad.setStatus(RunStatus.ERROR);
			userLoad.setError(true);
			userLoad.setMessage(message);
			userLoad.setEndDate(new Timestamp(System.currentTimeMillis()));
		}
		else {
			userLoad.setTreatment(userLoader.getTreatment());
			userLoader.sortConditions();
			userLoader.sortControllers();
			userLoader.sortSchedulers();
		}
		userLoad = userLoadRepository.save(userLoad);
		log.trace("Initialized! Source dir : {}", fileDirs);
	}
	
	
	private boolean ValidateFile(File file, UserLoadLog userLoadLog) {
		String expression = "";
		for(UserLoaderCondition condition : userLoader.getConditions()) {
			String part = buildExpression(condition, file);
			if(StringUtils.hasText(part)) {
				boolean isFirst = condition.getPosition() == 0;
				FilterVerb verb = condition.getVerb() != null ? condition.getVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					part = "(" + part + ")";
				}
				expression = expression.concat(verbString).concat(part);				
			}
			else {
				saveUserLoadLogError(userLoadLog, "Malformed condition at position : " + (condition.getPosition() + 1));
				return false;
			}
		}
		if(StringUtils.hasText(expression)) {
			expression = "Select ".concat(expression);
			try {
				Query query = entityManager.createNativeQuery(expression);
				Boolean result = (Boolean)query.getSingleResult();
				if(result == null || !result) {
					saveUserLoadLogError(userLoadLog, "Invalid conditions");
					return false;
				}
			}
			catch (Exception e) {
				log.error("Unable to evaluate expression : {}", expression, e);
				saveUserLoadLogError(userLoadLog, "Unable to evaluate conditions");
				return false;
			}
		}
		return ValidateControls(file, userLoadLog);
	}
	
	private boolean ValidateControls(File file, UserLoadLog userLoadLog) {
		for(UserLoaderController control : userLoader.getControllers()) {
			String expression = buildExpression(control, file);
			if(StringUtils.hasText(expression)) {
				try {
					Query query = entityManager.createNativeQuery(expression);
					Boolean result = (Boolean)query.getSingleResult();
					if(result == null || !result) {
						if(control.isChecked() && listener != null) {
							ConfirmationRequest request = control.getRequest();
							String message = control.getErrorMessage();							
							message = message.replace(FILE_VAR, file.getName());
							if(request == null) {								
								request = ConfirmationRequest.builder().question(message).build();
								request = listener.sendConfirmation(request);
							}
							
							if(request != null) {
								if(request.isApplyToAll()) {
									control.setRequest(request);
								}
								if(request.isYes()) {
									return true;
								};
							}
							
							saveUserLoadLogError(userLoadLog, message);
							return false;
						}
						else{
							saveUserLoadLogError(userLoadLog, "Invalid control at position = " + (control.getPosition() + 1));
							return false;
						}						
					}
				}
				catch (Exception e) {
					log.error("Unable to evaluate control : {}", expression, e);
					saveUserLoadLogError(userLoadLog, "Unable to evaluate control at position : " + (control.getPosition() + 1));
					return false;
				}				
			}
			else {
				saveUserLoadLogError(userLoadLog, "Malformed control at position : " + (control.getPosition() + 1));
				return false;
			}
		}
		return true;
	}
	
	private String buildExpression(UserLoaderCondition condition, File file) {
		String expression = null;
		String open = !StringUtils.hasText(condition.getOpeningBracket()) ? "" : condition.getOpeningBracket();
		String close = !StringUtils.hasText(condition.getClosingBracket()) ? "" : condition.getClosingBracket();
		
		if(condition.getType() == UserLoaderItemType.FILE_EXTENSION) {
			String comparator = condition.getComparator();
			AttributeOperator operation = AttributeOperator.valueOf(comparator);
			String val = condition.getStringValue();
			if (operation == null) {
				operation = !StringUtils.hasText(val) ? AttributeOperator.NOT_NULL : AttributeOperator.EQUALS;
			}
			String s = operation.buidSql(val);
			String name = FilenameUtils.getExtension(file.getName());
			name = "'".concat(name).concat("'");
			expression = name.concat(" ").concat(s);			
			if(operation.isNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" OR ").concat(name).concat(" = '')");
			}
			else if(operation.isNotNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" AND ").concat(name).concat(" != '')");
			}
			
		}		
		else if(condition.getType() == UserLoaderItemType.FILE_NAME) {
			String comparator = condition.getComparator();
			AttributeOperator operation = AttributeOperator.valueOf(comparator);
			String val = condition.getStringValue();
			if (operation == null) {
				operation = !StringUtils.hasText(val) ? AttributeOperator.NOT_NULL : AttributeOperator.EQUALS;
			}
			String s = operation.buidSql(val);
			String name = FilenameUtils.getBaseName(file.getName());
			name = "'".concat(name).concat("'");
			expression = name.concat(" ").concat(s);			
			if(operation.isNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" OR ").concat(name).concat(" = '')");
			}
			else if(operation.isNotNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" AND ").concat(name).concat(" != '')");
			}
		}
		else if(condition.getType() == UserLoaderItemType.SPOT && condition.getLongValue() != null) {
			Optional<Spot> response = spotRepository.findById(condition.getLongValue());
			if(response.isPresent()) {
				try {
					BigDecimal amount = spotEvaluator.evaluate(response.get(), Lists.newArrayList());
					amount = amount != null ? amount : BigDecimal.ZERO;
					String comparator = condition.getComparator();
					BigDecimal value = condition.getDecimalValue();
					value = value != null ? value : BigDecimal.ZERO;
					if (StringUtils.hasText(comparator)) {
						expression = amount.toString().concat(" ").concat(comparator).concat(" ").concat(value.toString());						
					}
				} catch (Exception e) {
					log.error("Unable to evaluate spot", e);
				}
			}			
		}
		else if(condition.getType() == UserLoaderItemType.COLUMN_COUNT) {
			
		}
		else if(condition.getType() == UserLoaderItemType.CHECK_DUPLICATE) {
			
		}
		if(StringUtils.hasText(expression)) {
			expression = open.concat(expression).concat(close);
		}		
		return expression;
	}
	
	
	private String buildExpression(UserLoaderController control, File file) {
		String expression = null;
		
		if(control.getType() == UserLoaderItemType.FILE_EXTENSION) {
			String comparator = control.getComparator();
			AttributeOperator operation = AttributeOperator.valueOf(comparator);
			String val = control.getStringValue();
			if (operation == null) {
				operation = !StringUtils.hasText(val) ? AttributeOperator.NOT_NULL : AttributeOperator.EQUALS;
			}
			String s = operation.buidSql(val);
			String name = FilenameUtils.getExtension(file.getName());
			name = "'".concat(name).concat("'");
			expression = name.concat(" ").concat(s);			
			if(operation.isNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" OR ").concat(name).concat(" = '')");
			}
			else if(operation.isNotNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" AND ").concat(name).concat(" != '')");
			}			
		}		
		else if(control.getType() == UserLoaderItemType.FILE_NAME) {
			String comparator = control.getComparator();
			AttributeOperator operation = AttributeOperator.valueOf(comparator);
			String val = control.getStringValue();
			if (operation == null) {
				operation = !StringUtils.hasText(val) ? AttributeOperator.NOT_NULL : AttributeOperator.EQUALS;
			}
			String s = operation.buidSql(val);
			String name = FilenameUtils.getBaseName(file.getName());
			name = "'".concat(name).concat("'");
			expression = name.concat(" ").concat(s);			
			if(operation.isNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" OR ").concat(name).concat(" = '')");
			}
			else if(operation.isNotNull()) {
				expression = "(".concat(name).concat(" ").concat(s).concat(" AND ").concat(name).concat(" != '')");
			}
		}
		else if(control.getType() == UserLoaderItemType.SPOT && control.getLongValue() != null) {
			Optional<Spot> response = spotRepository.findById(control.getLongValue());
			if(response.isPresent()) {
				try {
					BigDecimal amount = spotEvaluator.evaluate(response.get(), Lists.newArrayList());
					amount = amount != null ? amount : BigDecimal.ZERO;
					String comparator = control.getComparator();
					BigDecimal value = control.getDecimalValue();
					value = value != null ? value : BigDecimal.ZERO;
					if (StringUtils.hasText(comparator)) {
						expression = amount.toString().concat(" ").concat(comparator).concat(" ").concat(value.toString());						
					}
				} catch (Exception e) {
					log.error("Unable to evaluate spot", e);
				}
			}			
		}
		else if(control.getType() == UserLoaderItemType.COLUMN_COUNT) {
			
		}
		else if(control.getType() == UserLoaderItemType.CHECK_DUPLICATE) {
			Number count = userLoadLogRepository.countByLoaderIdAndFile(userLoader.getId(), file.getName());
			expression = "1 >= " + count.intValue();
		}
		if(StringUtils.hasText(expression)) {
			expression = "Select ".concat(expression);
		}		
		return expression;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void buildAndSaveEndLog() {
		log.trace("User load : {} : ending...", userLoad.getName());
		userLoad.setEndDate(new Timestamp(System.currentTimeMillis()));
		try {
			userLoad = userLoadRepository.save(userLoad);
		} catch (Exception e) {
			userLoad = userLoadRepository.save(userLoad);
		}		
		log.trace("User load : {} : ended!", userLoad.getName());
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected UserLoadLog buildUserLoadLog(File file) {
		UserLoadLog userLoadLog = new UserLoadLog();
		userLoadLog.setFile(file.getName());
		userLoadLog.setLoadId(userLoad.getId());
		
		try {
			return userLoadLogRepository.save(userLoadLog);
		} catch (Exception e) {
			return userLoadLogRepository.save(userLoadLog);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveUserLoadLog(UserLoadLog userLoadLog) {
		userLoad = userLoadRepository.save(userLoad);
		try {
			userLoadLogRepository.save(userLoadLog);
		} catch (Exception e) {
			userLoadLogRepository.save(userLoadLog);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveUserLoadLogError(UserLoadLog userLoadLog, String message) {
		userLoadLog.setError(true);
		userLoadLog.setMessage(message);
		userLoad.setErrorFileCount(userLoad.getErrorFileCount() + 1);
		userLoadLogRepository.save(userLoadLog);
		userLoad = userLoadRepository.save(userLoad);
	}
	
	
	
	private void checkUserStop() {
		if(stop) {
			throw new BcephalException("Stopped by user.");
		}
	}
	
}
