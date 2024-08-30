/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 */
@Data
@Slf4j
public class DbTableManager {
	
	EntityManager entityManager;
	
	String scheme = "public";
	
	
	public DbTableManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	
	
	/**
	 * Is table with the given name exist?
	 * @param tableName Table name to check
	 * @return True if table exist. False otherwise.
	 */
	public boolean isTableExists(String tableName) {
		if(StringUtils.hasText(tableName)) {
			tableName = tableName.toUpperCase();										
			String sql = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = '"
					+ scheme.toLowerCase() + "' AND tablename = '" + tableName.toLowerCase() + "')";
			try {
				Query query = this.entityManager.createNativeQuery(sql);
				Object nbr = query.getSingleResult();
				if(nbr != null){
					return (Boolean)nbr;
				}
			} 
			catch (NoResultException ex) { }
			catch (Exception ex) {
				log.error("Unable to check if table exist: " + tableName, ex);
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTableColumns(String tableName) throws SQLException {
		tableName = tableName.toUpperCase();
		String sql = "SELECT upper(column_name) FROM information_schema.columns "
				+ "WHERE table_schema = '" + scheme.toLowerCase() + "'"
				+ " AND table_name = '" + tableName.toLowerCase() + "' ORDER BY ordinal_position";
		Query query = this.entityManager.createNativeQuery(sql);
		return query.getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> getMaterializedViewColumns(String tableName) throws SQLException {
		tableName = tableName.toUpperCase();
		String sql = "SELECT upper(att.attname)	from pg_catalog.pg_attribute att "
				+ "join pg_catalog.pg_class mv ON mv.oid = att.attrelid "
				+ "join pg_catalog.pg_namespace nsp ON nsp.oid = mv.relnamespace "
				+ "where mv.relkind = 'm' "
				+ "AND mv.relname = '" + tableName.toLowerCase() + "' "
				+ "AND nsp.nspname = '" + scheme.toLowerCase() + "' "
				+ "AND not att.attisdropped AND att.attnum > 0 "
				+ "order by att.attnum";		
		Query query = this.entityManager.createNativeQuery(sql);
		return query.getResultList();
	}
	
}
