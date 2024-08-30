/**
 * 
 */
package com.moriset.bcephal.planification.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineCalculateItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineConcatenateItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineLog;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineLogItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSourceType;
import com.moriset.bcephal.planification.repository.TransformationRoutineLogItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineLogRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.CsvGenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
//@Component
@Data
@Slf4j
public class RoutineRunner {

	private TransformationRoutine routine;
	private TaskProgressListener listener;
	private CsvGenerator universeCsvGenerator;

	private boolean stopped;

	String loaderNbrColumn;
	String loaderFileColumn;
	String loadNbr;

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired TransformationRoutineLogRepository logRepository;
	@Autowired TransformationRoutineLogItemRepository logItemRepository;
	
	@Autowired 
	GrilleService grilleService;
	
	@Autowired 
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired 
	MaterializedGridService materializedGridService;
	
	@Autowired 
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	TransformationRoutineLog routineLog = null;
	
	String sessionId;

	String username = "B-CEPHAL";
	
	@Transactional
	public void run() {		
		log.debug("Try to run routine : {}", routine.getName());
		
		try {	
			routine.sortItems();			
			routineLog = initLog();	
			int size = routine.getItems().size();
			if(size == 0) {
				throw new BcephalException("The routine is empty! There is no item.");
			}
			log.trace("Routine : {} - Size : {}", routine.getName(), size);
			if(listener != null) {
				listener.start(size + 1);
			}
			buildGrids();
			if(listener != null) {
				listener.nextStep(1);
			}
			for(TransformationRoutineItem item : routine.getItems()) {
				if(item.isActive()) {
					try {
						TransformationRoutineLogItem logItem = initLogItem(item, routineLog);
						runItem(item, routineLog);
						endLogItem(logItem, null);
					} 
					catch (Exception e) {
						String message = "Unable to run routine " + item.getName() + ". \nUnexpected error!";
						log.error(message, e);
						if (e instanceof BcephalException) {
							message = e.getMessage();
						}
						else if(e instanceof DataException && ((DataException)e).getSQLException() != null) {
							message = ((DataException)e).getSQLException().getMessage();
						}
						else {
							message = "Unable to run routine " + item.getName() + ". " + e.getMessage();
						}			
						endArchiveLog(routineLog, message);
						throw new BcephalException("Unable to run routine " + item.getName()); 
					}
				}
				if(listener != null) {
					listener.nextStep(1);
				}
			}	
			endArchiveLog(routineLog, null);
		} 
		catch (Exception e) {
			log.error("Routine : {}		Run fail !", routine.getName(), e);
			String message = "Unable to run routine. \nUnexpected error!";
			if (e instanceof BcephalException) {
				message = e.getMessage();
			}
			else if(e instanceof DataException && ((DataException)e).getSQLException() != null) {
				message = ((DataException)e).getSQLException().getMessage();
			}
			else {
				message = "Unable to run routine. " + e.getMessage();
			}			
			endArchiveLog(routineLog, message);
			if (getListener() != null) {
				getListener().error(message, true);
			}
		} finally {

		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	private void runItem(TransformationRoutineItem item, TransformationRoutineLog routineLog) {
		log.debug("Routine : {}. Run item ({}) : {}", routine.getName(), item.getPosition() + 1,item.getName());
		
		log.trace("Routine : {}. Run item : {}. Grid : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
		if(routine.getDataSourceType().isMaterializedGrid()) {
			MaterializedGridDataFilter filter = new MaterializedGridDataFilter();
			filter.setGrid(materializedGridService.getById(routine.getDataSourceId()));
			filter.setFilter(item.getFilter());		
			if (filter.getFilter() != null) {
				if (filter.getFilter().getMeasureFilter() != null) {
					for(MeasureFilterItem filterItem : filter.getFilter().getMeasureFilter().getItemListChangeHandler().getItems()) {
						filterItem.setDataSourceId(routine.getDataSourceId());	
						filterItem.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
					}
				}
				if (filter.getFilter().getAttributeFilter() != null) {
					for(AttributeFilterItem filterItem : filter.getFilter().getAttributeFilter().getItemListChangeHandler().getItems()) {
						filterItem.setDataSourceId(routine.getDataSourceId());	
						filterItem.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
					}
				}
				if (filter.getFilter().getPeriodFilter() != null) {
					for(PeriodFilterItem filterItem : filter.getFilter().getPeriodFilter().getItemListChangeHandler().getItems()) {
						filterItem.setDataSourceId(routine.getDataSourceId());	
						filterItem.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
					}
				}
			}
			
			RoutineItemForMatGridQueryBuilder builder = new RoutineItemForMatGridQueryBuilder(filter, item);	
			builder.setLoaderData(routine.getLoaderData());
			
			log.debug("Routine : {}. Run item : {}. Grid : {}. Try to build query...", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
			String sql = null;	
			
			if((item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT 
					|| item.getSourceField().getSourceType() == TransformationRoutineSourceType.CALCULATE)
					&& item.getRanking() != null && item.getRanking().getDimensionId() != null) {		
			
				sql = builder.buildSelectIdsQuery();
				if(org.springframework.util.StringUtils.hasText(sql)) {
					Query query = entityManager.createNativeQuery(sql);
					if(builder.parameters != null) {
						for(String param : builder.parameters.keySet()) {
							query.setParameter(param, builder.parameters.get(param));
							log.trace("Routine : {}. Run item : {}. Grid : {}. Parameter : {} : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", param, builder.parameters.get(param));
						}
					}
					@SuppressWarnings("unchecked")
					List<Long> ids = query.getResultList();					
					sql = item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT ? builder.buildSpotQuery(null)
							: builder.buildCalculateQuery(null);
					query = entityManager.createNativeQuery(sql);
					if(listener != null) {
						listener.createSubInfo(null, "");
						listener.startSubInfo(ids.size());;
					}
					for(Long id : ids) {
						if(listener != null) {
							listener.nextSubInfoStep(1);
						}
						builder.parameters.put("id", id);						
						if(builder.parameters != null) {
							for(String param : builder.parameters.keySet()) {
								query.setParameter(param, builder.parameters.get(param));
								log.trace("Routine : {}. Run item : {}. Grid : {}. Parameter : {} : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", param, builder.parameters.get(param));
							}
						}
						query.executeUpdate();
					}
					if(listener != null) {
						listener.endSubInfo();
					}
					log.debug("Routine : {}. Run item : {}. Grid : {}. Query executed!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				}
			}
			else {	
				if(item.isDeleteEntry()) {
					sql = builder.buildDeleteQuery();
				}
				else {
					sql = builder.buildQuery();
				}			
				log.debug("Routine : {}. Run item : {}. Grid : {}. Query builded!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				log.trace("Routine : {}. Run item : {}. Grid : {}. Query : \n{}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", sql);
				if(StringUtils.hasText(sql)) {
					Query query = entityManager.createNativeQuery(sql);
					if(builder.parameters != null) {
						for(String param : builder.parameters.keySet()) {
							query.setParameter(param, builder.parameters.get(param));
							log.trace("Routine : {}. Run item : {}. Grid : {}. Parameter : {} : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", param, builder.parameters.get(param));
						}
					}
					query.executeUpdate();
					log.debug("Routine : {}. Run item : {}. Grid : {}. Query executed!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				}
				else {
					log.debug("Routine : {}. Run item : {}. Grid : {}. Empty query!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				}
			}
		}
		else {
			GrilleDataFilter filter = new GrilleDataFilter();
			if(item.getTargetGridId() != null) {
				filter.setGrid(grilleService.getById(item.getTargetGridId()));
			}
			filter.setFilter(item.getFilter());
			RoutineItemQueryBuilder builder = new RoutineItemQueryBuilder(filter, item);
			builder.setLoaderData(routine.getLoaderData());
			if(item.getTargetGridId() != null) {
				builder.setSourceType(UniverseSourceType.INPUT_GRID);
				builder.setSourceId(item.getTargetGridId());
			}
			log.debug("Routine : {}. Run item : {}. Grid : {}. Try to build query...", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
			String sql = null;
			
			if((item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT 
					|| item.getSourceField().getSourceType() == TransformationRoutineSourceType.CALCULATE)
					&& item.getRanking() != null && item.getRanking().getDimensionId() != null) {		
				sql = builder.buildSelectIdsQuery();
				if(org.springframework.util.StringUtils.hasText(sql)) {
					Query query = entityManager.createNativeQuery(sql);
					if(builder.parameters != null) {
						for(String param : builder.parameters.keySet()) {
							query.setParameter(param, builder.parameters.get(param));
							log.trace("Routine : {}. Run item : {}. Grid : {}. Parameter : {} : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", param, builder.parameters.get(param));
						}
					}
					@SuppressWarnings("unchecked")
					List<Long> ids = query.getResultList();
					sql = item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT ? builder.buildSpotQuery(null)
							: builder.buildCalculateQuery(null);
					query = entityManager.createNativeQuery(sql);
					if(listener != null) {
						listener.createSubInfo(null, "");
						listener.startSubInfo(ids.size());
					}
					for(Long id : ids) {
						if(listener != null) {
							listener.nextSubInfoStep(1);
						}
						builder.parameters.put("id", id);			
						if(builder.parameters != null) {
							for(String param : builder.parameters.keySet()) {
								query.setParameter(param, builder.parameters.get(param));
								log.trace("Routine : {}. Run item : {}. Grid : {}. Parameter : {} : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", param, builder.parameters.get(param));
							}
						}
						query.executeUpdate();
					}
					if(listener != null) {
						listener.endSubInfo();
					}
					log.debug("Routine : {}. Run item : {}. Grid : {}. Query executed!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				}
			}			
			else {
				if(item.isDeleteEntry()) {
					sql = builder.buildDeleteQuery();
				}
				else {
					sql = builder.buildQuery();
				}
				log.debug("Routine : {}. Run item : {}. Grid : {}. Query builded!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				log.trace("Routine : {}. Run item : {}. Grid : {}. Query : \n{}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", sql);
				if(org.springframework.util.StringUtils.hasText(sql)) {
					Query query = entityManager.createNativeQuery(sql);
					if(builder.parameters != null) {
						for(String param : builder.parameters.keySet()) {
							query.setParameter(param, builder.parameters.get(param));
							log.trace("Routine : {}. Run item : {}. Grid : {}. Parameter : {} : {}", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "", param, builder.parameters.get(param));
						}
					}
					query.executeUpdate();
					log.debug("Routine : {}. Run item : {}. Grid : {}. Query executed!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				}
				else {
					log.debug("Routine : {}. Run item : {}. Grid : {}. Empty query!", routine.getName(), item.getName(), item.getGrid() != null ? item.getGrid().getName() : "");
				}
			}			
			
		}
			
		log.debug("Routine : {}. Item runed : {}", routine.getName(), item.getName());
	}


	private void buildGrids() {
		log.debug("Routine : {}. Build grids...", routine.getName());
		Map<String, AbstractSmartGrid<?>> grilles = new HashMap<>();
		for(TransformationRoutineItem item : routine.getItems()) {
			if(!item.isActive()) {
				continue;
			}
			item.setDataSourceType(routine.getDataSourceType());
			item.setDataSourceId(item.getTargetGridId());
			if(item.getTargetGridId() != null) {
				item.setGrid(getGrid(routine.getDataSourceType(), item.getTargetGridId(), grilles));
			}
			if(item.getSourceField() != null) {				
				item.getSourceField().setDataSourceType(routine.getDataSourceType());
				item.getSourceField().setDataSourceId(item.getTargetGridId());
				if(item.getSourceField().getMapping() != null && item.getSourceField().getMapping().getGridId() != null) {
					item.getSourceField().setDataSourceType(item.getSourceField().getMapping().getGridType().GetDataSource());
					item.getSourceField().setDataSourceId(item.getSourceField().getMapping().getGridId());
					item.getSourceField().getMapping().setGrid(getGrid(item.getSourceField().getDataSourceType(),item.getSourceField().getDataSourceId(), grilles));
				}
			}
			
			if(item.getSpot() != null) {	
				item.getSpot().setSourceType(item.getSpot().getDataSourceType().GetDataSource());
				item.getSpot().setGrid(getGrid(item.getSpot().getSourceType(), item.getSpot().getDataSourceId(), grilles));
			}
			
			for(TransformationRoutineCalculateItem elt : item.getCalculateItems()) {
				elt.getField().setDataSourceType(routine.getDataSourceType());
				elt.getField().setDataSourceId(item.getTargetGridId());
				if(elt.getField().getMapping() != null && item.getSourceField().getMapping().getGridId() != null) {
					elt.getField().setDataSourceType(elt.getField().getMapping().getGridType().GetDataSource());
					elt.getField().setDataSourceId(item.getSourceField().getMapping().getGridId());
					elt.getField().getMapping().setGrid(getGrid(elt.getField().getDataSourceType(),elt.getField().getDataSourceId(), grilles));
				}
				if(elt.getSpot() != null) {	
					elt.getSpot().setSourceType(elt.getSpot().getDataSourceType().GetDataSource());
					elt.getSpot().setGrid(getGrid(elt.getSpot().getSourceType(), elt.getSpot().getDataSourceId(), grilles));
				}
				
//				if(elt.getField() != null && elt.getField().getMapping() != null && elt.getField().getMapping().getGridId() != null) {
//					elt.getField().setDataSourceType(elt.getField().getMapping().getGridType().GetDataSource());
//					elt.getField().setDataSourceId(elt.getField().getMapping().getGridId());
//					elt.getField().getMapping().setGrid(getGrid(elt.getField().getDataSourceType(),elt.getField().getDataSourceId(), grilles));
//				}
			}
			
			for(TransformationRoutineConcatenateItem elt : item.getConcatenateItems()) {
				elt.getField().setDataSourceType(routine.getDataSourceType());
				elt.getField().setDataSourceId(item.getTargetGridId());
				if(elt.getField().getMapping() != null && item.getSourceField().getMapping().getGridId() != null) {
					elt.getField().setDataSourceType(elt.getField().getMapping().getGridType().GetDataSource());
					elt.getField().setDataSourceId(item.getSourceField().getMapping().getGridId());
					elt.getField().getMapping().setGrid(getGrid(elt.getField().getDataSourceType(),elt.getField().getDataSourceId(), grilles));
				}
//				if(elt.getField() != null && elt.getField().getMapping() != null && elt.getField().getMapping().getGridId() != null) {
//					elt.getField().setDataSourceType(elt.getField().getMapping().getGridType().GetDataSource());
//					elt.getField().setDataSourceId(elt.getField().getMapping().getGridId());
//					elt.getField().getMapping().setGrid(getGrid(elt.getField().getDataSourceType(),elt.getField().getDataSourceId(), grilles));
//				}
			}
		}		
		log.debug("Routine : {}. Grids builded!", routine.getName());		
	}

	private AbstractSmartGrid<?> getGrid(DataSourceType type, Long id, Map<String, AbstractSmartGrid<?>> grilles) {
		if(id != null) {
			String key = type.name() + "_" + id;
			AbstractSmartGrid<?> grid = grilles.get(key);
			if(grid == null) {
				if(type.isMaterializedGrid()) {
					Optional<SmartMaterializedGrid> resp = smartMaterializedGridRepository.findById(id);
					grid = resp.isPresent() ? resp.get() : null;
				}
				else {
					Optional<SmartGrille> resp = smartGrilleRepository.findById(id);
					grid = resp.isPresent() ? resp.get() : null;
				}				
				if(grid != null) {
					grilles.put(key, grid);
				}
			}
			return grid;
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private TransformationRoutineLog initLog() {
		TransformationRoutineLog routineLog = new TransformationRoutineLog();
		routineLog.setRoutineId(routine.getId());
		routineLog.setRoutineName(routine.getName());
		routineLog.setUsername(username);
		routineLog.setStatus(RunStatus.IN_PROGRESS);
		routineLog.setStartDate(new Timestamp(System.currentTimeMillis()));
		routineLog.setCount(routine.getItems().size());
		routineLog = logRepository.save(routineLog);
		return routineLog;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private TransformationRoutineLogItem initLogItem(TransformationRoutineItem item, TransformationRoutineLog routineLog) {
		TransformationRoutineLogItem routineLogItem = new TransformationRoutineLogItem();
		routineLogItem.setLogId(routineLog.getId());
		routineLogItem.setItemName(item.getName());
		routineLogItem.setCount(item.getPosition() + 1);
		routineLogItem.setUsername(username);
		routineLogItem.setStatus(RunStatus.IN_PROGRESS);
		routineLogItem.setStartDate(new Timestamp(System.currentTimeMillis()));
		routineLogItem = logItemRepository.save(routineLogItem);
		return routineLogItem;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void endArchiveLog(TransformationRoutineLog routineLog, String message) {
		try {
			routineLog.setMessage(message);
			routineLog.setStatus(RunStatus.ENDED);
			if (StringUtils.hasText(message)) {
				routineLog.setStatus(RunStatus.ERROR);
			}
			if(routineLog.getStartDate() == null) {
				routineLog.setStartDate(new Timestamp(System.currentTimeMillis()));
			}
			routineLog.setEndDate(new Timestamp(System.currentTimeMillis()));
			logRepository.save(routineLog);
		}
		catch (Exception e) {
			log.error("Routine : {}		Unable to save log.", routine.getName(), e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void endLogItem(TransformationRoutineLogItem routineLogItem, String message) {
		try {
			routineLogItem.setMessage(message);
			routineLogItem.setStatus(RunStatus.ENDED);
			if (StringUtils.hasText(message)) {
				routineLogItem.setStatus(RunStatus.ERROR);
			}
			if(routineLogItem.getStartDate() == null) {
				routineLogItem.setStartDate(new Timestamp(System.currentTimeMillis()));
			}
			routineLogItem.setEndDate(new Timestamp(System.currentTimeMillis()));
			logItemRepository.save(routineLogItem);
		}
		catch (Exception e) {
			log.error("Routine : {}		Unable to save log item.", routine.getName(), e);
		}
	}
	
}
