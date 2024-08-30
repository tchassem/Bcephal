package com.moriset.bcephal.scheduler.service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.alarm.repository.AlarmRepository;
import com.moriset.bcephal.billing.repository.BillingModelRepository;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.JoinPublicationMethod;
import com.moriset.bcephal.grid.domain.VariableInterval;
import com.moriset.bcephal.grid.domain.VariableReference;
import com.moriset.bcephal.grid.domain.VariableReferenceCondition;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.JoinRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartJoinRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.repository.VariableIntervalRepository;
import com.moriset.bcephal.grid.repository.VariableReferenceConditionRepository;
import com.moriset.bcephal.grid.repository.VariableReferenceRepository;
import com.moriset.bcephal.integration.repository.PontoConnectEntityRepository;
import com.moriset.bcephal.loader.repository.FileLoaderRepository;
import com.moriset.bcephal.reconciliation.repository.AutoRecoRepository;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.scheduler.domain.PresentationVariables;
import com.moriset.bcephal.scheduler.domain.SchedulerBrowserData;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerBrowserData;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerEditorData;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItem;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemLoop;
import com.moriset.bcephal.scheduler.repository.PresentationTemplateRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerItemLoopRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerItemRepository;
import com.moriset.bcephal.scheduler.repository.SchedulerPlannerRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.task.domain.TaskCategory;
import com.moriset.bcephal.task.repository.TaskRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchedulerPlannerService extends MainObjectService<SchedulerPlanner, SchedulerPlannerBrowserData>{

	@Autowired
	SchedulerPlannerRepository schedulerPlannerRepository;
	
	@Autowired
	SchedulerPlannerItemRepository schedulerPlannerItemRepository;
	
	@Autowired
	SchedulerPlannerItemLoopRepository schedulerPlannerItemLoopRepository;
	
	@Autowired
	VariableReferenceRepository variableReferenceRepository;
	
	@Autowired
	VariableReferenceConditionRepository variableReferenceConditionRepository;
	
	@Autowired
	VariableIntervalRepository variableIntervalRepository;
	
	@Autowired
	PresentationTemplateRepository presentationTemplateRepository;
	
	@Autowired
	JoinRepository joinRepository;
	
	@Autowired
	SpotRepository spotRepository;
	
	@Autowired
	AutoRecoRepository autoRecoRepository;
	
	@Autowired
	AlarmRepository alarmRepository;
	
	@Autowired
	FileLoaderRepository fileLoaderRepository;
	
	@Autowired
	BillingModelRepository billingModelRepository;
	
	@Autowired
	MaterializedGridRepository materializedGridRepository;
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	TaskRepository taskRepository;

	@Autowired
	SecurityService securityService;
	
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	PontoConnectEntityRepository pontoConnectEntityRepository;
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired
	SmartJoinRepository smartJoinRepository;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@Autowired
	protected SchedulerManager manager;
	
	@Override
	public SchedulerPlannerRepository getRepository() {
		return schedulerPlannerRepository;
	}
	
	@Override
	public SchedulerPlannerEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<SchedulerPlanner> superdata = super.getEditorData(filter, session, locale);
		SchedulerPlannerEditorData data = new SchedulerPlannerEditorData(superdata);	
		
		List<Object[]> routines = joinRepository.findRoutines();
		for(Object[] objs : routines) {
			data.getRoutines().add(new Nameable(((Number)objs[0]).longValue(), (String)objs[1]) );
		}
		
		data.variables = new PresentationVariables().getAll();
		
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		List<Long> hightGridItems = getHidedObjectId(profileId, FunctionalityCodes.REPORTING_REPORT_JOIN_GRID, projectCode);
		List<Long> hightReportGridItems = getHidedObjectId(profileId, FunctionalityCodes.REPORTING_REPORT_GRID, projectCode);
		List<Long> hightInputGridItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_INPUT_GRID, projectCode);
		List<Long> hightMatGridItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);
		List<Long> hightSpotItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_SPOT, projectCode);
		List<Long> hightAutoRecoItems = getHidedObjectId(profileId, FunctionalityCodes.RECONCILIATION_AUTO_RECO, projectCode);
		List<Long> hightAlarmItems = getHidedObjectId(profileId, FunctionalityCodes.DASHBOARDING_ALARM, projectCode);
		List<Long> hightFileLoaderItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_FILE_LOADER, projectCode);
		List<Long> hightBillingModelItems = getHidedObjectId(profileId, FunctionalityCodes.BILLING_MODEL, projectCode);
		List<Long> hightIntegrationItems = getHidedObjectId(profileId, FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE, projectCode);
		List<Long> hightPresentationTemplateItems = getHidedObjectId(profileId, FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION_TEMPLATE, projectCode);
		
		List<GrilleType> types = new ArrayList<>();
		types.add(GrilleType.INPUT);
		types.add(GrilleType.REPORT);
		
		if(hightReportGridItems != null && hightReportGridItems.size() > 0) {
			data.getGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightReportGridItems));
		}else {
			data.getGrids().addAll(smartGrilleRepository.findByTypes(types));
		}		
		if(hightInputGridItems != null && hightInputGridItems.size() > 0) {
			data.getGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightInputGridItems));
		}		
		if(hightMatGridItems != null && hightMatGridItems.size() > 0) {
			data.getGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMatGridItems));
		}else {
			data.getGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		
		
		if(hightGridItems != null && hightGridItems.size() > 0) {
			data.setJoins(joinRepository.findAllAsNameablesExcludeIds(hightGridItems));
		}else {
			data.setJoins(joinRepository.findAllAsNameables());
		}
		if(hightMatGridItems != null && hightMatGridItems.size() > 0) {
			data.setMatGrids(materializedGridRepository.findAllAsNameablesExcludeIds(hightMatGridItems));
		}else {
			data.setMatGrids(materializedGridRepository.findAllAsNameables());
		}
		if(hightReportGridItems != null && hightReportGridItems.size() > 0) {
			data.setReportGrids(grilleRepository.findByTypeExclude(GrilleType.REPORT, hightReportGridItems));
		}else {
			data.setReportGrids(grilleRepository.findByType(GrilleType.REPORT));
		}
		if(hightInputGridItems != null && hightInputGridItems.size() > 0) {
			data.setInputGrids(grilleRepository.findByTypeExclude(GrilleType.INPUT, hightInputGridItems));
		}else {
			data.setInputGrids(grilleRepository.findByType(GrilleType.INPUT));
		}
		if(hightSpotItems != null && hightSpotItems.size() > 0) {
			data.setSpots(spotRepository.findAllAsNameablesExcludeIds(hightSpotItems));
		}else {
			data.setSpots(spotRepository.findAllAsNameables());
		}
		if(hightAutoRecoItems != null && hightAutoRecoItems.size() > 0) {
			data.setRecos(autoRecoRepository.findAllAsNameablesExcludeIds(hightAutoRecoItems));
		}else {
			data.setRecos(autoRecoRepository.findAllAsNameables());
		}
		if(hightAlarmItems != null && hightAlarmItems.size() > 0) {
			data.setAlarms(alarmRepository.findAllAsNameablesExcludeIds(hightAlarmItems));
		}else {
			data.setAlarms(alarmRepository.findAllAsNameables());
		}
		if(hightFileLoaderItems != null && hightFileLoaderItems.size() > 0) {
			data.setFileLoaders(fileLoaderRepository.findAllAsNameablesExcludeIds(hightFileLoaderItems));
		}else {
			data.setFileLoaders(fileLoaderRepository.findAllAsNameables());
		}
		if(hightBillingModelItems != null && hightBillingModelItems.size() > 0) {
			data.setBillings(billingModelRepository.findAllAsNameablesExcludeIds(hightBillingModelItems));
		}else {
			data.setBillings(billingModelRepository.findAllAsNameables());
		}
		if(hightIntegrationItems != null && hightIntegrationItems.size() > 0) {
			data.setIntegrations(pontoConnectEntityRepository.findAllAsNameablesExcludeIds(hightIntegrationItems));
		}else {
			data.setIntegrations(pontoConnectEntityRepository.findAllAsNameables());
		}
		if(hightPresentationTemplateItems != null && hightPresentationTemplateItems.size() > 0) {
			data.setPresentationTemplates(presentationTemplateRepository.findAllAsNameablesExcludeIds(hightPresentationTemplateItems));
		}else {
			data.setPresentationTemplates(presentationTemplateRepository.findAllAsNameables());
		}
		data.setTasks(taskRepository.findAllAsNameablesByCategory(TaskCategory.SCHEDULED));
		
		return data;
	}

	@Override
	protected SchedulerPlanner getNewItem() {
		SchedulerPlanner schedulerPlanner = new SchedulerPlanner();
		String baseName = "Scheduler ";
		int i = 1;
		schedulerPlanner.setName(baseName + i);
		while (getByName(schedulerPlanner.getName()) != null) {
			i++;
			schedulerPlanner.setName(baseName + i);
		}		
		return schedulerPlanner;
	}
	
	@Override
	public BrowserDataPage<SchedulerPlannerBrowserData> search(BrowserDataFilter filter, java.util.Locale locale, Long profileId,String projectCode) {
		BrowserDataPage<SchedulerPlannerBrowserData> page = super.search(filter, locale, profileId, projectCode);
		for(SchedulerPlannerBrowserData browserData : page.getItems()) {
			SchedulerBrowserData data = manager.getScheduledFutureRepository().getByProjectAndId(projectCode, browserData.getId());
			if(data != null && data.getFuture() != null) {
//				var state = data.getFuture().state();
//				if(state != null) {
//					browserData.setState(state.toString());
//				}
			}
		}
		return page;		
	}

	@Override
	protected SchedulerPlannerBrowserData getNewBrowserData(SchedulerPlanner item) {
		SchedulerPlannerBrowserData browserData = new SchedulerPlannerBrowserData(item);
		return browserData;
	}

	@Override
	protected Specification<SchedulerPlanner> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SchedulerPlanner> qBuilder = new RequestQueryBuilder<SchedulerPlanner>(root, query, cb);
			qBuilder.select(SchedulerPlanner.class);			
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
	
	protected void build(ColumnFilter columnFilter) {
		super.build(columnFilter);
		if ("active".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("active");
			columnFilter.setType(Boolean.class);
		} else if ("scheduled".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("scheduled");
			columnFilter.setType(Boolean.class);
		} else if ("visibleInShortcut".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("visibleInShortcut");
			columnFilter.setType(Boolean.class);
		} else if ("cronExpression".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("cronExpression");
			columnFilter.setType(String.class);
		} 
		
		else if ("showAllRowsByDefault".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("showAllRowsByDefault");
			columnFilter.setType(Boolean.class);
		} else if ("allowLineCounting".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("allowLineCounting");
			columnFilter.setType(Boolean.class);
		} else if ("consolidated".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("consolidated");
			columnFilter.setType(Boolean.class);
		} else if ("refreshGridsBeforePublication".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("refreshGridsBeforePublication");
			columnFilter.setType(Boolean.class);
		} else if ("addPublicationRunNbr".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("addPublicationRunNbr");
			columnFilter.setType(Boolean.class);
		} else if ("publicationRunAttributeId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationRunAttributeId");
			columnFilter.setType(Long.class);
		} else if ("publicationRunSequenceId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationRunSequenceId");
			columnFilter.setType(Long.class);
		} else if ("publicationMethod".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationMethod");
			columnFilter.setType(JoinPublicationMethod.class);
		} else if ("publicationGridId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationGridId");
			columnFilter.setType(Long.class);
		} else if ("publicationGridName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("publicationGridName");
			columnFilter.setType(String.class);
		}
	}
	
	
	@Override
	@Transactional
	public SchedulerPlanner save(SchedulerPlanner schedulerPlanner, Locale locale) {
		log.debug("Try to  Save scheduler : {}", schedulerPlanner);		
		try {	
			if(schedulerPlanner == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.scheduler", new Object[]{schedulerPlanner} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(schedulerPlanner.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.scheduler.with.empty.name", new String[]{schedulerPlanner.getName()} , locale);
				throw new BcephalException(message);
			}
			
			ListChangeHandler<SchedulerPlannerItem> items = schedulerPlanner.getItemListChangeHandler();
						
			schedulerPlanner.setModificationDate(new Timestamp(System.currentTimeMillis()));				
			validateBeforeSave(schedulerPlanner, locale);
			schedulerPlanner = getRepository().save(schedulerPlanner);
			SchedulerPlanner schedulerPlannerId = schedulerPlanner;
			
			items.getNewItems().forEach( item -> {
				log.trace("Try to save scheduler : {}", item);
				item.setSchedulerId(schedulerPlannerId);
				item.initFileGroupByImpl();
				save(item);
				log.trace("scheduler saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				log.trace("Try to save scheduler : {}", item);
				item.setSchedulerId(schedulerPlannerId);
				item.initFileGroupByImpl();
				save(item);
				log.trace("scheduler saved : {}", item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete scheduler : {}", item);
					delete(item);
					log.trace("scheduler deleted : {}", item.getId());
				}
			});
			
			log.debug("Scheduler saved : {} ", schedulerPlanner.getId());
	        return schedulerPlanner;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save scheduler : {}", schedulerPlanner, e);
			String message = getMessageSource().getMessage("unable.to.save.scheduler.model", new Object[]{schedulerPlanner} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

	@Override
	@Transactional
	public void delete(SchedulerPlanner schedulerPlanner) {
		log.debug("Try to delete scheduler : {}", schedulerPlanner);
		if (schedulerPlanner == null || schedulerPlanner.getId() == null) {
			return;
		}
		try {
			ListChangeHandler<SchedulerPlannerItem> items = schedulerPlanner.getItemListChangeHandler();
			
			items.getNewItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete scheduler : {}", item);
					delete(item);
					log.trace("scheduler deleted : {}", item.getId());
				}
			});
			items.getUpdatedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete scheduler : {}", item);
					delete(item);
					log.trace("scheduler deleted : {}", item.getId());
				}
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete scheduler : {}", item);
					delete(item);
					log.trace("scheduler deleted : {}", item.getId());
				}
			});		
		
			getRepository().delete(schedulerPlanner);
			log.debug("Scheduler successfully to delete : {} ", schedulerPlanner);
			return;
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save scheduler : {}", schedulerPlanner, e);
			String message = getMessageSource().getMessage("unable.to.save.scheduler.model", new Object[]{schedulerPlanner} , Locale.ENGLISH);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}


	private void save(SchedulerPlannerItem item) {
		SchedulerPlannerItemLoop loop = item.getItemLoop();
		if(loop != null) {
			loop = save(loop);
			item.setItemLoop(loop);
		}
		schedulerPlannerItemRepository.save(item);		
	}
	
	private SchedulerPlannerItemLoop save(SchedulerPlannerItemLoop loop) {
		ListChangeHandler<SchedulerPlannerItem> items = loop.getItemListChangeHandler();		
		if(loop.getVariableInterval() != null) {
			VariableInterval interval = variableIntervalRepository.save(loop.getVariableInterval());
			loop.setVariableInterval(interval);
		}
		if(loop.getVariableReference() != null) {
			VariableReference reference = save(loop.getVariableReference());
			loop.setVariableReference(reference);
		}
		loop = schedulerPlannerItemLoopRepository.save(loop);
		SchedulerPlannerItemLoop parent = loop;
		
		items.getNewItems().forEach( item -> {
			log.trace("Try to save scheduler item : {}", item);
			item.setParent(parent);
			item.initFileGroupByImpl();
			save(item);
			log.trace("scheduler item saved : {}", item.getId());
		});
		items.getUpdatedItems().forEach( item -> {
			log.trace("Try to save scheduler item : {}", item);
			item.setParent(parent);
			item.initFileGroupByImpl();
			save(item);
			log.trace("scheduler item saved : {}", item.getId());
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete scheduler : {}", item);
				delete(item);
				log.trace("scheduler deleted : {}", item.getId());
			}
		});
		return loop;
	}

	private VariableReference save(VariableReference variableReference) {
		ListChangeHandler<VariableReferenceCondition> items = variableReference.getConditionListChangeHandler();
		variableReference = variableReferenceRepository.save(variableReference);
		VariableReference parent = variableReference;
		
		items.getNewItems().forEach( item -> {
			item.setReference(parent);
			variableReferenceConditionRepository.save(item);
		});
		items.getUpdatedItems().forEach( item -> {
			item.setReference(parent);
			variableReferenceConditionRepository.save(item);
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				variableReferenceConditionRepository.deleteById(item.getId());
			}
		});
		return variableReference;
	}

	private void delete(SchedulerPlannerItem item) {
		SchedulerPlannerItemLoop loop = item.getItemLoop();
		schedulerPlannerItemRepository.deleteById(item.getId());
		if(loop != null && loop.isPersistent()) {
			delete(loop);
		}		
	}

	private void delete(SchedulerPlannerItemLoop loop) {
		if (loop == null || loop.getId() == null) {
			return;
		}		
		ListChangeHandler<SchedulerPlannerItem> items = loop.getItemListChangeHandler();
		VariableInterval interval = loop.getVariableInterval();
		VariableReference reference  = loop.getVariableReference();
		
		items.getNewItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete scheduler : {}", item);
				delete(item);
				log.trace("scheduler deleted : {}", item.getId());
			}
		});
		items.getUpdatedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete scheduler : {}", item);
				delete(item);
				log.trace("scheduler deleted : {}", item.getId());
			}
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete scheduler : {}", item);
				delete(item);
				log.trace("scheduler deleted : {}", item.getId());
			}
		});		
	
		schedulerPlannerItemLoopRepository.deleteById(loop.getId());
		
		if(interval != null && interval.isPersistent()) {
			variableIntervalRepository.deleteById(interval.getId());
		}	
		if(reference != null && reference.isPersistent()) {
			delete(reference);
		}	
	}

	private void delete(VariableReference reference) {
		if (reference == null || reference.getId() == null) {
			return;
		}		
		ListChangeHandler<VariableReferenceCondition> items = reference.getConditionListChangeHandler();		
		items.getNewItems().forEach( item -> {
			if(item.getId() != null) {
				variableReferenceConditionRepository.deleteById(item.getId());
			}
		});
		items.getUpdatedItems().forEach( item -> {
			if(item.getId() != null) {
				variableReferenceConditionRepository.deleteById(item.getId());
			}
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				variableReferenceConditionRepository.deleteById(item.getId());
			}
		});	
	
		variableReferenceRepository.deleteById(reference.getId());		
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel,profileId);
	}


	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	@Value("${bcephal.project.data-dir}")
	String projectDataDir;
	
	
	public Resource downloadFileAsResource(Long schedulerId, String projectCode) {
		log.debug("Enter inside download file like resource in file service : {}");
		SchedulerPlanner scheduler = getById(schedulerId);
		if(scheduler == null) {
			throw new BcephalException("Scheduler not found from id: {}", schedulerId);
		}
		Path filePath = scheduler.buildLogFilePath(projectDataDir, projectCode);
		try {
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new BcephalException("File not found: {}", FilenameUtils.getName(filePath.toString()));
            }
        } catch (MalformedURLException ex) {
            throw new BcephalException("Request url malformed: {}", FilenameUtils.getName(filePath.toString()), ex);
        }
	}
}
