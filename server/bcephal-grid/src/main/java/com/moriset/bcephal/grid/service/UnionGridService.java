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

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridBrowserData;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridCondition;
import com.moriset.bcephal.grid.domain.UnionGridConditionItem;
import com.moriset.bcephal.grid.domain.UnionGridCreateColumnData;
import com.moriset.bcephal.grid.domain.UnionGridEditorData;
import com.moriset.bcephal.grid.domain.UnionGridExportData;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.grid.repository.UnionGridColumnRepository;
import com.moriset.bcephal.grid.repository.UnionGridConditionItemRepository;
import com.moriset.bcephal.grid.repository.UnionGridConditionRepository;
import com.moriset.bcephal.grid.repository.UnionGridItemRepository;
import com.moriset.bcephal.grid.repository.UnionGridRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Service
public class UnionGridService extends MainObjectService<UnionGrid, UnionGridBrowserData> {
	
	@Autowired
	private UnionGridRepository unionGridRepository;
	@Autowired
	private UnionGridItemRepository unionGridItemRepository;
	@Autowired
	private UnionGridColumnRepository unionGridColumnRepository;
	@Autowired
	private ParameterRepository parameterRepository;
	@Autowired
	private UnionGridConditionRepository unionGridConditionRepository;
	@Autowired
	private UnionGridConditionItemRepository unionGridConditionItemRepository;
	
	@Autowired
	SpotService spotService;
	
	@Autowired
	private MaterializedGridService materializedGridService;
	
	@Autowired
	private SmartMaterializedGridRepository smartMaterializedGridRepository;
		
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@PersistenceContext
	EntityManager session;
	
