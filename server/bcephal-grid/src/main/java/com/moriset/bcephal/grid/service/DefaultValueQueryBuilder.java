/**
 * 
 */
package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleColumn;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * @author Moriset
 *
 */
public class DefaultValueQueryBuilder {

	public int countAffectedRowsByApplyDefaultValue(Long gridId, GrilleColumn column, Long rowId, EntityManager session) {			
		String col = column.getUniverseTableColumnName();
		String sql = "SELECT COUNT(1) FROM ".concat(UniverseParameters.UNIVERSE_TABLE_NAME)
				.concat(" WHERE ").concat(UniverseParameters.SOURCE_TYPE).concat(" = :sourceType")
				.concat(" AND ").concat(UniverseParameters.SOURCE_ID).concat(" = :sourceId");
		if(column.isApplyDefaultValueIfCellEmpty()) {
			if(column.isAttribute()) {
				sql = sql.concat(" AND (").concat(col).concat(" IS NULL OR ").concat(col).concat(" = '')");
			}
			else {
				sql = sql.concat(" AND ").concat(col).concat(" IS NULL");
			}
		}
		if(rowId != null) {
			sql = sql.concat(" AND ").concat(UniverseParameters.ID).concat(" = :rowId");
		}
		
		Query query = session.createNativeQuery(sql);
		query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
		query.setParameter("sourceId", gridId);
		if(rowId != null) {
			query.setParameter("rowId", rowId);
		}		
		Number count = (Number)query.getSingleResult();		
		return count.intValue();
	}	
	
	public int applyDefaultValue(Long gridId, GrilleColumn column, Long rowId, EntityManager session) {			
		if(rowId != null && !column.isApplyDefaultValueToFutureLine()) {
			return 0;
		}
		String col = column.getUniverseTableColumnName();
		String sql = "UPDATE ".concat(UniverseParameters.UNIVERSE_TABLE_NAME).concat(" SET ").concat(col).concat(" = :defaultValue")
				.concat(" WHERE ").concat(UniverseParameters.SOURCE_TYPE).concat(" = :sourceType")
				.concat(" AND ").concat(UniverseParameters.SOURCE_ID).concat(" = :sourceId");
		if(column.isApplyDefaultValueIfCellEmpty()) {
			if(column.isAttribute()) {
				sql = sql.concat(" AND (").concat(col).concat(" IS NULL OR ").concat(col).concat(" = '')");
			}
			else {
				sql = sql.concat(" AND ").concat(col).concat(" IS NULL");
			}
		}
		if(rowId != null) {
			sql = sql.concat(" AND ").concat(UniverseParameters.ID).concat(" = :rowId");
		}
		
		Query query = session.createNativeQuery(sql);
		query.setParameter("defaultValue", column.getDefaultValue());
		query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
		query.setParameter("sourceId", gridId);
		if(rowId != null) {
			query.setParameter("rowId", rowId);
		}		
		int count = query.executeUpdate();		
		return count;
	}
	
	
}
