/**
 * 
 */
package com.moriset.bcephal.loader.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderLog;
import com.moriset.bcephal.loader.domain.FileLoaderLogItem;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.loader.domain.FileLoaderRepository;
import com.moriset.bcephal.loader.domain.FileLoaderSource;
import com.moriset.bcephal.loader.domain.LoaderTemplate;
import com.moriset.bcephal.loader.repository.FileLoaderLogItemRepository;
import com.moriset.bcephal.loader.repository.FileLoaderLogRepository;
import com.moriset.bcephal.loader.repository.LoaderTemplateRepository;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineLog;
import com.moriset.bcephal.planification.repository.TransformationRoutineRepository;
import com.moriset.bcephal.planification.service.RoutineRunner;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.utils.ApiCodeGenerator;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.CharsetDetector;
import com.moriset.bcephal.utils.CsvGenerator;
import com.moriset.bcephal.utils.ExcelLoader;
import com.moriset.bcephal.utils.FileUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Data
@Slf4j
public class FileLoaderRunner {

	private FileLoaderRunData data;
	private TaskProgressListener listener;
	private CsvGenerator universeCsvGenerator;

	private boolean stopped;
	
	private boolean deleteTmpDir;

	String loaderNbrColumn;
	String loaderFileColumn;
	String loadNbr;
	
	String operationCodeColumn;
	String operationCode;

	@PersistenceContext
	EntityManager entityManager;

	HttpSession session;

	String username = "B-CEPHAL";
	
	Boolean rebuildRepositories = null;
	
	@Autowired
	Map<String, DataSource> bcephalDataSources;

	@Autowired
	ParameterRepository parameterRepository;

	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;

	@Autowired
	FileLoaderLogRepository fileLoaderLogRepository;

	@Autowired
	FileLoaderLogItemRepository fileLoaderLogItemRepository;

	@Autowired
	private FileLoaderProperties properties;

	@Autowired
	FileLoaderService fileLoaderService;
	
	@Autowired
	LoaderTemplateRepository loaderTemplateRepository;
	
	@Autowired
	RoutineRunner routineRunner;
	
	@Autowired
	TransformationRoutineRepository routineRepository;
	
	@Autowired 
	MaterializedGridService materializedGridService;

	private String backupRepository;
	private String backupLoadedFilesRepository;
	private String backupFilesInErrorRepository;
	
	MaterializedGrid logMatGrid;
	Map<String, Object> parameters;
	
	List<String> filesToBackup;
	boolean moveFiles = true;

	public FileLoaderRunner() {

	}

