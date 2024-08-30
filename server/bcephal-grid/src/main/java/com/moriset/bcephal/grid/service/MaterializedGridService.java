package com.moriset.bcephal.grid.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleBrowserData;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleColumnCount;
import com.moriset.bcephal.grid.domain.GrilleEditedResult;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.GrilleSource;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.MaterializedExportData;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridCopyData;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGridEditedElement;
import com.moriset.bcephal.grid.domain.MaterializedGridFindReplaceFilter;
import com.moriset.bcephal.grid.repository.MaterializedGridColumnRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Primary
public class MaterializedGridService extends MainObjectService<MaterializedGrid, BrowserData> {
	
	@Autowired
	MaterializedGridRepository materializedGridRepository;
	
	@Autowired
	MaterializedGridColumnRepository materializedGridColumnRepository;
	
	@Autowired
	GridPublicationManager publicationManager;
	
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	InitiationService initiationService;
	
	@Autowired
	CalculatedMeasureRepository calculatedMeasureRepository;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SOURCING_MATERIALIZED_GRID;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel,profileId);
	}
	
	@Override
	public MaterializedGridRepository getRepository() {
		return materializedGridRepository;
	}
	
	
	@Override
	@Transactional
	public MaterializedGrid save(MaterializedGrid materializedGrid, Locale locale) {
		log.debug("Try to Save materialized grid : {}", materializedGrid);
		try {
			if (materializedGrid == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.materialized.grid", new Object[] { materializedGrid },
						locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(materializedGrid.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.materialized.grid.with.empty.name",
						new String[] { materializedGrid.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<MaterializedGridColumn> materializedGridColumns = materializedGrid.getColumnListChangeHandler();
			
			materializedGrid = super.save(materializedGrid, locale);
			
			MaterializedGrid id = materializedGrid;
			boolean isPublished = materializedGrid.isPublished();
			String table = materializedGrid.getMaterializationTableName();
			
			materializedGridColumns.getNewItems().forEach(item -> {
				item.setGrid(id);
				materializedGridColumnRepository.save(item);
			});
			materializedGridColumns.getUpdatedItems().forEach(item -> {
				item.setGrid(id);
				materializedGridColumnRepository.save(item);
			});
			materializedGridColumns.getDeletedItems().forEach(item -> {
				if(item.getId() != null) {
					materializedGridColumnRepository.deleteById(item.getId());
					if(isPublished) {
						String sql = "ALTER TABLE " + table + " DROP COLUMN IF EXISTS " + item.getDbColumnName();
						Query query = entityManager.createNativeQuery(sql);
						query.executeUpdate();	
					}
				}
			});			
						
			log.debug("Materialized grid successfully to save : {} ", materializedGrid);		
			
			return materializedGrid;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save materialized grid : {}", materializedGrid, e);
			String message = getMessageSource().getMessage("unable.to.save.materialized.grid", new Object[] { materializedGrid }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public int copyRows(MaterializedGridCopyData data, Locale locale) {		
		if (data.getFilter() == null && data.getFilter().getGrid() == null) {
			return -1;
		}
		MaterializedGridCopyRunner runner = new MaterializedGridCopyRunner();
		runner.setService(this);
		runner.setData(data);
		runner.setEntityManager(entityManager);
		int count = runner.run();	
		return count;
	}
	
	@Transactional
	public int duplicateRows(Long gridId, List<Long> ids, Locale locale, String username) {
		if (gridId == null || ids == null || ids.isEmpty()) {
			return 0;
		}
		MaterializedGrid grid =  getById(gridId);
		if(grid == null) {
			return 0;
		}
		try {
			String table = grid.getMaterializationTableName();
			String coma = "";
			String cols = "";
			String values = "";
			HashMap<String, Object> parameters = new HashMap<>();
			for(MaterializedGridColumn column : grid.getColumns()) {
				cols += coma + column.getDbColumnName();
								
				if(column.getCategory() == GrilleColumnCategory.LOAD_DATE) {
					values += coma + ":loadDate";
					parameters.put("loadDate", new Date());
				}
				else if(column.getCategory() == GrilleColumnCategory.LOAD_MODE) {
					values += coma + ":loadMode";
					parameters.put("loadMode", "M");
				}
				else if(column.getCategory() == GrilleColumnCategory.LOAD_USER) {
					values += coma + ":loadUser";
					parameters.put("loadUser", username);
				}
				else {
					values += coma + column.getDbColumnName();
				}			
				
				coma = ",";
			}			
//			String sql = "INSERT INTO " + table + "(" + cols + ") SELECT " + cols + " FROM " + table + " WHERE id IN (";	
			String sql = "INSERT INTO " + table + "(" + cols + ") SELECT " + values + " FROM " + table + " WHERE id IN (";
			coma = "";
			for (Long id : ids) {
				sql += coma + id;
				coma = ", ";
			}
			sql += ")";
			Query query = entityManager.createNativeQuery(sql);
			for(String var : parameters.keySet()) {
				query.setParameter(var, parameters.get(var));
			}
			query.executeUpdate();
			return 0;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to duplicate rows", ex);
			String message = getMessageSource().getMessage("unable.to.duplicate.rows", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 		
	}
	
	@Transactional
	public int deleteRows(Long gridId, List<Long> ids, Locale locale) {
		if (gridId == null || ids == null || ids.isEmpty()) {
			return 0;
		}
		MaterializedGrid grid =  getById(gridId);
		if(grid == null) {
			return 0;
		}		
		try {			
			String table = grid.getMaterializationTableName();
			String coma = "";
			String sql = "DELETE FROM " + table + " WHERE id IN (";
			for (Long id : ids) {
				sql += coma + id;
				coma = ", ";
			}
			sql += ")";
			Query query = entityManager.createNativeQuery(sql);
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.rows", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public int deleteAllRows(Long gridId, Long id, Locale locale) {
		if (gridId == null ) {
			return 0;
		}
		MaterializedGrid grid =  getById(gridId);
		if(grid == null) {
			return 0;
		}		
		try {			
			String table = grid.getMaterializationTableName();
			String sql = "DELETE FROM " + table;			
			Query query = entityManager.createNativeQuery(sql);
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete all rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.all.rows", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}	
	
	@Transactional
	public int deleteAllRows(MaterializedGridDataFilter filter, Locale locale) {			
		try {	
			loadFilterClosures(filter);	
			MaterializedGridQueryBuilder builder = new MaterializedGridQueryBuilder(filter);
			String sql = builder.buildDeleteQuery();
			log.trace("Delete all rows query : {}", sql);
			Query query = this.entityManager.createNativeQuery(sql);
			int count = query.executeUpdate();
			return count;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete all rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.all.rows", new Object[]{filter} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}	
	
	@Transactional
	public GrilleEditedResult editCell(MaterializedGridEditedElement element, Locale locale, String username) {
		return editOneCell(element, locale, username);
	}
	
	@Transactional
	public Long editCells(List<MaterializedGridEditedElement> elements, Locale locale, String username) {
		Long id = null;
		for(MaterializedGridEditedElement element : elements) {
			if(id != null && element.getId() == null) {
				element.setId(id);
			}
			editOneCell(element, locale, username);
			if(id == null) {
				id = element.getId();
			}
		}					
		return id;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected GrilleEditedResult editOneCell(MaterializedGridEditedElement element, Locale locale, String username) {
		try {	
			if(element.getColumn().getCategory() != GrilleColumnCategory.USER) {
				return new GrilleEditedResult();		
			}
			
			String table = element.getGrid().getMaterializationTableName();
			if (element.getId() == null) {
				String cols = " (ID";
				String values = " values(DEFAULT";
				String coma = ", ";
				HashMap<String, Object> parameters = new HashMap<>();
				MaterializedGridColumn column = element.getGrid().getColumnByCategory(GrilleColumnCategory.LOAD_DATE);
				if(column != null) {
					cols += coma + column.getDbColumnName();
					values += coma + ":loadDate";
					parameters.put("loadDate", new Date());
				}
				column = element.getGrid().getColumnByCategory(GrilleColumnCategory.LOAD_MODE);
				if(column != null) {
					cols += coma + column.getDbColumnName();
					values += coma + ":loadMode";
					parameters.put("loadMode", "M");
				}
				column = element.getGrid().getColumnByCategory(GrilleColumnCategory.LOAD_USER);
				if(column != null) {
					cols += coma + column.getDbColumnName();
					values += coma + ":loadUser";
					parameters.put("loadUser", username);
				}
				String sql = "INSERT INTO " + table + cols + ")" + values + ") returning ID";
				Query query = entityManager.createNativeQuery(sql);
				for(String var : parameters.keySet()) {
					query.setParameter(var, parameters.get(var));
				}
				Number id = (Number)query.getSingleResult();
				if(id != null) {
					element.setId(id.longValue());
				}
				
//				String sql = "INSERT INTO " + table + " (ID) values(DEFAULT) returning ID";
//				Query query = entityManager.createNativeQuery(sql);
//				Number id = (Number)query.getSingleResult();
//				if(id != null) {
//					element.setId(id.longValue());
//				}
			}

			if (element.getColumn().isMeasure()) {
				String col = element.getColumn().getDbColumnName();
				String sql = "UPDATE " + table + " SET " + col
						+ " = :measure WHERE id = :id";
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("measure", new TypedParameterValue(StandardBasicTypes.BIG_DECIMAL, element.getDecimalValue()));
				query.setParameter("id", element.getId());
				query.executeUpdate();
			} else if (element.getColumn().isAttribute()) {
				String col = element.getColumn().getDbColumnName();
				String sql = "UPDATE " + table + " SET " + col
						+ " = :scope WHERE id = :id";
				Query query = entityManager.createNativeQuery(sql);
				org.hibernate.query.Query query1 = (org.hibernate.query.Query) query;
				query1.setParameter("scope", new TypedParameterValue(StandardBasicTypes.STRING, element.getStringValue()));
				query.setParameter("id", element.getId());
				query.executeUpdate();
			} else if (element.getColumn().isPeriod()) {
				String col = element.getColumn().getDbColumnName();
				String sql = "UPDATE " + table + " SET " + col
						+ " = :period WHERE id = :id";
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("period", element.getDateValue(), TemporalType.DATE);				
				query.setParameter("id", element.getId());
				query.executeUpdate();
			}
			
			GrilleEditedResult result = new GrilleEditedResult();			
			return result;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to edit cell", ex);
			String message = getMessageSource().getMessage("unable.to.edit.cell", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}	
	
	public Object[] getGridRow(MaterializedGrid grid, Long id, Locale locale) {
		try {			
			if (grid != null || id != null) {				
				List<MaterializedGridColumn> columns = grid.getColumnListChangeHandler().getItems();
				Collections.sort(columns, new Comparator<MaterializedGridColumn>() {
					@Override
					public int compare(MaterializedGridColumn o1, MaterializedGridColumn o2) {
						return o1.getPosition() - o2.getPosition();
					}
				});
				
				String sql = "SELECT ";
				String coma = "";
				int i = 0;
				String table = grid.getMaterializationTableName();
				for(MaterializedGridColumn column : columns) {
					String col = column.isPersistent() && column.isPublished() ? column.getDbColumnName() : "null as nullcol" + i++;
					if(col != null) {
						sql = sql.concat(coma).concat(col);
						coma = ", ";
					}
				}
				sql = sql.concat(coma).concat(UniverseParameters.ID);
				sql = sql.concat(" FROM " ).concat(table);
				sql = sql.concat(" WHERE " ).concat(UniverseParameters.ID).concat(" = :id");
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("id", id);
				Object[] objects = (Object[])query.getSingleResult();
				return objects;
			}
		} 
		catch (NoResultException e) { }
		catch (Exception ex) {
			log.error("Unable to read row data", ex);
			String message = getMessageSource().getMessage("unable.to.read.row.data", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
		return null;
	}
	
	public int findAndReplaceCount(MaterializedGridFindReplaceFilter criateria, Locale locale) {		
		try {
			if(criateria.getFilter() != null && criateria.getFilter().getGrid() != null) {
				loadFilterClosures(criateria.getFilter());	
				MaterializedGridFindReplaceQueryBuilder builder = new MaterializedGridFindReplaceQueryBuilder(criateria);
				String sql = builder.buildCountQuery();
				Query query = entityManager.createNativeQuery(sql);
				Number number = (Number)query.getSingleResult();
				return number.intValue();
			}
		} catch (Exception ex) {
			log.error("Unable to count value to remplace", ex);
			String message = getMessageSource().getMessage("unable.to.count.value.to.replace", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
		return -1;
	}

	@Transactional
	public int findAndReplace(MaterializedGridFindReplaceFilter criateria, Locale locale) {		
		try {
			if(criateria.getFilter() != null && criateria.getFilter().getGrid() != null) {
				loadFilterClosures(criateria.getFilter());	
				MaterializedGridFindReplaceQueryBuilder builder = new MaterializedGridFindReplaceQueryBuilder(criateria);
				String sql = builder.buildQuery();
				boolean isMeasure = criateria.getColumn().isMeasure();
				boolean isPeriod = criateria.getColumn().isPeriod();
				Object value = criateria.getReplaceValue();
				if(isMeasure) {
					value = criateria.getMeasureReplaceValue();
				}
				else if(isPeriod) {
					value = builder.buildPeriodVal(criateria.getReplaceValue());
				}
				else {
					if(value == null) {
						value = "";
					}
				}
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("value", value);
				int count = query.executeUpdate();
				return count;
			}
		} catch (Exception ex) {
			log.error("Unable to replace value", ex);
			String message = getMessageSource().getMessage("unable.to.replace.value", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
		return -1;		
	}
	
	@Transactional
	public EditorData<MaterializedGrid> publish(Long id, Locale locale, HttpSession session) {
		try {
			log.debug("Try to read grid by id : {}", id);
			MaterializedGrid grid = getById(id);
			if(grid == null) {
				throw new BcephalException("There is no grid with id : " + id);
			}
			
			if(grid.isPublished()) {
				grid = refreshPublication(grid);
			}
			else {
				grid = performPublication(grid);							
			}
			materializedGridRepository.save(grid);
			
//			String sql = "UPDATE MaterializedGridColumn SET published = true WHERE grid = " + grid.getId();
//			Query query = entityManager.createQuery(sql);
//			query.executeUpdate();
			
			
			EditorDataFilter filter = new EditorDataFilter();
			filter.setId(id);
			return getEditorData(filter, session, locale);
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to publish the grid", ex);
			String message = getMessageSource().getMessage("unable.to.publish.grid", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	@Transactional
	public MaterializedGrid publish(MaterializedGrid grid, Locale locale) {
		try {
			
			if(grid.isPublished()) {
				grid = refreshPublication(grid);
			}
			else {
				grid = performPublication(grid);							
			}
			materializedGridRepository.save(grid);			
			return grid;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to publish the grid", ex);
			String message = getMessageSource().getMessage("unable.to.publish.grid", new Object[]{} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public boolean publish(List<Long> ids, Locale locale, HttpSession session) {
		try {
			for(Long id : ids) {
				publish(id, locale, session);
			}
			return true;
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to publish the grids", ex);
			String message = getMessageSource().getMessage("unable.to.publish.grids", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	public MaterializedGrid performPublication(MaterializedGrid grid) {
		grid.setPublished(true);		
		String sql = grid.getCreationSql();
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();	
		for(MaterializedGridColumn column : grid.getColumnListChangeHandler().getItems()) {
			column.setPublished(true);
			grid.getColumnListChangeHandler().addUpdated(column);
			materializedGridColumnRepository.save(column);
		}		
		return grid;
	}
	
	private MaterializedGrid refreshPublication(MaterializedGrid grid) {
		String table = grid.getMaterializationTableName();		
		for(MaterializedGridColumn column : grid.getColumnListChangeHandler().getItems()) {
			if(!column.isPublished()) {
				String sql = "ALTER TABLE " + table + " ADD COLUMN IF NOT EXISTS " + column.getDbColumnName() + " " + column.getDbColumnType();
				Query query = entityManager.createNativeQuery(sql);
				query.executeUpdate();	
				column.setPublished(true);
				grid.getColumnListChangeHandler().addUpdated(column);
				materializedGridColumnRepository.save(column);
			}
		}		
		return grid;
	}
	
	public List<MaterializedGridColumn> getColumns(Long gridId) {
		return materializedGridColumnRepository.findByGrid(gridId);
	}
	
	public boolean setEditable(Long id, boolean editable, Locale locale) {
		log.debug("Try to set etitable : {}", editable);		
		try {	
			if(id == null) {
				String message = getMessageSource().getMessage("id.is.null", new Object[]{} , locale);
				throw new BcephalException(message);
			}
			MaterializedGrid grid = getById(id);
			if(grid == null) {
				String message = getMessageSource().getMessage("unkown.grid", new Object[]{id} , locale);
				throw new BcephalException(message);
			}			
			grid.setEditable(editable);			
			grid = materializedGridRepository.save(grid);
	        return true;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while setting editable : {}", id, e);
			String message = getMessageSource().getMessage("unable.to.save.grille", new Object[]{id} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
		
	public BrowserDataPage<Object[]> searchRows(MaterializedGridDataFilter filter, java.util.Locale locale) {		
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		if(filter.getGrid() != null && filter.getGrid().isPublished()) {
			loadFilterClosures(filter);	
			MaterializedGridQueryBuilder builder = new MaterializedGridQueryBuilder(filter);
			Integer count = 0;
			if(filter.isAllowRowCounting()) {
				String sql = builder.buildCountQuery();
				log.trace("Count query : {}", sql);
				Query query = this.entityManager.createNativeQuery(sql);
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
			Query query = this.entityManager.createNativeQuery(sql);
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
		}		
		return page;
	}

	private void loadFilterClosures(MaterializedGridDataFilter filter) {
		List<MaterializedGridColumn> columns = filter.getGrid().getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<MaterializedGridColumn>() {
			@Override
			public int compare(MaterializedGridColumn o1, MaterializedGridColumn o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		filter.getGrid().setColumns(columns);
		
		columns.forEach(column -> {
			if(column.isCalculatedMeasure() && column.getDimensionId() != null) {
				Optional<CalculatedMeasure> result = calculatedMeasureRepository.findById(column.getDimensionId());	
				column.setCalculatedMeasure(result.isPresent() ? result.get() : null);
				if(column.getCalculatedMeasure() != null) {
					column.getCalculatedMeasure().sortItems();
				}
			}			
			});
		
	}

	@Override
	protected Specification<MaterializedGrid> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<MaterializedGrid> qBuilder = new RequestQueryBuilder<MaterializedGrid>(root, query, cb);
			qBuilder.select(GrilleBrowserData.class);			
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
	protected void build(ColumnFilter columnFilter) {
		super.build(columnFilter);
		if ("CategoryMaterialized".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("category");
			columnFilter.setType(GrilleCategory.class);
		}else if ("published".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("published");
			columnFilter.setType(Boolean.class);
		}else if ("editable".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("editable");
			columnFilter.setType(Boolean.class);
		}
	}
	public GrilleColumnCount getColumnCountDetails(MaterializedGridDataFilter filter, java.util.Locale locale) {	
		GrilleColumnCount columnCount = new GrilleColumnCount();
		loadFilterClosures(filter);	
		MaterializedGridQueryBuilder builder = new MaterializedGridQueryBuilder(filter);
		String sql = builder.buildColumnCountDetailsQuery();
		log.trace("Column count details query : {}", sql);
		Query query = this.entityManager.createNativeQuery(sql);
		Object[] number = (Object[])query.getSingleResult();
		columnCount.setCountItems(((Number)number[0]).longValue());
		columnCount.setSumItems(BigDecimal.valueOf(((Number)number[1]).doubleValue()));
		columnCount.setMaxItem(BigDecimal.valueOf(((Number)number[2]).doubleValue()));
		columnCount.setMinItem(BigDecimal.valueOf(((Number)number[3]).doubleValue()));
		columnCount.setAverageItems(BigDecimal.valueOf(((Number)number[4]).doubleValue()));
		return columnCount;
	}
	
	
	public long getColumnDuplicateCount(MaterializedGridDataFilter filter, java.util.Locale locale) {	
		loadFilterClosures(filter);	
		MaterializedGridQueryBuilder builder = new MaterializedGridQueryBuilder(filter);
		String sql = builder.buildColumnDuplicateCountQuery();
		log.trace("Column duplicate count query : {}", sql);
		Query query = this.entityManager.createNativeQuery(sql);
		Number number = (Number)query.getSingleResult();
		return number.longValue();
	}
	
	public BrowserDataPage<Object[]> getColumnDuplicate(MaterializedGridDataFilter filter, java.util.Locale locale) {
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
		loadFilterClosures(filter);	
		MaterializedGridQueryBuilder builder = new MaterializedGridQueryBuilder(filter);	
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
		Query query = this.entityManager.createNativeQuery(sql);
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

	@Override
	protected BrowserData getNewBrowserData(MaterializedGrid item) {	
		return new GrilleBrowserData(item);
	}
	
	public MaterializedGrid buildNewGrid(String baseName, Locale locale) {
		if(StringUtils.hasText(baseName)) {
			return getNewItem(baseName, false);
        }
		return getNewItem();
	}
	
	protected MaterializedGrid getNewItem(String baseName, boolean startWithOne) {
		MaterializedGrid grid = new MaterializedGrid();
		int i = 0;
		grid.setName(baseName);
		if(startWithOne) {
			i = 1;
			grid.setName(baseName + i);
		}
		while(getByName(grid.getName()) != null) {
			i++;
			grid.setName(baseName + i);
		}
		grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_NBR, DimensionType.ATTRIBUTE, "Load nbr", 0));
		grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_DATE, DimensionType.PERIOD, "Load date", 1));
		grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_MODE, DimensionType.ATTRIBUTE, "Load mode", 2));
		grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_USER, DimensionType.ATTRIBUTE, "Creator", 3));
		grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_SOURCE_NAME, DimensionType.ATTRIBUTE, "Load source", 4));
		grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.OPERATION_CODE, DimensionType.ATTRIBUTE, "Operation code", 5));
		return grid;
	}
	
	protected MaterializedGrid getNewItem(String baseName) {
		MaterializedGrid grid = getNewItem(baseName, true);
		return grid;
	}

	@Override
	protected MaterializedGrid getNewItem() {
		MaterializedGrid grid = getNewItem("Materialized Grid ");
		return grid;
	}
	static Long PAGE_SIZE = (long) 100000;

	public List<String> export(MaterializedExportData data, java.util.Locale locale) throws Exception {
		return export(data.getFilter(), data.getType(), locale);
	}
	
	public List<String> export(MaterializedGridDataFilter filter, GrilleExportDataType type, java.util.Locale locale)
			throws Exception {
		List<String> paths = new ArrayList<String>();
		boolean canSearch = true;
		Long minPageSize = (long) 25000;
		filter.setPageSize(minPageSize.intValue());
		filter.setPage(1);
		filter.setOrderById(true);
		int offste = 0;
		String path = null;
		MaterializedExporter excelExporter = null;
		MaterializedCsvExporter csvExporter = null;
		MaterializedJsonExporter jsonExporter = null;
		while (canSearch) {
			offste++;
			BrowserDataPage<Object[]> page = searchRows(filter, locale);
			if(path == null) {
				path = getPath(offste + " - " + filter.getGrid().getName(), type);
			}
			boolean end = page.getItems().size() < minPageSize || (page.getPageLastItem()) % PAGE_SIZE == 0;
			if (type == GrilleExportDataType.EXCEL) {
				if(excelExporter == null) {
					excelExporter = new MaterializedExporter(filter.getGrid(),filter.getExportColumnIds(), filter.isExportAllColumns());
				}
				excelExporter.writeRowss(page.getItems(), true);
				if(end) {
					excelExporter.writeFiles(path);
				}
			} else if (type == GrilleExportDataType.CSV) {
				if(csvExporter == null) {
					csvExporter = new MaterializedCsvExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
				}else {				
					csvExporter.export(page.getItems(), true);
				}
				if(end) {
					csvExporter.close();
				}
			} else if (type == GrilleExportDataType.JSON) {
				if(jsonExporter == null) {
					jsonExporter = new MaterializedJsonExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
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
	
	public List<String> performExport(MaterializedGridDataFilter filter, GrilleExportDataType type, int maxRowCountPerFile) throws Exception {
		List<String> paths = new ArrayList<>();
		int fileNbr = 0;
		String baseName = filter.getGrid().getName() + System.currentTimeMillis();
		boolean canSearch = true;
		filter.setPageSize(maxRowCountPerFile);
		filter.setPage(1);
		filter.setAllowRowCounting(false);
		MaterializedExporter excelExporter = null;
		MaterializedCsvExporter csvExporter = null;
		MaterializedJsonExporter jsonExporter = null;
		
		while (canSearch) {
			BrowserDataPage<Object[]> page = searchRows(filter, Locale.ENGLISH);
			int count = page.getItems().size();
			if(count > 0) {
				String path = getPath(baseName, type, ++fileNbr);
				paths.add(path);
				if (type == GrilleExportDataType.EXCEL) {
					excelExporter = new MaterializedExporter(filter.getGrid(), filter.getExportColumnIds(), filter.isExportAllColumns());
					excelExporter.writeRowss(page.getItems(), true);
					excelExporter.writeFiles(path);
				} else if (type == GrilleExportDataType.CSV) {
					csvExporter = new MaterializedCsvExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
					csvExporter.export(page.getItems(), true);
					csvExporter.close();
				} else if (type == GrilleExportDataType.JSON) {
					jsonExporter = new MaterializedJsonExporter(path, filter.getGrid(),true,filter.getExportColumnIds(), filter.isExportAllColumns());
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
		Optional<MaterializedGridColumn> result = materializedGridColumnRepository.findById(filter.getGroupId());
		if (result.isPresent()) {
			MaterializedGridColumn column = result.get();
			if (column.isPublished()) {
				return getColumnValues(filter, column, locale);
			} else {
				throw new BcephalException("Materialized grid column is not published. ID : {}, Name : {}", filter.getGroupId(), column.getName());
			}
		} else {
			throw new BcephalException("Materialized grid column not found. ID : {}", filter.getGroupId());
		}
	}
	
	private BrowserDataPage<String> getColumnValues(BrowserDataFilter filter, MaterializedGridColumn column, java.util.Locale locale) {

		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<String> page = new BrowserDataPage<String>();
		page.setPageSize(filter.getPageSize());
		Integer count = 0;

		MaterializedGrid grid = new MaterializedGrid(filter.getDataSourceId());
		String col = column.getDbColumnName();
		String sql = "SELECT distinct " + col + " FROM " + grid.getMaterializationTableName() + " WHERE " + col
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
			Query query = this.entityManager.createNativeQuery(countsql);
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

		Query query = this.entityManager.createNativeQuery(sql);
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

	public Long createReport(Long gridId, String reportName, Locale locale) {
		MaterializedGrid grid = getById(gridId);
		if(grid != null) {
			Grille report = new Grille();
			report.setName(reportName);
			report.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
			report.setDataSourceId(grid.getId());
			report.setType(GrilleType.REPORT);
			report.setSourceType(GrilleSource.USER);
			report.setSourceId(null);
			report.setEditable(false);	
			report.setConsolidated(true);
			report.setDebit(false);
			report.setCredit(false);
			report.setPublished(false);
			report.setGroup(grid.getGroup());
			report.setVisibleInShortcut(grid.isVisibleInShortcut());
			report.setShowAllRowsByDefault(grid.isShowAllRowsByDefault());
			report.setAllowLineCounting(grid.isAllowLineCounting());
			
			for(MaterializedGridColumn col : grid.getColumnListChangeHandler().getItems()) {
				GrilleColumn column = new GrilleColumn(null, col.getName(), col.getType(), col.getId(), col.getName());
				column.setDataSourceType(report.getDataSourceType());
				column.setDataSourceId(report.getDataSourceId());
				column.setPosition(col.getPosition());
				column.setFormat(col.getFormat());
				column.setEditable(col.isEditable());
				column.setShow(col.isShow());	
				column.setBackgroundColor(col.getBackgroundColor());
				column.setForegroundColor(col.getForegroundColor());
				column.setWidth(col.getWidth());
				column.setFixedType(col.getFixedType());
				column.setGroupBy(col.getGroupBy());
				report.getColumnListChangeHandler().addNew(column);
			}	
			
			report = grilleService.save(report, locale);
			return report.getId();
		}
		return null;
	}

	@Transactional
	public MaterializedGridColumn createColumn(Long gridId, DimensionType dimensionType, String columnName) {
		MaterializedGrid grid = getById(gridId);
		if(grid == null) {
			throw new BcephalException("Standalone grid not found!");
		}
		
		MaterializedGridColumn column = grid.getColumnByDimensionAndName(dimensionType, columnName);
		if(column == null) {
			column = new MaterializedGridColumn();
			column.setName(columnName);
			column.setType(dimensionType);
			column.setPosition(grid.getColumnListChangeHandler().getItems().size());
			grid.getColumnListChangeHandler().addNew(column);
			grid = save(grid, Locale.ENGLISH);
		}
		if(!column.isPublished()) {
			grid = publish(grid, Locale.ENGLISH);
		}
		return column;
	}
}
