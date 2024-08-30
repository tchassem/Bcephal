/**
 * 
 */
package com.moriset.bcephal.archive.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.archive.domain.ArchiveConfiguration;
import com.moriset.bcephal.archive.domain.ArchiveConfigurationEnrichmentItem;
import com.moriset.bcephal.archive.domain.ArchiveLog;
import com.moriset.bcephal.archive.domain.ArchiveLogAction;
import com.moriset.bcephal.archive.domain.ArchiveLogStatus;
import com.moriset.bcephal.archive.domain.ArchiveStatus;
import com.moriset.bcephal.archive.repository.ArchiveLogRepository;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.ArchiveGridQueryBuilder;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.CsvGenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Component
@Data
@Slf4j
public class ArchiveGenerationRunner {

	private ArchiveConfiguration configuration;
	private TaskProgressListener listener;
	private CsvGenerator universeCsvGenerator;

	private boolean stopped;

	String loaderNbrColumn;
	String loaderFileColumn;
	String loadNbr;

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired ArchiveLogRepository archiveLogRepository;
	@Autowired ArchiveService archiveService;
	@Autowired ArchiveLogService archiveLogService;
	@Autowired MaterializedGridService materializedGridService;
	@Autowired ParameterRepository parameterRepository;
	
	String sessionId;

	String username = "B-CEPHAL";
	
