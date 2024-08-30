/**
 * 
 */
package com.moriset.bcephal.planification.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.TransformationRoutineRanking;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartJoinRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.service.DataSourcableService;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineBrowserData;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineCalculateItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineConcatenateItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineEditorData;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineField;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineMapping;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineMappingCondition;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSpot;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSpotCondition;
import com.moriset.bcephal.planification.repository.TransformationRoutineCalculateItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineConcatenateItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineFieldRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineMappingConditionRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineMappingRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineSpotConditionRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineSpotRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Slf4j
@Service
public class TransformationRoutineService extends DataSourcableService<TransformationRoutine, TransformationRoutineBrowserData>{

	@Autowired
	TransformationRoutineRepository routineRepository;	
	
	@Autowired
	TransformationRoutineItemRepository routineItemRepository;
	
	@Autowired
	TransformationRoutineFieldRepository routineFieldRepository;
	
	@Autowired
	TransformationRoutineSpotRepository routineSpotRepository;
	
	@Autowired
	TransformationRoutineSpotConditionRepository routineSpotConditionRepository;
	
	@Autowired
	TransformationRoutineCalculateItemRepository routineCalculateItemRepository;
	
	@Autowired
	TransformationRoutineConcatenateItemRepository routineConcatenateItemRepository;
	
	@Autowired
	TransformationRoutineMappingRepository routineMappingRepository;
	
	@Autowired
	TransformationRoutineMappingConditionRepository routineMappingConditionRepository;
	
	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	GrilleRepository grilleRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	@Autowired
	SmartJoinRepository smartJoinRepository;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@Value("${bcepahl.date.formats}")
	List<String> dateFormats;
	