	@Override
	public EditorData<UnionGrid> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		UnionGridEditorData data = new UnionGridEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}		
		if(data.getItem() != null && data.getItem().getGroup() == null) {
			data.getItem().setGroup(getDefaultGroup());
		}
		data.getGrids().addAll(smartMaterializedGridRepository.findAll());
		return data;
	}
	
	@Override
	public UnionGrid getById(Long id) {
		UnionGrid grid = super.getById(id);
		for(UnionGridItem item : grid.getItems()) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getGrid().getId());
			if(response.isPresent()) {
				item.setGrid(response.get());
				
				for(UnionGridColumn column : grid.getColumns()) {
					List<Long> ids = column.buildColumnIds();
					for(Long columnId : ids) {
						SmartMaterializedGridColumn c = item.getGrid().getColumnById(columnId);
						if(c != null) {
							column.getColumns().add(c);
							//column.getColumns().add(new Nameable(c.getId(), c.getName()));
						}
					}
				}
				
			}
		}
		return grid;
	}
	
	@SuppressWarnings("unchecked")
	public BrowserDataPage<GridItem> searchRows(UnionGridFilter filter, java.util.Locale locale) throws Exception {		
		BrowserDataPage<GridItem> page = new BrowserDataPage<GridItem>();
		page.setPageSize(filter.getPageSize());
		if(filter.getUnionGrid() != null) {
			loadFilterClosures(filter, false);	
			UnionGridQueryBuilder builder = getUnionGridQueryBuilder(filter);
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
			int columnCount = filter.getUnionGrid().getColumns().size();
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
			
			formatDates(objects, filter.getUnionGrid());
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(GridItem.buildItems(objects)); 
			log.debug("Row found : {}", objects.size());
		}		
		return page;
	}
	
	private UnionGridQueryBuilder getUnionGridQueryBuilder(UnionGridFilter filter) {
		//boolean ispublished = filter.getUnionGrid() != null && filter.getUnionGrid().isPublished();
		UnionGridQueryBuilder builder = new UnionGridQueryBuilder(filter, unionGridColumnRepository, spotService);		
		builder.setParameterRepository(parameterRepository);
		return builder;
	}

	public void loadFilterClosures(UnionGridFilter filter, boolean loadRecoData) {
		if (filter != null && filter.getUnionGrid() != null) {
			filter.setSmartGrids(loadFilterClosures(filter.getUnionGrid(), loadRecoData));			
		}
	}
	
	public List<AbstractSmartGrid<?>> loadFilterClosures(UnionGrid unionGrid, boolean loadRecoData) {
		List<AbstractSmartGrid<?>> smartGrids = new ArrayList<>();
		if (unionGrid != null) {
			loadFilterClosures(unionGrid);	
			for(UnionGridItem item :  unionGrid.getItems()) {
				AbstractSmartGrid<?> grid = item.getGrid();				
				if(grid == null) {
					throw new BcephalException("The standalone for item '{}' is not found!", item.getId());
				}
				if(!grid.isPublished()) {
					throw new BcephalException("The The standalone '{}' is not published!", grid.getName());
				}
				smartGrids.add(grid);
				item.setUnionGrid(unionGrid);
			}
			
			for(UnionGridColumn column :  unionGrid.getColumns()) {	
				loadClosure(column, smartGrids);
			}
			
			for(UnionGridCondition condition :  unionGrid.getConditionListChangeHandler().getItems()) {	
				loadClosure(condition, unionGrid);
			}
		}
		return smartGrids;
	}
	
	public void loadFilterClosures(UnionGrid unionGrid) {
		if (unionGrid != null) {
			List<UnionGridItem> items = unionGrid.getItemListChangeHandler().getItems();			
			Collections.sort(items, new Comparator<UnionGridItem>() {
				@Override
				public int compare(UnionGridItem value1, UnionGridItem value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			unionGrid.setItems(items);
			
			if(unionGrid.getItems().size() == 0) {
				throw new BcephalException("The list of grids to united is empty!");
			}								
			
			List<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler().getItems();
			Collections.sort(columns, new Comparator<UnionGridColumn>() {
				@Override
				public int compare(UnionGridColumn value1, UnionGridColumn value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			unionGrid.setColumns(columns);
			
			for(UnionGridColumn column :  columns) {	
				if(!column.isPersistent()) {
					throw new BcephalException("You have to save your union grid.");
				}
			}
						
			List<UnionGridCondition> conditions = unionGrid.getConditionListChangeHandler().getItems();
			Collections.sort(conditions, new Comparator<UnionGridCondition>() {
				@Override
				public int compare(UnionGridCondition value1, UnionGridCondition value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			unionGrid.setConditions(conditions);			
		}
	}
	
	private void loadClosure(UnionGridColumn column,  List<AbstractSmartGrid<?>> smartGrids) {
		column.setColumnsMap(new HashMap<>());
		if(column.getColumns().size() == 0) {
			column.buildColumns();
		}			
		for(SmartMaterializedGridColumn col : column.getColumns()) {
			for(AbstractSmartGrid<?> grid : smartGrids) {
				SmartMaterializedGridColumn c = (SmartMaterializedGridColumn)grid.getColumnById(col.getId());
				if(c != null) {
					column.getColumnsMap().put(grid.getId(), col);
				}
			}			
		}
	}
	
	private void loadClosure(UnionGridCondition condition, UnionGrid unionGrid) {
		if(condition.getItem1() != null) {
			loadClosure(condition.getItem1(), unionGrid);
		}
		if(condition.getItem2() != null) {
			loadClosure(condition.getItem2(), unionGrid);
		}
	}
	
	private void loadClosure(UnionGridConditionItem item, UnionGrid unionGrid) {
		if(item.isColumn() && item.getColumnId() != null) {
			item.setColumn(unionGrid.getColumnById(item.getColumnId()));
		}
	}
	
	
	private void formatDates(List<Object[]> objects, UnionGrid unionGrid) {
		for (Object obj : objects) {
			Object[] row = null;
			if(obj instanceof Object[]) {
				row = (Object[])obj;
			}
			else {
				row = new Object[] {obj};
			}
			formatDates(row, unionGrid);
		}
	}
		
	private void formatDates(Object[] row, UnionGrid unionGrid) {
		for (UnionGridColumn col : getPeriodColumns(unionGrid)) {
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
	
	private List<UnionGridColumn> getPeriodColumns(UnionGrid unionGrid) {
		List<UnionGridColumn> columns = new ArrayList<>(0);
		for (UnionGridColumn col : unionGrid.getColumns()) {
			if (col.isPeriod()) {
				columns.add(col);
			}
		}
		return columns;
	}
	
	
	@Override
	@Transactional
	public UnionGrid save(UnionGrid unionGrid, Locale locale) {
		log.debug("Try to  Save union grid : {}", unionGrid);		
		try {	
			if(unionGrid == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.union.grid", new Object[]{unionGrid} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(unionGrid.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.union.grid.with.empty.name", new String[]{unionGrid.getName()} , locale);
				throw new BcephalException(message);
			}
			
			ListChangeHandler<UnionGridItem> items = unionGrid.getItemListChangeHandler();	
			ListChangeHandler<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler();
			ListChangeHandler<UnionGridCondition> conditions = unionGrid.getConditionListChangeHandler();	
										
			validateBeforeSave(unionGrid, locale);
			unionGrid.setModificationDate(new Timestamp(System.currentTimeMillis()));
						
			unionGrid = getRepository().save(unionGrid);
			UnionGrid unionGridId = unionGrid;
			
			items.getNewItems().forEach( item -> {
				log.trace("Try to save union grid item : {}", item);
				item.setUnionGrid(unionGridId);
				unionGridItemRepository.save(item);
				log.trace("Union grid item saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				log.trace("Try to save union grid item : {}", item);
				item.setUnionGrid(unionGridId);
				unionGridItemRepository.save(item);
				log.trace("Union grid item saved : {}", item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete union grid item : {}", item);
					unionGridItemRepository.deleteById(item.getId());
					log.trace("Union grid item deleted : {}", item.getId());
				}
			});
						
			columns.getNewItems().forEach( item -> {
				save(item, unionGridId, locale);
			});
			columns.getUpdatedItems().forEach( item -> {
				save(item, unionGridId, locale);
			});
			columns.getDeletedItems().forEach( item -> {
				delete(item, locale);
			});
			
			conditions.getNewItems().forEach( item -> {
				save(item, unionGridId, locale);
			});
			conditions.getUpdatedItems().forEach( item -> {
				save(item, unionGridId, locale);
			});
			conditions.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					delete(item, locale);
				}
			});			
			
			log.debug("Union grid saved : {} ", unionGrid.getId());
	        return unionGrid;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save union grid : {}", unionGrid, e);
			String message = getMessageSource().getMessage("unable.to.save.union.grid.model", new Object[]{unionGrid} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}		

	private void save(UnionGridColumn item, UnionGrid unionGridId, Locale locale) {
		log.trace("Try to save union grid column : {}", item);
		item.setUnionGrid(unionGridId);	
		item.setColumnIds(item.buildColumnIdsAsString());
		unionGridColumnRepository.save(item);
		log.trace("Union grid column saved : {}", item.getId());	
		
	}
	
	private int countUnionGridChart(UnionGrid unionGrid) {
		if (unionGrid.getId() != null) {
			String sql = "SELECT * FROM BCP_DASHBOARD_REPORT WHERE reportType = 'CHART' AND dataSourceType = 'UNION_GRID' AND dataSourceId = " + unionGrid.getId();
			Query query = session.createNativeQuery(sql);
			List<?> reports = query.getResultList();
			return reports.size();
		}
		return 0;
	}
	
	@Override
	public boolean canDelete(Long id, Locale locale) {
		boolean res = false;
		Optional<UnionGrid> unionGrid = getRepository().findById(id);
		if (unionGrid.isPresent()) {
			int countChart = countUnionGridChart(unionGrid.get());
			if (countChart > 0) {
				String message = null;
				if (countChart == 1) {
					message = getMessageSource().getMessage("unable.to.delete.unionGrid.linked.chart", new Object[]{unionGrid.get().getName()} , locale);
				} else {
					message = getMessageSource().getMessage("unable.to.delete.unionGrid.linked.max.chart", new Object[]{countChart} , locale);
				}
				log.error("Unable to delete UnionGrid : {}, linked with a chart", unionGrid.get().getName());
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			res = true;
		}
		return res;
	}
	
	@Override
	@Transactional
	public void delete(UnionGrid unionGrid) {
		log.debug("Try to delete UnionGrid : {}", unionGrid);
		if (unionGrid == null || unionGrid.getId() == null) {
			return;
		}
		try {
			ListChangeHandler<UnionGridItem> items = unionGrid.getItemListChangeHandler();	
			ListChangeHandler<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler();
			ListChangeHandler<UnionGridCondition> conditions = unionGrid.getConditionListChangeHandler();
			
			items.getItems().forEach(item -> {
				log.trace("Try to delete union grid item : {}", item);			
				unionGridItemRepository.deleteById(item.getId());
				log.trace("Union grid item deleted : {}", item.getId());
			});
			
			columns.getItems().forEach(item -> {
				delete(item, Locale.FRENCH);
			});			
			
			conditions.getItems().forEach(item -> {
				delete(item, Locale.FRENCH);
			});
			
			unionGridRepository.delete(unionGrid);
			log.debug("Union grid successfull deleted : {} ", unionGrid);
			return;
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while delete union grid : {}", unionGrid, e);
			String message = getMessageSource().getMessage("unable.to.delete.unionGrid.model", new Object[]{unionGrid} , Locale.ENGLISH);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void delete(UnionGridColumn item, Locale locale) {
		log.trace("Try to delete union grid column : {}", item);
		unionGridColumnRepository.deleteById(item.getId());
		log.trace("Union grid column deleted : {}", item.getId());
	}

	private void save(UnionGridCondition item, UnionGrid unionGridId, Locale locale) {
		log.trace("Try to save union grid condition : {}", item);
		item.setUnionGrid(unionGridId);
		if(item.getItem1() != null) {
			log.trace("Try to save union grid condition item1 : {}", item.getItem1());
			item.setItem1(unionGridConditionItemRepository.save(item.getItem1()));
			log.trace("Union grid condition item1 saved : {}", item.getItem1().getId());
		}
		if(item.getItem2() != null) {
			log.trace("Try to save union grid condition item2 : {}", item.getItem2());
			item.setItem2(unionGridConditionItemRepository.save(item.getItem2()));
			log.trace("Union grid condition item2 saved : {}", item.getItem2().getId());
		}
		unionGridConditionRepository.save(item);
		log.trace("Union grid condition saved : {}", item.getId());		
	}
	
	private void delete(UnionGridCondition item, Locale locale) {
		if(item.getItem1() != null && item.getItem1().getId() != null) {
			log.trace("Try to delete union grid condition item1 : {}", item.getItem1());
			unionGridConditionItemRepository.deleteById(item.getItem1().getId());
			log.trace("Union grid condition item1 deleted : {}", item.getItem1().getId());
		}
		if(item.getItem2() != null && item.getItem2().getId() != null) {
			log.trace("Try to delete union grid condition item2 : {}", item.getItem2());
			unionGridConditionItemRepository.deleteById(item.getItem2().getId());
			log.trace("Union grid condition item2 deleted : {}", item.getItem2().getId());
		}
		log.trace("Try to delete union grid condition : {}", item);
		unionGridConditionRepository.deleteById(item.getId());
		log.trace("Union grid condition deleted : {}", item.getId());
	}
		





	@Override
	public UnionGridRepository getRepository() {
		return unionGridRepository;
	}
	
	public UnionGrid getNewUnionGrid(String name) {
		UnionGrid unionGrid = new UnionGrid();
		String baseName = "UnionGrid ";
		if(StringUtils.hasText(name)) {
			baseName = name  + " ";
		}
		int i = 1;
		unionGrid.setName(baseName + i);
		while (getByName(unionGrid.getName()) != null) {
			i++;
			unionGrid.setName(baseName + i);
		}		
		return unionGrid;
	}

	@Override
	protected UnionGrid getNewItem() {
		UnionGrid grid = new UnionGrid();
		String baseName = "Union ";
		int i = 1;
		grid.setName(baseName + i);
		while (getByName(grid.getName()) != null) {
			i++;
			grid.setName(baseName + i);
		}	
		return grid;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.REPORTING_UNION_GRID;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}

	@Override
	protected UnionGridBrowserData getNewBrowserData(UnionGrid item) {
		return new UnionGridBrowserData(item);
	}

	@Override
	protected Specification<UnionGrid> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<UnionGrid> qBuilder = new RequestQueryBuilder<UnionGrid>(root, query, cb);
			qBuilder.select(UnionGrid.class);			
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
	

	public GrilleColumnCount getColumnCountDetails(UnionGridFilter filter, java.util.Locale locale) throws Exception {	
		GrilleColumnCount columnCount = new GrilleColumnCount();
		loadFilterClosures(filter, true);	
		boolean ispublished = filter.getUnionGrid() != null && filter.getUnionGrid().isPublished();
		UnionGridQueryBuilder builder = ispublished ? new UnionGridPublishedQueryBuilder(filter, unionGridColumnRepository, spotService)
				: new UnionGridQueryBuilder(filter, unionGridColumnRepository, spotService);
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
	
	public long getColumnDuplicateCount(UnionGridFilter filter, java.util.Locale locale) throws Exception {	
		loadFilterClosures(filter, true);	
		boolean ispublished = filter.getUnionGrid() != null && filter.getUnionGrid().isPublished();
		UnionGridQueryBuilder builder = ispublished ? new UnionGridPublishedQueryBuilder(filter, unionGridColumnRepository, spotService) 
				: new UnionGridQueryBuilder(filter, unionGridColumnRepository, spotService);
		String sql = builder.buildColumnDuplicateCountQuery();
		log.trace("Column duplicate count query : {}", sql);
		Query query = this.session.createNativeQuery(sql);
		for (String key : builder.getParameters().keySet()) {
			query.setParameter(key, builder.getParameters().get(key));
		}
		Number number = (Number)query.getSingleResult();
		return number.longValue();
	}
	
	public BrowserDataPage<Object[]> getColumnDuplicate(UnionGridFilter filter, java.util.Locale locale) throws Exception {
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		loadFilterClosures(filter, true);	
		boolean ispublished = filter.getUnionGrid() != null && filter.getUnionGrid().isPublished();
		UnionGridQueryBuilder builder = ispublished ? new UnionGridPublishedQueryBuilder(filter, unionGridColumnRepository, spotService) 
				: new UnionGridQueryBuilder(filter, unionGridColumnRepository, spotService);
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

	@Transactional
	public UnionGridColumn createColumn(UnionGridCreateColumnData data, Locale locale) {
		if(data == null) {
			throw new BcephalException("Union grid creation data is NULL!");
		}
		if(data.getGridId() == null) {
			throw new BcephalException("Union grid ID is NULL!");
		}
		UnionGrid unionGrid = getById(data.getGridId());
		if(unionGrid == null) {
			throw new BcephalException("Union grid not found!");
		}
		UnionGridColumn column = createColumn(unionGrid, data.getDimensionType(), data.getColumnName(), locale);		
		return column;
	}
	
	
	public UnionGridColumn createColumn(UnionGrid unionGrid, DimensionType dimensionType, String columnName, Locale locale) {		
		UnionGridColumn column = unionGrid.getColumnByDimensionAndName(dimensionType, columnName);		
		
		for(UnionGridItem item : unionGrid.getItemListChangeHandler().getItems()) {
			if(item.getGrid() != null) {
				SmartMaterializedGridColumn smartColumn = null;
				if(column != null) {
					smartColumn = column.getColumnInListByGridId(item.getGrid().getId());
				}
				if(smartColumn == null) {
					MaterializedGridColumn col = materializedGridService.createColumn(item.getGrid().getId(), dimensionType, columnName);
					if(col != null) {
						if(column == null) {
							column = new UnionGridColumn();
							column.setName(columnName);
							column.setType(dimensionType);
							column.setPosition(unionGrid.getColumnListChangeHandler().getItems().size());
							unionGrid.getColumnListChangeHandler().addNew(column);
						}
						smartColumn = new SmartMaterializedGridColumn();
						smartColumn.setId(col.getId());
						column.getColumns().add(smartColumn);
					}
				}
				
			}
		}
		if(column != null) {
			
			unionGrid = save(unionGrid, locale);
		}
		return column;
	}
	
	public BrowserDataPage<Object> searchDimensionValues(DimensionDataFilter f, Locale locale) throws Exception {
		BrowserDataPage<Object> page = new BrowserDataPage<Object>();
		page.setPageSize(f.getPageSize());
		
		if(f != null) {
			UnionGridFilter filter = new UnionGridFilter(f);
			filter.setUnionGrid(getById(f.getDataSourceId()));
			loadFilterClosures(filter, false);	
			
			//loadDimensionFilterClosures(filter);
			DimensionValuesUnionGridQueryBuilder builder = new DimensionValuesUnionGridQueryBuilder(filter, f);
			builder.setParameterRepository(parameterRepository);
			Integer count = 0;
			if(filter.isAllowRowCounting()) {
				String sql = builder.buildCountQuery();
				log.trace("Count query : {}", sql);
				Query query = this.session.createNativeQuery(sql);
				for(String key : builder.getParameters().keySet()) {
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
			for(String key : builder.getParameters().keySet()) {
				query.setParameter(key, builder.getParameters().get(key));
			}
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			@SuppressWarnings("unchecked")
			List<Object> objects = query.getResultList();
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(objects); 
			log.debug("Row found : {}", objects.size());
		}
		return page;
	}
	
	
	static Long PAGE_SIZE = (long) 100000;
	
	public List<String> export(UnionGridExportData data, java.util.Locale locale) throws Exception {
		return export(data.getFilter(), data.getType(), locale);
	}
	
	public List<String> export(UnionGridFilter filter, GrilleExportDataType type, java.util.Locale locale) 
			throws Exception {
		List<String> paths = new ArrayList<String>();
		boolean canSearch = true;
		Long minPageSize = (long) 25000;
		filter.setPageSize(minPageSize.intValue());
		filter.setPage(1);
		int offste = 0;
		String path = null;
		UnionGridExporter excelExporter = null;
		UnionGridCsvExporter csvExporter = null;
		UnionGridJsonExporter jsonExporter = null;
		while (canSearch) {
			offste++;
			BrowserDataPage<GridItem> page = searchRows(filter, locale);
			if(path == null) {
				path = getPath(offste + " - " + filter.getUnionGrid().getName(), type);
			}
			boolean end = page.getItems().size() < minPageSize || (page.getPageLastItem()) % PAGE_SIZE == 0;
			if (type == GrilleExportDataType.EXCEL) {
				if(excelExporter == null) {
					excelExporter = new UnionGridExporter(filter.getUnionGrid(),filter.getExportColumnIds(), filter.isExportAllColumns());
				}
				excelExporter.writeRowss(page.getItems(), true);
				if(end) {
					excelExporter.writeFiles(path);
				}
			} else if (type == GrilleExportDataType.CSV) {
				if(csvExporter == null) {
					csvExporter = new UnionGridCsvExporter(path, filter.getUnionGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
				}else {				
					csvExporter.export(page.getItems(), true);
				}
				if(end) {
					csvExporter.close();
				}
			} else if (type == GrilleExportDataType.JSON) {
				if(jsonExporter == null) {
					jsonExporter = new UnionGridJsonExporter(path, filter.getUnionGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
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
