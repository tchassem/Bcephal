/**
 * 
 */
package com.moriset.bcephal.task.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.task.domain.TaskLogItem;
import com.moriset.bcephal.task.domain.TaskLogItemBrowserData;
import com.moriset.bcephal.task.domain.TaskLogItemType;
import com.moriset.bcephal.task.repository.TaskLogItemRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class TaskLogItemService extends MainObjectService<TaskLogItem, TaskLogItemBrowserData> {
	
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
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.TASK_LOG_ITEM;
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
	public TaskLogItemRepository getRepository() {
		return taskLogItemRepository;
	}
	
	
	@Override
	public EditorData<TaskLogItem> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {		
		return null;
	}

	@Override
	protected TaskLogItem getNewItem() {
		TaskLogItem item = new TaskLogItem();
		String baseName = "Task log item ";
		int i = 1;
		item.setName(baseName + i);
		while(getByName(item.getName()) != null) {
			i++;
			item.setName(baseName + i);
		}
		return item;
	}
		
		
	@Override
	@Transactional
	public TaskLogItem save(TaskLogItem item, Locale locale) {
		log.debug("Try to  Save TaskLogItem : {}", item);		
		try {	
			if(item == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.task.log.item", new Object[]{item} , locale);
				throw new BcephalException(message);
			}
//			if(!StringUtils.hasLength(item.getName())) {
//				String message = getMessageSource().getMessage("unable.to.save.task.log.item.with.empty.name", new String[]{item.getName()} , locale);
//				throw new BcephalException(message);
//			}
						
			item.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(item, locale);
			item = getRepository().save(item);		
						
			log.debug("TaskLogItem saved : {} ", item.getId());
	        return item;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save TaskLogItem : {}", item, e);
			String message = getMessageSource().getMessage("unable.to.save.task.log.item", new Object[]{item} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	protected void validateBeforeSave(TaskLogItem entity, Locale locale) {
		
	}
	
	
	@Override
	public void delete(TaskLogItem item) {
		log.debug("Try to delete TaskLogItem : {}", item);	
		if(item == null || item.getId() == null) {
			return;
		}
		getRepository().deleteById(item.getId());
		log.debug("TaskLogItem successfully to delete : {} ", item);
	    return;	
	}

	@Override
	protected TaskLogItemBrowserData getNewBrowserData(TaskLogItem item) {
		return new TaskLogItemBrowserData(item);
	}

	@Override
	protected Specification<TaskLogItem> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<TaskLogItem> qBuilder = new RequestQueryBuilder<TaskLogItem>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("creationDate"), root.get("modificationDate"));
		    if(filter.getGroupId() != null) {
		    	Predicate predicate = qBuilder.getCriteriaBuilder().equal(qBuilder.getRoot().get("taskId"), filter.getGroupId());	
		    	qBuilder.add(predicate);
		    }		    
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    if (filter != null && StringUtils.hasText(filter.getCriteria())) {
		    	qBuilder.addLikeCriteria("name", filter.getCriteria());
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
	
	@Override
	protected  Sort  getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			return super.getBrowserDatasSort(filter, locale);
    	}
		return Sort.by(Order.desc("id"));
	}
	
	@Override
	protected void build(ColumnFilter columnFilter) {
		if ("type".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("type");
			columnFilter.setType(TaskLogItemType.class);
		} else if ("fileName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("fileName");
			columnFilter.setType(String.class);
		} else if ("oldValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("oldValue");
			columnFilter.setType(String.class);
		} else if ("newValue".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("newValue");
			columnFilter.setType(String.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		}
		super.build(columnFilter);
	}

}