	@Override
	public MainObjectRepository<TransformationRoutine> getRepository() {
		return routineRepository;
	}
	
	
	@Override
	@Transactional
	public TransformationRoutine save(TransformationRoutine routine, Locale locale) {
		log.debug("Try to  Save routine : {}", routine);
		if (getRepository() == null) {
			return routine;
		}
		try {
			if (routine == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.routine",
						new Object[] { routine }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ListChangeHandler<TransformationRoutineItem> items = routine.getItemListChangeHandler();			
			if(routine.getFilter() != null) {
				UniverseFilter filter = universeFilterService.save(routine.getFilter());
				routine.setFilter(filter);
			}
			
			routine.setModificationDate(new Timestamp(System.currentTimeMillis()));
			routine = getRepository().save(routine);
			TransformationRoutine id = routine;
			
			items.getNewItems().forEach(item -> {
				item.setRoutine(id);
				saveRoutineItem(item);
			});
			items.getUpdatedItems().forEach(item -> {
				item.setRoutine(id);
				saveRoutineItem(item);
			});
			items.getDeletedItems().forEach(item -> {
				deleteRoutineItem(item);
			});
			
			log.debug("Routine saved : {} ", routine.getId());

			log.debug("Routine successfully saved : {} ", routine);
			return routine;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save routine : {}", routine, e);
			String message = getMessageSource().getMessage("unable.to.save.routine", new Object[] { routine },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

	private void saveRoutineItem(TransformationRoutineItem item) {
		ListChangeHandler<TransformationRoutineCalculateItem> calculateItems = item.getCalculateItemListChangeHandler();	
		ListChangeHandler<TransformationRoutineConcatenateItem> concatenateItems = item.getConcatenateItemListChangeHandler();
		if(item.getSourceField() != null) {
			TransformationRoutineField field = saveField(item.getSourceField());
			item.setSourceField(field);
		}
		if(item.getSpot() != null) {
			TransformationRoutineSpot spot = saveSpot(item.getSpot());
			item.setSpot(spot);
		}
		if(item.getFilter() != null) {
			UniverseFilter filter = universeFilterService.save(item.getFilter());
			item.setFilter(filter);
		}
		if(item.getRanking() == null) {
			item.setRanking(new TransformationRoutineRanking());
		}
		item.setName("Item " + (item.getPosition() + 1));
		item = routineItemRepository.save(item);
		TransformationRoutineItem id = item;
		
		calculateItems.getNewItems().forEach(elt -> {
			elt.setRoutineItem(id);
			saveCalculateItem(elt);
		});
		calculateItems.getUpdatedItems().forEach(elt -> {
			elt.setRoutineItem(id);
			saveCalculateItem(elt);
		});
		calculateItems.getDeletedItems().forEach(elt -> {
			deleteCalculateItem(elt);
		});
		
		concatenateItems.getNewItems().forEach(elt -> {
			elt.setRoutineItem(id);
			saveConcatenateItem(elt);
		});
		concatenateItems.getUpdatedItems().forEach(elt -> {
			elt.setRoutineItem(id);
			saveConcatenateItem(elt);
		});
		concatenateItems.getDeletedItems().forEach(elt -> {
			deleteConcatenateItem(elt);
		});
		
		log.debug("Routine Item saved : {} ", item.getId());
	}


	private void saveCalculateItem(TransformationRoutineCalculateItem item) {
		if(item.getField() != null) {
			TransformationRoutineField field = saveField(item.getField());
			item.setField(field);
		}
		if(item.getSpot() != null) {
			TransformationRoutineSpot spot = saveSpot(item.getSpot());
			item.setSpot(spot);
		}
		item = routineCalculateItemRepository.save(item);
	}


	private void saveConcatenateItem(TransformationRoutineConcatenateItem item) {
		if(item.getField() != null) {
			TransformationRoutineField field = saveField(item.getField());
			item.setField(field);
		}		
		item = routineConcatenateItemRepository.save(item);
	}
	
	private TransformationRoutineField saveField(TransformationRoutineField field) {
		if(field.getMapping() != null) {
			TransformationRoutineMapping mapping = saveMapping(field.getMapping());
			field.setMapping(mapping);
		}		
		field = routineFieldRepository.save(field);
		return field;
	}
	
	private TransformationRoutineSpot saveSpot(TransformationRoutineSpot spot) {
		
		
		ListChangeHandler<TransformationRoutineSpotCondition> conditions = spot.getConditionListChangeHandler();	
		spot = routineSpotRepository.save(spot);
		TransformationRoutineSpot id = spot;		
		conditions.getNewItems().forEach(elt -> {
			elt.setSpotId(id);
			routineSpotConditionRepository.save(elt);
		});
		conditions.getUpdatedItems().forEach(elt -> {
			elt.setSpotId(id);
			routineSpotConditionRepository.save(elt);
		});
		conditions.getDeletedItems().forEach(elt -> {
			if(elt.getId() != null) {
				routineSpotConditionRepository.deleteById(elt.getId());
			}
		});		
		
		
		return spot;
	}
	
	private TransformationRoutineMapping saveMapping(TransformationRoutineMapping mapping) {
		ListChangeHandler<TransformationRoutineMappingCondition> conditions = mapping.getConditionListChangeHandler();	
		mapping = routineMappingRepository.save(mapping);
		TransformationRoutineMapping id = mapping;		
		conditions.getNewItems().forEach(elt -> {
			elt.setMappingId(id);
			routineMappingConditionRepository.save(elt);
		});
		conditions.getUpdatedItems().forEach(elt -> {
			elt.setMappingId(id);
			routineMappingConditionRepository.save(elt);
		});
		conditions.getDeletedItems().forEach(elt -> {
			if(elt.getId() != null) {
				routineMappingConditionRepository.deleteById(elt.getId());
			}
		});		
		return mapping;
	}
	

	@Override
	@Transactional
	public void delete(TransformationRoutine routine) {
		if(routine == null || routine.getId() == null) {
			return;
		}

		routine.getItemListChangeHandler().getItems().forEach(elt -> {
			deleteRoutineItem(elt);
		});
//		routine.getItemListChangeHandler().getOriginalList().forEach(elt -> {
//			deleteRoutineItem(elt);
//		});
		routine.getItemListChangeHandler().getDeletedItems().forEach(elt -> {
			deleteRoutineItem(elt);
		});
		
		getRepository().deleteById(routine.getId());		
		log.debug("Grid successfully to delete : {} ", routine);
	}

	private void deleteCalculateItem(TransformationRoutineCalculateItem item) {
		if(item == null || item.getId() == null) {
			return;
		}
		deleteField(item.getField());
		deleteSpot(item.getSpot());
		routineCalculateItemRepository.deleteById(item.getId());
	}
	
	private void deleteConcatenateItem(TransformationRoutineConcatenateItem item) {
		if(item == null || item.getId() == null) {
			return;
		}
		deleteField(item.getField());
		routineConcatenateItemRepository.deleteById(item.getId());
	}
	
	private void deleteField(TransformationRoutineField field) {
		if(field == null || field.getId() == null) {
			return;
		}
		if(field.getMapping() != null && field.getMapping().getId() != null) {
			deleteMapping(field.getMapping());
		}
		routineFieldRepository.deleteById(field.getId());
	}
	
	private void deleteSpot(TransformationRoutineSpot spot) {		
		if(spot == null || spot.getId() == null) {
			return;
		}
		spot.getConditionListChangeHandler().getItems().forEach(elt -> {
			routineSpotConditionRepository.deleteById(elt.getId());
		});
		spot.getConditionListChangeHandler().getDeletedItems().forEach(elt -> {
			routineSpotConditionRepository.deleteById(elt.getId());
		});
		routineSpotRepository.deleteById(spot.getId());
	}


	private void deleteRoutineItem(TransformationRoutineItem item) {
		if(item == null || item.getId() == null) {
			return;
		}
		item.getCalculateItemListChangeHandler().getItems().forEach(elt -> {
			deleteCalculateItem(elt);
		});
		/*item.getCalculateItemListChangeHandler().getOriginalList().forEach(elt -> {
			deleteCalculateItem(elt);
		});*/
		item.getCalculateItemListChangeHandler().getDeletedItems().forEach(elt -> {
			deleteCalculateItem(elt);
		});
		
		item.getConcatenateItemListChangeHandler().getItems().forEach(elt -> {
			deleteConcatenateItem(elt);
		});
		/*item.getConcatenateItemListChangeHandler().getOriginalList().forEach(elt -> {
			deleteConcatenateItem(elt);
		});*/
		item.getConcatenateItemListChangeHandler().getDeletedItems().forEach(elt -> {
			deleteConcatenateItem(elt);
		});
		deleteField(item.getSourceField());
		deleteSpot(item.getSpot());
		routineItemRepository.deleteById(item.getId());
	}
	
	private void deleteMapping(TransformationRoutineMapping mapping) {
		if(mapping == null || mapping.getId() == null) {
			return;
		}
		mapping.getConditionListChangeHandler().getItems().forEach(elt -> {
			routineMappingConditionRepository.deleteById(elt.getId());
		});
		mapping.getConditionListChangeHandler().getDeletedItems().forEach(elt -> {
			routineMappingConditionRepository.deleteById(elt.getId());
		});
		routineMappingRepository.deleteById(mapping.getId());
	}

	
	@Override
	public EditorData<TransformationRoutine> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		TransformationRoutineEditorData data = new TransformationRoutineEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			data.getItem().setDataSourceType(filter.getDataSourceType());
			data.getItem().setDataSourceId( filter.getDataSourceId());
		} else {
			data.setItem(getById(filter.getId()));
		}
		
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);
		List<Long> hightGridItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_INPUT_GRID, projectCode);
		if(data.getItem() != null) {
			if(data.getItem().getDataSourceType().isMaterializedGrid()) {				
				Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(data.getItem().getDataSourceId());
				if(response.isPresent()) {
					data.grids = new ArrayList<>();
					data.grids.add(new Nameable(response.get().getId(), response.get().getName()));
				}
				
				String dName = initEditorDataForMaterializedGrid(data, data.getItem().getDataSourceId(), session, locale);
				data.getItem().setDataSourceName(dName);
			}
			else {				
				if(hightGridItems != null && hightGridItems.size() > 0) {
					data.grids = grilleRepository.findByTypeExclude(GrilleType.INPUT, hightGridItems);
				}else {
					data.grids = grilleRepository.findByType(GrilleType.INPUT);
				}
				
				initEditorData(data, session, locale);				
			}
		}			
		
		List<Long> hightMaterializedItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);
		List<Long> hightJointItems = getHidedObjectId(profileId, FunctionalityCodes.REPORTING_REPORT_JOIN_GRID, projectCode);		
		List<GrilleType> types = getSmallGridTypes();
		
