/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.routine.RoutineExecutor;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoEditorData;
import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModel;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelColumns;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModel;
import com.moriset.bcephal.reconciliation.repository.AutoRecoRankingItemRepository;
import com.moriset.bcephal.reconciliation.repository.AutoRecoRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationConditionRepository;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
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
public class AutoRecoService extends MainObjectService<AutoReco, BrowserData> {
	
	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	AutoRecoRepository autoRecoRepository;
	
	@Autowired
	ReconciliationConditionRepository reconciliationConditionRepository;
	
	@Autowired
	AutoRecoRankingItemRepository rankingItemRepository;
	
	@Autowired
	ReconciliationModelService reconciliationModelService;
	
	@Autowired
	ReconciliationUnionModelService reconciliationUnionModelService;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.RECONCILIATION_AUTO_RECO;
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
	public AutoRecoRepository getRepository() {
		return autoRecoRepository;
	}
	
	public ReconciliationModelColumns getModelColumns(Long modelId) {
		ReconciliationModel model = reconciliationModelService.getById(modelId);
		if(model == null) {
			throw new BcephalException("Unknown reconciliation model: " + modelId);
		}
		ReconciliationModelColumns columns = new ReconciliationModelColumns(model);
		return columns;
	}
	
	public ReconciliationModelColumns getModelColumns(Long modelId, boolean forUnion) {
		if(forUnion) {
			ReconciliationUnionModel model = reconciliationUnionModelService.getById(modelId);
			if(model == null) {
				throw new BcephalException("Unknown reconciliation union model: " + modelId);
			}
			ReconciliationModelColumns columns = new ReconciliationModelColumns(model);
			return columns;
		}
		else {
			ReconciliationModel model = reconciliationModelService.getById(modelId);
			if(model == null) {
				throw new BcephalException("Unknown reconciliation model: " + modelId);
			}
			ReconciliationModelColumns columns = new ReconciliationModelColumns(model);
			return columns;
		}
	}
	