	//@Transactional
	public void run() {
		FileLoader loader = data != null ? data.getLoader() : null;
		filesToBackup = new ArrayList<>();
		log.debug("Try to run file loader : {}", loader != null ? loader.getName() : null);
		FileLoaderLog loaderLog = null;
		try {
			if (getListener() != null) {
				getListener().createInfo(loader.getId(), loader.getName());
			}
			loaderLog = initLog();
			if (loader == null) {
				throw new BcephalException("Loader not found!");
			}
			if (data.getRepositories().isEmpty()) {
				throw new BcephalException("The repository is NULL!");
			}
			
			if (data.getRepositories() == null || data.getRepositories().isEmpty()) {
				log.info("FILE LOADER : {}		The repository list is empty !", loader.getName());
				String message = "The repository list is empty!";
				loaderLog.setMessage(message);
				loaderLog.setStatus(RunStatus.ENDED);
				//fileLoaderLogRepository.save(loaderLog);
				saveOrUpdateMatGridLog(loaderLog);
				return;
			}
									
			int fileCount = loadDatas(loaderLog);

			if (fileCount == 0) {
				//throw new BcephalException("There is no file to load!");
				log.info("FILE LOADER : {}		There is no file to load !", loader.getName());
				String message = "There is no file to load!";
				loaderLog.setMessage(message);
				loaderLog.setStatus(RunStatus.ENDED);
				//fileLoaderLogRepository.save(loaderLog);
				saveOrUpdateMatGridLog(loaderLog);
				return;
			}
			
			

			log.debug("FILE LOADER : {}		File count : {}", loader.getName(), fileCount);
			if (this.data.getLoader().isAllowBackup()) {
				buildBackupRepository();
			}
			loaderLog.setFileCount(fileCount);
			if (getListener() != null) {
				getListener().createInfo(loader.getId(), loader.getName());
				getListener().start(fileCount + 1 + data.getLoader().getRoutineListChangeHandler().getItems().size());
			}
			
			saveOrUpdateMatGridLog(loaderLog);
			
			log.debug("FILE LOADER : {}		PRE Routines execution ...", loader.getName());
			for (RoutineExecutor routineExecutor : data.getLoader().getRoutineListChangeHandler().getItems()) {
				if (routineExecutor.getType() == RoutineExecutorType.PRE) {
					if (getListener() != null) {
						getListener().nextStep(1);
					}
					runRoutine(routineExecutor, loaderLog);
				}					
			}			
			
			File firstFile = null;
			for (File file : data.getFileDatas()) {
				if (getListener() != null) {
					getListener().nextStep(1);
				}
				if(firstFile == null) {
					firstFile = file;
				}
				LoadFile(file, loaderLog);				
			}
			deleteTmpDir = this.getData().getLoader().getSource() == FileLoaderSource.LOCAL && RunModes.M == loaderLog.getMode();
			if(firstFile != null && deleteTmpDir) {
				deleteTmpDir(firstFile);
			}
			
			log.debug("FILE LOADER : {}	POST and LOADED Routines execution ...", loader.getName());
			for (RoutineExecutor routineExecutor : data.getLoader().getRoutineListChangeHandler().getItems()) {
				if (routineExecutor.getType() != RoutineExecutorType.PRE) {
					if (getListener() != null) {
						getListener().nextStep(1);
					}
					runRoutine(routineExecutor, loaderLog);
				}				
			}
			
			endLog(loaderLog, null);			
		} catch (Exception e) {
			log.error("FILE LOADER : {}		Load fail !", loader.getName(), e);
			String message = "Unable to run file loader. \nUnexpected error!";
			if (e instanceof BcephalException) {
				message = e.getMessage();
			}
			endLog(loaderLog, message);
			if (getListener() != null) {
				getListener().error(message, true);
			}
		} finally {
			if (getListener() != null) {
				getListener().end();
			}
		}
	}
	
	
	protected void runRoutine(RoutineExecutor routineExecutor, FileLoaderLog loaderLog) {
		log.debug("FILE LOADER : {}\nTry to run routine : {}", data.getLoader().getName(), routineExecutor.getId());
		if(!routineExecutor.isActive()) {
			log.debug("FILE LOADER : {}\nRoutine disable : {}", data.getLoader().getName(), routineExecutor.getId());
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
			loaderData.setLoaderId(loaderLog.getLoaderId());
			loaderData.setLoaderName(loaderLog.getLoaderName());
			loaderData.setLoadNbr(loadNbr);
			loaderData.setLoaderNbrColumn(loaderNbrColumn);
			loaderData.setLoaderFileColumn(loaderFileColumn);			
			loaderData.setOperationCode(operationCode);
			loaderData.setOperationCodeColumn(operationCodeColumn);
			loaderData.setMode(loaderLog.getMode());
			loaderData.setFiles(loaderLog.getFiles());
			if (routine != null) {
				log.trace("Routine : {}", routine.getName());
				routineListener.createInfo(routine.getId(), routine.getName());
				routine = routine.copy();
				if(routineExecutor.getType() == RoutineExecutorType.LOADED) {
					routine.setLoaderData(loaderData);									
				}
				String sessionId = this.session != null ? this.session.getId() : null;
				routineRunner.setUsername(username);
				routineRunner.setSessionId(sessionId);
				routineRunner.setRoutine(routine);
				routineRunner.setListener(routineListener);
				routineRunner.run();
				TransformationRoutineLog routineLog = routineRunner.getRoutineLog();
				if(routineLog != null) {
					FileLoaderLogItem logItem = new FileLoaderLogItem();
					logItem.setLog(loaderLog.getId());
					logItem.setFile("R - " + routineLog.getRoutineName());
					logItem.setLoaded(true);
					endLogItem(logItem, routineLog.getMessage());
				}
			} else {
				log.error("Routine is NULL!");
				routineListener.error("Unknown routine.", false);
			}
			if (routineListener != null) {
				routineListener.end();
			}

			log.debug("FILE LOADER : {}\nRoutine executed : {}", data.getLoader().getName(), routineExecutor.getId());			
		} catch (Exception e) {
			log.error("FILE LOADER : {}		Routine execution faild : {}", data.getLoader().getName(), routineExecutor.getId(), e);
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
	
	private TaskProgressListener getRoutineNewListener() {
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
	

	private boolean LoadFile(File file, FileLoaderLog loaderLog) {
		log.debug("FILE LOADER : {}\nTry to load File : {}", data.getLoader().getName(), file.getPath());
		FileLoaderLogItem logItem = null;
		try {
			
			if(data.getLoader().isCheckFileNameDuplication()) {
				FileLoaderLog oldLog = getDuplicationFile(file.getName());
				if(oldLog != null) {
					logItem = initLogItem(file.getName(), loaderLog);
					throw new BcephalException("The same file has been loaded at : " + oldLog.getStartDate());
				}
			}
			
			logItem = initLogItem(file.getName(), loaderLog);
			if (getListener() != null) {
				getListener().createSubInfo(null, file.getName());
			}

			if (!file.exists()) {
				throw new BcephalException("File not found : " + file.getPath());
			}
					

			createCsvFiles();
			if (file != null && isExcelFile(file.getName())) {
				loadFileExcel(file, file.getPath(), logItem);
			} 
			else if (file != null && isXmlFile(file.getName())) {
				loadFileXml(file, file.getPath(), logItem);
			}
			else {
				loadFile(file, file.getPath(), logItem);
			}
			saveCsvFiles();
			if(moveFiles) {
				backupFile(file, true);
			}
			else {
				filesToBackup.add(file.getPath());
			}

			loaderLog.setLoadedFileCount(loaderLog.getLoadedFileCount() + 1);
			logItem.setLoaded(!StringUtils.hasText(logItem.getMessage()));
			endLogItem(logItem, null);
			log.debug("FILE LOADER : {}\nFile loaded : {}", data.getLoader().getName(), file);
			return true;
		} catch (Exception e) {
			log.error("FILE LOADER : {}		Load fail for file : {}", data.getLoader().getName(), file, e);
			String message = e.getMessage();
			endLogItem(logItem, message);
			if(logItem.isEmpty()) {
				loaderLog.setEmptyFileCount(loaderLog.getEmptyFileCount() + 1);
			}
			else {
				loaderLog.setErrorFileCount(loaderLog.getErrorFileCount() + 1);
			}
			backupFile(file, false);
			if (getListener() != null) {
				getListener().error(message, false);
			}
			return false;
		} finally {
			if (getListener() != null) {
				getListener().endSubInfo();
			}
		}
	}

	private boolean isExcelFile(String fileName) {
		String value = fileName.toLowerCase();
		return value.endsWith(".xlsx") || value.endsWith(".xls");
	}
	
	private boolean isXmlFile(String fileName) {
		String value = fileName.toLowerCase();
		return value.endsWith(".xml");
	}

	protected void loadFile(File file, String filePath, FileLoaderLogItem logItem) throws Exception {
		Long countOfLines = (long) 0;
		String fileName = file.getName();		
		try {
			InputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.ISO_8859_1);
			LineNumberReader reader_ = new LineNumberReader(isr);			
			while (reader_.readLine() != null) {
				countOfLines++;
			}
			reader_.close();
			isr.close();
			fis.close();
			logItem.setLineCount(countOfLines.intValue());
			if (getListener() != null) {
				getListener().startSubInfo(countOfLines);
			}
		} catch (Exception e) {			
			throw new BcephalException("Can not open file: " + filePath);
		}
		//try (Reader reader = Files.newBufferedReader(Paths.get(filePath),StandardCharsets.UTF_8);) {
        Charset charset = new CharsetDetector().detectCharset(new File(filePath));
        if(charset == null) {
        	charset = StandardCharsets.UTF_8;
        }
		try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),charset));) {
			String separator = !StringUtils.hasText(data.getLoader().getFileSeparator())
					? String.valueOf(CsvGenerator.DEFAULT_SEPARATOR)
					: data.getLoader().getFileSeparator();
			char delimiter = separator.charAt(0);
			CSVFormat csvFormat = null;
			CSVParser csvParser = null;

			if (data.getLoader().isHasHeader()) {
				csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).setHeader().setSkipHeaderRecord(true)
						.build();
				try {
					csvParser = csvFormat.parse(reader);
				} catch (Exception e) {
					try {
						csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).build();
						csvParser = csvFormat.parse(reader);
					} catch (Exception e2) {
						reader.close();
						throw new BcephalException("Input file error: " + e2.getMessage());
					}
				}
			} else {
				try {
					csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).build();
					csvParser = csvFormat.parse(reader);
				} catch (Exception e2) {
					reader.close();
					throw new BcephalException("Input file error: " + e2.getMessage());
				}
			}
			
			Iterable<CSVRecord> records = csvParser;
			Iterator<CSVRecord> iterator = records.iterator();

			if (!iterator.hasNext()) {
				reader.close();
				logItem.setEmpty(true);
				logItem.setMessage("Empty File: " + filePath);
				throw new BcephalException("Empty File: " + filePath);
			}		
			long totalLine = data.getLoader().isHasHeader() ? countOfLines - 1 : countOfLines;
			log.trace("Total line count : {}", countOfLines);
			log.trace("Has header", data.getLoader().isHasHeader());
			log.trace("Total line to load : {}", totalLine);
			int chunck = countOfLines > 1000000 ? 700 : countOfLines > 100000 ? 400 : countOfLines > 50000 ? 300 : 50;
			int count = 0;
			int totalcount = 0;
			
			while (iterator.hasNext()) {
				log.trace("Go to next...");
				CSVRecord line = iterator.next();
				if (this.stopped) {
					break;
				}
				if (line != null && line.size() > 0) {
					log.trace("Line n° : {}", line.getRecordNumber());
					log.trace("line.getRecordNumber() == totalLine && line.size() < data.getLoader().getColumns().size() : {} == {} && {} < {}"
							,line.getRecordNumber(), totalLine, line.size(), data.getLoader().getColumns().size());
					if(line.getRecordNumber() > totalLine && line.size() < data.getLoader().getColumns().size()) {						
						log.trace("Line n° : {} is KO!!!!!", line.getRecordNumber());
						continue;
					}
					log.trace("Line n° : {} is OK!", line.getRecordNumber());
					List<Object> lineToWrite = buildLineToWrite(line, fileName, separator, line.getRecordNumber(), logItem);
					universeCsvGenerator.printRecord(lineToWrite.toArray());
				} else {
					log.debug("Empty line!");
				}
				
				count++;
				totalcount++;
				if(count >= chunck || totalcount == countOfLines) {
					if (getListener() != null) {
						getListener().nextSubInfoStep(count);
					}
					count = 0;
				}				
			}
			if (this.stopped) {
				return;
			}
			reader.close();
		} catch (Exception e) {		
			if(e instanceof BcephalException ) {
				throw e;
			}
			throw new BcephalException("Can not open file: " + filePath);
		}		
		// saveLogItem(logItem);
	}
	
	protected void loadFileXml(File file, String filePath, FileLoaderLogItem logItem) throws Exception {
		if (this.stopped) {
			return;
		}
		String path = FilenameUtils.getFullPath(filePath);
		String fileName = FilenameUtils.getBaseName(filePath) + ".csv";
		String outputFile = FilenameUtils.concat(path, fileName);		
		try {
			
			if(data.getLoader().getTemplateId() == null) {
				//data.getLoader().setTemplateId(0L);
				throw new BcephalException("Loader template is NULL!");
			}
			Optional<LoaderTemplate> response = loaderTemplateRepository.findById(data.getLoader().getTemplateId());
			if(response.isEmpty()) {
				throw new BcephalException("Loader template not found : " + data.getLoader().getTemplateId());
			}
			LoaderTemplate template = response.get();
			if(!StringUtils.hasText(template.getRepository())) {
				throw new BcephalException("Loader template repository is NULL : " + template.getName());
			}
			String templatePath = template.getRepository();
			if(!new File(templatePath).exists()) {
				throw new BcephalException("Loader template file not found : " + templatePath);
			}
			//data.getLoader().setXsltTemplate("D:\\bcephal\\V08\\docs\\bcephal-docs\\Paynovate\\SpecsPaynovate\\Auth_SVXP_20240308221500.xslt");
			XmlToCsvUtils.generateCsv(templatePath, filePath, outputFile);			
			loadFile(new File(outputFile), outputFile, logItem);
		} catch (Exception e) {
			throw e;
		}
		finally {
			if(StringUtils.hasText(outputFile) && new File(outputFile).exists()) {
				try {
					FileUtil.delete(new File(outputFile));
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		if (this.stopped) {
			return;
		}
	}

	protected void loadFileExcel(File file, String filePath, FileLoaderLogItem logItem) throws Exception {
		if (this.stopped) {
			return;
		}
		ExcelLoader excelLoader = null;
		try {
			String fileName = file.getName();
			excelLoader = new ExcelLoader();
			excelLoader.excelFile = filePath;
			excelLoader.loadWorkbookInReadMode();
	
			if (data.getLoader().isLoadAllSheets()) {
				Iterator<Sheet> sheets = excelLoader.workbook.sheetIterator();
				while (sheets.hasNext()) {
					Sheet sheet = sheets.next();
					loadSheet(excelLoader, sheet, fileName, logItem);
				}
			} else {
				Sheet sheet = excelLoader.getSheet(data.getLoader().getSheetIndex());
				if (data.getLoader().isIndentifySheetByPosition() || !org.springframework.util.StringUtils.hasText(data.getLoader().getSheetName())) {
					sheet = excelLoader.getSheet(data.getLoader().getSheetIndex());
					if (sheet == null) {
						throw new BcephalException("Sheet at position : " + data.getLoader().getSheetIndex()
								+ " not found! File: " + fileName);
					}
				} else {
					sheet = excelLoader.getSheet(data.getLoader().getSheetName());
					if (sheet == null) {
						throw new BcephalException(
								"Sheet named ': " + data.getLoader().getSheetName() + "' not found! File: " + fileName);
					}
				}
				loadSheet(excelLoader, sheet, fileName, logItem);
			}		
		} catch (Exception e) {
			throw e;
		}
		finally {
			if(excelLoader != null) {
				excelLoader.closeWorkBook();
//				excelLoader.closeFile();
			}
		}
		if (this.stopped) {
			return;
		}
	}

	protected void loadSheet(ExcelLoader excelLoader, Sheet sheet, String fileName, FileLoaderLogItem logItem)
			throws Exception {
		if (this.stopped) {
			return;
		}

		int countOfLines = sheet.getLastRowNum();
		log.debug("#line : {}", countOfLines);
		logItem.setLineCount(countOfLines);
		if (getListener() != null) {
			getListener().startSubInfo(countOfLines);
		}
		Long count = (long) 0;
		int tempo = 10;
		int tempoCount = 0;
		while (count <= countOfLines) {
			if (this.stopped) {
				break;
			}
			log.debug("Build Univers line : {}", count);
			if (data.getLoader().isHasHeader() && count < data.getLoader().getHeaderRowCount()) {
				log.debug("Skippe header row : {}", count);
				count++;
				continue;
			}
			List<Object> lineToWrite = buildExcelLineToWrite(readSheetRow(sheet.getRow(count.intValue()), count, excelLoader),
					fileName, count, logItem);
			count++;
			universeCsvGenerator.printRecord(lineToWrite.toArray());
			tempoCount++;
			if (getListener() != null) {
				if(tempoCount == tempo) {
					getListener().nextSubInfoStep(tempo);
					tempoCount = 0;
				}
			}
		}
		
		if (getListener() != null) {
			if(tempoCount > 0) {
				getListener().nextSubInfoStep(tempoCount);
			}
		}
	}
	
	

	private List<Object> readSheetRow(Row row, long rowIndex, ExcelLoader excelLoader) {
		List<Object> output = new ArrayList<>(0);
		int count = 0;
		if(row == null) {
			throw new BcephalException(
					"Line: " + rowIndex + " Is Empty ");
		}
		int countOfColumns = row.getLastCellNum();
		while (count < countOfColumns) {
			Object object = excelLoader.getCellValue(row.getCell(count));
			if (object instanceof Date) {
				output.add(object);
//				try {
//					String date = formatDate((Date) object);
//					output.add(date);
//				} catch (ParseException e) {
//					throw new BcephalException(
//							"Line: " + rowIndex + "\nColumn: " + (count + 1) + "\nUnparseable date: " + object);
//				}
			} else {
				output.add(object);
			}
			count++;
		}
		return output;
	}

	protected List<Object> buildLineToWrite(String line, String fileName, String separator, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		List<Object> output = new ArrayList<>(0);
		output.add(username); // user
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(data.getLoader().getTargetId());
		output.add(""); // Source name
		output.add(UniverseExternalSourceType.FILE_LOADER);
		output.add(fileName);
		output.add(data.getLoader().getId());
		output.add(true);

//		if(this.etlLoadFileNameAttributeOid != null) {
//			output.add(fileName);
//		}
//		if(this.etlLoadNbrAttributeOid != null) {
//			output.add("" + logItem.getLog());
//		}

		output.addAll(Arrays.asList(line.split(separator)));

		return output;
	}
	
	
	protected List<Object> buildLineToWrite(CSVRecord line, String fileName, String separator, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		List<Object> output = new ArrayList<>(0);
		output.add(username); // user
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(data.getLoader().getTargetId());
		output.add(""); // Source name
		output.add(UniverseExternalSourceType.FILE_LOADER);
		output.add(fileName);
		output.add(data.getLoader().getId());
		output.add(true);

//		if(this.etlLoadFileNameAttributeOid != null) {
//			output.add(fileName);
//		}
//		if(this.etlLoadNbrAttributeOid != null) {
//			output.add("" + logItem.getLog());
//		}

		output.addAll(line.toList());
		log.trace("Line = {}", output);

		return output;
	}
	

	protected List<Object> buildExcelLineToWrite(List<Object> line, String fileName, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		List<Object> output = new ArrayList<>(0);
		output.add(username); // user
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(data.getLoader().getTargetId());
		output.add(""); // Source name
		output.add(UniverseExternalSourceType.FILE_LOADER);
		output.add(fileName);
		output.add(data.getLoader().getId());
		output.add(true);

//		if(this.etlLoadFileNameAttributeOid != null) {
//			output.add(fileName);
//		}
//		if(this.etlLoadNbrAttributeOid != null) {
//			output.add("" + logItem.getLog());
//		}

		output.addAll(line);

		if (StringUtils.hasText(this.loaderFileColumn)) {
			output.add("");
		}
		if (StringUtils.hasText(this.loaderNbrColumn)) {
			output.add("");
		}

		return output;
	}

	protected String buildCsvFileColumns() {
		String columns = UniverseParameters.USERNAME + "," + UniverseParameters.SOURCE_TYPE + ","
				+ UniverseParameters.SOURCE_ID + "," + UniverseParameters.SOURCE_NAME + ","
				+ UniverseParameters.EXTERNAL_SOURCE_TYPE + "," + UniverseParameters.EXTERNAL_SOURCE_REF + ","
				+ UniverseParameters.EXTERNAL_SOURCE_ID + "," + UniverseParameters.ISREADY;
//		if(this.etlLoadFileNameAttributeOid != null) {
//			Attribute attribute = new Attribute();
//			attribute.setOid(Integer.valueOf(this.etlLoadFileNameAttributeOid));
//			columns += "," + attribute.getUniverseTableColName();
//		}
//		if(this.etlLoadNbrAttributeOid != null) {
//			Attribute attribute = new Attribute();
//			attribute.setOid(Integer.valueOf(this.etlLoadNbrAttributeOid));
//			columns += "," + attribute.getUniverseTableColName();
//		}
		return columns;
	}

	protected String formatDate(Date date) throws ParseException {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setTime(date);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String date1 = format1.format(cal.getTime());
		return date1;
	}

	protected String formatDate(String cell) throws Exception {
		if (!StringUtils.hasText(cell)) {
			return "";
		}
		String format = data.getLoader().getDateFormat();
		Date date = null;
		List<String> formatStrings = new ArrayList<>();
		if(StringUtils.hasText(format)) {
			formatStrings.add(format);
		}
		else {
			formatStrings.add("yyyy-MM-dd");
//			formatStrings.add("dd/MM/yyyy");
//			formatStrings.add("dd-MM-yyyy");
//			formatStrings.add("yyyy/MM/dd");
		}
		
		for (String formatString : formatStrings) {
			try {
				date = new SimpleDateFormat(formatString).parse(cell);
				String value = new SimpleDateFormat(formatString).format(date);
				if(!cell.equalsIgnoreCase(value)) {
					throw new BcephalException("Unparseable date using format " + formatString + " : " + cell, 0);
				}
				break;
			} catch (ParseException e) {
				throw new BcephalException("Unparseable date: " + cell, 0);
			}
		}
		
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String result = formatter.format(date);
			return result;
		} else {
			throw new BcephalException("Unparseable date: " + cell, 0);
		}
	}
	
	
	protected Number formatDecimal(String cell) throws Exception {
		DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
		DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
		char decimalSeparator = decimalFormatSymbols.getDecimalSeparator();							
		
		String val = cell;
		if(getData().getLoader().getThousandSeparator() != null) {
			val = val.replace(getData().getLoader().getThousandSeparator(), "");
		}
		if(StringUtils.hasText(getData().getLoader().getDecimalSeparator())) {
			val = val.replace(getData().getLoader().getDecimalSeparator(), String.valueOf(decimalSeparator));
		}
		Number amount = decimalFormat.parse(val);
		if (amount instanceof Double) {
			String pattern = "####.##############";
			DecimalFormat df = new DecimalFormat(pattern,
					DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			Number value = (Number) df.parse(df.format(amount));
			return value;
		} else {
			return amount;
		}
	}

	protected void createCsvFiles() throws Exception {
		if (this.stopped) {
			return;
		}
		log.trace("Create CSV file....");
		java.io.File file = FileUtil.createTempFile("universe-" + System.currentTimeMillis(), ".csv");
		universeCsvGenerator = new CsvGenerator(file.getPath());
		universeCsvGenerator.start();
		log.trace("CSV file : {}", file.getPath());
	}

	/**
	 * Inject data in Universe table
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveCsvFiles() throws Exception {
		if (this.stopped) {
			return;
		}
		universeCsvGenerator.end();
		String columns = buildCsvFileColumns();
		try {
			Session session = entityManager.unwrap(Session.class);
			session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
					StringReader reader = null;
					try {
						reader =  loadCsvFromFile(universeCsvGenerator.getPath());
						cp.copyIn(
								"COPY " + UniverseParameters.SCHEMA_NAME.toLowerCase()
										+ UniverseParameters.UNIVERSE_TABLE_NAME.toLowerCase() + "(" + columns + ")"
										+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'",
										reader);
					} catch (IOException e) {
						e.printStackTrace();
						log.error(e.getMessage());
						connection.rollback();
						throw new SQLException(e);
					}finally{
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
				universeCsvGenerator.dispose();
			} catch (IOException e) {
				log.trace("Unable to delete file: {}", universeCsvGenerator.getPath(), e);
			}
		}
	}

	protected StringReader loadCsvFromFile(final String fileName) throws IOException {
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

	protected synchronized void backupFile(File file, boolean successful) {
		try {
			if (this.data.getLoader().isAllowBackup()) {				
				String backupDir = successful ? this.backupLoadedFilesRepository : this.backupFilesInErrorRepository;
				backupFile(backupDir, file);
			}
		} catch (Exception e) {
			log.error("Unable to backup file: {}", file, e);
		}
	}
	
	protected synchronized void deleteTmpDir(File file) {
		try {
			File removeFile = file;
			if (removeFile.getParentFile().exists()) {
				File fileDir = new File(removeFile.getParent());
				log.info("The folder of the uploaded file is {}", fileDir.getAbsolutePath());
				if(removeFile.exists()) {
					FileUtils.forceDelete(fileDir);
				}
//				if (fileDir.exists() && Files.isDirectory(fileDir.toPath()) && fileDir.listFiles().length == 0) {
//					try {
//						FileUtils.deleteDirectory(fileDir);
//					} catch (Exception e) {
//						log.error("Unable to delete folder: {}", fileDir.getAbsolutePath(), e);
//					}
//				}
			}
		} catch (Exception e) {
			log.error("Unable to delete file: {}", file, e);
		}
	}
	
	public void backupFiles(List<String> paths, boolean successful) {
		if (this.data.getLoader().isAllowBackup()) {
			String backupDir = successful ? this.backupLoadedFilesRepository : this.backupFilesInErrorRepository;
			for (String path : paths) {
				try {
					File file = new File(path);
					if (file.exists()) {
						backupFile(backupDir, file);
					}
				} catch (Exception e) {
					log.error("Unable to backup file: {}", path, e);
				}
			}
		}
	}

	private void backupFile(String path, File file) throws IOException {
		log.debug("try to backup file: '{}' in repository: '{}'", file.getPath(), path);
		String destinationPath = FilenameUtils.concat(path, file.getName());
		File sourceFile = file;
		File destinationFile = new File(destinationPath);
		if (sourceFile.exists()) {
			if(destinationFile.exists()) {
				destinationFile.delete();
			}
			FileUtils.moveFile(sourceFile, destinationFile);
		}
	}

	protected void buildBackupRepository() {		
		try {
			log.debug("Try to backup file...");
			this.backupRepository = FilenameUtils.concat(properties.getBackupDir(),
					this.data.getLoader().getName());				
			this.backupRepository = FilenameUtils.concat(this.backupRepository,
					new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()));
			log.trace("Backup repository : {}", this.backupRepository);
			this.backupLoadedFilesRepository = FilenameUtils.concat(this.backupRepository,
					properties.getBackupLoadedFilesFolder());
			log.trace("Backup success repository : {}", this.backupLoadedFilesRepository);
			this.backupFilesInErrorRepository = FilenameUtils.concat(this.backupRepository,
					properties.getBackupFilesInErrorFolder());
			log.trace("Backup error repository : {}", this.backupFilesInErrorRepository);
			File file = new File(this.backupRepository);
			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(this.backupLoadedFilesRepository);
			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(this.backupFilesInErrorRepository);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			log.error("Unable to build backup repository!", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private FileLoaderLog initLog() {
		FileLoaderLog loaderLog = new FileLoaderLog();
		loaderLog.setLoaderId(data.getLoader().getId());
		loaderLog.setLoaderName(data.getLoader().getName());
		loaderLog.setMode(data.getMode());
		loaderLog.setStatus(RunStatus.IN_PROGRESS);
		loaderLog.setStartDate(new Date());
		loaderLog.setUsername(data.isManual() ? username : "B-CEPAHL");
		loadNbr = new SimpleDateFormat("yyyyMMddHHmmss").format(loaderLog.getStartDate());
		
		operationCode = data.getOperationCode();
		if(!StringUtils.hasText(operationCode)) {
			operationCode = ApiCodeGenerator.generate(ApiCodeGenerator.FILE_LOADER_PREFIX);
		}
		loaderLog.setOperationCode(operationCode);
		
		Parameter parameter = parameterRepository.findByCodeAndParameterType(InitiationParameterCodes.LOGS_FILE_LOADER_MAT_GRID, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = materializedGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				logMatGrid = result.get();
			}
		}
		
		loaderLog = fileLoaderLogRepository.save(loaderLog);
		return loaderLog;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void endLog(FileLoaderLog loaderLog, String message) {
		loaderLog.setMessage(message);
		loaderLog.setStatus(RunStatus.ENDED);
		if (StringUtils.hasText(message)) {
			loaderLog.setStatus(RunStatus.ERROR);
		}
		fileLoaderLogRepository.save(loaderLog);
		saveOrUpdateMatGridLog(loaderLog);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private FileLoaderLogItem initLogItem(String file, FileLoaderLog loaderLog) {
		FileLoaderLogItem logItem = new FileLoaderLogItem();
		logItem.setLog(loaderLog.getId());
		logItem.setFile(file);
		loaderLog.getFiles().add(file);
		return logItem;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void endLogItem(FileLoaderLogItem logItem, String message) {
		logItem.setMessage(message);
		logItem.setError(!logItem.isEmpty() && StringUtils.hasText(message));
		fileLoaderLogItemRepository.save(logItem);
	}
	
	private FileLoaderLog getDuplicationFile(String file) {
		List<FileLoaderLog> logs = fileLoaderLogItemRepository.getDuplicationFile(data.getLoader().getId(), file);
		return logs.size() > 0 ? logs.get(0) : null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected int loadDatas(FileLoaderLog fileLoaderLog) throws Exception {
		log.debug("Load Target datas...");
		
		this.data.setFileDatas(new ArrayList<>(0));			
		if(data.getLoader().getSource() == FileLoaderSource.LOCAL || (rebuildRepositories != null && !rebuildRepositories)) {
			String repository = data.getRepositories().get(0);					
			for(String name : new ArrayList<>(data.getFiles())) {
				String path = Paths.get(repository, name).toString();
				data.getFileDatas().add(new File(path));
			}	
		}	
		else if (data.getMode() == RunModes.A || data.getLoader().getSource() == FileLoaderSource.SERVER) {	
			String inDir = "";
			if(this.properties != null) {
				inDir = this.properties.getInDir();
			}
			else {
				log.info("Loader properties is null!");
			}
			
			log.debug("Loader Repository dir: {}", inDir);
			
			for(FileLoaderRepository item : data.getLoader().getRepositoryListChangeHandler().getItems()) {	
				if(!org.flywaydb.core.internal.util.StringUtils.hasText(item.getRepositoryOnServer())) {
					continue;
				}
				String repository = item.getRepositoryOnServer();
				String path = Paths.get(inDir, repository).toString();				
				//log.debug("Check repository: {}", path);
				File repo = new File(path);
				if (!repo.exists()) {
					//log.debug("Repository not found: {}", path);
					continue;
				}
				File[] files = repo.listFiles();
				for (File file : files) {
					String name = file.getName();
					//log.trace("File: {}", name);
					if (file.isFile() && validateFileName(name, item)) {
						this.data.getFileDatas().add(file);
						//log.trace("File added: {}", name);
					}
				}
			}			
			
//			for(String repository : data.getRepositories()) {				
//				String path = Paths.get(inDir, repository).toString();				
//				log.debug("Check repository: {}", path);
//				File repo = new File(path);
//				if (!repo.exists()) {
//					log.debug("Repository not found: {}", path);
//					continue;
//				}
//				File[] files = repo.listFiles();
//				for (File file : files) {
//					String name = file.getName();
//					//log.trace("File: {}", name);
//					if (file.isFile() && validateFileName(name)) {
//						this.data.getFileDatas().add(file);
//						log.trace("File added: {}", name);
//					}
//				}
//			}			
		}
		int fileCount = data.getFileDatas().size();
		fileLoaderLog.setFileCount(fileCount);
		
		if (this.data.getLoader().getTargetId() == null) {
			log.debug("The target grid is null.");
			FileLoader loader = data.getLoader(); // fileLoaderService.getById(this.data.getLoader().getId());
			if (loader.getColumnListChangeHandler().getItems().size() > 0) {
				if(data.getFiles() != null && data.getFiles().size() > 0) {
					if(loader.getUploadMethod() == FileLoaderMethod.NEW_MATERIALIZED_GRID) {
						fileLoaderService.saveNewMaterializedGrid(loader, data.getFileDatas().get(0).getName(), session, Locale.ENGLISH);
					}
					else {
						fileLoaderService.saveNewGrid(loader, data.getFileDatas().get(0).getName(), session, Locale.ENGLISH);
					}
				}
				else {
					throw new BcephalException(
							"Unable to run loader : " + data.getLoader().getName() + ".\nThere is no file to load.");
				}
			} else {
				log.debug("The target grid is null.");
				throw new BcephalException(
						"Unable to run loader : " + data.getLoader().getName() + ".\nThere is no column.");
			}
		}

		if (this.data.getLoader().getTargetId() != null) {
			loadTarget(this.data.getLoader().getTargetId());
			initializeLoaderSettings();
			ValidateDatas();
		} else {
			String type = data.getLoader().getUploadMethod() == FileLoaderMethod.VIA_INPUT_TABLE ? "table"
					: data.getLoader().getUploadMethod() == FileLoaderMethod.VIA_AUTOMATIC_SOURCING
							? "automatic sourcing"
							: "grid";
			throw new BcephalException(
					"Unable to run files loader : " + data.getLoader().getName() + ". Undefined " + type);
		}
		
		return fileCount;
	}

	protected boolean validateFileName(String file, FileLoaderRepository repository) {
		if (!repository.validateFileName(file)) {
			log.trace("Validate file: {}. Result: {}", file, false);
			return false;
		}
		log.trace("Validate file: {}. Result: {}", file, true);
		return true;
	}

	protected void loadTarget(Long targetId) throws Exception {

	}

	private void initializeLoaderSettings() {
		try {
			Parameter parameter = parameterRepository.findByCodeAndParameterType(
					InitiationParameterCodes.INITIATION_FILE_LOADER_FILE_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if (parameter != null && parameter.getLongValue() != null) {
				Attribute attribute = new Attribute();
				attribute.setId(parameter.getLongValue());
				this.loaderFileColumn = attribute.getUniverseTableColumnName();
			}

			parameter = parameterRepository.findByCodeAndParameterType(
					InitiationParameterCodes.INITIATION_FILE_LOADER_LOAD_NBR_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if (parameter != null && parameter.getLongValue() != null) {
				Attribute attribute = new Attribute();
				attribute.setId(parameter.getLongValue());
				this.loaderNbrColumn = attribute.getUniverseTableColumnName();
			}
			
			parameter = parameterRepository.findByCodeAndParameterType(
					InitiationParameterCodes.OPERATION_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if (parameter != null && parameter.getLongValue() != null) {
				Attribute attribute = new Attribute();
				attribute.setId(parameter.getLongValue());
				this.operationCodeColumn = attribute.getUniverseTableColumnName();
			}

//			parameter = parameterRepository.findByCodeAndParameterType(
//					InitiationParameterCodes.INITIATION_FILE_LOADER_LOAD_NBR_SEQUENCE,
//					ParameterType.INCREMENTAL_NUMBER);
//			if (parameter != null && parameter.getLongValue() != null && this.loaderNbrColumn != null) {
//				Optional<IncrementalNumber> result = incrementalNumberRepository.findById(parameter.getLongValue());
//				if (result.isPresent()) {
//					IncrementalNumber number = result.get();
//					this.loadNbr = number.buildNextValue();
//					incrementalNumberRepository.save(number);
//				}
//			}
		} catch (Exception e) {
			log.error("Unable to load loarder settings", e);
			throw e;
		}
	}

	protected void ValidateDatas() throws Exception {

	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveOrUpdateMatGridLog(FileLoaderLog loaderLog) {
		fileLoaderLogRepository.save(loaderLog);
		if(logMatGrid != null) {			
			Object obj = null;
			Query query = null;
			try {
				query = entityManager.createNativeQuery(buildSelectQuery(loaderLog.getId()));
				obj = (Object[])query.getSingleResult();	
			}
			catch (NoResultException e) {
				
			}
			String sql = obj == null ? buildInsertQuery(loaderLog) : buildUpdateQuery(loaderLog);
			log.trace("Log query: {}", sql);
			if(sql != null) {
				query = entityManager.createNativeQuery(sql);
				for(String key : parameters.keySet()) {
					query.setParameter(key, parameters.get(key));
				}
				//query.executeUpdate();
			} 
		}
	}
	
	private String buildSelectQuery(Long loaderLogId) {
		MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
		String sql = "SELECT * FROM " + logMatGrid.getMaterializationTableName() + " WHERE " + matgGridColumn.getDbColumnName() + " = '" + loaderLogId + "' LIMIT 1";
		return sql;
	}
	
	private String buildInsertQuery(FileLoaderLog loaderLog) {
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
				Object value = "" + loaderLog.getId(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NAME);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "" + loaderLog.getLoaderName(); 
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
				Object value = "File loader"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ACTION);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "Load"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = loaderLog.getStatus().name(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = loaderLog.getFileCount(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = loaderLog.getStatus() == RunStatus.ERROR ? 1L : 0L;
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				String value = loaderLog.getMessage(); 
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
	
	private String buildUpdateQuery(FileLoaderLog loaderLog) {
		if(logMatGrid != null) {
			parameters = new HashedMap<>();
			String coma = ",";
			String sql = "UPDATE " + logMatGrid.getMaterializationTableName();
			sql += " SET " + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT).getDbColumnName() + " = :linecount ";
			parameters.put("linecount", loaderLog.getFileCount());
			if(loaderLog.getStatus() == RunStatus.ERROR) {
				sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT).getDbColumnName() + " = :errorcount ";
				parameters.put("errorcount", 1L);
			}
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS).getDbColumnName() + " = :status ";
			parameters.put("status", loaderLog.getStatus().name());
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE).getDbColumnName() + " = :message ";
			parameters.put("message", loaderLog.getMessage());
			
			MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
			sql += " WHERE " + matgGridColumn.getDbColumnName() + " = '" + loaderLog.getId() + "'";
			return sql;
		}
		return null;
	}
	
	private String getParameterName(String col) {		
		return ":" + col;
	}

}
