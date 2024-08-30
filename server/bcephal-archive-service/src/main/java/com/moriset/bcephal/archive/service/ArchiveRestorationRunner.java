/**
 * 
 */
package com.moriset.bcephal.archive.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.archive.domain.ArchiveLog;
import com.moriset.bcephal.archive.domain.ArchiveLogAction;
import com.moriset.bcephal.archive.domain.ArchiveLogStatus;
import com.moriset.bcephal.archive.domain.ArchiveStatus;
import com.moriset.bcephal.archive.repository.ArchiveLogRepository;
import com.moriset.bcephal.archive.repository.ArchiveRepository;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

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
public class ArchiveRestorationRunner {

	private Archive archive;
	private TaskProgressListener listener;

	private boolean stopped;

	String loaderNbrColumn;
	String loaderFileColumn;
	String loadNbr;

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired ArchiveLogRepository archiveLogRepository;
	@Autowired ArchiveRepository archiveRepository;
	@Autowired MaterializedGridService materializedGridService;
	@Autowired ParameterRepository parameterRepository;
	
	String sessionId;

	String username = "B-CEPHAL";
	
	MaterializedGrid logMatGrid;
	Map<String, Object> parameters;
		
	@Transactional
	public void run() {		
		log.debug("Try restore archive : {}", archive.getName());
		
		try {
			
			
			if (getListener() != null) {
				getListener().start(10);
			}
			
			if(archive.getStatus() == ArchiveStatus.IMPORTED) {
				throw new BcephalException("Unable to restore archive : '{}' because it is already restored!", archive.getName());
			}
			String tableName = archive.getTableName();
			if(!org.springframework.util.StringUtils.hasLength(tableName)) {
				throw new BcephalException("Unable to restore archive : '{}'. Archive table name is undefined!", archive.getName());
			}
			List<String> archiveColumns = null;
			try {
				
				log.debug("Archive restoration : {}. Try to dimensions checking...", this.archive.getName());
				archiveColumns = GetTableColumns(tableName);
				List<String> universeColumns = GetTableColumns(UniverseParameters.UNIVERSE_TABLE_NAME);
				
				List<String> columns = new ArrayList<>();
				for(String col : archiveColumns) {
					if(!universeColumns.contains(col)) {
						columns.add(col);
					}
				}
				log.debug("Archive restoration : {}. Dimensions checked!", this.archive.getName());
				if(columns.size() > 0) {
					log.error("Missing columns : {}", columns);
					throw new BcephalException("Unable to restore archive : " + this.archive.getName() + ". Some dimensions are missing.");
				}
			}
			catch (Exception e) {
				throw new BcephalException("Unable to restore archive : '{}'. Unexpected error : ", archive.getName(), e);
			}
			
			log.trace("Archive restoration : {}. Build count query...", this.archive.getName());
			String sql = "SELECT COUNT(1) FROM ".concat(tableName);	
			if(listener != null) {	
				listener.nextStep(1);
			}
			log.trace("Archive restoration : {}. Count query : \n{}", this.archive.getName(), sql);
			
			log.trace("Archive restoration : {}. Execute count query...", this.archive.getName());
			long count = 0;
			try {
				Query query = entityManager.createNativeQuery(sql);
				Number number = (Number)query.getSingleResult();
				if(number != null) {
					count = number.longValue();
				}
			}
			catch (Exception e) {
				throw new BcephalException("Unexpected error when counting lines to restore.", e);
			}
			if(listener != null) {	
				listener.nextStep(1);
			}
			
	        log.debug("Archive restoration : {}. Line Count : {}", this.archive.getName(), count);
	        if(count == 0) {
	        	throw new BcephalException("No data found in archive!");
	        }
	        
	        performRestoration(tableName, archiveColumns);
	        ArchiveLog archiveLog = initArchiveLog(archive);;
			endArchiveLog(archiveLog);
							
			if (getListener() != null) {
				getListener().end();
			}
			
		} catch (Exception e) {
			log.error("ARCHIVE RESTORATION : {}		restoration fail !", archive.getName(), e);
			String message = "Unable to restore archive. \nUnexpected error!";
			if (e instanceof BcephalException) {
				message = e.getMessage();
			}
			endArchiveLog(message);
			if (getListener() != null) {
				getListener().error(message, true);
			}
		} finally {

		}
	}


	//@Transactional(propagation = Propagation.REQUIRES_NEW )
	private void performRestoration(String tableName, List<String> archiveColumns) {
		log.trace("Archive restoration : {}. Build insert query...", this.archive.getName());
		String columns = "";
		String coma = "";
		for(String col : archiveColumns) {
			if(!UniverseParameters.ID.toUpperCase().equals(col.toUpperCase())) {
				columns += coma + col; 
				coma = ",";
			}
		}
		
        String createSql = "INSERT INTO ".concat(UniverseParameters.UNIVERSE_TABLE_NAME)
        		.concat(" (").concat(columns).concat(") ")
        		.concat("SELECT ").concat(columns).concat(" FROM ").concat(tableName);
		if(listener != null) {	
			listener.nextStep(1);
		}
		log.trace("Archive restoration : {}. Insert query : \n{}", this.archive.getName(), createSql);
        					
		log.trace("Archive restoration : {}. Build delete query...", this.archive.getName());
		String deleteSql = "DROP TABLE ".concat(tableName);
		String deleteConsolidationSql = "DELETE FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME 
				+ " WHERE " + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.ARCHIVE + "' AND " 
				+ UniverseParameters.SOURCE_ID + " = " + archive.getId();
		
		if(listener != null) {	
			listener.nextStep(1);
		}
		log.trace("Archive restoration : {}. Delete query : \n{}", this.archive.getName(), deleteSql);
		
					
		Query query = entityManager.createNativeQuery(createSql);
		query.executeUpdate();				
		query = entityManager.createNativeQuery(deleteSql);
		query.executeUpdate();	
		query = entityManager.createNativeQuery(deleteConsolidationSql);
		query.executeUpdate();		
	}

	private ArchiveLog initArchiveLog(Archive archive) {
		ArchiveLog archiveLog = new ArchiveLog();
		archiveLog.setArchiveId(archive.getId());
		archiveLog.setName(archive.getName());
		archiveLog.setUserName(username);
		archiveLog.setAction(ArchiveLogAction.IMPORT);
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
	
	@Transactional
	private void endArchiveLog(ArchiveLog archiveLog) {
		archiveLog.setStatus(ArchiveLogStatus.SUCCESS);
		this.archive.setStatus(ArchiveStatus.IMPORTED);
		archiveLogRepository.save(archiveLog);
		saveOrUpdateMatGridLog(archiveLog);
		archiveRepository.save(archive);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW )
	private void endArchiveLog(String message) {
		ArchiveLog archiveLog = initArchiveLog(archive);
		archiveLog.setMessage(message);
		archiveLog.setStatus(ArchiveLogStatus.ERROR);		
		archiveLogRepository.save(archiveLog);
		saveOrUpdateMatGridLog(archiveLog);
	}
	

	@SuppressWarnings("unchecked")
	private List<String> GetTableColumns(String tableName) {
		String sql = "SELECT UPPER(column_name) FROM information_schema.columns "
				+ "WHERE table_schema = :tableSchema AND UPPER(table_name) = :tableName ORDER BY ordinal_position";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("tableSchema", "public");
		query.setParameter("tableName", tableName.toUpperCase());		
		return query.getResultList();
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
				Object value = "Restoration"; 
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
			parameters.put("status", archiveLog.getStatus());
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
