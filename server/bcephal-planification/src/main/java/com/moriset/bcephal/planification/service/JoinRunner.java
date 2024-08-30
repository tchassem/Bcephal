/**
 * 
 */
package com.moriset.bcephal.planification.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.LoaderData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.routine.RoutineExecutor;
import com.moriset.bcephal.domain.routine.RoutineExecutorType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.domain.universe.UniverseExternalSourceType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinLog;
import com.moriset.bcephal.grid.domain.JoinPublicationMethod;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.repository.JoinLogRepository;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.repository.TransformationRoutineRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.CsvGenerator;
import com.moriset.bcephal.utils.FileUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author EMMENI Emmanuel
 *
 */
@Component
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JoinRunner {

	private Join join;
	private RunModes mode;
	private TaskProgressListener listener;
	private CsvGenerator universeCsvGenerator;

	private boolean stopped;

	String loaderNbrColumn;
	String loaderFileColumn;
	String loadNbr;
	
	String operationCodeColumn;
	String operationCode;

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired 
	JoinService joinService;
	
	@Autowired 
	JoinLogRepository logRepository;
	
	@Autowired 
	GrilleService grilleService;
	
	@Autowired 
	MaterializedGridService materializedGridService;
	
	@Autowired 
	ParameterRepository parameterRepository;
	
	@Autowired
	TransformationRoutineRepository routineRepository;
	
	@Autowired
	RoutineRunner routineRunner;
	
	String sessionId;

	String username = "B-CEPHAL";
	
	JoinLog joinLog;
	
	HttpSession session;
	
	MaterializedGrid logMatGrid;
	
	Map<String, Object> parameters;
	
	Map<String, Object> variableValues;
		
	@Transactional
	public void run() {		
		log.debug("Try to publish join : {}", join.getName());
		try {			
			joinLog = buildAndSaveStartLog();
			if(getListener() != null) {				
				this.getListener().createInfo(join.getId(), join.getName());
				getListener().start(5 + join.getRoutineListChangeHandler().getItems().size());
			}
			log.debug("Join : {}  -  Loding closures...", this.join.getName());
			
			JoinFilter filter = new JoinFilter();
			filter.setJoin(join);
			filter.setVariableValues(variableValues);
			joinService.loadFilterClosures(filter);
			log.debug("Join : {}  -  Closures loaded!", this.join.getName());
			if(getListener() != null) {	
				this.getListener().nextStep(1);
			}
			if(stopped) {
				throw new BcephalException("Stopped by user.");
			}
			
			publishSubGrids(join);			
			
			if(getListener() != null) {	
				this.getListener().nextStep(1);
			}
			
			if(stopped) {
				throw new BcephalException("Stopped by user.");
			}
			
			log.debug("Join : {}		PRE Routines execution ...", join.getName());
			for (RoutineExecutor routineExecutor : join.getRoutineListChangeHandler().getItems()) {
				if (routineExecutor.getType() == RoutineExecutorType.PRE) {
					if (getListener() != null) {
						getListener().nextStep(1);
					}
					if(stopped) {
						throw new BcephalException("Stopped by user.");
					}
					runRoutine(routineExecutor);
				}					
			}	
			
			if(stopped) {
				throw new BcephalException("Stopped by user.");
			}
			if(join.getPublicationDataSourceType().isMaterializedGrid()) {
				publishMaterializedGrid(filter);
			}
			else {
				publishInputGrid(filter);
			}
						
			if(getListener() != null) {	
				this.getListener().nextStep(1);
			}
			joinLog.setStatus(RunStatus.ENDED);
			
			log.debug("Join : {}	POST and LOADED Routines execution ...", join.getName());
			for (RoutineExecutor routineExecutor : join.getRoutineListChangeHandler().getItems()) {
				if (routineExecutor.getType() != RoutineExecutorType.PRE) {
					if (getListener() != null) {
						getListener().nextStep(1);
					}
					if(stopped) {
						throw new BcephalException("Stopped by user.");
					}
					runRoutine(routineExecutor);
				}				
			}
						
		} 
		catch (Exception e) {
			joinLog.setMessage(e.getMessage());
			joinLog.setStatus(RunStatus.ERROR);
			log.error("Join : {} : unexpected error", join.getName(), e);			
		} 
		finally {
			buildAndSaveEndLog(joinLog);
			if(getListener() != null) {	
				if(joinLog.getStatus() == RunStatus.ERROR) {
					this.getListener().error(joinLog.getMessage(), true);
				}
				else {
					this.getListener().end();
				}
			}
		}
	}
	
	protected void publishSubGrids(Join join) {
		if(join.isRefreshGridsBeforePublication() || join.isRepublishGridsBeforePublication()) {
			log.debug("Join : {}  -  Try to refresh grids publications...!", this.join.getName());
			List<Long> ids = new ArrayList<Long>();
			for(JoinGrid item : join.getGrids()) {
				if(item.isGrid()) {
					ids.add(item.getGridId());
				}
			}
			if(join.isRepublishGridsBeforePublication()) {
				grilleService.republish(ids, Locale.ENGLISH);
			}
			else {
				grilleService.refreshPublication(ids, Locale.ENGLISH);					
			}
			log.debug("Join : {}  -  {} grids publications refreshed!", this.join.getName(), ids.size());
		}
	}
	
	
	protected void runRoutine(RoutineExecutor routineExecutor) {
		log.debug("Join : {}\nTry to run routine : {}", join.getName(), routineExecutor.getId());
		if(!routineExecutor.isActive()) {
			log.debug("Join : {}\nRoutine disable : {}", join.getName(), routineExecutor.getId());
			return;
		}
		try {
			TaskProgressListener routineListener = getRoutineNewListener();
			routineListener.createInfo(null, "");
			
			Optional<TransformationRoutine> response = routineRepository.findById(routineExecutor.getRoutineId());
			TransformationRoutine routine = response.isPresent() ? response.get() : null;
			LoaderData loaderData = new LoaderData();
			loaderData.setUsername(username);
			//loaderData.setLoadDate(loaderLog.);
			loaderData.setLoaderId(joinLog.getPublicationGridId());
			loaderData.setLoaderName(joinLog.getPublicationGridName());
			loaderData.setLoadNbr(loadNbr);
			loaderData.setLoaderNbrColumn(loaderNbrColumn);
			loaderData.setLoaderFileColumn(loaderFileColumn);
			loaderData.setOperationCode(operationCode);
			loaderData.setOperationCodeColumn(operationCodeColumn);
			loaderData.setMode(mode);
			//loaderData.setFiles(loaderLog.getFiles());
			if (routine != null) {
				log.trace("Routine : {}", routine.getName());
				routineListener.createInfo(routine.getId(), routine.getName());
				routine = routine.copy();
				if(routineExecutor.getType() == RoutineExecutorType.LOADED) {
					//routine.setLoaderData(loaderData);									
				}
				String sessionId = this.session != null ? this.session.getId() : null;
				routineRunner.setUsername(username);
				routineRunner.setSessionId(sessionId);
				routineRunner.setRoutine(routine);
				routineRunner.setListener(routineListener);
				routineRunner.run();
			} else {
				log.error("Routine is NULL!");
				routineListener.error("Unknown routine.", false);
			}
			if (routineListener != null) {
				routineListener.end();
			}

			log.debug("Join : {}\nRoutine executed : {}", join.getName(), routineExecutor.getId());			
		} catch (Exception e) {
			log.error("Join : {}		Routine execution faild : {}", join.getName(), routineExecutor.getId(), e);
			String message = e.getMessage();
			if (getListener() != null) {
				getListener().error(message, false);
			}
		} finally {
			if (getListener() != null) {
				getListener().endSubInfo();
			}
		}
	}
	
	private void publishMaterializedGrid(JoinFilter filter) throws Exception {
		MaterializedGrid matGrid = null;
		boolean newGrid = false;
		if((join.getPublicationMethod() == JoinPublicationMethod.APPEND || join.getPublicationMethod() == JoinPublicationMethod.REPLACE)
				&& join.getPublicationGridId() != null) {
			log.debug("Join : {}  -  Read materialized grid for publication : {}", this.join.getName(), this.join.getPublicationGridId());
			matGrid = materializedGridService.getById(join.getPublicationGridId());
			log.debug("Join : {}  -  Materialized grid found : {}", this.join.getName(), matGrid);				
		}
		else {
			String name = !StringUtils.hasText(this.join.getPublicationGridName()) ? this.join.getName() : this.join.getPublicationGridName();
			log.debug("Join : {}  -  Build materialized grid for publication : {}", this.join.getName(), name);
			matGrid = joinService.saveNewMaterializedGrid(join, session, Locale.ENGLISH);
			newGrid = true;
		}						
		if(matGrid == null) {
			throw new BcephalException("Unable to publish join.\nPublication grid is Null!");
		}
		matGrid.setColumns(matGrid.getColumnListChangeHandler().getItems());
		if(getListener() != null) {	
			this.getListener().nextStep(1);
		}
		joinLog.setPublicationGridName(matGrid.getName());
		joinLog.setRowCount(0L);
		if(stopped) {
			throw new BcephalException("Stopped by user.");
		}
			
		if(stopped) {
			throw new BcephalException("Stopped by user.");
		}
		Number count = joinService.getGridRowCount(filter);			
					
		if(getListener() != null) {				
			this.getListener().start(count.intValue() + 2);
		}			
		
		int pageSize = join.getPublicationPageSize();
		int firstItem = 0;
		
		int chunk = 50000;		
		int r = count.intValue() % chunk;		
		int pageCount = (count.intValue() / chunk) + (r > 0 ? 1 : 0);
		List<String> paths = new ArrayList<>();
		for(int p = 1; p <= pageCount; p++) {
			CsvGenerator universeCsvGenerator = createCsvFiles();
			while(firstItem <= count.intValue() && firstItem <= (chunk * p)) {
				if(stopped) {
					throw new BcephalException("Stopped by user.");
				}
				
				List<Object[]> objects = joinService.getGridRows(filter, firstItem, pageSize, false);
				if(objects.size() > 0) {
					joinLog.setRowCount(joinLog.getRowCount() + objects.size());
					for(Object[] obj : objects) {
						List<Object> lineToWrite = buildLineToWrite(obj, matGrid, newGrid);								
						universeCsvGenerator.printRecord(lineToWrite.toArray());	
					}
									
					if(getListener() != null) {	
						this.getListener().nextStep(objects.size());
					}
					firstItem += pageSize;
				}
				else {
					break;
				}
			}		
			//saveCsvFiles(matGrid);
			universeCsvGenerator.end();
			paths.add(universeCsvGenerator.getPath());
		}	
		
		saveCsvFiles(matGrid, paths);
		
//		createCsvFiles();
//		while(firstItem <= count.intValue()) {
//			if(stopped) {
//				throw new BcephalException("Stopped by user.");
//			}
//			
//			List<Object[]> objects = joinService.getGridRows(filter, firstItem, pageSize, false);
//			if(objects.size() > 0) {
//				joinLog.setRowCount(joinLog.getRowCount() + objects.size());
//				for(Object[] obj : objects) {
//					List<Object> lineToWrite = buildLineToWrite(obj, matGrid, newGrid);								
//					universeCsvGenerator.printRecord(lineToWrite.toArray());	
//				}
//								
//				if(getListener() != null) {	
//					this.getListener().nextStep(objects.size());
//				}
//				firstItem += pageSize;
//			}
//			else {
//				break;
//			}
//		}		
//		saveCsvFiles(matGrid);
		
	}

	private void publishInputGrid(JoinFilter filter) throws Exception {
		Grille inputGrid = null;
		if((join.getPublicationMethod() == JoinPublicationMethod.APPEND || join.getPublicationMethod() == JoinPublicationMethod.REPLACE)
				&& join.getPublicationGridId() != null) {
			log.debug("Join : {}  -  Read input grid for publication : {}", this.join.getName(), this.join.getPublicationGridId());
			inputGrid = grilleService.getById(join.getPublicationGridId());
			log.debug("Join : {}  -  Input grid found : {}", this.join.getName(), inputGrid);				
		}
		else {
			String name = !StringUtils.hasText(this.join.getPublicationGridName()) ? this.join.getName() : this.join.getPublicationGridName();
			log.debug("Join : {}  -  Build input grid for publication : {}", this.join.getName(), name);
			inputGrid = joinService.saveNewGrid(join, session, Locale.ENGLISH);
		}						
		if(inputGrid == null) {
			throw new BcephalException("Unable to publish join.\nPublication grid is Null!");
		}
		inputGrid.setColumns(inputGrid.getColumnListChangeHandler().getItems());
		if(getListener() != null) {	
			this.getListener().nextStep(1);
		}
		joinLog.setPublicationGridName(inputGrid.getName());
		joinLog.setRowCount(0L);
		if(stopped) {
			throw new BcephalException("Stopped by user.");
		}
			
		Number count = joinService.getGridRowCount(filter);					
		if(getListener() != null) {				
			this.getListener().start(count.intValue() + 2);
		}		
				
		int pageSize = join.getPublicationPageSize();
		int firstItem = 0;
		
		int chunk = 50000;		
		int r = count.intValue() % chunk;		
		int pageCount = (count.intValue() / chunk) + (r > 0 ? 1 : 0);
		List<String> paths = new ArrayList<>();
		for(int p = 1; p <= pageCount; p++) {
			CsvGenerator universeCsvGenerator = createCsvFiles();
			while(firstItem <= count.intValue() && firstItem <= (chunk * p)) {
				if(stopped) {
					throw new BcephalException("Stopped by user.");
				}
				
				List<Object[]> objects = joinService.getGridRows(filter, firstItem, pageSize, false);
				if(objects.size() > 0) {
					joinLog.setRowCount(joinLog.getRowCount() + objects.size());
					for(Object[] obj : objects) {
						List<Object> lineToWrite = buildLineToWrite(obj, inputGrid);								
						universeCsvGenerator.printRecord(lineToWrite.toArray());	
					}
									
					if(getListener() != null) {	
						this.getListener().nextStep(objects.size());
					}
					firstItem += pageSize;
				}
				else {
					break;
				}
			}		
			//saveCsvFiles(matGrid);
			universeCsvGenerator.end();
			paths.add(universeCsvGenerator.getPath());
		}	
		
		saveCsvFiles(inputGrid, paths);
		
//		int pageSize = 500;
//		int firstItem = 0;		
//		universeCsvGenerator = createCsvFiles();
//		while(firstItem <= count.intValue()) {
//			if(stopped) {
//				throw new BcephalException("Stopped by user.");
//			}
//			
//			List<Object[]> objects = joinService.getGridRows(filter, firstItem, pageSize, false);
//			if(objects.size() > 0) {
//				joinLog.setRowCount(joinLog.getRowCount() + objects.size());
//				for(Object[] obj : objects) {
//					List<Object> lineToWrite = buildLineToWrite(obj, inputGrid);								
//					universeCsvGenerator.printRecord(lineToWrite.toArray());	
//				}
//								
//				if(getListener() != null) {	
//					this.getListener().nextStep(objects.size());
//				}
//				firstItem += pageSize;
//			}
//			else {
//				break;
//			}
//		}
//		
//		saveCsvFiles(inputGrid);
	}

	private List<Object> buildLineToWrite(Object[] line, Grille inputGrid) throws Exception {
		List<Object> output = new ArrayList<>(0);
		output.add(joinLog.getUser());
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(inputGrid.getId());
		output.add(inputGrid.getName());
		output.add(UniverseExternalSourceType.JOIN);
		output.add(join.getName());
		output.add(join.getId());
		output.add(1);	
		
		if(this.join.isAddPublicationRunNbr() && join.getPublicationRunAttributeId() != null) {
			output.add(joinLog.getPublicationNumber());
		}
		
		for(GrilleColumn column : inputGrid.getColumns()) {
			JoinColumn joinColumn = join.getColumnByPlublication(column);
			if(joinColumn != null) {
				Object value = line[joinColumn.getPosition()];				
				if(joinColumn.isPeriod() && value != null && value instanceof Date) {
					value = new SimpleDateFormat("yyyy-MM-dd").format(value);
				}				
				output.add(value != null ? value : "");	
			}
			else {
				output.add("");	
			}
		}
		
		return output;
	}
	
	private List<Object> buildLineToWrite(Object[] line, MaterializedGrid matGrid, boolean newGrid) throws Exception {
		
		List<MaterializedGridColumn> systemColumns = new ArrayList<>(0);
		List<MaterializedGridColumn> userColums = new ArrayList<>(0);
		for(MaterializedGridColumn column : matGrid.getColumns()){
			if(column.getCategory() == null || column.getCategory() == GrilleColumnCategory.USER || column.getCategory() == GrilleColumnCategory.SYSTEM) {
				userColums.add(column);
			}
			else {
				systemColumns.add(column);
			}
		}
		List<Object> output = new ArrayList<>(0);
		Date loadDate = new Date();
		for(MaterializedGridColumn column : systemColumns) {
			if(column.getCategory() == GrilleColumnCategory.LOAD_NBR) {
				output.add(joinLog.getPublicationNumber());
				//output.add(new SimpleDateFormat("yyyyMMddHHmmss").format(loadDate));
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_DATE) {
				output.add(new SimpleDateFormat("yyyy-MM-dd").format(loadDate));
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_USER) {
				output.add(username);
			}			
			else if(column.getCategory() == GrilleColumnCategory.LOAD_MODE) {
				String mode = getMode() != null ? getMode().toString() : "M";
				output.add(mode);				
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_SOURCE_NAME) {
				output.add(join.getName());
			}
			else if(column.getCategory() == GrilleColumnCategory.OPERATION_CODE) {
				output.add(operationCode);
			}
		}
		
		for(MaterializedGridColumn column : userColums) {
			JoinColumn joinColumn = join.getColumnByPlublication(column, newGrid);
			if(joinColumn != null) {
				Object value = line[joinColumn.getPosition()];
				if(joinColumn.isPeriod() && value != null && value instanceof Date) {
					value = new SimpleDateFormat("yyyy-MM-dd").format(value);
				}	
				output.add(value != null ? value : "");	
			}
			else {
				output.add("");	
			}
		}
		
		return output;
	}
	
	private CsvGenerator createCsvFiles() throws Exception {
		if(this.stopped) {
			throw new BcephalException("Stopped by user.");
		}
		log.trace("Create CSV file....");
		java.io.File file = FileUtil.createTempFile("universe-" + RandomUtils.nextInt(10000) + "-" + System.currentTimeMillis(), ".csv");
		CsvGenerator universeCsvGenerator = new CsvGenerator(file.getPath());
		universeCsvGenerator.start();
		log.trace("CSV file : {}", file.getPath());
		return universeCsvGenerator;
	}
	
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	protected void saveCsvFiles(Grille inputGrid) throws Exception {
//		if (this.stopped) {
//			return;
//		}
//		universeCsvGenerator.end();
//		String columns = buildCsvFileColumns(inputGrid);
//		try {
//			if(join.getPublicationMethod() == JoinPublicationMethod.REPLACE) {
//				grilleService.deleteAllRows(inputGrid.getId(), Locale.ENGLISH);
//			}
//			Session session = entityManager.unwrap(Session.class);
//			session.doWork(new Work() {
//				@Override
//				public void execute(Connection connection) throws SQLException {
//					CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
//					StringReader reader = null;
//					try {
//						reader =  loadCsvFromFile(universeCsvGenerator.getPath());
//						cp.copyIn(
//								"COPY " + UniverseParameters.SCHEMA_NAME.toLowerCase()
//										+ UniverseParameters.UNIVERSE_TABLE_NAME.toLowerCase() + "(" + columns + ")"
//										+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'",
//										reader);
//						connection.commit();
//					} 
//					catch (IOException e) {
//						e.printStackTrace();
//						log.error(e.getMessage());
//						connection.rollback();
//						throw new SQLException(e);
//					}
//					finally{
//						if(reader != null) {
//							reader.close();
//						}
//					}
//				}
//			});
//		} catch (jakarta.persistence.PersistenceException e) {
//			throw new BcephalException(e.getCause().getMessage());
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			try {
//				universeCsvGenerator.dispose();
//			} catch (IOException e) {
//				log.trace("Unable to delete file: {}", universeCsvGenerator.getPath(), e);
//			}
//		}
//	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveCsvFiles(Grille inputGrid, List<String> paths) throws Exception {
		if (this.stopped) {
			return;
		}
		String columns = buildCsvFileColumns(inputGrid);
		try {
			if(join.getPublicationMethod() == JoinPublicationMethod.REPLACE) {
				grilleService.deleteAllRows(inputGrid.getId(), Locale.ENGLISH);
			}
			Session session = entityManager.unwrap(Session.class);
			int count = 0;
			for(String path : paths) {
				boolean toCommit = paths.size() == ++count;
				saveCsvFile(inputGrid, path, columns, session, toCommit);
			}
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void saveCsvFile(Grille inputGrid, String path, String columns, Session session, boolean toCommit) throws Exception {
		if (this.stopped) {
			return;
		}
		try {
			session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
					StringReader reader = null;
					try {
						reader =  loadCsvFromFile(path);						
						cp.copyIn(
								"COPY " + UniverseParameters.SCHEMA_NAME.toLowerCase()
										+ UniverseParameters.UNIVERSE_TABLE_NAME.toLowerCase() + "(" + columns + ")"
										+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'",
										reader);
						if(toCommit) {
							connection.commit();						
						}
					} 
					catch (IOException e) {
						e.printStackTrace();
						log.error(e.getMessage());
						connection.rollback();
						throw new SQLException(e);
					}
					finally{
						if(reader != null) {
							reader.close();
						}
					}
				}
			});
		} catch (jakarta.persistence.PersistenceException e) {
			throw new BcephalException(e.getCause().getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				File file = new File(path);
				if(file.exists()) {
					FileUtils.forceDelete(file);
				}
			} catch (IOException e) {
				log.trace("Unable to delete file: {}", path, e);
			}
		}
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveCsvFiles(MaterializedGrid matGrid, List<String> paths) throws Exception {
		if (this.stopped) {
			return;
		}
		String columns = buildCsvFileColumns(matGrid);
		try {
			if(join.getPublicationMethod() == JoinPublicationMethod.REPLACE) {
				materializedGridService.deleteAllRows(matGrid.getId(), matGrid.getId(), Locale.ENGLISH);
			}
			Session session = entityManager.unwrap(Session.class);
			int count = 0;
			for(String path : paths) {
				boolean toCommit = paths.size() == ++count;
				saveCsvFile(matGrid, path, columns, session, toCommit);
			}
//			Transaction transaction = session.getTransaction();
//			if(transaction.isActive()) {
//				transaction.commit();
//			}
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void saveCsvFile(MaterializedGrid matGrid, String path, String columns, Session session, boolean toCommit) throws Exception {
		if (this.stopped) {
			return;
		}
		try {
			session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
					StringReader reader = null;
					try {
						reader =  loadCsvFromFile(path);						
						cp.copyIn(
								"COPY " + UniverseParameters.SCHEMA_NAME.toLowerCase()
										+ matGrid.getMaterializationTableName().toLowerCase() + "(" + columns + ")"
										+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'",
										reader);
						if(toCommit) {
							connection.commit();						
						}
					} 
					catch (IOException e) {
						e.printStackTrace();
						log.error(e.getMessage());
						connection.rollback();
						throw new SQLException(e);
					}
					finally{
						if(reader != null) {
							reader.close();
						}
					}
				}
			});
		} catch (jakarta.persistence.PersistenceException e) {
			throw new BcephalException(e.getCause().getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				File file = new File(path);
				if(file.exists()) {
					FileUtils.forceDelete(file);
				}
			} catch (IOException e) {
				log.trace("Unable to delete file: {}", path, e);
			}
		}
	}
	
	
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	protected void saveCsvFiles(MaterializedGrid matGrid) throws Exception {
//		if (this.stopped) {
//			return;
//		}
//		universeCsvGenerator.end();
//		String columns = buildCsvFileColumns(matGrid);
//		try {
//			if(join.getPublicationMethod() == JoinPublicationMethod.REPLACE) {
//				materializedGridService.deleteAllRows(matGrid.getId(), matGrid.getId(), Locale.ENGLISH);
//			}
//			Session session = entityManager.unwrap(Session.class);
//			session.doWork(new Work() {
//				@Override
//				public void execute(Connection connection) throws SQLException {
//					CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
//					StringReader reader = null;
//					try {
//						reader =  loadCsvFromFile(universeCsvGenerator.getPath());						
//						cp.copyIn(
//								"COPY " + UniverseParameters.SCHEMA_NAME.toLowerCase()
//										+ matGrid.getMaterializationTableName().toLowerCase() + "(" + columns + ")"
//										+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'",
//										reader);
//						connection.commit();						
//					} 
//					catch (IOException e) {
//						e.printStackTrace();
//						log.error(e.getMessage());
//						connection.rollback();
//						throw new SQLException(e);
//					}
//					finally{
//						if(reader != null) {
//							reader.close();
//						}
//					}
//				}
//			});
//		} catch (jakarta.persistence.PersistenceException e) {
//			throw new BcephalException(e.getCause().getMessage());
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			try {
//				universeCsvGenerator.dispose();
//			} catch (IOException e) {
//				log.trace("Unable to delete file: {}", universeCsvGenerator.getPath(), e);
//			}
//		}
//	}
	
	private StringReader loadCsvFromFile(final String fileName) throws IOException {
		try (InputStream is = new FileInputStream(fileName)) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			is.close();
			StringReader reader =  new StringReader(result.toString("UTF-8"));
			result.close();
			return reader;
		}
	}
		
	private String buildCsvFileColumns(Grille inputGrid) {
		String columns = UniverseParameters.USERNAME + "," + UniverseParameters.SOURCE_TYPE + ","
				+ UniverseParameters.SOURCE_ID + "," + UniverseParameters.SOURCE_NAME + ","
				+ UniverseParameters.EXTERNAL_SOURCE_TYPE + "," + UniverseParameters.EXTERNAL_SOURCE_REF + ","
				+ UniverseParameters.EXTERNAL_SOURCE_ID + "," + UniverseParameters.ISREADY;
		
		if(this.join.isAddPublicationRunNbr() && join.getPublicationRunAttributeId() != null) {
			Attribute attribute = new Attribute(join.getPublicationRunAttributeId(), null);
			columns += "," + attribute.getUniverseTableColumnName();
		}		
		for(GrilleColumn column : inputGrid.getColumns()) {
			columns += "," + column.getUniverseTableColumnName();
		}
		return columns;
	}
	
	private String buildCsvFileColumns(MaterializedGrid matGrid) {
		String columns = "";
		List<MaterializedGridColumn> systemColumns = new ArrayList<>(0);
		List<MaterializedGridColumn> userColums = new ArrayList<>(0);
		for(MaterializedGridColumn column : matGrid.getColumns()){
			if(column.getCategory() == null || column.getCategory() == GrilleColumnCategory.USER || column.getCategory() == GrilleColumnCategory.SYSTEM) {
				userColums.add(column);
			}
			else {
				systemColumns.add(column);
			}
		}	
		String coma = "";
		for(MaterializedGridColumn column : systemColumns) {
			String name = column.getDbColumnName();
			columns += coma + name;
			coma = ",";
		}		
		for(MaterializedGridColumn column : userColums) {
			String name = column.getDbColumnName();
			columns += coma + name;
			coma = ",";
		}		
		return columns;
	}
	
	@Transactional
	private JoinLog buildAndSaveStartLog() {
		log.trace("Join : {} : Build Log...!", join.getName());
		String publicationNbr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		JoinLog joinLog = new JoinLog(this.join);
		joinLog.setMode(mode != null ? mode : RunModes.M);
		joinLog.setStatus(RunStatus.IN_PROGRESS);;
		joinLog.setUser(username);
		joinLog.setRowCount(0L);
		joinLog.setPublicationNumber(publicationNbr);
		joinLog = logRepository.save(joinLog);		
		log.trace("Join : {} : Log builded!", join.getName());
		return joinLog;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private JoinLog buildAndSaveEndLog(JoinLog joinLog) {
		log.trace("Join : {} : Build Log...!", join.getName());
		joinLog.setEndDate(new Timestamp(System.currentTimeMillis()));		
		
		Parameter parameter = parameterRepository.findByCodeAndParameterType(InitiationParameterCodes.LOGS_JOIN_MAT_GRID, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = materializedGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				logMatGrid = result.get();
			}
		}
		
		try {
			joinLog = logRepository.save(joinLog);
		} catch (Exception e) {
			joinLog = logRepository.save(joinLog);
		} 
		saveOrUpdateMatGridLog(joinLog);
		log.trace("Join : {} : Log builded!", join.getName());
		return joinLog;
	}
	
	//@Transactional(propagation = Propagation.MANDATORY )
	private void saveOrUpdateMatGridLog(JoinLog joinLog) {
		if(logMatGrid != null) {
			Object obj = null;
			Query query = null;
			try {
				query = entityManager.createNativeQuery(buildSelectQuery(joinLog.getId()));
				obj = (Object[])query.getSingleResult();	
			}
			catch (NoResultException e) {
				
			}
			String sql = obj == null ? buildInsertQuery(joinLog) : buildUpdateQuery(joinLog);
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
	
	private String buildSelectQuery(Long joinLogId) {
		MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
		String sql = "SELECT * FROM " + logMatGrid.getMaterializationTableName() + " WHERE " + matgGridColumn.getDbColumnName() + " = '" + joinLogId + "' LIMIT 1";
		return sql;
	}
	
	private String buildInsertQuery(JoinLog joinLog) {
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
				Object value = "" + joinLog.getId(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NAME);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "" + joinLog.getName(); 
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
				Object value = "Join"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ACTION);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = join.getPublicationDataSourceType().isMaterializedGrid() ? "Materialized Grid" : "Input Grid"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = joinLog.getStatus().name(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = joinLog.getRowCount(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = joinLog.getStatus() == RunStatus.ERROR ? 1L : 0L;
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				String value = joinLog.getMessage(); 
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
	
	private String buildUpdateQuery(JoinLog joinLog) {
		if(logMatGrid != null) {
			parameters = new HashedMap<>();
			String coma = ",";
			String sql = "UPDATE " + logMatGrid.getMaterializationTableName();
			sql += " SET " + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT).getDbColumnName() + " = :linecount ";
			parameters.put("linecount", joinLog.getRowCount());
			if(joinLog.getStatus() == RunStatus.ERROR) {
				sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT).getDbColumnName() + " = :errorcount ";
				parameters.put("errorcount", 1L);
			}
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS).getDbColumnName() + " = :status ";
			parameters.put("status", joinLog.getStatus().name());
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE).getDbColumnName() + " = :message ";
			parameters.put("message", joinLog.getMessage());
			
			MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
			sql += " WHERE " + matgGridColumn.getDbColumnName() + " = '" + joinLog.getId() + "'";
			return sql;
		}
		return null;
	}
	
	private String getParameterName(String col) {		
		return ":" + col;
	}
	
	
	protected TaskProgressListener getRoutineNewListener() {
		return new TaskProgressListener() {			
			@Override
			public void createInfo(Long id, String name) {				
				//super.createInfo(id, name);
				if (getListener() != null) {
					getListener().createSubInfo(id, name);
				}
			}
			
			@Override
			public void start(long stepCount) {
				//super.start(stepCount);
				if (getListener() != null) {
					getListener().startSubInfo(stepCount);
				}
			}

			@Override
			public void end() {
				//super.end();
				if (getListener() != null) {
					getListener().endSubInfo();
				}
			}

			@Override
			public void error(String message, boolean ended) {
				//super.error(message, ended);
				if (getListener() != null) {
					getListener().error(message, ended);
				}
			}

			@Override
			public void nextStep(long step) {
				//super.nextStep(step);
				if (getListener() != null) {
					getListener().nextSubInfoStep(step);
				}
			}

			@Override
			public void SendInfo() {
				if (getListener() != null) {
					getListener().SendInfo();
				}
			}
		};
	}
	
}
