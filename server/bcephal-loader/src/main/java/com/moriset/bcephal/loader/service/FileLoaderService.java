/**
 * 
 */
package com.moriset.bcephal.loader.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.RightEditorData;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.routine.RoutineExecutor;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderBrowserData;
import com.moriset.bcephal.loader.domain.FileLoaderColumn;
import com.moriset.bcephal.loader.domain.FileLoaderColumnDataBuilder;
import com.moriset.bcephal.loader.domain.FileLoaderEditorData;
import com.moriset.bcephal.loader.domain.FileLoaderNameCondition;
import com.moriset.bcephal.loader.domain.LoaderTemplate;
import com.moriset.bcephal.loader.repository.FileLoaderNameConditionRepository;
import com.moriset.bcephal.loader.repository.FileLoaderRepository;
import com.moriset.bcephal.loader.repository.FileLoaderRepositoryRepository;
import com.moriset.bcephal.loader.repository.LoaderTemplateRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.CharsetDetector;
import com.moriset.bcephal.utils.DataFormater;
import com.moriset.bcephal.utils.ExcelLoader;
import com.moriset.bcephal.utils.FileUtil;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class FileLoaderService extends MainObjectService<FileLoader, FileLoaderBrowserData> {

	@Autowired
	FileLoaderRepository loaderRepository;
	
	@Autowired
	FileLoaderRepositoryRepository loaderRepositoryRepository;

	@Autowired
	GrilleService grilleService;

	@Autowired
	FileLoaderNameConditionRepository conditionRepository;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;

	@Autowired
	FileLoaderColumnService fileLoaderColumnService;
	
	@Autowired
	LoaderTemplateRepository loaderTemplateRepository;

	@Autowired
	InitiationService initiationService;
	

	@Autowired
	MaterializedGridService materializedGridService;

	@Autowired
	ParameterRepository parameterRepository;
	
	@Value("${bcepahl.date.formats}")
	List<String> dateFormats;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_FILE_LOADER;
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
	public FileLoaderRepository getRepository() {
		return loaderRepository;
	}

	@Override
	public FileLoaderEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		FileLoaderEditorData data = new FileLoaderEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		data.setModels(initiationService.getModels(session, locale));
		data.setPeriods(initiationService.getPeriods(session, locale));
		data.setMeasures(initiationService.getMeasures(session, locale));
		data.setCalendarCategories(initiationService.getCalendarsAsNameable(session, locale));
		data.setSpots(initiationService.getSpotsAsNameable(session, locale));
		data.setTemplates(loaderTemplateRepository.findAllAsNameables());
		//BrowserDataFilter dataFilter = new BrowserDataFilter();
		//dataFilter.setShowAll(true);
		//dataFilter.setPageSize(Integer.MAX_VALUE);
		//Long profileId = (Long)session.getAttribute(RequestParams.BC_PROFILE);
		//String projectCode = (String)session.getAttribute(RequestParams.BC_PROJECT);
		//data.setGrids(grilleService.search(dataFilter, locale, profileId, projectCode).getItems());
		//data.setMaterializedGrids(materializedGridService.search(dataFilter, locale, profileId, projectCode).getItems());
		List<Object[]> routines = loaderRepository.findRoutines();
		for(Object[] objs : routines) {
			data.getRoutines().add(new Nameable(((Number)objs[0]).longValue(), (String)objs[1]) );
		}	
		if(data.getItem() != null && !data.getItem().isPersistent()) {
			SimpleDateFormat format=new SimpleDateFormat();
			data.getItem().setDateFormat(format.toLocalizedPattern());
			log.info("DateFormat Localized Pattern : {}, parttern {}",format.toLocalizedPattern(),format.toPattern());
		}
		data.setDateFormats(this.dateFormats);
		return data;
	}
	
	@Override
	public RightEditorData<FileLoader> getRightLowLevelEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		RightEditorData<FileLoader> data = super.getRightLowLevelEditorData(filter, session, locale);
		if(filter != null && StringUtils.hasText(filter.getSubjectType())) {
			data.setItems(loaderRepository.findAllAsNameables());
		}
		return data;
	}
	
	@Override
	public FileLoader getById(Long id) {
		FileLoader loader = super.getById(id);
		if(loader != null && loader.getId() != null) {
			loader.getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(loader.getId(), FileLoader.class.getName()));
		}
		return loader;
	}

	@Override
	protected FileLoader getNewItem() {
		FileLoader loader = new FileLoader();
		String baseName = "Loader ";
		int i = 1;
		loader.setName(baseName + i);
		while (getByName(loader.getName()) != null) {
			i++;
			loader.setName(baseName + i);
		}
		return loader;
	}

	@Override
	@Transactional
	public FileLoader save(FileLoader loader, Locale locale) {
		log.debug("Try to save FileLoader : {}", loader);
		try {
			if (loader == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.file.loader",
						new Object[] { loader }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(loader.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.file.loader.with.empty.name",
						new String[] { loader.getName() }, locale);
				throw new BcephalException(message);
			}

			ListChangeHandler<com.moriset.bcephal.loader.domain.FileLoaderRepository> repositories = loader.getRepositoryListChangeHandler();
			ListChangeHandler<RoutineExecutor> routines = loader.getRoutineListChangeHandler();

			ListChangeHandler<FileLoaderColumn> columns = loader.getColumnListChangeHandler();
			loader.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(loader, locale);
			loader = loaderRepository.save(loader);
			FileLoader id = loader;

			saveColumns(columns, locale, id);

			repositories.getNewItems().forEach(item -> {
				log.trace("Try to save repository : {}", item);
				item.setLoaderId(id);
				save(item);
				log.trace("repository saved : {}", item.getId());
			});
			repositories.getUpdatedItems().forEach(item -> {
				log.trace("Try to save repository : {}", item);
				item.setLoaderId(id);
				save(item);
				log.trace("repository saved : {}", item.getId());
			});
			repositories.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete repository : {}", item);
					delete(item);
					log.trace("repository deleted : {}", item.getId());
				}
			});
			
			routines.getNewItems().forEach( item -> {
				log.trace("Try to save Routine executor : {}", item);
				item.setObjectId(id.getId());
				item.setObjectType(FileLoader.class.getName());
				routineExecutorReopository.save(item);
				log.trace("Rroutine executor saved : {}", item.getId());
			});
			routines.getUpdatedItems().forEach( item -> {
				log.trace("Try to save Routine executor : {}", item);
				item.setObjectId(id.getId());
				item.setObjectType(FileLoader.class.getName());
				routineExecutorReopository.save(item);
				log.trace("RRroutine executor saved : {}", item.getId());
			});
			routines.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete Rroutine executor : {}", item);
					routineExecutorReopository.deleteById(item.getId());
					log.trace("Rroutine executor deleted : {}", item.getId());
				}
			});
			
			log.debug("FileLoader saved : {} ", loader.getId());
			return loader;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save FileLoader : {}", loader, e);
			String message = getMessageSource().getMessage("unable.to.save.file.loader", new Object[] { loader },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	public com.moriset.bcephal.loader.domain.FileLoaderRepository save(com.moriset.bcephal.loader.domain.FileLoaderRepository repository) {
		ListChangeHandler<FileLoaderNameCondition> conditions = repository.getConditionListChangeHandler();
		repository = loaderRepositoryRepository.save(repository);
		com.moriset.bcephal.loader.domain.FileLoaderRepository id = repository;
		conditions.getNewItems().forEach(item -> {
			log.trace("Try to save condition : {}", item);
			item.setLoader(id);
			conditionRepository.save(item);
			log.trace("condition saved : {}", item.getId());
		});
		conditions.getUpdatedItems().forEach(item -> {
			log.trace("Try to save condition : {}", item);
			item.setLoader(id);
			conditionRepository.save(item);
			log.trace("condition saved : {}", item.getId());
		});
		conditions.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete condition : {}", item);
				conditionRepository.deleteById(item.getId());
				log.trace("condition deleted : {}", item.getId());
			}
		});
		return repository;
	}
	
	public void delete(com.moriset.bcephal.loader.domain.FileLoaderRepository repository) {		
		if (repository == null || repository.getId() == null) {
			return;
		}
//		ListChangeHandler<FileLoaderNameCondition> conditions = repository.getConditionListChangeHandler();
//		conditions.getItems().forEach(item -> {
//			if (item.getId() != null) {
//				log.trace("Try to delete condition : {}", item);
//				conditionRepository.deleteById(item.getId());
//				log.trace("condition deleted : {}", item.getId());
//			}
//		});
//		conditions.getDeletedItems().forEach(item -> {
//			if (item.getId() != null) {
//				log.trace("Try to delete condition : {}", item);
//				conditionRepository.deleteById(item.getId());
//				log.trace("condition deleted : {}", item.getId());
//			}
//		});
		
		conditionRepository.deleteByLoader(repository.getId());
		
		loaderRepositoryRepository.deleteById(repository.getId());
	}

	private String getFileNameWithoutExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		String extension = "";
		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			extension = fileName.substring(index);
		}
		if (StringUtils.hasText(extension)) {
			return fileName.replace(extension, "");
		}
		return extension;
	}
	
	@Transactional
	public MaterializedGrid saveNewMaterializedGrid(FileLoader loader, String fileName, HttpSession session, Locale locale) throws Exception {
		log.debug("Try to create new materialized grid...");
		ListChangeHandler<FileLoaderColumn> columns = loader.getColumnListChangeHandler();
		String gridName = loader.getTargetName();
		if (!StringUtils.hasText(gridName)) {
			gridName = getFileNameWithoutExtension(fileName);
		}
		MaterializedGrid grid = materializedGridService.buildNewGrid(gridName, locale);	
		grid.setGroup(getDefaultGroup());
		List<MaterializedGridColumn> sysCols = grid.getColumnListChangeHandler().getItems();
		grid.getColumnListChangeHandler().setOriginalList(new ArrayList<>());
				
		for (FileLoaderColumn column : columns.getItems()) {
			log.trace("Try to Create column : Position = {} - Name= {}", column.getPosition(), column.getFileColumn());
			if (column.getType() == null) {
				if (!StringUtils.hasText(column.getFileColumn())) {
					throw new BcephalException("Empty file column type : " + column.getPosition());
				}				
			} 
			MaterializedGridColumn gridColumn = new MaterializedGridColumn(GrilleColumnCategory.USER, column.getType(), column.getFileColumn(), column.getPosition());			
			grid.getColumnListChangeHandler().addNew(gridColumn);
		}
		
		int position = grid.getColumnListChangeHandler().getItems().size();
		for (MaterializedGridColumn column : sysCols) {
			column.setPosition(position++);		
			grid.getColumnListChangeHandler().addNew(column);
		}
				
//		validateGrid(grid);
		grid = materializedGridService.save(grid, locale);
		loader.setTargetId(grid.getId());
		grid = materializedGridService.performPublication(grid);
//		for (FileLoaderColumn column : columns.getItems()) {
//			column.setGrilleColumn(grid.getColumnAt(column.getPosition()));
//			loader.getColumnListChangeHandler().addUpdated(column);
//		}
		loader = save(loader, locale);

		log.debug("Materialized grid created : OID = {} - NAME = {}", grid.getId(), grid.getName());
		return grid;
	}

	@Transactional
	public Grille saveNewGrid(FileLoader loader, String fileName, HttpSession session, Locale locale) throws Exception {
		log.debug("Try to create new input grid...");
		ListChangeHandler<FileLoaderColumn> columns = loader.getColumnListChangeHandler();
		String gridName = loader.getTargetName();
		if (!StringUtils.hasText(gridName)) {
			gridName = getFileNameWithoutExtension(fileName);
		}
		Grille grid = grilleService.buildNewGridName(gridName, locale);
		grid.setGroup(getDefaultGroup());
		Parameter parameter = parameterRepository
				.findByCodeAndParameterType(InitiationParameterCodes.INITIATION_DEFAULT_ENTITY, ParameterType.ENTITY);
		Long defaultEntityId = null;
		if (parameter != null && parameter.getLongValue() != null) {
			//defaultEntityId = parameter.getLongValue();
		}
		for (FileLoaderColumn column : columns.getItems()) {
			log.trace("Try to Create column : Position = {} - Name= {}", column.getPosition(), column.getFileColumn());
			Dimension dimension = null;
			if (column.getDimensionId() == null) {
				if (!StringUtils.hasText(column.getFileColumn())) {
					throw new BcephalException("Empty file column name : " + column.getPosition());
				}
				dimension = initiationService.getDimension(column.getType(), column.getFileColumn(), false, session,
						locale);
				if (dimension == null) {
					if (defaultEntityId == null) {
						//defaultEntityId = createDefaultEntity(gridName, session, locale);
						Long modelId = createDefaultModel(session, locale);
						String name = getFileNameWithoutExtension(fileName);
						Entity entity = initiationService.getEntity(name, session, locale);
						if(entity == null) {
							defaultEntityId = initiationService.createEntity(gridName, modelId, session, locale);
						}
						else {
							defaultEntityId = entity.getId();
						}
					}
					dimension = initiationService.createDimension(column.getType(), column.getFileColumn(),
							defaultEntityId, session, locale);
				}
				if (dimension == null) {
					throw new BcephalException("Unable to create {} dimension : {}", column.getType().name(),
							column.getFileColumn());
				}
				column.setDimensionId((long) dimension.getId());
				loader.getColumnListChangeHandler().addUpdated(column);
			} else {
				dimension = initiationService.getDimension(column.getType(), column.getDimensionId(), session,
						locale);
				if (dimension == null) {
					throw new BcephalException("Unknown dimension. Type {}  - ID : {}", column.getType().name(),
							column.getDimensionId());
				}
			}
			GrilleColumn gridColumn = new GrilleColumn(null, column.getFileColumn(), column.getType(),
					column.getDimensionId(), dimension.getName());
			gridColumn.setPosition(column.getPosition());
			grid.getColumnListChangeHandler().addNew(gridColumn);
		}
		validateGrid(grid);
		grid = grilleService.save(grid, locale);
		loader.setTargetId(grid.getId());
		for (FileLoaderColumn column : columns.getItems()) {
			column.setGrilleColumn(grid.getColumnAt(column.getPosition()));
			loader.getColumnListChangeHandler().addUpdated(column);
		}
		loader = save(loader, locale);

		log.debug("Input grid created : OID = {} - NAME = {}", grid.getId(), grid.getName());
		return grid;
	}

	private Long createDefaultModel(HttpSession session, Locale locale) throws Exception {
		Parameter parameter = parameterRepository
				.findByCodeAndParameterType(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, ParameterType.MODEL);
		Long modelId = parameter != null ? parameter.getLongValue() : null;
		if (modelId == null) {
			modelId = initiationService.createModel("Default model", session, locale);
			if (parameter == null) {
				parameter = new Parameter(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, ParameterType.MODEL);
			}
			parameter.setLongValue(modelId);
			parameterRepository.save(parameter);
		}		
		return modelId;
	}
	
	@SuppressWarnings("unused")
	private Long createDefaultEntity(String gridName, HttpSession session, Locale locale) throws Exception {
		Long modelId = createDefaultModel(session, locale);
		Long entityId = initiationService.createEntity(gridName, modelId, session, locale);
		Parameter parameter = parameterRepository.findByCodeAndParameterType(InitiationParameterCodes.INITIATION_DEFAULT_ENTITY,
				ParameterType.ENTITY);
		if (parameter == null) {
			parameter = new Parameter(InitiationParameterCodes.INITIATION_DEFAULT_ENTITY, ParameterType.ENTITY);
		}
		parameter.setLongValue(entityId);
		parameterRepository.save(parameter);
		return entityId;
	}

	private void validateGrid(Grille grid) throws Exception {
		for (GrilleColumn column : grid.getColumnListChangeHandler().getItems()) {
			List<GrilleColumn> columns = grid.getColumns(column.getType(), column.getDimensionId());
			if (columns.size() > 1) {
				throw new BcephalException("Duplicate columns : Dimension = {} ", column.getDimensionName());
			}
		}
	}

	private void saveColumns(ListChangeHandler<FileLoaderColumn> columns, Locale locale, FileLoader id) {
		columns.getNewItems().forEach(item -> {
			log.trace("Try to save columns : {}", item);
			item.setLoader(id);
			fileLoaderColumnService.save(item, locale);
			log.trace("columns saved : {}", item.getId());
		});
		columns.getUpdatedItems().forEach(item -> {
			log.trace("Try to save columns : {}", item);
			item.setLoader(id);
			fileLoaderColumnService.save(item, locale);
			log.trace("columns saved : {}", item.getId());
		});
		columns.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete columns : {}", item);
				fileLoaderColumnService.deleteById(item.getId());
				log.trace("columns deleted : {}", item.getId());
			}
		});
	}

	@Override
	public void delete(FileLoader loader) {
		log.debug("Try to delete FileLoader : {}", loader);
		if (loader == null || loader.getId() == null) {
			return;
		}

		ListChangeHandler<com.moriset.bcephal.loader.domain.FileLoaderRepository> repositories = loader.getRepositoryListChangeHandler();
		repositories.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete condition : {}", item);
				delete(item);
				log.trace("condition deleted : {}", item.getId());
			}
		});
		repositories.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete condition : {}", item);
				delete(item);
				log.trace("condition deleted : {}", item.getId());
			}
		});
		
		ListChangeHandler<RoutineExecutor> routines = loader.getRoutineListChangeHandler();
		routines.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete Routine Executor : {}", item);
				routineExecutorReopository.deleteById(item.getId());
				log.trace("Routine Executor deleted : {}", item.getId());
			}
		});
		routines.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete Routine Executor : {}", item);
				routineExecutorReopository.deleteById(item.getId());
				log.trace("Routine Executor deleted : {}", item.getId());
			}
		});

		ListChangeHandler<FileLoaderColumn> columns = loader.getColumnListChangeHandler();
		columns.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete file Loader Column : {}", item);
				fileLoaderColumnService.deleteById(item.getId());
				log.trace("condition deleted : {}", item.getId());
			}
		});
		columns.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete file Loader Column : {}", item);
				fileLoaderColumnService.deleteById(item.getId());
				log.trace("condition deleted : {}", item.getId());
			}
		});

		loaderRepository.deleteById(loader.getId());
		log.debug("FileLoader successfully to delete : {} ", loader);
		return;
	}

	@Override
	protected Specification<FileLoader> getBrowserDatasSpecification(BrowserDataFilter filter, java.util.Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<FileLoader> qBuilder = new RequestQueryBuilder<FileLoader>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
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

	@Override
	protected FileLoaderBrowserData getNewBrowserData(FileLoader item) {
		return new FileLoaderBrowserData(item);
	}

	public List<FileLoaderColumn> buildFileLoaderColumns(FileLoaderColumnDataBuilder data, String path, Locale locale,
			HttpSession session) throws Exception {
		if (data.getFileType() != null && data.getFileType().isExcel()) {
			return buildFileLoaderColumnsExecel(data, path, locale, session);
		} 
		else if (data.getFileType() != null && data.getFileType().isXml()) {
			return buildFileLoaderColumnsXml(data, path, locale, session);
		}		
		else {
			return buildFileLoaderColumnsCsv(data, path, locale, session);
		}
	}

	private List<FileLoaderColumn> buildFileLoaderColumnsXml(FileLoaderColumnDataBuilder data, String filePath, Locale locale,
			HttpSession session) {		
		String path = FilenameUtils.getFullPath(filePath);
		String fileName = FilenameUtils.getBaseName(filePath) + ".csv";
		String outputFile = FilenameUtils.concat(path, fileName);		
		try {
			if(data.getTemplateId() == null) {
				throw new BcephalException("Loader template is NULL!");
			}
			Optional<LoaderTemplate> response = loaderTemplateRepository.findById(data.getTemplateId());
			if(response.isEmpty()) {
				throw new BcephalException("Loader template not found : " + data.getTemplateId());
			}
			LoaderTemplate template = response.get();
			if(!StringUtils.hasText(template.getRepository())) {
				throw new BcephalException("Loader template repository is NULL : " + template.getName());
			}
			String templatePath = template.getRepository();
			if(!new File(templatePath).exists()) {
				throw new BcephalException("Loader template file not found : " + templatePath);
			}			
			XmlToCsvUtils.generateCsv(templatePath, filePath, outputFile);			
			return buildFileLoaderColumnsCsv(data, outputFile, locale, session);
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
	}

	private List<FileLoaderColumn> buildFileLoaderColumnsCsv(FileLoaderColumnDataBuilder data, String path,
			Locale locale, HttpSession session) {
		List<FileLoaderColumn> columns = new ArrayList<>();
		Grille grid = null;
		if (data.getGridId() != null && data.getGridId() > 0) {
			grid = grilleService.getById(data.getGridId());
			if (grid == null) {
				throw new BcephalException("Input grid not found: " + data.getGridId());
			}
		}
		String errorMessage = "";	
        Charset charset = new CharsetDetector().detectCharset(new File(path));
        if(charset == null) {
        	charset = StandardCharsets.UTF_8;
        }
		try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),charset));) {
			
			char delimiter = ';';
			if (StringUtils.hasText(data.getSeparator())) {
				String val = data.getSeparator().trim();
				delimiter = val.charAt(0);
			}
			CSVFormat csvFormat = null;
			CSVParser csvParser = null;

			if (data.isHasHeader()) {
				csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).setHeader().setSkipHeaderRecord(true)
						.build();
				try {
					csvParser = csvFormat.parse(reader);
				} catch (Exception e) {
					log.error("", e);
					try {
						errorMessage += " " + e.getMessage();
						csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).build();
						csvParser = csvFormat.parse(reader);
					} catch (Exception e2) {
						
						throw new BcephalException("Input file error: " + e2.getMessage());
					}
				}
			} else {
				try {
					csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(delimiter).build();
					csvParser = csvFormat.parse(reader);
				} catch (Exception e2) {
					throw new BcephalException("Input file error: " + e2.getMessage());
				}
			}

			Map<String, Integer> header = null;
			List<String> headerList = null;
			long count = 0;
			if (data.isHasHeader()) {
				header = csvParser.getHeaderMap();
				if (header == null) {
					reader.close();
					throw new BcephalException("Error to read header from file {} " + path + "\n " + errorMessage);
				}
				headerList = csvParser.getHeaderNames();
				//count = header.size();
				count = headerList.size();
			}
			Iterable<CSVRecord> records = csvParser;
			Iterator<CSVRecord> iterator = records.iterator();

			if (!iterator.hasNext()) {
				reader.close();
				errorMessage += "Empty File: ";
				throw new BcephalException("Empty File: " + path + "\n " + errorMessage);
			}
			CSVRecord record = iterator.next();
			if (!data.isHasHeader()) {
				count = record.size();
			}
			int maxLineToCheck = 50;
			int current = 0;
			List<CSVRecord> items = new ArrayList<>(0);			
			while (iterator.hasNext() && current < maxLineToCheck) {
				CSVRecord record2 = iterator.next();
				items.add(record2);
				current++;
			}
			for (int i = 0; i < count; i++) {
				FileLoaderColumn column = new FileLoaderColumn();
				column.setPosition(i);
				DimensionType type = getDimensionType(i, items, data);
				column.setType(type);
				if (data.isHasHeader()) {
					String value = headerList.get(i);
					String name = value != null ? value.toString() : DataFormater.getColumnName(i);
					column.setFileColumn(name);
					if (grid == null) {
						column.setDimensionId(getDimensionId(type, name, session, locale));
					}
				} else {
					String name = DataFormater.getColumnName(i + 1);
					column.setFileColumn(name);
				}

				if (grid != null) {
					GrilleColumn gridColumn = grid.getColumnAt(i);
					if (gridColumn != null && gridColumn.getType() != type) {
						throw new BcephalException("Wrong column type : " + column.getFileColumn() + "\n- Grid type : "
								+ gridColumn.getType().name() + "\n- File type : " + type.name());
					}
					column.setGrilleColumn(gridColumn);
					column.setDimensionId(gridColumn.getDimensionId());
				}
				columns.add(column);
			}
			reader.close();
		} catch (Exception e) {
			log.error("", e);
			throw new BcephalException("Can not open file: " + path + "\n " + errorMessage);
		}

		return columns;
	}

	private List<FileLoaderColumn> buildFileLoaderColumnsExecel(FileLoaderColumnDataBuilder data, String path,
			Locale locale, HttpSession session) throws Exception {
		List<FileLoaderColumn> columns = new ArrayList<>();

		Grille grid = null;
		if (data.getGridId() != null && data.getGridId() > 0) {
			grid = grilleService.getById(data.getGridId());
			if (grid == null) {
				throw new BcephalException("Input grid not found: " + data.getGridId());
			}
		}

		ExcelLoader excelLoader = new ExcelLoader();
		excelLoader.excelFile = path;
		excelLoader.loadWorkbookInReadMode();
		Sheet sheet = excelLoader.getSheet(data.getSheetName());
		if (sheet == null) {
			sheet = excelLoader.getSheet(data.getSheetIndex());
			if (sheet == null) {
				throw new BcephalException("Sheet not found: " + data.getSheetName());
			}
		}

		if (sheet.getLastRowNum() <= 0) {
			throw new BcephalException("Empty sheet: " + data.getSheetName());
		}

		Row firtRow = sheet.getRow(0);
		int count = firtRow.getLastCellNum();

		for (int i = 0; i < count; i++) {
			FileLoaderColumn column = new FileLoaderColumn();
			column.setPosition(i);
			int headerRowCount = data.isHasHeader() ? data.getHeaderRowcount() : 0;
			DimensionType type = getDimensionType(i, headerRowCount, sheet, excelLoader);
			column.setType(type);
			if (data.isHasHeader()) {
				Object value = excelLoader.getCellValue(firtRow.getCell(i));
				String name = value != null ? value.toString() : DataFormater.getColumnName(i);
				column.setFileColumn(name);
				if (grid == null) {
					column.setDimensionId(getDimensionId(type, name, session, locale));
				}
			} else {
				String name = DataFormater.getColumnName(i);
				column.setFileColumn(name);
			}

			if (grid != null) {
				GrilleColumn gridColumn = grid.getColumnAt(i);
				if (gridColumn != null && gridColumn.getType() != type) {
					throw new BcephalException("Wrong column type : " + column.getFileColumn() + "\n- Grid type : "
							+ gridColumn.getType().name() + "\n- File type : " + type.name());
				}
				column.setGrilleColumn(gridColumn);
				column.setDimensionId(gridColumn.getDimensionId());
			}

			columns.add(column);
		}

		return columns;
	}

	private Long getDimensionId(DimensionType type, String name, HttpSession session, Locale locale) {
		Dimension dimension = initiationService.getDimension(type, name, false, session, locale);
		return dimension != null ? (Long) dimension.getId() : null;
	}

	private DimensionType getDimensionType(int col, int headerRowCount, Sheet sheet, ExcelLoader excelLoader) {
		if (sheet.getLastRowNum() > headerRowCount) {
			Row row = sheet.getRow(headerRowCount);
			Cell cell = row.getCell(col);
			Object value = excelLoader.getCellValue(cell);
			if (value != null && value instanceof Date) {
				return DimensionType.PERIOD;
			} else if (value != null && (value instanceof Double || value instanceof Number)) {
				return DimensionType.MEASURE;
			}
		}
		return DimensionType.ATTRIBUTE;
	}
	
	private DimensionType getDimensionType(int col, List<CSVRecord> records, FileLoaderColumnDataBuilder data) {		
		
		DimensionType type = DimensionType.ATTRIBUTE;
		int current = 0;
		while (current < records.size()) {
			CSVRecord record = records.get(current);
			DimensionType dt = getDimensionType(col, record);
			if(type == null) {
				type = dt;
			}
			if(dt.isAttribute()) {
				type = dt;
				break;
			}
			if((dt.isMeasure() && type.isPeriod()) || (dt.isPeriod() && type.isMeasure())) {
				type = DimensionType.ATTRIBUTE;
				break;
			}
			type = dt;
			current++;
		}		
		return type;
	}

	private DimensionType getDimensionType(int col, CSVRecord record) {
		if (record != null && record.size() > col && record.getRecordNumber() > 0) {
			String value = record.get(col);
			if (isDate(value)) {
				return DimensionType.PERIOD;
			} else if (isNumber(value)) {
				return DimensionType.MEASURE;
			}
		}
		return DimensionType.ATTRIBUTE;
	}

	private boolean isDate(String value) {
		String[] format = { "yyyy/MM/dd", "dd/MM/yyyy", "MM/dd/yyyy", "yy/MM/dd", "dd/MM/yy", "MM/dd/yy", "yyyy-MM-dd",
				"dd-MM-yyyy", "MM-dd-yyyy", "yy-MM-dd", "dd-MM-yy", "MM-dd-yy", };
		for (String val : format) {
			try {
				new SimpleDateFormat(val).parse(value);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	private boolean isNumber(String value) {
		String regx = "-?\\d+(\\.\\d+)?";
		boolean ok = Pattern.compile(regx).matcher(value).matches();
		return ok;
	}

}