	MaterializedGrid logMatGrid;
	Map<String, Object> parameters;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void run() {		
		log.debug("Try to generate archive : {}", configuration.getName());
		ArchiveLog archiveLog = null;
		try {			
			
			Archive archive = initArchive(configuration);			
			archiveLog = initArchiveLog(archive, configuration);			
			
			if(configuration.getBackupGrid().getColumnListChangeHandler().getItems().size() == 0) {
				throw new BcephalException("The backup grid has no column.");
			}			
			if(configuration.isAllowReplacementGrid()) {
				if(configuration.getReplacementGrid().getColumnListChangeHandler().getItems().size() == 0) {
					throw new BcephalException("The replacement grid has no column.");
				}
				if(configuration.getReplacementTargetType().isMaterializedGrid() 
						&& configuration.getReplacementGridCreationtType().isAppend()
						&& configuration.getReplacementMatGridId() == null) {
					throw new BcephalException("The replacement materialized grid should be provied.");
				}
			}
						
			
			if (getListener() != null) {
				getListener().start(16);
			}
			
			log.trace("Archive generation : {}. Build GrilleDataFilter...", configuration.getName());
			GrilleDataFilter backupFilter = buildGrilleFilter(configuration.getBackupGrid(), null);			
			if (getListener() != null) { getListener().nextStep(1); }
			log.trace("Archive generation : {}. Build replacementFilter...", configuration.getName());
			GrilleDataFilter replacementFilter = configuration.isAllowReplacementGrid() ? buildGrilleFilter(configuration.getReplacementGrid(), configuration.getBackupGrid()) : null;
			if (getListener() != null) { getListener().nextStep(1); }
						
			
			
			log.trace("Archive generation : {}. Build Backup count query...", configuration.getName());
			String sql = new ArchiveGridQueryBuilder(backupFilter).buildCountQuery();	
			if (getListener() != null) { getListener().nextStep(1); }			
			log.trace("Archive generation : {}. Backup Count query : \n{}", configuration.getName(), sql);			
			log.trace("Archive generation : {}. Execute Backup count query...", configuration.getName());
			long count = 0;
			try {
				Query query = entityManager.createNativeQuery(sql);
				Number number = (Number)query.getSingleResult();
				if(number != null) {
					count = number.longValue();
				}				
			}
			catch (Exception e) {
				throw new BcephalException("Unexpected error when counting backup lines to archive.", e);
			}
			if (getListener() != null) { getListener().nextStep(1); }
			
			log.debug("Archive generation : {}. Backup line Count : {}", configuration.getName(), count);
	        if(count == 0) {
	        	throw new BcephalException("The backup grid is empty!");
	        }
	        
	        if(configuration.isAllowReplacementGrid() && replacementFilter != null) {
		        log.trace("Archive generation : {}. Build Replacement count query...", configuration.getName());
		        sql = new ArchiveGridQueryBuilder(replacementFilter).buildCountQuery();
		        if (getListener() != null) { getListener().nextStep(1); }	
		        log.trace("Archive generation : {}. Replacement Count query : \n{}", configuration.getName(), sql);			
		        log.trace("Archive generation : {}. Execute Replacement count query...", configuration.getName());
				long c = 0;
				try {
					Query query = entityManager.createNativeQuery(sql);
					Number number = (Number)query.getSingleResult();
					if(number != null) {
						c = number.longValue();
					}				
				}
				catch (Exception e) {
					throw new BcephalException("Unexpected error when counting replacement lines to archive.", e);
				}
				log.debug("Archive generation : {}. Replacement line Count : {}", configuration.getName(), c);
		        if(c == 0) {
		        	throw new BcephalException("The replacement grid is empty!");
		        }
	        }
	        if (getListener() != null) { getListener().nextStep(1); }		
	        
			
	        log.trace("Archive generation : {}. Build backup select query...", configuration.getName());
			sql = new ArchiveGridQueryBuilder(backupFilter).buildBackupQuery();	
			log.trace("Archive generation : {}. Backup select query : \n{}", configuration.getName(), sql);
			if (getListener() != null) { getListener().nextStep(1); }
	        
			String code = "ARCHIVE_" + System.currentTimeMillis();
			String tableName = code;
			
			String createSql = "CREATE TABLE ".concat(tableName).concat(" AS (").concat(sql).concat(")");
			log.trace("Archive generation : {}. Create backup table query : \n{}", configuration.getName(), createSql);
			if (getListener() != null) { getListener().nextStep(1); }
						
			log.trace("Archive generation : {}. Build delete query...", configuration.getName());
			String deleteSql = new ArchiveGridQueryBuilder(backupFilter).buildDeleteQuery();
			log.trace("Archive generation : {}. Delete query : \n{}", configuration.getName(), deleteSql);
			if (getListener() != null) { getListener().nextStep(1); }
			
			log.trace("Archive generation : {}. Build relacement query...", configuration.getName());
			String replacementSql = configuration.isAllowReplacementGrid() ? new ArchiveGridQueryBuilder(replacementFilter).buildQuery() : null;
			log.trace("Archive generation : {}. Replacement query : \n{}", configuration.getName(), replacementSql);	
			if (getListener() != null) { getListener().nextStep(1); }
			
			archive.setCode(code);
			archive.setLineCount(count);
			archive.setTableName(tableName);	
			archive.setAllowReplacementGrid(configuration.isAllowReplacementGrid());
			archive.setReplacementTargetType(configuration.getReplacementTargetType());
			archiveLog.setLineCount(count);
						
			try {
				
				archive = archiveService.save(archive, Locale.ENGLISH);
				if (getListener() != null) { getListener().nextStep(1); }
				archiveLog.setArchiveId(archive.getId());
				archiveLog = archiveLogService.save(archiveLog, Locale.ENGLISH);
				saveOrUpdateMatGridLog(archiveLog);
				if (getListener() != null) { getListener().nextStep(1); }
				
				log.debug("Archive generation : {}. Try to create archive table '{}'...", configuration.getName(), tableName);
				log.trace("Query : {}", createSql);
				Query query = entityManager.createNativeQuery(createSql);
				query.executeUpdate();
				if (getListener() != null) { getListener().nextStep(1); }
				log.debug("Archive generation : {}. Archive table '{}' created!", configuration.getName(), tableName);
				
				List<Object[]> rows = new ArrayList<>();
				if(configuration.isAllowReplacementGrid()) {
					log.debug("Archive generation : {}. Try to build replacement rows...", configuration.getName());
					log.trace("Query : {}", replacementSql);
					query = entityManager.createNativeQuery(replacementSql);
					rows = query.getResultList();
					log.debug("Archive generation : {}. Replacement rows count : {}", configuration.getName(), rows.size());
				}
				if (getListener() != null) { getListener().nextStep(1); }
				
				log.debug("Archive generation : {}. Try to delete archived rows...", configuration.getName());
				query = entityManager.createNativeQuery(deleteSql);
				query.executeUpdate();
				if (getListener() != null) { getListener().nextStep(1); }
				log.debug("Archive generation : {}. Archived rows deleted!", configuration.getName());
				
				if(replacementFilter != null && replacementFilter.getGrid() != null) {					
					log.debug("Archive generation : {}. Try to load enrichment values...", configuration.getName());
					for(ArchiveConfigurationEnrichmentItem item : configuration.getEnrichmentItems()) {
						String backupValueSql = item.getBackupValueSql(tableName);			
						if (backupValueSql == null) continue;
						query = entityManager.createNativeQuery(backupValueSql);
						List<Object> value = query.getResultList();
						if (value.size() > 0) {
							item.columnValue = value.get(0);
						}	
					}
					log.debug("Archive generation : {}. Enrichment values loaded!", configuration.getName(), tableName);
					
					if(configuration.isAllowReplacementGrid()) {
						if(configuration.getReplacementTargetType().isUniverse()) {
							ArchiveConsolidationQueryBuilder cosolidationBuilder = new ArchiveConsolidationQueryBuilder(replacementFilter, archive.getId(), username);					
							for(Object[] row : rows) {
								log.debug("Archive generation : {}. Try to insert replacement row...", configuration.getName());
								String consolidationSql = cosolidationBuilder.buildInsertQuery(replacementFilter, configuration.getEnrichmentItems(), row);
								log.trace("Archive generation : {}. Insert Replacement query : \n{}", configuration.getName(), consolidationSql);
								query = entityManager.createNativeQuery(consolidationSql);
								for(String key : cosolidationBuilder.parameters.keySet()) {
									query.setParameter(key, cosolidationBuilder.parameters.get(key));
								}
								query.executeUpdate();
								log.debug("Archive generation : {}. Replacement row insert!", configuration.getName());
							}							
						}
						else if(configuration.getReplacementTargetType().isMaterializedGrid()) {
							MaterializedGrid matGrid = buildReplacementMaterializedGrid(replacementFilter);							
							if(matGrid == null) {
								throw new BcephalException("The replacement materialized grid not found: " + configuration.getReplacementMatGridId()) ;
							}
							archive.setReplacementMatGridId(matGrid.getId());
							archive = archiveService.save(archive, Locale.ENGLISH);
							
							ArchiveMatGridQueryBuilder matGridBuilder = new ArchiveMatGridQueryBuilder(replacementFilter, archive, matGrid, username, RunModes.A);					
							for(Object[] row : rows) {
								log.debug("Archive generation : {}. Try to insert replacement row into materialized grid...", configuration.getName());
								String matGridInsertSql = matGridBuilder.buildInsertQuery(replacementFilter, configuration.getEnrichmentItems(), row);
								log.trace("Archive generation : {}. Materialoized grid insert Replacement query : \n{}", configuration.getName(), matGridInsertSql);
								query = entityManager.createNativeQuery(matGridInsertSql);
								for(String key : matGridBuilder.parameters.keySet()) {
									query.setParameter(key, matGridBuilder.parameters.get(key));
								}
								query.executeUpdate();
								log.debug("Archive generation : {}. Replacement row insert into materialized grid!", configuration.getName());
							}
						}
					}
					if (getListener() != null) { getListener().nextStep(1); }
				}
			} 
			catch (Exception e) {
				log.error("Archive generation : {}. Unexpected error", configuration.getName(), e);
				throw new BcephalException("Unable to save archive.", e);
			} 
			endArchiveLog(archiveLog, null);
			if (getListener() != null) {
				getListener().end();
			}
			
		} catch (Exception e) {
			log.error("ARCHIVE GENERATION : {}		generation fail !", configuration.getName(), e);
			String message = "Unable to generate archive. \nUnexpected error!";
			if (e instanceof BcephalException) {
				message = e.getMessage();
			}
			endArchiveLog(archiveLog, message);
			if (getListener() != null) {
				getListener().error(message, true);
			}
		} finally {

		}
	}

