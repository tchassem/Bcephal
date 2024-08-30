/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.routine.RoutineExecutor;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinBrowserData;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinColumnCalculateItem;
import com.moriset.bcephal.grid.domain.JoinColumnConcatenateItem;
import com.moriset.bcephal.grid.domain.JoinColumnConditionItem;
import com.moriset.bcephal.grid.domain.JoinColumnConditionItemOperand;
import com.moriset.bcephal.grid.domain.JoinColumnField;
import com.moriset.bcephal.grid.domain.JoinColumnProperties;
import com.moriset.bcephal.grid.domain.JoinCondition;
import com.moriset.bcephal.grid.domain.JoinConditionItem;
import com.moriset.bcephal.grid.domain.JoinEditorData;
import com.moriset.bcephal.grid.domain.JoinExportData;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.grid.domain.JoinPublicationMethod;
import com.moriset.bcephal.grid.domain.JoinUnionKey;
import com.moriset.bcephal.grid.domain.JoinUnionKeyItem;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.repository.JoinColumnCalculateItemRepository;
import com.moriset.bcephal.grid.repository.JoinColumnConcatenateItemRepository;
import com.moriset.bcephal.grid.repository.JoinColumnConditionItemOperandRepository;
import com.moriset.bcephal.grid.repository.JoinColumnConditionItemRepository;
import com.moriset.bcephal.grid.repository.JoinColumnFieldRepository;
import com.moriset.bcephal.grid.repository.JoinColumnPropertiesRepository;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.grid.repository.JoinConditionItemRepository;
import com.moriset.bcephal.grid.repository.JoinConditionRepository;
import com.moriset.bcephal.grid.repository.JoinGridRepository;
import com.moriset.bcephal.grid.repository.JoinKeyRepository;
import com.moriset.bcephal.grid.repository.JoinRepository;
import com.moriset.bcephal.grid.repository.JoinUnionKeyItemRepository;
import com.moriset.bcephal.grid.repository.JoinUnionKeyRepository;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.repository.SmartJoinRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.repository.routine.RoutineExecutorReopository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.BGroupService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Service
public class JoinService extends MainObjectService<Join, JoinBrowserData>{

	@Autowired
	JoinRepository repository;
	
	@Autowired
	JoinGridRepository joinGridRepository;
	
	@Autowired
	JoinColumnRepository joinColumnRepository;
	
	@Autowired
	JoinKeyRepository joinKeyRepository;
	
	@Autowired
	JoinUnionKeyRepository joinUnionKeyRepository;
	
	@Autowired
	JoinUnionKeyItemRepository joinUnionKeyItemRepository;
	
	@Autowired
	JoinConditionRepository joinConditionRepository;
	
	@Autowired
	JoinConditionItemRepository joinConditionItemRepository;
	
	@Autowired
	RoutineExecutorReopository routineExecutorReopository;
	
	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	JoinColumnPropertiesRepository joinColumnPropertiesRepository;
	
	@Autowired
	JoinColumnFieldRepository joinColumnFieldRepository;
	
	@Autowired
	JoinColumnConcatenateItemRepository joinColumnConcatenateItemRepository;
	
	@Autowired
	JoinColumnCalculateItemRepository joinColumnCalculateItemRepository;
	
	@Autowired
	JoinColumnConditionItemRepository joinColumnConditionItemRepository;
	
	@Autowired
	JoinColumnConditionItemOperandRepository joinColumnConditionItemOperandRepository;
	
	@Autowired
	SmartGrilleRepository smartGrilleRepository;
	
	@Autowired
	SmartJoinRepository smartJoinRepository;
	
	@Autowired
	SmartMaterializedGridRepository smartMaterializedGridRepository;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired 
	MaterializedGridService materializedGridService;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	BGroupService groupService;
	
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
	
	@Autowired
	SpotRepository spotRepository;
	
	@Autowired
	SpotService spotService;
	
	@PersistenceContext
	EntityManager session;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.REPORTING_REPORT_JOIN_GRID;
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
	@Transactional
	public Long copy(Long id, String newName, Locale locale) {
		log.debug("Try to copy join : {} as {}", id, newName);
		List<Join> objects = getAllByName(newName);
		if(objects.size() > 0) {
			throw new BcephalException("Duplicate name : " + newName);
		}
		Join item = getById(id);
		if(item != null) {
			HashMap<Long, JoinColumn> columns = new HashMap<>();
			Join copy = item.copy(columns);
			copy.setName(newName);
			Join result = saveWithoutCommit(copy, locale);
			result.setGrids(result.getGridListChangeHandler().getItems());
			result.setKeys(result.getKeyListChangeHandler().getItems());
			result.setColumns(result.getColumnListChangeHandler().getItems());
			result.setConditions(result.getConditionListChangeHandler().getItems());
			
			item.updateCustomColumns(result, columns);
			result = saveWithoutCommit(result, locale);
			return result.getId();
		}
		else {
			throw new BcephalException("Join not found : " + id);
		}
	}
	
	
	@Override
	public JoinEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<Join> superdata = super.getEditorData(filter, session, locale);
		JoinEditorData data = new JoinEditorData(superdata);	
		List<GrilleType> types = getSmallGridTypes();
		Long profileId = (Long) session.getAttribute(RequestParams.BC_PROFILE);
		String projectCode = (String) session.getAttribute(RequestParams.BC_PROJECT);		
		List<Long> hightGridItems = getHidedObjectId(profileId, grilleService.getFunctionalityCode(), projectCode);
		List<Long> hightMaterializedItems = getHidedObjectId(profileId, FunctionalityCodes.SOURCING_MATERIALIZED_GRID, projectCode);
		List<Long> hightJointItems = getHidedObjectId(profileId, FunctionalityCodes.REPORTING_REPORT_JOIN_GRID, projectCode);
		if(filter.getId() != null) {
			hightJointItems.add(filter.getId());
		}
		if(hightGridItems != null && hightGridItems.size() > 0) {
			data.getGrids().addAll(smartGrilleRepository.findByTypesAllExclude(types, hightGridItems));
		}else {
			data.getGrids().addAll(smartGrilleRepository.findByTypes(types));
		}
		if(hightMaterializedItems != null && hightMaterializedItems.size() > 0) {
			data.getGrids().addAll(smartMaterializedGridRepository.findAllExclude(hightMaterializedItems));
		}else {
			data.getGrids().addAll(smartMaterializedGridRepository.findAll());
		}
		if(hightJointItems != null && hightJointItems.size() > 0) {
			data.getGrids().addAll(smartJoinRepository.findAllExclude(hightJointItems));
		}else {
			data.getGrids().addAll(smartJoinRepository.findAll());
		}
		List<Object[]> routines = smartGrilleRepository.findRoutines();
		for(Object[] objs : routines) {
			data.getRoutines().add(new Nameable(((Number)objs[0]).longValue(), (String)objs[1]) );
		}
		data.setSequences(incrementalNumberRepository.getAllIncrementalNumbers());
		
		if(data.getItem() != null && data.getItem().getId() != null && data.getItem().isPublished()) {
			initEditorDataForJoin(data, session, locale);
		}
		
		if(data.getItem() != null && data.getItem().getId() != null) {
			for(JoinGrid joinGrid : data.getItem().getGrids()) {
				if(joinGrid.getGridId() != null) {
					try {
						if(joinGrid.isGrid()) {
							String name = smartGrilleRepository.findNameById(joinGrid.getGridId());
							joinGrid.setName(name);
						}
						else if(joinGrid.isMaterializedGrid()) {
							String name = smartMaterializedGridRepository.findNameById(joinGrid.getGridId());
							joinGrid.setName(name);
						}
						else if(joinGrid.isJoin()) {
							String name = smartJoinRepository.findNameById(joinGrid.getGridId());
							joinGrid.setName(name);
						}
					}
					catch (NoResultException e) {
						
					}
					catch (Exception e) {
						log.error("", e);
					}
				}
			}
		}
		