		if(hightGridItems != null && hightGridItems.size() > 0) {
			data.getSmartGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightGridItems));
		}else {
			data.getSmartGrids().addAll(smartGrilleRepository.findByTypes(types));
		}
		if(hightMaterializedItems != null && hightMaterializedItems.size() > 0) {
			data.getSmartGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMaterializedItems));
		}else {
			data.getSmartGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		if(hightJointItems != null && hightJointItems.size() > 0) {
			data.getSmartGrids().addAll(smartJoinRepository.findAllExclude(hightJointItems));
		}else {
			data.getSmartGrids().addAll(smartJoinRepository.findAll());
		}	
		data.setDateFormats(this.dateFormats);
		return data;
	}
	
	@Override
	protected void setDataSourceName(TransformationRoutine item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}

	protected List<GrilleType> getSmallGridTypes(){
		List<GrilleType> types = new ArrayList<>();
		types.add(GrilleType.INPUT);
		types.add(GrilleType.REPORT);
		//types.add(GrilleType.BILLING_EVENT_REPOSITORY);
		//types.add(GrilleType.CLIENT_REPOSITORY);
		return types;
	}
	
	@Override
	protected TransformationRoutine getNewItem() {
		TransformationRoutine routine = new TransformationRoutine();		
		String baseName = "Routine ";
		int i = 1;
		routine.setName(baseName + i);
		while (getByName(routine.getName()) != null) {
			i++;
			routine.setName(baseName + i);
		}
		return routine;
	}

	@Override
	protected TransformationRoutineBrowserData getNewBrowserData(TransformationRoutine item) {
		return new TransformationRoutineBrowserData(item);
	}

	@Override
	protected Specification<TransformationRoutine> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<TransformationRoutine> qBuilder = new RequestQueryBuilder<TransformationRoutine>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
			qBuilder.addNoTInObjectId(hidedObjectIds);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				filter.getColumnFilters().getItems().forEach(filte -> {
					build(filte);
				});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.TRANSFORMATION_ROUTINE;
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
}