	private MaterializedGrid buildReplacementMaterializedGrid(GrilleDataFilter replacementFilter) {
		MaterializedGrid matGrid = null;
		
		if(configuration.getReplacementGridCreationtType().isAppend()) {
			matGrid = materializedGridService.getById(configuration.getReplacementMatGridId());
		}
		else if(configuration.getReplacementGridCreationtType().isNew()) {
			matGrid = new MaterializedGrid();
			String prefix = !StringUtils.hasText(configuration.getReplacementMatGridName()) ? configuration.getName() : configuration.getReplacementMatGridName();
			String name = prefix;
			boolean found = true;
			int index = 1;
			while(found) {
				found = materializedGridService.getByName(name) != null;
				if(found) {
					name = prefix + " " + index++;
				}
			}
			matGrid.setName(name);
			for(GrilleColumn col : replacementFilter.getGrid().getColumns()) {
				MaterializedGridColumn column = new MaterializedGridColumn(col);
				matGrid.getColumnListChangeHandler().addNew(column);
			}
		}
		if(matGrid != null) {
			int position = matGrid.getColumnListChangeHandler().getItems().size();	
			if(matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_NBR) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("Archive ID", position++, DimensionType.ATTRIBUTE, GrilleColumnCategory.LOAD_NBR));
			}
			if(matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_SOURCE_NAME) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("Archive name", position++, DimensionType.ATTRIBUTE, GrilleColumnCategory.LOAD_SOURCE_NAME));
			}
			if(matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_MODE) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("Archive mode", position++, DimensionType.ATTRIBUTE, GrilleColumnCategory.LOAD_MODE));
			}
			if(matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_USER) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("User", position++, DimensionType.ATTRIBUTE, GrilleColumnCategory.LOAD_USER));
			}
			if(matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_DATE) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("Archive date", position++, DimensionType.PERIOD, GrilleColumnCategory.LOAD_DATE));
			}
			if(matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_TIME) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("Archive time", position++, DimensionType.ATTRIBUTE, GrilleColumnCategory.LOAD_TIME));
			}
			if(matGrid.getColumnByCategory(GrilleColumnCategory.OPERATION_CODE) == null) {
				matGrid.getColumnListChangeHandler().addNew(buildMatGridColumn("Operation code", position++, DimensionType.ATTRIBUTE, GrilleColumnCategory.OPERATION_CODE));
			}
			matGrid = materializedGridService.save(matGrid, Locale.ENGLISH);
			matGrid = materializedGridService.publish(matGrid, Locale.ENGLISH);
		}		
		return matGrid;
	}

	private MaterializedGridColumn buildMatGridColumn(String name, int position, DimensionType type, GrilleColumnCategory category) {
		MaterializedGridColumn column = new MaterializedGridColumn();
		column.setCategory(category);
		column.setType(type);
		column.setName(name);
		column.setPosition(position);
		return column;
	}

	private GrilleDataFilter buildGrilleFilter(Grille grid, Grille secondGrid) {
		GrilleDataFilter filter = new GrilleDataFilter();
		filter.setGrid(grid);	
		if(secondGrid != null && secondGrid.getAdminFilter() != null) {
			grid.setAdminFilter(secondGrid.getAdminFilter().copy());
		}
		if(secondGrid != null && secondGrid.getUserFilter() != null){
			grid.setUserFilter(secondGrid.getUserFilter().copy());
		}
		
		List<GrilleColumn> columns = filter.getGrid().getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<GrilleColumn>() {
			@Override
			public int compare(GrilleColumn value1, GrilleColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}			
		});
		filter.getGrid().setColumns(columns);		
		return filter;
	}

	private Archive initArchive(ArchiveConfiguration config) {
		log.trace("Archive initialization...");
		Archive archive = new Archive();
		archive.setConfigurationId(config.getId());
		archive.setGroup(config.getGroup());
		archive.setUserName(username);
		archive.setName(StringUtils.hasText(config.getArchiveName()) ? config.getArchiveName() : config.getName());
		//archive.setDescription(StringUtils.hasText(config.getArchiveDescription()) ? config.getArchiveDescription() : config.getDescription());
		archive.setStatus(ArchiveStatus.ARCHIVED);
		log.trace("Archive initialized!");
		return archive;
	}

	private ArchiveLog initArchiveLog(Archive archive, ArchiveConfiguration config) {
		log.trace("Archive log initialization...");
		ArchiveLog archiveLog = new ArchiveLog();
		archiveLog.setName(archive.getName());
		archiveLog.setUserName(username);
		archiveLog.setMessage(config.getDescription());
		archiveLog.setAction(ArchiveLogAction.CREATION);
		archiveLog.setStatus(ArchiveLogStatus.SUCCESS);
		
		Parameter parameter = parameterRepository.findByCodeAndParameterType(InitiationParameterCodes.LOGS_ARCHIVE_MAT_GRID, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = materializedGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				logMatGrid = result.get();
			}
		}
		
		log.trace("Archive log initialized!");
		return archiveLog;
	}
	
	private void endArchiveLog(ArchiveLog archiveLog, String message) {
		archiveLog.setMessage(message);
		archiveLog.setStatus(ArchiveLogStatus.SUCCESS);
		if (StringUtils.hasText(message)) {
			archiveLog.setStatus(ArchiveLogStatus.ERROR);
		}
		archiveLogRepository.save(archiveLog);
		saveOrUpdateMatGridLog(archiveLog);
	}
	
	private void saveOrUpdateMatGridLog(ArchiveLog archiveLog) {
		if(logMatGrid != null) {
			Object obj = null;
			Query query = null;
			try {
				query = entityManager.createNativeQuery(buildSelectQuery(archiveLog.getId()));
				obj = (Object[])query.getSingleResult();	
			}
			catch (NoResultException e) {
				
			}
			String sql = obj == null ? buildInsertQuery(archiveLog) : buildUpdateQuery(archiveLog);
			log.trace("Log query: {}", sql);
			if(sql != null) {				
				query = entityManager.createNativeQuery(sql);
				for(String key : parameters.keySet()) {
					query.setParameter(key, parameters.get(key));
				}
				query.executeUpdate();
			} 
		}
	}
	
	private String buildSelectQuery(Long archiveLogId) {
		MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
		String sql = "SELECT * FROM " + logMatGrid.getMaterializationTableName() + " WHERE " + matgGridColumn.getDbColumnName() + " = '" + archiveLogId + "' LIMIT 1";
		return sql;
	}
	
	private String buildInsertQuery(ArchiveLog archiveLog) {
		if(logMatGrid != null) {
			String sql = "INSERT INTO " + logMatGrid.getMaterializationTableName()
					+ " (";	
			String values = " VALUES(";
			String coma = "";
			parameters = new HashedMap<>();
			
			MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "" + archiveLog.getId(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NAME);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "" + archiveLog.getName(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_AM);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = RunModes.A.name(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_USER);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = username; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			Timestamp logDate = Timestamp.from(Instant.now());
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_DATE);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = logDate;
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_TIME);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = new SimpleDateFormat("HH:mm:ss").format(logDate); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_CATEGORY);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "Archive"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ACTION);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = "Generation"; 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				Object value = archiveLog.getStatus().name(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				long value = archiveLog.getLineCount(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();
				String param = getParameterName(col);	
				long value = archiveLog.getStatus() == ArchiveLogStatus.ERROR ? 1L : 0L;
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE);
			if(matgGridColumn != null) {
				String col = matgGridColumn.getDbColumnName();			
				String param = getParameterName(col);	
				String value = archiveLog.getMessage(); 
				sql += coma + col;
				values += coma + param; 
				coma = ", ";
				parameters.put(col, value);
			}
			
			sql += ") ";
			values += ")";		
			sql += values;		
			return sql;
		}
		return null;
	}
	
	private String buildUpdateQuery(ArchiveLog archiveLog) {
		if(logMatGrid != null) {
			parameters = new HashedMap<>();
			String coma = ",";
			String sql = "UPDATE " + logMatGrid.getMaterializationTableName();
			sql += " SET " + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_COUNT).getDbColumnName() + " = :linecount ";
			parameters.put("linecount", archiveLog.getLineCount());
			if(archiveLog.getStatus() == ArchiveLogStatus.ERROR) {
				sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_ERROR_COUNT).getDbColumnName() + " = :errorcount ";
				parameters.put("errorcount", 1L);
			}
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_STATUS).getDbColumnName() + " = :status ";
			parameters.put("status", archiveLog.getStatus().name());
			sql += coma + logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_MESSAGE).getDbColumnName() + " = :message ";
			parameters.put("message", archiveLog.getMessage());
			
			MaterializedGridColumn matgGridColumn = logMatGrid.getColumnByRole(GrilleColumnCategory.LOG_NBR);
			sql += " WHERE " + matgGridColumn.getDbColumnName() + " = '" + archiveLog.getId() + "'";
			return sql;
		}
		return null;
	}
	
	private String getParameterName(String col) {		
		return ":" + col;
	}
	
}