		return data;
	}
	
	protected String initEditorDataForJoin(JoinEditorData data, HttpSession session, Locale locale) throws Exception {
				
		Join grid = data.getItem();
		
		List<Model> models = new ArrayList<>(0);
		List<Measure> measures = new ArrayList<>(0);
		List<Period> periods = new ArrayList<>(0);
		Model model = new Model();
		models.add(model);
		model.setName(grid.getName());
		Entity entity = new Entity();
		entity.setName("Columns");
		model.getEntities().add(entity);

		for (JoinColumn column : grid.getColumns()) {
			if (column.getType() == DimensionType.MEASURE) {
				measures.add(new Measure(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));						
			} else if (column.getType() == DimensionType.ATTRIBUTE) {						
				entity.getAttributes().add(new Attribute(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));
			} else if (column.getType() == DimensionType.PERIOD) {
				periods.add(new Period(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));
			}
		}

		Comparator<Dimension> comparator = new Comparator<Dimension>() {
			@Override
			public int compare(Dimension data1, Dimension data2) {
				return data1.getName().compareTo(data2.getName());
			}
		};

		if (models.get(0).getEntities().size() > 0) {
			Collections.sort(models.get(0).getEntities().get(0).getAttributes(), comparator);
		}
		Collections.sort(measures, comparator);
		Collections.sort(periods, comparator);
		
		data.setModels(models);
		data.setMeasures(measures);
		data.setPeriods(periods);
		return grid.getName();
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
	public Join getById(Long id) {
		log.debug("Try to get Join by id : {}", id);
		Join join = super.getById(id);
		if (join != null) {
			join.getRoutineListChangeHandler().setOriginalList(routineExecutorReopository.findByObjectIdAndObjectType(join.getId(), Join.class.getName()));
		}
		return join;
	}
	
	public Join getNewJoin(String name) {
		Join join = new Join();
		String baseName = "Join ";
		if(StringUtils.hasText(name)) {
			baseName = name  + " ";
		}
		int i = 1;
		join.setName(baseName + i);
		while (getByName(join.getName()) != null) {
			i++;
			join.setName(baseName + i);
		}		
		return join;
	}
	
	@Override
	protected Join getNewItem() {
		Join join = new Join();
		String baseName = "Join ";
		int i = 1;
		join.setName(baseName + i);
		while (getByName(join.getName()) != null) {
			i++;
			join.setName(baseName + i);
		}		
		return join;
	}

	@SuppressWarnings("unchecked")	
	public BrowserDataPage<Object[]> searchRows(JoinFilter filter, java.util.Locale locale) throws Exception {		
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		if(filter.getJoin() != null) {
			loadFilterClosures(filter);	
			boolean ispublished = filter.getJoin() != null && filter.getJoin().isPublished();
			JoinQueryBuilder builder = ispublished ? new JoinPublishedQueryBuilder(filter, joinColumnRepository, spotService) 
					: new JoinQueryBuilder(filter, joinColumnRepository, spotService);
			Integer count = 0;
			if(filter.isAllowRowCounting()) {
				String sql = builder.buildCountQuery();
				log.trace("Count query : {}", sql);
				Query query = this.session.createNativeQuery(sql);
				for (String key : builder.getParameters().keySet()) {
					query.setParameter(key, builder.getParameters().get(key));
				}
				Number number = (Number)query.getSingleResult();
				count = number.intValue();
				if(count == 0) {
					return page;
				}
				if(filter.isShowAll()) {
					page.setTotalItemCount(count);
					page.setPageCount(1);
					page.setCurrentPage(1);
					page.setPageFirstItem(1);
				}
				else {
					page.setTotalItemCount(count);
					page.setPageCount(((int)count/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
					page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
					if(page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
				}
			} 
			else {
				page.setTotalItemCount(1);
				page.setPageCount(1);
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
						
			String sql = builder.buildQuery();
			log.trace("Search query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			for (String key : builder.getParameters().keySet()) {
				query.setParameter(key, builder.getParameters().get(key));
			}
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			
			List<Object[]> objects = null;
			int columnCount = filter.getJoin().getColumns().size();
			if(columnCount <= 1) {
				List<Object> objs = query.getResultList();
				objects = new ArrayList<Object[]>();
				for(Object ob : objs) {
					if(ob instanceof Object[]) {
						objects.add((Object[])ob);
					}
					else {
						objects.add(new Object[]{ob});
					}
				}
			}
			else {
				objects = query.getResultList();
			}
			
//			@SuppressWarnings("unchecked")
//			List<Object[]> objects = query.getResultList();
			formatDates(objects, filter.getJoin());
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(objects); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	JoinQueryBuilder getJoinQueryBuilder(JoinFilter filter) {
		boolean ispublished = filter.getJoin() != null && filter.getJoin().isPublished();
		JoinQueryBuilder builder = new JoinQueryBuilder(filter, joinColumnRepository, spotService);
		if(filter.getGridType() != null && filter.getGridType().equals(GrilleType.RECONCILIATION)) {
			if(ispublished) {
				builder = new JoinRecoPublishedQueryBuilder(filter, joinColumnRepository, spotService);
			}else {
				builder = new JoinRecoQueryBuilder(filter, joinColumnRepository, spotService);
			}
		}else {
			if(ispublished) {
				builder = new JoinPublishedQueryBuilder(filter, joinColumnRepository, spotService);
			}
		}
		if(builder != null) {
			builder.setParameterRepository(parameterRepository);
		}
		return builder;
	}
	
	@SuppressWarnings("unchecked")	
	public BrowserDataPage<GridItem> searchRows2(JoinFilter filter, java.util.Locale locale) throws Exception {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		page.setPageSize(filter.getPageSize());
		if(filter.getJoin() != null) {
			filter.setFilter(filter.getJoin().getFilter());
			loadFilterClosures(filter);	
			JoinQueryBuilder builder = getJoinQueryBuilder(filter);
			Integer count = 0;
			if(filter.isAllowRowCounting()) {
				String sql = builder.buildCountQuery();
				log.trace("Count query : {}", sql);
				Query query = this.session.createNativeQuery(sql);
				for (String key : builder.getParameters().keySet()) {
					query.setParameter(key, builder.getParameters().get(key));
				}
				Number number = (Number)query.getSingleResult();
				count = number.intValue();
				if(count == 0) {
					return page;
				}
				if(filter.isShowAll()) {
					page.setTotalItemCount(count);
					page.setPageCount(1);
					page.setCurrentPage(1);
					page.setPageFirstItem(1);
				}
				else {
					page.setTotalItemCount(count);
					page.setPageCount(((int)count/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
					page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
					if(page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
				}
			} 
			else {
				page.setTotalItemCount(1);
				page.setPageCount(1);
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
						
			String sql = builder.buildQuery();
			log.trace("Search query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			for (String key : builder.getParameters().keySet()) {
				query.setParameter(key, builder.getParameters().get(key));
			}
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			
			List<Object[]> objects = null;
			int columnCount = filter.getJoin().getColumns().size();
			if(columnCount <= 1) {
				List<Object[]> objs = query.getResultList();
				objects = new ArrayList<Object[]>();
				for(Object ob : objs) {
					if(ob instanceof Object[]) {
						objects.add((Object[])ob);
					}
					else {
						objects.add(new Object[]{ob});
					}
				}
			}
			else {
				objects = query.getResultList();
			}
			
//			@SuppressWarnings("unchecked")
//			List<Object[]> objects = query.getResultList();
			formatDates(objects, filter.getJoin());
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(GridItem.buildItems(objects)); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	

	public GrilleColumnCount getColumnCountDetails(JoinFilter filter, java.util.Locale locale) throws Exception {	
		GrilleColumnCount columnCount = new GrilleColumnCount();
		loadFilterClosures(filter);	
		boolean ispublished = filter.getJoin() != null && filter.getJoin().isPublished();
		JoinQueryBuilder builder = ispublished ? new JoinPublishedQueryBuilder(filter, joinColumnRepository, spotService) 
				: new JoinQueryBuilder(filter, joinColumnRepository, spotService);
		String sql = builder.buildColumnCountDetailsQuery();
		log.trace("Column count details query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		for (String key : builder.getParameters().keySet()) {
			query.setParameter(key, builder.getParameters().get(key));
		}
		Object[] number = (Object[])query.getSingleResult();
		columnCount.setCountItems(((Number)number[0]).longValue());
		columnCount.setSumItems(BigDecimal.valueOf(((Number)number[1]).doubleValue()));
		columnCount.setMaxItem(BigDecimal.valueOf(((Number)number[2]).doubleValue()));
		columnCount.setMinItem(BigDecimal.valueOf(((Number)number[3]).doubleValue()));
		columnCount.setAverageItems(BigDecimal.valueOf(((Number)number[4]).doubleValue()));
		return columnCount;
	}
	
	
	public long getColumnDuplicateCount(JoinFilter filter, java.util.Locale locale) throws Exception {	
		loadFilterClosures(filter);	
		boolean ispublished = filter.getJoin() != null && filter.getJoin().isPublished();
		JoinQueryBuilder builder = ispublished ? new JoinPublishedQueryBuilder(filter, joinColumnRepository, spotService) 
				: new JoinQueryBuilder(filter, joinColumnRepository, spotService);
		String sql = builder.buildColumnDuplicateCountQuery();
		log.trace("Column duplicate count query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		for (String key : builder.getParameters().keySet()) {
			query.setParameter(key, builder.getParameters().get(key));
		}
		Number number = (Number)query.getSingleResult();
		return number.longValue();
	}
	
	public BrowserDataPage<Object[]> getColumnDuplicate(JoinFilter filter, java.util.Locale locale) throws Exception {
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		loadFilterClosures(filter);	
		boolean ispublished = filter.getJoin() != null && filter.getJoin().isPublished();
		JoinQueryBuilder builder = ispublished ? new JoinPublishedQueryBuilder(filter, joinColumnRepository, spotService) 
				: new JoinQueryBuilder(filter, joinColumnRepository, spotService);
		Long count = 0L;
		if(filter.isAllowRowCounting()) {
			count = getColumnDuplicateCount(filter, locale);
			if(count == 0) {
				return page;
			}
			if(filter.isShowAll()) {
				page.setTotalItemCount(count.intValue());
				page.setPageCount(1);
				page.setCurrentPage(1);
				page.setPageFirstItem(1);
			}
			else {
				page.setTotalItemCount(count.intValue());
				page.setPageCount((count.intValue()/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				if(page.getCurrentPage() > page.getPageCount()) {
					page.setCurrentPage(page.getPageCount());
				}
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
		} 
		else {
			page.setTotalItemCount(1);
			page.setPageCount(1);
			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
		}
					
		String sql = builder.buildColumnDuplicateQuery();
		log.trace("Column duplicate query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		for (String key : builder.getParameters().keySet()) {
			query.setParameter(key, builder.getParameters().get(key));
		}
		if(!filter.isShowAll()) {
			query.setFirstResult(page.getPageFirstItem() - 1);
			query.setMaxResults(page.getPageSize());
		}
		@SuppressWarnings("unchecked")
		List<Object[]> objects = query.getResultList();
		//formatDates(objects);
		page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
		page.setItems(objects); 
		log.debug("Row found : {}", objects.size());		
		return page;
	}

	
	
	@SuppressWarnings("unchecked")	
	public List<Object[]> getGridRows(JoinFilter filter, int firstItem, int pageSize, boolean formatDates) throws Exception {		
		if(filter.getJoin() != null) {
			JoinQueryBuilder builder = new JoinQueryBuilder(filter,joinColumnRepository, spotService);
			String sql = builder.buildQuery();
			log.trace("Search query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			for (String key : builder.getParameters().keySet()) {
				query.setParameter(key, builder.getParameters().get(key));
			}
			query.setFirstResult(firstItem);
			query.setMaxResults(pageSize);
			
			List<Object[]> objects = null;
			int columnCount = filter.getJoin().getColumns().size();
			if(columnCount <= 1) {
				List<Object> objs = query.getResultList();
				objects = new ArrayList<Object[]>();
				for(Object ob : objs) {
					if(ob instanceof Object[]) {
						objects.add((Object[])ob);
					}
					else {
						objects.add(new Object[]{ob});
					}
				}
			}
			else {				
				objects = query.getResultList();
			}
			if(formatDates) {
				formatDates(objects, filter.getJoin());			
			}
			log.debug("Row found : {}", objects.size());
			return objects;
		}		
		return new ArrayList<Object[]>();
	}
	
	
	public Number getGridRowCount(JoinFilter filter) throws Exception {		
		if(filter.getJoin() != null) {
			JoinQueryBuilder builder = new JoinQueryBuilder(filter,joinColumnRepository, spotService);
			String sql = builder.buildCountQuery();
			log.trace("Count query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			for (String key : builder.getParameters().keySet()) {
				query.setParameter(key, builder.getParameters().get(key));
			}
			Number number = (Number)query.getSingleResult();
			return number;
		}		
		return 0;
	}
	
	
	
	
	private void formatDates(List<Object[]> objects, Join grid) {
		for (Object obj : objects) {
			Object[] row = null;
			if(obj instanceof Object[]) {
				row = (Object[])obj;
			}
			else {
				row = new Object[] {obj};
			}
			formatDates(row, grid);
		}
	}
	
	
	private void formatDates(Object[] row, Join grid) {
		for (JoinColumn col : getPeriodColumns(grid)) {
			if (row.length > col.getPosition()) {
				Object period = row[col.getPosition()];
				if (period != null) {
					try {
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(period.toString());
						String formatPattern = col.getFormat() != null && StringUtils.hasText(col.getFormat().getDefaultFormat()) ? col.getFormat().getDefaultFormat() : "dd/MM/yyyy";
						SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
						row[col.getPosition()] = formatter.format(date);
					} catch (ParseException e) {
						
					}
				}
			}
		}		
	}
	
	private List<JoinColumn> getPeriodColumns(Join grid) {
		List<JoinColumn> positions = new ArrayList<>(0);
		for (JoinColumn col : grid.getColumns()) {
			if (col.getType() == DimensionType.PERIOD) {
				positions.add(col);
			}
		}
		return positions;
	}
	
	public void loadFilterClosures(JoinFilter filter) {
		loadFilterClosures(filter, true);
	}
	
	public void loadFilterClosures(JoinFilter filter, boolean loadRecoData) {
		if (filter != null && filter.getJoin() != null) {
			loadFilterClosures(filter.getJoin());			
			filter.setSmartGrids(new ArrayList<>());			
			for(JoinGrid item :  filter.getJoin().getGrids()) {
				AbstractSmartGrid<?> grid = getSmartGrid(item);
				String name = item.isMaterializedGrid() ? "materialized grid"
						: item.isJoin() ? "join" : "grid"; 
				if(grid == null) {
					throw new BcephalException("The {} '{}' is not found!", name, item.getName());
				}
				if(!grid.isPublished()) {
					throw new BcephalException("The {} '{}' is not published!", name, item.getName());
				}
				filter.getSmartGrids().add(grid);
			}
			
			for(JoinColumn column :  filter.getJoin().getColumns()) {	
				loadClosure(column, filter);
			}
			
			for(JoinCondition condition :  filter.getJoin().getConditionListChangeHandler().getItems()) {	
				loadClosure(condition, filter);
			}
			
			for(JoinKey key :  filter.getJoin().getKeyListChangeHandler().getItems()) {	
				loadClosure(key, filter);
			}
			
			if(loadRecoData && filter.getRecoData() != null) {
				filter.getRecoData().setAmountMeasureId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getAmountMeasureId(), DimensionType.MEASURE));
				filter.getRecoData().setRecoAttributeId(getJoinColumnId(filter.getJoin(), filter.getRecoAttributeId(), DimensionType.ATTRIBUTE));
				filter.getRecoData().setPartialRecoAttributeId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getPartialRecoAttributeId(), DimensionType.ATTRIBUTE));
				filter.getRecoData().setReconciliatedMeasureId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getReconciliatedMeasureId(), DimensionType.MEASURE));
				filter.getRecoData().setRemainningMeasureId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getRemainningMeasureId(), DimensionType.MEASURE));
				filter.getRecoData().setFreezeAttributeId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getFreezeAttributeId(), DimensionType.ATTRIBUTE));
				filter.getRecoData().setNeutralizationAttributeId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getNeutralizationAttributeId(), DimensionType.ATTRIBUTE));
				filter.getRecoData().setNoteAttributeId(getJoinColumnId(filter.getJoin(), filter.getRecoData().getNoteAttributeId(), DimensionType.ATTRIBUTE));
				filter.getRecoData().setDebitCreditAttributeId(getJoinColumnId(filter.getJoin(), filter.getDebitCreditAttributeId(), DimensionType.ATTRIBUTE));
			}
		}
	}
	
	private void loadClosure(JoinCondition condition, JoinFilter filter) {
		if(condition.getItem1() != null) {
			loadClosure(condition.getItem1(), filter);
		}
		if(condition.getItem2() != null) {
			loadClosure(condition.getItem2(), filter);
		}
	}
	
	private void loadClosure(JoinConditionItem item, JoinFilter filter) {
		if(item.isColumn() && item.getGridId() != null && item.getGridType() == JoinGridType.REPORT_GRID) {
			AbstractSmartGrid<?> grid = getGrid(item.getGridId(), JoinGridType.REPORT_GRID, filter);
			if(grid != null && ((SmartGrille)grid).isDataSourceMaterialized()) {
				item.setDataSourceType(((SmartGrille)grid).getDataSourceType());
				item.setColumnId(item.getDimensionId());
			}
		}
	}
	
	private void loadClosure(JoinKey key, JoinFilter filter) {
		if(key.getGridId1() != null && key.getGridType1() == JoinGridType.REPORT_GRID) {
			AbstractSmartGrid<?> grid = getGrid(key.getGridId1(), JoinGridType.REPORT_GRID, filter);
			if(grid != null && ((SmartGrille)grid).isDataSourceMaterialized()) {
				key.setDataSourceType1(((SmartGrille)grid).getDataSourceType());
				key.setColumnId1(key.getValueId1());
			}
		}
		if(key.getGridId2() != null && key.getGridType2() == JoinGridType.REPORT_GRID) {
			AbstractSmartGrid<?> grid = getGrid(key.getGridId2(), JoinGridType.REPORT_GRID, filter);
			if(grid != null && ((SmartGrille)grid).isDataSourceMaterialized()) {
				key.setDataSourceType2(((SmartGrille)grid).getDataSourceType());
				key.setColumnId2(key.getValueId2());
			}
		}
	}
	
	private void loadClosure(JoinColumn column, JoinFilter filter) {
		if(column.getGridId() != null && column.getGridType() == JoinGridType.GRID) {
			for(AbstractSmartGrid<?> grid : filter.getSmartGrids()) {
				if((grid.getId() == column.getGridId() || grid.getId().equals(column.getGridId())) && JoinGridType.REPORT_GRID == grid.getGridType()) {
					column.setDataSourceType(((SmartGrille)grid).getDataSourceType());
				}
			}
		}
		
		if(column.isStandard()) {		
			if(column.getGridId() != null && column.getGridType() == JoinGridType.REPORT_GRID) {
				AbstractSmartGrid<?> grid = getGrid(column.getGridId(), JoinGridType.REPORT_GRID, filter);
				if(grid != null && ((SmartGrille)grid).isDataSourceMaterialized()) {
					column.setDataSourceType(((SmartGrille)grid).getDataSourceType());
					column.setColumnId(column.getDimensionId());
				}
			}
		}
		else if(column.isCustom() && column.getProperties() != null && column.getProperties().getField() != null) {
			loadPropertiesClosure(column.getProperties(), filter);
		}
		
		
	}
	
	private void loadPropertiesClosure(JoinColumnProperties properties, JoinFilter filter) {
		JoinColumnField field = properties.getField();		
		if(field.isConcatenate()) {
			for(JoinColumnConcatenateItem item : properties.getConcatenateItemListChangeHandler().getItems()) {
				if(item.getField() != null) {
					loadFieldClosure(item.getField(), filter, properties);
				}
			}
		}
		else if(field.isCalculate()) {
			for(JoinColumnCalculateItem item : properties.getCalculateItemListChangeHandler().getItems()) {
				if(item.getField() != null) {
					loadFieldClosure(item.getField(), filter, properties);
				}
			}
		}
		
		else {
			loadFieldClosure(field, filter, properties);
		}
	}
	
	private void loadFieldClosure(JoinColumnField field, JoinFilter filter, JoinColumnProperties properties) {
		if(field.isColumn() && field.getGridId() != null && field.getGridType() == JoinGridType.REPORT_GRID) {
			AbstractSmartGrid<?> grid = getGrid(field.getGridId(), JoinGridType.REPORT_GRID, filter);
			if(grid != null && ((SmartGrille)grid).isDataSourceMaterialized()) {
				field.setDataSourceType(((SmartGrille)grid).getDataSourceType());
				field.setColumnId(field.getDimensionId());
			}
		}
		else if(field.isCondition()) {
			for(JoinColumnConditionItem item : properties.getConditionItemListChangeHandler().getItems()) {
				if(item.getThenField() != null) {
					loadFieldClosure(item.getThenField(), filter, properties);
				}
				for(JoinColumnConditionItemOperand operand : item.getOperandListChangeHandler().getItems()) {
					if(operand.getField1() != null) {
						loadFieldClosure(operand.getField1(), filter, properties);
					}
					if(operand.getField2() != null) {
						loadFieldClosure(operand.getField2(), filter, properties);
					}
				}
			}
		}
		else if(field.isSpot() && field.getSpot() != null) {
			
		}
		
	}
	
	private AbstractSmartGrid<?> getGrid(Long gridId, JoinGridType gridType, JoinFilter filter) {
		for(AbstractSmartGrid<?> grid : filter.getSmartGrids()) {
			if((grid.getId() == gridId || grid.getId().equals(gridId)) && gridType == grid.getGridType()) {
				return grid;
			}
		}
		return null;
	}
	
	public JoinColumn getJoinColumn(Join join, Long mainGridColumnId, DimensionType type) {
		if(join == null || join.getMainGrid() == null || mainGridColumnId == null) {
			return null;
		}
		return join.getColumn(mainGridColumnId, type, join.getMainGrid().getGridType());
	}
	
	public Long getJoinColumnId(Join join, Long mainGridColumnId, DimensionType type) {
		JoinColumn column = getJoinColumn(join, mainGridColumnId, type);
		return column != null ? column.getId() : null;
	}
	
	public void loadFilterClosures(Join join) {
		if (join != null) {
			List<JoinGrid> items = join.getGridListChangeHandler().getItems();			
			Collections.sort(items, new Comparator<JoinGrid>() {
				@Override
				public int compare(JoinGrid value1, JoinGrid value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			join.setGrids(items);
			
			if(join.getGrids().size() == 0) {
				throw new BcephalException("The list of grids to join is empty!");
			}
									
			
			List<JoinColumn> columns = join.getColumnListChangeHandler().getItems();
			Collections.sort(columns, new Comparator<JoinColumn>() {
				@Override
				public int compare(JoinColumn value1, JoinColumn value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			join.setColumns(columns);
			
			for(JoinColumn column :  columns) {	
				if(!column.isPersistent()) {
					throw new BcephalException("You have to save your join.");
				}
				if(column.getType() == DimensionType.MEASURE) {
					if(column.getDimensionName() == null) {
//						Measure measure = new MeasureService(getUserSession()).getByOid(column.getDimensionId());
//						column.setValueName(measure != null ? measure.getName() : "");
					}
				}
				else if(column.getType() == DimensionType.ATTRIBUTE) {
					if(column.getDimensionName() == null) {
//						Attribute attribute = new AttributeService(getUserSession()).getByOid(column.getDimensionId());
//						column.setValueName(attribute != null ? attribute.getName() : "");
					}	
				}
				else if(column.getType() == DimensionType.PERIOD) {
					if(column.getDimensionName() == null) {
//						PeriodName period = new PeriodNameService(getUserSession()).getByOid(column.getDimensionId());
//						column.setValueName(period != null ? period.getName() : "");
					}
				}
				
				if(column.getProperties() != null) {
					List<JoinColumnCalculateItem> calculates = column.getProperties().getCalculateItemListChangeHandler().getItems();
					Collections.sort(calculates, new Comparator<JoinColumnCalculateItem>() {
						@Override
						public int compare(JoinColumnCalculateItem value1, JoinColumnCalculateItem value2) {
							return value1.getPosition() - value2.getPosition();
						}
					});
					column.getProperties().setCalculateItems(calculates);
				}
				if(column.getProperties() != null) {
					List<JoinColumnConcatenateItem> calculates = column.getProperties().getConcatenateItemListChangeHandler().getItems();
					Collections.sort(calculates, new Comparator<JoinColumnConcatenateItem>() {
						@Override
						public int compare(JoinColumnConcatenateItem value1, JoinColumnConcatenateItem value2) {
							return value1.getPosition() - value2.getPosition();
						}
					});
					column.getProperties().setConcatenateItems(calculates);
				}				
				if(column.getProperties() != null) {
					List<JoinColumnConditionItem> conditions = column.getProperties().getConditionItemListChangeHandler().getItems();
					Collections.sort(conditions, new Comparator<JoinColumnConditionItem>() {
						@Override
						public int compare(JoinColumnConditionItem value1, JoinColumnConditionItem value2) {
							return value1.getPosition() - value2.getPosition();
						}
					});
					column.getProperties().setConditionItems(conditions);
					for(JoinColumnConditionItem condition :  conditions) {				
						List<JoinColumnConditionItemOperand> operands = condition.getOperandListChangeHandler().getItems();
						Collections.sort(operands, new Comparator<JoinColumnConditionItemOperand>() {
							@Override
							public int compare(JoinColumnConditionItemOperand value1, JoinColumnConditionItemOperand value2) {
								return value1.getPosition() - value2.getPosition();
							}
						});
						condition.setOperands(operands);
					}
				}
				
				if(column.isCustom() && column.getProperties() != null && column.getProperties().getField() != null
						&& column.getProperties().getField().getColumnId() != null) {
					if(column.getProperties().getField().isSpot()) {
						Optional<Spot> response = spotRepository.findById(column.getProperties().getField().getColumnId());
						if(response.isPresent()) {
							column.getProperties().getField().setSpot(response.get());
						}
					}
					else if(column.getProperties().getField().isSequence()) {
						Optional<IncrementalNumber> response = incrementalNumberRepository.findById(column.getProperties().getField().getColumnId());
						if(response.isPresent()) {
							//column.getProperties().getField().setSequence(response.get());
						}
					}
				}
			}
			
			List<JoinKey> keys = join.getKeyListChangeHandler().getItems();
			Collections.sort(keys, new Comparator<JoinKey>() {
				@Override
				public int compare(JoinKey value1, JoinKey value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			join.setKeys(keys);
			
			List<JoinCondition> conditions = join.getConditionListChangeHandler().getItems();
			Collections.sort(conditions, new Comparator<JoinCondition>() {
				@Override
				public int compare(JoinCondition value1, JoinCondition value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			join.setConditions(conditions);			
		}
	}
		
	private AbstractSmartGrid<?> getSmartGrid(JoinGrid grid){
		if(grid != null && grid.getGridType() != null && grid.getGridId() != null) {
			Optional<?> resp = grid.isMaterializedGrid() ? smartMaterializedGridRepository.findById(grid.getGridId())
					: grid.isJoin() ? smartJoinRepository.findById(grid.getGridId()) 
							: smartGrilleRepository.findById(grid.getGridId());			
			return resp.isEmpty() ? null : (AbstractSmartGrid<?>)resp.get(); 
		}
		return null;
	}
		
	@Override
	protected JoinBrowserData getNewBrowserData(Join item) {
		return new JoinBrowserData(item);
	}

	@Override
	protected Specification<Join> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_JOIN, ParameterType.JOIN);
		return (root, query, cb) -> {
			RequestQueryBuilder<Join> qBuilder = new RequestQueryBuilder<Join>(root, query, cb);
			qBuilder.select(Join.class);	
			qBuilder.addIsNullCriteria("gridType");
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}	
			if (parameter != null && parameter.getLongValue() != null) {
				hidedObjectIds.add(parameter.getLongValue());
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
		} else if ("showAllRowsByDefault".equalsIgnoreCase(columnFilter.getName())) {
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
		}else if ("materialized".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("published");
			columnFilter.setType(Boolean.class);
		}
	}
	
	@Override
	@Transactional
	public Join save(Join join, Locale locale) {
		log.debug("Try to  Save Join : {}", join);		
		try {	
			join = saveWithoutCommit(join, locale);			
			log.debug("Join saved : {} ", join.getId());
	        return join;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save join : {}", join, e);
			String message = getMessageSource().getMessage("unable.to.save.join.model", new Object[]{join} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	public Join saveWithoutCommit(Join join, Locale locale) {
		if(join == null) {
			String message = getMessageSource().getMessage("unable.to.save.null.join", new Object[]{join} , locale);
			throw new BcephalException(message);
		}
		if(!StringUtils.hasLength(join.getName())) {
			String message = getMessageSource().getMessage("unable.to.save.join.with.empty.name", new String[]{join.getName()} , locale);
			throw new BcephalException(message);
		}
		
		ListChangeHandler<JoinGrid> grids = join.getGridListChangeHandler();			
		ListChangeHandler<JoinKey> keys = join.getKeyListChangeHandler();
		ListChangeHandler<JoinUnionKey> unionKeys = join.getUnionKeyListChangeHandler();
		ListChangeHandler<JoinColumn> columns = join.getColumnListChangeHandler();
		ListChangeHandler<JoinCondition> conditions = join.getConditionListChangeHandler();			
		ListChangeHandler<RoutineExecutor> routines = join.getRoutineListChangeHandler();
									
		validateBeforeSave(join, locale);
		join.setModificationDate(new Timestamp(System.currentTimeMillis()));
		if(join.getAdminFilter() != null) {
			join.setAdminFilter(universeFilterService.save(join.getAdminFilter()));
		}
		if(join.getFilter() != null) {
			join.setFilter(universeFilterService.save(join.getFilter()));
		}		
		
		join = getRepository().save(join);
		Join joinId = join;
		
		grids.getNewItems().forEach( item -> {
			log.trace("Try to save join grid : {}", item);
			item.setJoinId(joinId);
			joinGridRepository.save(item);
			log.trace("Join grid saved : {}", item.getId());
		});
		grids.getUpdatedItems().forEach( item -> {
			log.trace("Try to save join grid : {}", item);
			item.setJoinId(joinId);
			joinGridRepository.save(item);
			log.trace("Join grid saved : {}", item.getId());
		});
		grids.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete join grid : {}", item);
				joinGridRepository.deleteById(item.getId());
				log.trace("Join grid deleted : {}", item.getId());
			}
		});
		
		keys.getNewItems().forEach( item -> {
			log.trace("Try to save join key : {}", item);
			item.setJoinId(joinId);
			joinKeyRepository.save(item);
			log.trace("Join key saved : {}", item.getId());
		});
		keys.getUpdatedItems().forEach( item -> {
			log.trace("Try to save join key : {}", item);
			item.setJoinId(joinId);
			joinKeyRepository.save(item);
			log.trace("Join key saved : {}", item.getId());
		});
		keys.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete join key : {}", item);
				joinKeyRepository.deleteById(item.getId());
				log.trace("Join key deleted : {}", item.getId());
			}
		});
					
		unionKeys.getNewItems().forEach( item -> {
			log.trace("Try to save join union key : {}", item);
			save(item, joinId, locale);
			log.trace("Join union key saved : {}", item.getId());
		});
		unionKeys.getUpdatedItems().forEach( item -> {
			log.trace("Try to save join union key : {}", item);
			save(item, joinId, locale);
			log.trace("Join union key saved : {}", item.getId());
		});
		unionKeys.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete join union key : {}", item);
				delete(item, locale);
				log.trace("Join union key deleted : {}", item.getId());
			}
		});
		
		columns.getNewItems().forEach( item -> {
			save(item, joinId, locale);
		});
		columns.getUpdatedItems().forEach( item -> {
			save(item, joinId, locale);
		});
		columns.getDeletedItems().forEach( item -> {
			delete(item, locale);
		});
		
		conditions.getNewItems().forEach( item -> {
			log.trace("Try to save join condition : {}", item);
			item.setJoinId(joinId);
			if(item.getItem1() != null) {
				log.trace("Try to save join condition item1 : {}", item.getItem1());
				item.setItem1(joinConditionItemRepository.save(item.getItem1()));
				log.trace("Join condition item1 saved : {}", item.getItem1().getId());
			}
			if(item.getItem2() != null) {
				log.trace("Try to save join condition item2 : {}", item.getItem2());
				item.setItem2(joinConditionItemRepository.save(item.getItem2()));
				log.trace("Join condition item2 saved : {}", item.getItem2().getId());
			}
			joinConditionRepository.save(item);
			log.trace("Join condition saved : {}", item.getId());
		});
		conditions.getUpdatedItems().forEach( item -> {
			log.trace("Try to save join condition : {}", item);
			item.setJoinId(joinId);
			if(item.getItem1() != null) {
				log.trace("Try to save join condition item1 : {}", item.getItem1());
				item.setItem1(joinConditionItemRepository.save(item.getItem1()));
				log.trace("Join condition item1 saved : {}", item.getItem1().getId());
			}
			if(item.getItem2() != null) {
				log.trace("Try to save join condition item2 : {}", item.getItem2());
				item.setItem2(joinConditionItemRepository.save(item.getItem2()));
				log.trace("Join condition item2 saved : {}", item.getItem2().getId());
			}
			joinConditionRepository.save(item);
			log.trace("Join condition saved : {}", item.getId());
		});
		conditions.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				if(item.getItem1() != null && item.getItem1().getId() != null) {
					log.trace("Try to delete join condition item1 : {}", item.getItem1());
					joinConditionItemRepository.deleteById(item.getItem1().getId());
					log.trace("Join condition item1 deleted : {}", item.getItem1().getId());
				}
				if(item.getItem2() != null && item.getItem2().getId() != null) {
					log.trace("Try to delete join condition item2 : {}", item.getItem2());
					joinConditionItemRepository.deleteById(item.getItem2().getId());
					log.trace("Join condition item2 deleted : {}", item.getItem2().getId());
				}
				log.trace("Try to delete join condition : {}", item);
				joinConditionRepository.deleteById(item.getId());
				log.trace("Join condition deleted : {}", item.getId());
			}
		});
		
		routines.getNewItems().forEach( item -> {
			log.trace("Try to save join routine : {}", item);
			item.setObjectId(joinId.getId());
			item.setObjectType(Join.class.getName());
			routineExecutorReopository.save(item);
			log.trace("Join condition saved : {}", item.getId());
		});
		routines.getUpdatedItems().forEach( item -> {
			log.trace("Try to save join routine : {}", item);
			item.setObjectId(joinId.getId());
			item.setObjectType(Join.class.getName());
			routineExecutorReopository.save(item);
			log.trace("Join routine saved : {}", item.getId());
		});
		routines.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete join routine : {}", item);
				routineExecutorReopository.deleteById(item.getId());
				log.trace("Join routine deleted : {}", item.getId());
			}
		});
		
		log.debug("Join saved : {} ", join.getId());
        return join;	
	}
	
	private JoinColumn save(JoinColumn column, Join joinId, Locale locale) {
		log.trace("Try to save join column : {}", column);		
		if(column.getProperties() != null) {
			column.setProperties(saveProperties(column.getProperties(), locale));
		}		
		column.setJoinId(joinId);
		column = joinColumnRepository.save(column);
		log.trace("Join column saved : {}", column.getId());
		return column;
	}
	
	private JoinUnionKey save(JoinUnionKey unionKey, Join joinId, Locale locale) {
		ListChangeHandler<JoinUnionKeyItem> items = unionKey.getItemListChangeHandler();
		unionKey.setJoinId(joinId);
		unionKey = joinUnionKeyRepository.save(unionKey);
		JoinUnionKey unionId = unionKey;
		items.getNewItems().forEach( item -> {
			log.trace("Try to save join union key item : {}", item);
			item.setUnionId(unionId);
			joinUnionKeyItemRepository.save(item);
			log.trace("Join union key item saved : {}", item.getId());
		});
		items.getUpdatedItems().forEach( item -> {
			log.trace("Try to save join union key item : {}", item);
			item.setUnionId(unionId);
			joinUnionKeyItemRepository.save(item);
			log.trace("Join union key item saved : {}", item.getId());
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete join union key item : {}", item);
				joinUnionKeyItemRepository.deleteById(item.getId());
				log.trace("Join union key item deleted : {}", item.getId());
			}
		});
		return unionKey;
	}
	
	private JoinColumnProperties saveProperties(JoinColumnProperties properties, Locale locale) {
		log.trace("Try to save join column properties: {}", properties);	
		if(properties.getField() != null) {
			properties.setField(joinColumnFieldRepository.save(properties.getField()));
		}
		ListChangeHandler<JoinColumnConcatenateItem> concatenates = properties.getConcatenateItemListChangeHandler();
		ListChangeHandler<JoinColumnCalculateItem> calculates = properties.getCalculateItemListChangeHandler();
		ListChangeHandler<JoinColumnConditionItem> conditions = properties.getConditionItemListChangeHandler();
		
		properties = joinColumnPropertiesRepository.save(properties);
		JoinColumnProperties propertiesId = properties;
		
		concatenates.getNewItems().forEach( item -> {
			save(item, propertiesId, locale);
		});
		concatenates.getUpdatedItems().forEach( item -> {
			save(item, propertiesId, locale);
		});
		concatenates.getDeletedItems().forEach( item -> {
			delete(item, locale);
		});
		
		calculates.getNewItems().forEach( item -> {
			save(item, propertiesId, locale);
		});
		calculates.getUpdatedItems().forEach( item -> {
			save(item, propertiesId, locale);
		});
		calculates.getDeletedItems().forEach( item -> {
			delete(item, locale);
		});
		
		conditions.getNewItems().forEach( item -> {
			save(item, propertiesId, locale);
		});
		conditions.getUpdatedItems().forEach( item -> {
			save(item, propertiesId, locale);
		});
		conditions.getDeletedItems().forEach( item -> {
			delete(item, locale);
		});
				
		log.trace("Join column properties saved : {}", properties.getId());
		return properties;
	}

	private JoinColumnCalculateItem save(JoinColumnCalculateItem item, JoinColumnProperties propertiesId, Locale locale) {
		log.trace("Try to save join column calculate item : {}", item);	
		if(item.getField() != null) {
			item.setField(joinColumnFieldRepository.save(item.getField()));
		}
		item.setPropertiesId(propertiesId);
		item = joinColumnCalculateItemRepository.save(item);
		log.trace("Join column calculate item saved : {}", item.getId());
		return item;
	}

	private JoinColumnConcatenateItem save(JoinColumnConcatenateItem item, JoinColumnProperties propertiesId, Locale locale) {
		log.trace("Try to save join column concatenate item : {}", item);	
		if(item.getField() != null) {
			item.setField(joinColumnFieldRepository.save(item.getField()));
		}
		item.setPropertiesId(propertiesId);
		item = joinColumnConcatenateItemRepository.save(item);
		log.trace("Join column concatenate item saved : {}", item.getId());
		return item;
	}
	
	private JoinColumnConditionItem save(JoinColumnConditionItem item, JoinColumnProperties propertiesId, Locale locale) {
		log.trace("Try to save join column condition item : {}", item);	
		
		ListChangeHandler<JoinColumnConditionItemOperand> operands = item.getOperandListChangeHandler();
		if(item.getThenField() != null) {
			item.setThenField(joinColumnFieldRepository.save(item.getThenField()));
		}
		item.setPropertiesId(propertiesId);
		item = joinColumnConditionItemRepository.save(item);		
		JoinColumnConditionItem itemId = item;
		
		operands.getNewItems().forEach( operand -> {
			save(operand, itemId, locale);
		});
		operands.getUpdatedItems().forEach( operand -> {
			save(operand, itemId, locale);
		});
		operands.getDeletedItems().forEach( operand -> {
			delete(operand, locale);
		});
				
		log.trace("Join column condition item saved : {}", item.getId());
		return item;
	}
	
	private JoinColumnConditionItemOperand save(JoinColumnConditionItemOperand item, JoinColumnConditionItem itemId, Locale locale) {
		log.trace("Try to save join column condition item operand : {}", item);	
		
		if(item.getField1() != null) {
			item.setField1(joinColumnFieldRepository.save(item.getField1()));
		}
		if(item.getField2() != null) {
			item.setField2(joinColumnFieldRepository.save(item.getField2()));
		}
		item.setItemId(itemId);
		item = joinColumnConditionItemOperandRepository.save(item);					
		log.trace("Join column condition item operand saved : {}", item.getId());
		return item;
	}

	
	@Override
	@Transactional
	public void delete(Join join) {
		log.debug("Try to delete Join : {}", join);
		if (join == null || join.getId() == null) {
			return;
		}
		try {
			ListChangeHandler<JoinGrid> grids = join.getGridListChangeHandler();			
			ListChangeHandler<JoinKey> keys = join.getKeyListChangeHandler();	
			ListChangeHandler<JoinUnionKey> unionKeys = join.getUnionKeyListChangeHandler();	
			ListChangeHandler<JoinColumn> columns = join.getColumnListChangeHandler();
			ListChangeHandler<JoinCondition> conditions = join.getConditionListChangeHandler();			
			ListChangeHandler<RoutineExecutor> routines = join.getRoutineListChangeHandler();
			
			if (countJoinChart(join) > 0) {
				deleteChartJoin(join);
			}
						
			join.setModificationDate(new Timestamp(System.currentTimeMillis()));
			
			grids.getItems().forEach( item -> {
				log.trace("Try to delete join grid : {}", item);			
				joinGridRepository.deleteById(item.getId());
				log.trace("Join grid deleted : {}", item.getId());
			});
//			grids.getDeletedItems().forEach( item -> {
//				if(item.getId() != null) {
//					log.trace("Try to delete join grid : {}", item);
//					joinGridRepository.deleteById(item.getId());
//					log.trace("Join grid deleted : {}", item.getId());
//				}
//			});
			
			keys.getItems().forEach( item -> {
				log.trace("Try to delete join key : {}", item);
				joinKeyRepository.deleteById(item.getId());
				log.trace("Join key deleted : {}", item.getId());
			});
//			keys.getDeletedItems().forEach( item -> {
//				if(item.getId() != null) {
//					log.trace("Try to delete join key : {}", item);
//					joinKeyRepository.deleteById(item.getId());
//					log.trace("Join key deleted : {}", item.getId());
//				}
//			});
			
			unionKeys.getItems().forEach( item -> {
				delete(item, Locale.FRENCH);
			});
//			unionKeys.getDeletedItems().forEach( item -> {
//				delete(item, Locale.ENGLISH);
//			});
			
			columns.getItems().forEach( item -> {
				delete(item, Locale.FRENCH);
			});
//			columns.getDeletedItems().forEach( item -> {
//				delete(item, Locale.ENGLISH);
//			});
			
			
			conditions.getItems().forEach( item -> {
				log.trace("Try to delete join condition : {}", item);
				joinConditionRepository.deleteById(item.getId());
				if(item.getItem1() != null && item.getItem1().getId() != null) {
					log.trace("Try to deleted join condition item1 : {}", item.getItem1());
					joinConditionItemRepository.deleteById(item.getItem1().getId());
					log.trace("Join condition item1 deleted : {}", item.getItem1().getId());
				}
				if(item.getItem2() != null && item.getItem2().getId() != null) {
					log.trace("Try to delete join condition item2 : {}", item.getItem2());
					joinConditionItemRepository.deleteById(item.getItem2().getId());
					log.trace("Join condition item2 deleted : {}", item.getItem2().getId());
				}				
				log.trace("Join condition deleted : {}", item.getId());
			});
			
			routines.getItems().forEach( item -> {
				log.trace("Try to delete join routine : {}", item);
				item.setObjectType(Join.class.getSimpleName());
				routineExecutorReopository.deleteById(item.getId());
				log.trace("Join routine deleted : {}", item.getId());
			});
			routines.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete join routine : {}", item);
					routineExecutorReopository.deleteById(item.getId());
					log.trace("Join routine deleted : {}", item.getId());
				}
			});			
			getRepository().delete(join);
			if(join.getAdminFilter() != null) {
				universeFilterService.delete(join.getAdminFilter());
			}
			if(join.getFilter() != null) {
				universeFilterService.delete(join.getFilter());
			}	
			log.debug("Join successfully to delete : {} ", join);
			return;
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while delete join : {}", join, e);
			String message = getMessageSource().getMessage("unable.to.delete.join.model", new Object[]{join} , Locale.ENGLISH);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private int countJoinChart(Join join) {
		if (join.getId() != null) {
			String sql = "SELECT * FROM BCP_DASHBOARD_REPORT WHERE reportType = 'CHART' AND dataSourceType ='JOIN' AND dataSourceId = " + join.getId();
			Query query = session.createNativeQuery(sql);
			List<?> reports = query.getResultList();
			return reports.size();
		}
		return 0;
	}

	private void deleteChartJoin(Join join) {
		if(join != null && join.getId() != null) {
			String sql = "DELETE FROM BCP_DASHBOARD_REPORT WHERE reportType = 'CHART' AND dataSourceType ='JOIN' AND dataSourceId = " + join.getId();
			Query query = session.createNativeQuery(sql);
			query.executeUpdate();
			log.trace("Chart in join {} deleted!", join.getName());
		}
	}
	
	@Override
	public boolean canDelete(Long id, Locale locale) {
		boolean res = false;
		Optional<Join> join = getRepository().findById(id);
		if (join.isPresent()) {
			int countChart = countJoinChart(join.get());
			if (countChart > 0) {
				String message = null;
				if (countChart == 1) {
					message = getMessageSource().getMessage("unable.to.delete.join.linked.chart", new Object[]{join.get().getName()} , locale);
				} else {

					message = getMessageSource().getMessage("unable.to.delete.join.linked.max.chart", new Object[]{countChart} , locale);
				}
				log.error("Unable to delete join : {}, linked with a chart", join.get().getName());
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			res = true;
		}
		return res;
	}

	private void delete(JoinColumn column, Locale locale) {
		if(column.getId() != null) {
			log.trace("Try to delete join column : {}", column);						
			joinColumnRepository.deleteById(column.getId());
			if(column.getProperties() != null) {
				deleteProperties(column.getProperties());
			}	
			log.trace("Join column deleted : {}", column.getId());
		}
	}
	
	private void delete(JoinUnionKey unionKey, Locale locale) {
		if(unionKey.getId() != null) {
			log.trace("Try to delete join union key : {}", unionKey);
			ListChangeHandler<JoinUnionKeyItem> items = unionKey.getItemListChangeHandler();
			items.getNewItems().forEach( item -> {
				joinUnionKeyItemRepository.deleteById(item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				joinUnionKeyItemRepository.deleteById(item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				joinUnionKeyItemRepository.deleteById(item.getId());
			});
			joinUnionKeyRepository.deleteById(unionKey.getId());	
			log.trace("Join union key deleted : {}", unionKey.getId());
		}
	}

	private void deleteProperties(JoinColumnProperties properties) {
		if(properties.getId() != null) {
			log.trace("Try to delete join column properties : {}", properties);						
			joinColumnPropertiesRepository.deleteById(properties.getId());
			if(properties.getField() != null && properties.getField().getId() != null) {
				joinColumnFieldRepository.deleteById(properties.getField().getId());
			}	
//			joinColumnCalculateItemRepository.deleteByPropertiesId(properties.getId());
//			joinColumnConcatenateItemRepository.deleteByPropertiesId(properties.getId());
			log.trace("Join column properties deleted : {}", properties.getId());
		}
	}

	private void delete(JoinColumnConcatenateItem item, Locale locale) {
		if(item.getId() != null) {
			log.trace("Try to delete join column Concatenate Item : {}", item);						
			joinColumnConcatenateItemRepository.deleteById(item.getId());
			if(item.getField() != null && item.getField().getId() != null) {
				joinColumnFieldRepository.deleteById(item.getField().getId());
			}	
			log.trace("Join column Concatenate Item deleted : {}", item.getId());
		}
	}

	private void delete(JoinColumnCalculateItem item, Locale locale) {
		if(item.getId() != null) {
			log.trace("Try to delete join column Calculate Item : {}", item);						
			joinColumnCalculateItemRepository.deleteById(item.getId());
			if(item.getField() != null && item.getField().getId() != null) {
				joinColumnFieldRepository.deleteById(item.getField().getId());
			}	
			log.trace("Join column Calculate Item deleted : {}", item.getId());
		}
	}
	
	private void delete(JoinColumnConditionItem item, Locale locale) {
		if(item.getId() != null) {
			log.trace("Try to delete join column condition Item : {}", item);	
			
			ListChangeHandler<JoinColumnConditionItemOperand> operands = item.getOperandListChangeHandler();
			operands.getNewItems().forEach( operand -> {
				delete(operand, locale);
			});
			operands.getUpdatedItems().forEach( operand -> {
				delete(operand, locale);
			});
			operands.getDeletedItems().forEach( operand -> {
				delete(operand, locale);
			});			
			joinColumnConditionItemRepository.deleteById(item.getId());
			if(item.getThenField() != null && item.getThenField().getId() != null) {
				joinColumnFieldRepository.deleteById(item.getThenField().getId());
			}	
			log.trace("Join column condition Item deleted : {}", item.getId());
		}
	}
	
	private void delete(JoinColumnConditionItemOperand item, Locale locale) {
		if(item.getId() != null) {
			log.trace("Try to delete join column condition Item operand : {}", item);						
			joinColumnConditionItemOperandRepository.deleteById(item.getId());
			if(item.getField1() != null && item.getField1().getId() != null) {
				joinColumnFieldRepository.deleteById(item.getField1().getId());
			}	
			if(item.getField2() != null && item.getField2().getId() != null) {
				joinColumnFieldRepository.deleteById(item.getField2().getId());
			}	
			log.trace("Join column condition Item operand deleted : {}", item.getId());
		}
	}
	
	
	
	@Transactional
	public MaterializedGrid saveNewMaterializedGrid(Join join, HttpSession session, Locale locale) throws Exception {
		log.debug("Try to create new materialized grid...");
		
		String gridName = StringUtils.hasText(join.getPublicationGridName()) ? join.getPublicationGridName() : join.getName();
		MaterializedGrid grid = null;
		if(StringUtils.hasText(gridName)) {
			grid = materializedGridService.getNewItem(gridName);
        }
		else{
			grid = materializedGridService.getNewItem();
		}	
		int count = join.getColumns().size();
		for(MaterializedGridColumn column : grid.getColumnListChangeHandler().getItems()) {
			column.setPosition(column.getPosition() + count);
		}
		int position = 0;
		for(JoinColumn joinGridColumn : join.getColumns()) {
			log.trace("Try to Create column : Position = {} - Name= {}", joinGridColumn.getPosition(), joinGridColumn.getName());
			log.trace("Column is for publication: {} = {}", joinGridColumn.getName(), joinGridColumn.isUsedForPublication());
			if(joinGridColumn.isUsedForPublication()) {
				MaterializedGridColumn column = new MaterializedGridColumn();				
				column.setName(joinGridColumn.getName());
				column.setType(joinGridColumn.getType());
				column.setCategory(GrilleColumnCategory.USER);
				column.setFixedType(joinGridColumn.getFixedType());
				column.setBackgroundColor(joinGridColumn.getBackgroundColor());
				column.setForegroundColor(joinGridColumn.getForegroundColor());
				column.setShow(joinGridColumn.isShow());
				column.setFormat(joinGridColumn.getFormat());				
				column.setPosition(position++);
				grid.getColumnListChangeHandler().addNew(column);
			}
		}
			
		BGroup group = groupService.getByName("Published join");
		if(group == null ) {
			group = new BGroup("Published join");
			groupService.save(group, locale);
		}
		grid.setGroup(group);		
		grid = materializedGridService.save(grid, locale);
		grid = materializedGridService.publish(grid, locale);
		if(join.getPublicationGridId() == null) {
			join.setPublicationGridId(grid.getId());
			join.setPublicationGridName(grid.getName());
			save(join, locale);
		}		
		log.debug("Materialized grid created : ID = {} - NAME = {}", grid.getId(), grid.getName());
		return grid;		
	}
	
	@Transactional
	public Grille saveNewGrid(Join join, HttpSession session, Locale locale) throws Exception {
		log.debug("Try to create new input grid...");
		
		String gridName = StringUtils.hasText(join.getPublicationGridName()) ? join.getPublicationGridName() : join.getName();		
		Grille grid = grilleService.buildNewGridName(gridName, locale);	
		
		int position = 0;
		for(JoinColumn joinGridColumn : join.getColumns()) {
			log.trace("Try to Create column : Position = {} - Name= {}", joinGridColumn.getPosition(), joinGridColumn.getName());
			log.trace("Column is for publication: {} = {}", joinGridColumn.getName(), joinGridColumn.isUsedForPublication());
			if(joinGridColumn.isUsedForPublication()) {
				GrilleColumn column = new GrilleColumn(null, joinGridColumn.getName(), joinGridColumn.getType(), joinGridColumn.getDimensionId(), joinGridColumn.getDimensionName());
				column.setCategory(GrilleColumnCategory.USER);
				column.setFixedType(joinGridColumn.getFixedType());
				column.setBackgroundColor(joinGridColumn.getBackgroundColor());
				column.setForegroundColor(joinGridColumn.getForegroundColor());
				column.setShow(joinGridColumn.isShow());
				column.setFormat(joinGridColumn.getFormat());
				if(joinGridColumn.getPublicationDimensionId() != null && joinGridColumn.getPublicationDimensionId() != 0) {
					column.setDimensionName(joinGridColumn.getPublicationDimensionName());
					column.setDimensionId(joinGridColumn.getPublicationDimensionId());
				}
				else if(joinGridColumn.isStandard()) {
					column.setDimensionName(joinGridColumn.getDimensionName());
					column.setDimensionId(joinGridColumn.getDimensionId());
				}
				if(column.getDimensionId() == null) {
					tryToBuildDimension(column, join, session, locale);
				}
				column.setPosition(position++);
				grid.getColumnListChangeHandler().addNew(column);
			}
		}
			
		BGroup group = groupService.getByName("Published join");
		if(group == null ) {
			group = new BGroup("Published join");
			groupService.save(group, locale);
		}
		grid.setGroup(group);		
		grid = grilleService.save(grid, locale);
		if(join.getPublicationGridId() == null) {
			join.setPublicationGridId(grid.getId());
			join.setPublicationGridName(grid.getName());
			save(join, locale);
		}		
		log.debug("Input grid created : ID = {} - NAME = {}", grid.getId(), grid.getName());
		return grid;		
	}

	private void tryToBuildDimension(GrilleColumn column, Join join, HttpSession session, Locale locale) throws Exception {
		if (!StringUtils.hasText(column.getName())) {
			throw new BcephalException("Empty column name : " + column.getPosition());
		}
		
		Dimension dimension = getInitiationService().getDimension(column.getType(), column.getName(), false, session, locale);
		if (dimension == null) {
			Long defaultEntityId = null;
			if (defaultEntityId == null) {
				Long modelId = createDefaultModel(session, locale);
				String name = join.getName();
				Entity entity = getInitiationService().getEntity(name, session, locale);
				if(entity == null) {
					defaultEntityId = getInitiationService().createEntity(name, modelId, session, locale);
				}
				else {
					defaultEntityId = entity.getId();
				}
			}
			dimension = getInitiationService().createDimension(column.getType(), column.getName(), defaultEntityId, session, locale);
		}
		if (dimension == null) {
			throw new BcephalException("Unable to create {} dimension : {}", column.getType().name(), column.getName());
		}
		column.setDimensionId((long) dimension.getId());
		column.setDimensionName(dimension.getName());		
	}
	
	private Long createDefaultModel(HttpSession session, Locale locale) throws Exception {
		Parameter parameter = parameterRepository
				.findByCodeAndParameterType(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, ParameterType.MODEL);
		Long modelId = parameter != null ? parameter.getLongValue() : null;
		if (modelId == null) {
			modelId = getInitiationService().createModel("Default model", session, locale);
			if (parameter == null) {
				parameter = new Parameter(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, ParameterType.MODEL);
			}
			parameter.setLongValue(modelId);
			parameterRepository.save(parameter);
		}		
		return modelId;
	}
		
	protected void performMaterialize(Long id) throws Exception {
		JoinMaterializationManager manager = getMaterialisationManager();
		manager.setJoin(getById(id));
		if(manager.getJoin() == null) {
			log.debug("Join not found : {}", id);
			throw new BcephalException("Join not found : " + id);
		}
		loadFilterClosures(manager.getJoin());
		if(manager.getJoin().isPublished()) {
			manager.refresh();
		}
		else {
			manager.publish();							
		}
	}

	@Transactional
	public JoinEditorData materialize(Long id, HttpSession session, Locale locale) {
		log.debug("Try to materialize join : {}", id);
		try {
			performMaterialize(id);
			EditorDataFilter filter = new EditorDataFilter();
			filter.setNewData(false);
			filter.setId(id);
			return getEditorData(filter, session, locale);
		}
		catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while materializing join : {}", id, e);
			String message = messageSource.getMessage("unable.to.materialize.join.by.filter", new Object[] { id }, locale);
			throw new BcephalException(message);
		}
	}
	
	@Transactional
	public boolean materialize(List<Long> ids, Locale locale) {
		log.debug("Try to materialize joins : {}", ids);
		try {
			for(Long id : ids) {
				performMaterialize(id);
			}
			return true;
		}
		catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while materializing joins : {}", ids, e);
			String message = messageSource.getMessage("unable.to.materialize.join.by.filter", new Object[] { ids }, locale);
			throw new BcephalException(message);
		}
	}
	
	
	@Transactional
	public boolean refreshMaterialization(Long id, Locale locale) {
		log.debug("Try to refresh join materialization : {}", id);
		try {
			JoinMaterializationManager manager = getMaterialisationManager();
			manager.setJoin(getById(id));
			if(manager.getJoin() == null) {
				log.debug("Join not found : {}", id);
				throw new BcephalException("Join not found : " + id);
			}
			loadFilterClosures(manager.getJoin());
			manager.refresh();
			return true;
		}
		catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while refreshing join materialization : {}", id, e);
			String message = messageSource.getMessage("unable.to.refresh.materialize.join.by.filter", new Object[] { id }, locale);
			throw new BcephalException(message);
		}
	}
	
	@Transactional
	public JoinEditorData resetMaterialization(Long id, HttpSession session, Locale locale) {
		log.debug("Try to reset join materialization : {}", id);
		try {
			JoinMaterializationManager manager = getMaterialisationManager();
			manager.setJoin(getById(id));
			if(manager.getJoin() == null) {
				log.debug("Join not found : {}", id);
				throw new BcephalException("Join not found : " + id);
			}
			manager.reset();
			EditorDataFilter filter = new EditorDataFilter();
			filter.setNewData(false);
			filter.setId(id);
			return getEditorData(filter, session, locale);
		}
		catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while reseting join materialization : {}", id, e);
			String message = messageSource.getMessage("unable.to.reset.materialize.join.by.filter", new Object[] { id }, locale);
			throw new BcephalException(message);
		}
	}

	protected JoinMaterializationManager getMaterialisationManager() {
		JoinMaterializationManager manager = new JoinMaterializationManager();
		manager.setEntityManager(session);
		manager.setJoinRepository(repository);
		manager.setJoinColumnRepository(joinColumnRepository);
		return manager;
	}

	
	
	static Long PAGE_SIZE = (long) 100000;

	public List<String> export(JoinExportData data, java.util.Locale locale) throws Exception {
		return export(data.getFilter(), data.getType(), locale);
	}
	
	public List<String> export(JoinFilter filter, GrilleExportDataType type, java.util.Locale locale)
			throws Exception {
		List<String> paths = new ArrayList<String>();
		boolean canSearch = true;
		Long minPageSize = (long) 25000;
		filter.setPageSize(minPageSize.intValue());
		filter.setPage(1);
		int offste = 0;
		String path = null;
		JoinExporter excelExporter = null;
		JoinCsvExporter csvExporter = null;
		JoinJsonExporter jsonExporter = null;
		while (canSearch) {
			offste++;
			BrowserDataPage<Object[]> page = searchRows(filter, locale);
			if(path == null) {
				path = getPath(offste + " - " + filter.getJoin().getName(), type);
			}
			boolean end = page.getItems().size() < minPageSize || (page.getPageLastItem()) % PAGE_SIZE == 0;
			if (type == GrilleExportDataType.EXCEL) {
				if(excelExporter == null) {
					excelExporter = new JoinExporter(filter.getJoin(),filter.getExportColumnIds(), filter.isExportAllColumns());
				}
				excelExporter.writeRowss(page.getItems(), true);
				if(end) {
					excelExporter.writeFiles(path);
				}
			} else if (type == GrilleExportDataType.CSV) {
				if(csvExporter == null) {
					csvExporter = new JoinCsvExporter(path, filter.getJoin(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
				}else {				
					csvExporter.export(page.getItems(), true);
				}
				if(end) {
					csvExporter.close();
				}
			} else if (type == GrilleExportDataType.JSON) {
				if(jsonExporter == null) {
					jsonExporter = new JoinJsonExporter(path, filter.getJoin(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					jsonExporter.export(page.getItems(), true);
				}else {				
					jsonExporter.export(page.getItems(), true);
				}
				if(end) {
					jsonExporter.close();
				}
			}
			if(end) {
				paths.add(path);
				path = null;
				jsonExporter = null;
				csvExporter = null;
				excelExporter = null;
			}
			
			if (page.getItems().size() < minPageSize ) {
				canSearch = false;
			} else {
				filter.setPage(filter.getPage() + 1);
			}
		}
		return paths;
	}
	
	public List<String> performExport(JoinFilter filter, GrilleExportDataType type, int maxRowCountPerFile) throws Exception {
		List<String> paths = new ArrayList<>();
		int fileNbr = 0;
		String baseName = filter.getJoin().getName() + System.currentTimeMillis();
		boolean canSearch = true;
		filter.setPageSize(maxRowCountPerFile);
		filter.setPage(1);
		filter.setAllowRowCounting(false);
		JoinExporter excelExporter = null;
		JoinCsvExporter csvExporter = null;
		JoinJsonExporter jsonExporter = null;
		while (canSearch) {
			BrowserDataPage<Object[]> page = searchRows(filter, Locale.ENGLISH);
			int count = page.getItems().size();
			if(count > 0) {
				String path = getPath(baseName, type, ++fileNbr);
				paths.add(path);
				if (type == GrilleExportDataType.EXCEL) {
					excelExporter = new JoinExporter(filter.getJoin(), filter.getExportColumnIds(), filter.isExportAllColumns());
					excelExporter.writeRowss(page.getItems(), true);
					excelExporter.writeFiles(path);
				} else if (type == GrilleExportDataType.CSV) {
					csvExporter = new JoinCsvExporter(path, filter.getJoin(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
					csvExporter.close();
				} else if (type == GrilleExportDataType.JSON) {
					jsonExporter = new JoinJsonExporter(path, filter.getJoin(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					jsonExporter.export(page.getItems(), true);
					jsonExporter.close();
				}
			}
						
			if (page.getItems().size() < maxRowCountPerFile ) {
				canSearch = false;
			} else {
				filter.setPage(filter.getPage() + 1);
			}
		}
		return paths;
	}
	
	public BrowserDataPage<String> searchColumnValues(BrowserDataFilter filter, Locale locale) throws Exception {	
		Join join = getById(filter.getDataSourceId());
		if (join != null) {
			if(join.isPublished()) {
				Optional<JoinColumn> result = joinColumnRepository.findById(filter.getGroupId());				
				if (result.isPresent()) {
					JoinColumn column = result.get();
					return getColumnValues(filter, join, column, locale);
				} else {
					throw new BcephalException("Join column not found. ID : {}", filter.getGroupId());
				}
			}
			else {
				throw new BcephalException("Join is not published. ID : {}, Name : {}", join.getId(), join.getName());
			}			
		} else {
			throw new BcephalException("Join not found. ID : {}", filter.getDataSourceId());
		}
	}
	
	private BrowserDataPage<String> getColumnValues(BrowserDataFilter filter, Join join, JoinColumn column, java.util.Locale locale) {

		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<String> page = new BrowserDataPage<String>();
		page.setPageSize(filter.getPageSize());
		Integer count = 0;

		String col = column.getDbColAliasName();
		String sql = "SELECT distinct " + col + " FROM " + join.getMaterializationTableName() + " WHERE " + col
				+ " IS NOT NULL AND " + col + " != ''";
		if (StringUtils.hasText(filter.getCriteria())) {
			sql += " AND UPPER(" + col + ") LIKE '" + filter.getCriteria().toUpperCase() + "%'";
		}

		if(filter.getColumnFilters() != null) {
			String v = build(filter.getColumnFilters(), col);
			if(StringUtils.hasText(v)) {
				sql += v;
			}
		}
		
		if (filter.isAllowRowCounting()) {
			String countsql = "SELECT COUNT(1) FROM (" + sql + ") AS A";
			log.trace("Count query : {}", countsql);
			Query query = this.session.createNativeQuery(countsql);
			Number number = (Number) query.getSingleResult();
			count = number.intValue();
			if (count == 0) {
				return page;
			}
			if (filter.isShowAll()) {
				page.setTotalItemCount(count);
				page.setPageCount(1);
				page.setCurrentPage(1);
				page.setPageFirstItem(1);
			} else {
				page.setTotalItemCount(count);
				page.setPageCount(((int) count / filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				if (page.getCurrentPage() > page.getPageCount()) {
					page.setCurrentPage(page.getPageCount());
				}
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
		} else {
			page.setTotalItemCount(1);
			page.setPageCount(1);
			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
		}

		sql += " ORDER BY " + col;
		log.trace("Search query : {}", sql);

		Query query = this.session.createNativeQuery(sql);
		if (!filter.isShowAll()) {
			query.setFirstResult(page.getPageFirstItem() - 1);
			query.setMaxResults(page.getPageSize());
		}
		@SuppressWarnings("unchecked")
		List<String> values = query.getResultList();
		page.setPageLastItem(page.getPageFirstItem() + values.size() - 1);
		page.setPageSize(values.size());
		page.setItems(values);
		log.debug("Row found : {}", values.size());

		return page;
	}
	
	private String build(ColumnFilter filter, String col) {
		if(filter == null) {
			return null;
		}
		
		String  criteria = filter.getValue();
		String  sql = " AND UPPER(" + col + ") %s '%s'";
		String operator = "LIKE";
		boolean isNull = false;
		if (filter != null) {
			GridFilterOperator gridFilterOperator = new GridFilterOperator();
			if (gridFilterOperator.isEquals(filter.getOperation())) {
				operator = "=";
			} else if (gridFilterOperator.isGreater(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isGreaterOrEquals(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isLess(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isLessOrEquals(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isNotEquals(filter.getOperation())) {
				operator = "!=";
			} else if (gridFilterOperator.isStartsWith(filter.getOperation())) {
				criteria = criteria +"%";
			} else if (gridFilterOperator.isEndsWith(filter.getOperation())) {
				criteria = "%" + criteria ;
			} else if (gridFilterOperator.isContains(filter.getOperation())) {
				criteria = "%" + criteria + "%";
			} else if (gridFilterOperator.isNotContains(filter.getOperation())) {
				criteria = "%" + criteria + "%";
				operator = " NOT LIKE";
			} else if (gridFilterOperator.isNotNullOrEmpty(filter.getOperation())) {
				isNull = true;
				operator = " IS NOT NULL OR " + col + " !=''";
			} else if (gridFilterOperator.isNullOrEmpty(filter.getOperation())) {
				isNull = true;
				operator = " IS NULL OR " + col + " =''";
			}
		}
		
		if(!isNull) {
			if(StringUtils.hasText(criteria)) {
				criteria = criteria.toUpperCase();
			}
			return String.format(sql, operator,criteria);
		}else {
			return String.format(" AND " + col + " %s ", operator);
		}
	}
	
	public String getPath(String baseName, GrilleExportDataType type, int filenbr) {
		String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "bcephal");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		String fileName = baseName + "_" + filenbr + type.getExtension(); 
		return FilenameUtils.concat(path, fileName);
	}

	public String getPath(String fileName, GrilleExportDataType type) {
		String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "bcephal");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		return FilenameUtils.concat(path, getFileName(fileName, type));
	}

	public String getFileName(String fileName, GrilleExportDataType type ) {
		return fileName + System.currentTimeMillis() + type.getExtension();
	}
	
}
