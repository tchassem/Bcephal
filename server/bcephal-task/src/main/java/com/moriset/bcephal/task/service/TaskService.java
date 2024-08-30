/**
 * 
 */
package com.moriset.bcephal.task.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskAudience;
import com.moriset.bcephal.task.domain.TaskBrowserData;
import com.moriset.bcephal.task.domain.TaskCategory;
import com.moriset.bcephal.task.domain.TaskEditorData;
import com.moriset.bcephal.task.domain.TaskLogItem;
import com.moriset.bcephal.task.domain.TaskLogItemType;
import com.moriset.bcephal.task.domain.TaskNature;
import com.moriset.bcephal.task.domain.TaskStatus;
import com.moriset.bcephal.task.repository.TaskAudienceRepository;
import com.moriset.bcephal.task.repository.TaskLogItemRepository;
import com.moriset.bcephal.task.repository.TaskRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class TaskService extends MainObjectService<Task, TaskBrowserData> {
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	TaskAudienceRepository taskAudienceRepository;
	
	@Autowired
	TaskLogItemRepository taskLogItemRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	UserDataRepository userRepository;
	
	@Autowired
	ProfileDataRepository profileRepository;
	
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
	
	@Autowired
	protected ObjectMapper mapper;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.TASK;
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
	public TaskRepository getRepository() {
		return taskRepository;
	}
	
	
	@Override
	public TaskEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<Task> base = super.getEditorData(filter, session, locale);
		TaskEditorData data = new TaskEditorData();
		data.setItem(base.getItem());
		data.setMeasures(base.getMeasures());
		data.setPeriods(base.getPeriods());
		data.setCalendarCategories(base.getCalendarCategories());
		data.setModels(base.getModels());		
		data.setSpots(base.getSpots());
		Long clientId = (Long)session.getAttribute(RequestParams.BC_CLIENT);
		data.setUsers(userRepository.findAllAsNameablesByClient(clientId));
		data.setProfils(profileRepository.findAllAsNameablesByClient(clientId));
		data.setSequences(new ArrayList<>());
		return data;
	}

	@Override
	protected Task getNewItem() {
		Task task = new Task();
		String baseName = "Task ";
		int i = 1;
		task.setName(baseName + i);
		while(getByName(task.getName()) != null) {
			i++;
			task.setName(baseName + i);
		}
		return task;
	}
	
	@Override
	public Task getById(Long id) {
		Task task = super.getById(id);
		if(task != null && task.getUserId() != null) {
			//task.getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(task.getId(), AutoReco.class.getName()));
		}
		return task;
	}
	
	public Task generateTask(Long parentId) {
		Optional<Task> response = taskRepository.findById(parentId);
		if (response.isEmpty()) {
			log.debug("Unknown task : {}", parentId);
			throw new BcephalException("Unknown task : " + parentId);
		}
		Task task = response.get();
		return generateTask(task);
	}
	
	public Task generateTask(Task parent) {
		if(parent == null) {
			log.debug("Unable to generate task from null parent");
			throw new BcephalException("Unable to generate task from null parent");
		}
		Task task = parent.copy();
		task.setStatus(TaskStatus.OPEN);
		task.setCategory(TaskCategory.STANDARD);
		task.setName(parent.getName());
		task.setSerieNbr(0);
		task.getByWhen().setDateOperator(PeriodOperator.SPECIFIC);
		task.getByWhen().setDateValue(parent.getByWhen().buildDynamicDate());
		task.getByWhen().setDateNumber(0);
		if(parent.getSequenceId() != null) {
			Optional<IncrementalNumber> sequence = incrementalNumberRepository.findById(parent.getSequenceId());
			if(sequence.isPresent()) {
				IncrementalNumber number = sequence.get();
				task.setCode(number.buildNextValue());
				incrementalNumberRepository.save(number);
			}
		}		
		task = save(task, Locale.ENGLISH);
		return task;
	}
	
		
	@Override
	@Transactional
	public Task save(Task task, Locale locale) {
		log.debug("Try to  Save Task : {}", task);		
		try {	
			if(task == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.task", new Object[]{task} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(task.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.task.with.empty.name", new String[]{task.getName()} , locale);
				throw new BcephalException(message);
			}
			Timestamp modifDate = new Timestamp(System.currentTimeMillis());
			Task oldTask = null;
			if(task.isPersistent()) {
				oldTask = getById(task.getId());
			}
			
						
			ListChangeHandler<TaskAudience> audiences = task.getAudienceListChangeHandler();			
			task.setModificationDate(modifDate);
			validateBeforeSave(task, locale);
			task = getRepository().save(task);
			Task id = task;
			if(!StringUtils.hasText(task.getCode())) {
				task.setCode("" + id);
			}
			
			audiences.getNewItems().forEach( item -> {
				log.trace("Try to save Task Audience : {}", item);
				item.setTaskId(id);
				taskAudienceRepository.save(item);
				log.trace("Task Audience saved : {}", item.getId());
			});
			audiences.getUpdatedItems().forEach( item -> {
				log.trace("Try to save Task Audience : {}", item);
				item.setTaskId(id);
				taskAudienceRepository.save(item);
				log.trace("Task Audience saved : {}", item.getId());
			});
			audiences.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete Task Audience : {}", item);
					taskAudienceRepository.deleteById(item.getId());
					log.trace("Task Audience deleted : {}", item.getId());
				}
			});
			
			TaskLogItem item = buildNewLogItem(oldTask, task, "B-CEPHAL", modifDate);
			item.setType(TaskLogItemType.FIELD_MODIFICATION);
			item.setOldValue(oldTask != null ? mapper.writeValueAsString(oldTask) : null);
			item.setNewValue(task != null ? mapper.writeValueAsString(task) : null);
			taskLogItemRepository.save(item);
						
			log.debug("Task saved : {} ", task.getId());
	        return task;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save Task : {}", task, e);
			String message = getMessageSource().getMessage("unable.to.save.task", new Object[]{task} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private TaskLogItem buildNewLogItem(Task oldTask, Task task, String username, Timestamp modifDate) throws Exception {		
		TaskLogItem item = new TaskLogItem();
		item.setName(task.getName());
		item.setDescription(oldTask != null ? mapper.writeValueAsString(oldTask) : null);
		item.setTaskId(task.getId());
		item.setUsername(username);
		item.setCreationDate(modifDate);
		item.setModificationDate(modifDate);
		//item.setFileName("");
		return item;
	}

	@Override
	protected void validateBeforeSave(Task entity, Locale locale) {
		
	}
	
	
	@Override
	public void delete(Task task) {
		log.debug("Try to delete task : {}", task);	
		if(task == null || task.getId() == null) {
			return;
		}
		
		ListChangeHandler<TaskAudience> audiences = task.getAudienceListChangeHandler();
		audiences.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Task Audience : {}", item);
				taskAudienceRepository.deleteById(item.getId());
				log.trace("Task Audience deleted : {}", item.getId());
			}
		});
		audiences.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Task Audience : {}", item);
				taskAudienceRepository.deleteById(item.getId());
				log.trace("Task Audience deleted : {}", item.getId());
			}
		});
		
		getRepository().deleteById(task.getId());
		log.debug("Task successfully to delete : {} ", task);
	    return;	
	}

	@Override
	protected TaskBrowserData getNewBrowserData(Task item) {
		return new TaskBrowserData(item);
	}

	@Override
	protected Specification<Task> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Task> qBuilder = new RequestQueryBuilder<Task>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("creationDate"), root.get("modificationDate"));
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    
		    if (filter != null && filter.isMyTasks()) {
		    	String name = StringUtils.hasText(filter.getUsername()) ? filter.getUsername() : filter.getCriteria();
		    	qBuilder.addEquals("username", name);
		    }
		    if (FunctionalityCodes.TASK_TEMPLATE.equals(getBrowserFunctionalityCode())) {
		    	qBuilder.addEquals("category", TaskCategory.SCHEDULED);
		    }
		    else {
		    	qBuilder.addEquals("category", TaskCategory.STANDARD);
		    }
		    
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
		if ("sequenceId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("sequenceId");
			columnFilter.setType(Long.class);
		} else if ("code".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("code");
			columnFilter.setType(String.class);
		} else if ("description".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("description");
			columnFilter.setType(String.class);
		}else if ("serieNbr".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("serieNbr");
			columnFilter.setType(Integer.class);
		} else if ("userId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("userId");
			columnFilter.setType(Long.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("sendNotice".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("sendNotice");
			columnFilter.setType(Boolean.class);
		} else if ("linkedFunctionality".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("linkedFunctionality");
			columnFilter.setType(String.class);
		} else if ("deadline".equalsIgnoreCase(columnFilter.getName()) || "DeadlineDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("deadline");
			columnFilter.setType(Date.class);
		} else if ("dateOperator".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dateOperator");
			columnFilter.setType(PeriodOperator.class);
		} else if ("dateValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dateValue");
			columnFilter.setType(Date.class);
		} else if ("dateSign".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dateSign");
			columnFilter.setType(String.class);
		} else if ("dateNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dateNumber");
			columnFilter.setType(Integer.class);
		} else if ("dateGranularity".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("dateGranularity");
			columnFilter.setType(PeriodGranularity.class);
		}
		super.build(columnFilter);
		if ("Category".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("category");
			columnFilter.setType(TaskCategory.class);
		}
		else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(TaskStatus.class);
		}
		else if ("nature".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("nature");
			columnFilter.setType(TaskNature.class);
		}
	}

}