	@Override
	public AutoRecoEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<AutoReco> base = super.getEditorData(filter, session, locale);
		AutoRecoEditorData data = new AutoRecoEditorData();
		data.setItem(base.getItem());
		if(data.getItem() != null) {
			data.getItem().setBuildCombinationsAsc(true);
		}
		data.setMeasures(base.getMeasures());
		data.setPeriods(base.getPeriods());
		data.setCalendarCategories(base.getCalendarCategories());
		data.setModels(base.getModels());		
		data.setSpots(base.getSpots());
		data.setReconciliationModels(reconciliationModelService.getRepository().findAllAsNameables());
		//data.setReconciliationJoinModels(reconciliationJoinModelService.getRepository().findAllAsNameables());
		data.setReconciliationJoinModels(reconciliationUnionModelService.getRepository().findAllAsNameables());
		List<Object[]> routines = autoRecoRepository.findRoutines();
		for(Object[] objs : routines) {
			data.getRoutines().add(new Nameable(((Number)objs[0]).longValue(), (String)objs[1]) );
		}
		return data;
	}

	@Override
	protected AutoReco getNewItem() {
		AutoReco autoReco = new AutoReco();
		String baseName = "Auto Reco ";
		int i = 1;
		autoReco.setName(baseName + i);
		while(getByName(autoReco.getName()) != null) {
			i++;
			autoReco.setName(baseName + i);
		}
		return autoReco;
	}
	
	@Override
	public AutoReco getById(Long id) {
		AutoReco reco = super.getById(id);
		if(reco != null && reco.getId() != null) {
			reco.getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(reco.getId(), AutoReco.class.getName()));
		}
		return reco;
	}
	
		
	@Override
	@Transactional
	public AutoReco save(AutoReco autoReco, Locale locale) {
		log.debug("Try to  Save AutoReco : {}", autoReco);		
		try {	
			if(autoReco == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{autoReco} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(autoReco.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.grid.with.empty.name", new String[]{autoReco.getName()} , locale);
				throw new BcephalException(message);
			}
						
			ListChangeHandler<ReconciliationCondition> conditions = autoReco.getConditionListChangeHandler();
			ListChangeHandler<AutoRecoRankingItem> rankingItems = autoReco.getRankingItemListChangeHandler();
			ListChangeHandler<RoutineExecutor> routines = autoReco.getRoutineListChangeHandler();
			if(autoReco.getLeftFilter() != null) {
				autoReco.setLeftFilter(universeFilterService.save(autoReco.getLeftFilter()));
			}
			if(autoReco.getRightFilter() != null) {
				autoReco.setRightFilter(universeFilterService.save(autoReco.getRightFilter()));
			}
			
			autoReco.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(autoReco, locale);
			autoReco = getRepository().save(autoReco);
			AutoReco id = autoReco;
			
			conditions.getNewItems().forEach( item -> {
				log.trace("Try to save Reconciliation Condition : {}", item);
				item.setAutoRecoId(id);
				reconciliationConditionRepository.save(item);
				log.trace("Reconciliation Condition saved : {}", item.getId());
			});
			conditions.getUpdatedItems().forEach( item -> {
				log.trace("Try to save Reconciliation Condition : {}", item);
				item.setAutoRecoId(id);
				reconciliationConditionRepository.save(item);
				log.trace("Reconciliation Condition saved : {}", item.getId());
			});
			conditions.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete Reconciliation Condition : {}", item);
					reconciliationConditionRepository.deleteById(item.getId());
					log.trace("Reconciliation Condition deleted : {}", item.getId());
				}
			});
			
			rankingItems.getNewItems().forEach( item -> {
				log.trace("Try to save ranking item : {}", item);
				item.setAutoRecoId(id);
				rankingItemRepository.save(item);
				log.trace("Ranking item saved : {}", item.getId());
			});
			rankingItems.getUpdatedItems().forEach( item -> {
				log.trace("Try to save ranking item : {}", item);
				item.setAutoRecoId(id);
				rankingItemRepository.save(item);
				log.trace("Ranking item saved : {}", item.getId());
			});
			rankingItems.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete ranking item : {}", item);
					rankingItemRepository.deleteById(item.getId());
					log.trace("Ranking item deleted : {}", item.getId());
				}
			});
			
			routines.getNewItems().forEach( item -> {
				log.trace("Try to save Routine executor : {}", item);
				item.setObjectId(id.getId());
				item.setObjectType(AutoReco.class.getName());
				routineExecutorReopository.save(item);
				log.trace("Rroutine executor saved : {}", item.getId());
			});
			routines.getUpdatedItems().forEach( item -> {
				log.trace("Try to save Routine executor : {}", item);
				item.setObjectId(id.getId());
				item.setObjectType(AutoReco.class.getName());
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
			
			log.debug("AutoReco saved : {} ", autoReco.getId());
	        return autoReco;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save AutoReco : {}", autoReco, e);
			String message = getMessageSource().getMessage("unable.to.save.AutoReco", new Object[]{autoReco} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public void delete(AutoReco autoReco) {
		log.debug("Try to delete AutoReco : {}", autoReco);	
		if(autoReco == null || autoReco.getId() == null) {
			return;
		}
		
		ListChangeHandler<ReconciliationCondition> conditions = autoReco.getConditionListChangeHandler();
		conditions.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Reconciliation Condition : {}", item);
				reconciliationConditionRepository.deleteById(item.getId());
				log.trace("Reconciliation Condition deleted : {}", item.getId());
			}
		});
		conditions.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Reconciliation Condition : {}", item);
				reconciliationConditionRepository.deleteById(item.getId());
				log.trace("Reconciliation Condition deleted : {}", item.getId());
			}
		});
		
		ListChangeHandler<AutoRecoRankingItem> rankingItems = autoReco.getRankingItemListChangeHandler();
		rankingItems.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Ranking Item : {}", item);
				rankingItemRepository.deleteById(item.getId());
				log.trace("Ranking Item deleted : {}", item.getId());
			}
		});
		rankingItems.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Ranking Item : {}", item);
				rankingItemRepository.deleteById(item.getId());
				log.trace("Ranking Item deleted : {}", item.getId());
			}
		});
		
		ListChangeHandler<RoutineExecutor> routines = autoReco.getRoutineListChangeHandler();
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
		
		if(autoReco.getLeftFilter() != null) {
			universeFilterService.delete(autoReco.getLeftFilter());
		}
		if(autoReco.getRightFilter() != null) {
			universeFilterService.delete(autoReco.getRightFilter());
		}	
		getRepository().deleteById(autoReco.getId());
		log.debug("AutoReco successfully to delete : {} ", autoReco);
	    return;	
	}

	@Override
	protected BrowserData getNewBrowserData(AutoReco item) {
		return new BrowserData(item);
	}

	@Override
	protected Specification<AutoReco> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<AutoReco> qBuilder = new RequestQueryBuilder<AutoReco>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("creationDate"), root.get("modificationDate"));
		    if(filter.getGroupId() != null) {
		    	Predicate predicate = qBuilder.getCriteriaBuilder().equal(qBuilder.getRoot().get("recoId"), filter.getGroupId());	
		    	qBuilder.add(predicate);
		    }
		    if(filter.getForModel() != null) {
		    	Predicate predicate = qBuilder.getCriteriaBuilder().equal(qBuilder.getRoot().get("forModel"), filter.getForModel());	
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
	
	protected void build(ColumnFilter columnFilter) {
		if ("CurrentlyExecuting".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("currentlyExecuting");
		} 
		super.build(columnFilter);
	}

}
