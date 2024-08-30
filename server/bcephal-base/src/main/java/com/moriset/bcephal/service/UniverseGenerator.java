/**
 * 
 */
package com.moriset.bcephal.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Data
@Slf4j
public class UniverseGenerator {

	@PersistenceContext
	EntityManager session;

	/**
	 * Is Universe table exist?
	 * 
	 * @return True if Universe table exist. False otherwise.
	 */
	public boolean isUniverseExists() {
		return isTableExists(UniverseParameters.UNIVERSE_TABLE_NAME);
	}

	/**
	 * 
	 * @return List of universe table columns
	 * @throws SQLException
	 */
	public List<String> getUniverseColumns() throws SQLException {
		List<String> columns = getTableColumns(UniverseParameters.UNIVERSE_TABLE_NAME);
		return columns;
	}

	/**
	 * 
	 * @return List of universe table attribute columns
	 * @throws SQLException
	 */
	public List<String> getUniverseAttributeColumns() throws SQLException {
		List<String> columns = getTableColumnsStartingBy(UniverseParameters.UNIVERSE_TABLE_NAME, "ATTRIBUTE_");
		return columns;
	}

	/**
	 * 
	 * @return List of universe table measure columns
	 * @throws SQLException
	 */
	public List<String> getUniverseMeasureColumns() throws SQLException {
		List<String> columns = getTableColumnsStartingBy(UniverseParameters.UNIVERSE_TABLE_NAME, "MEASURE_");
		return columns;
	}

	/**
	 * 
	 * @return List of universe table Period columns
	 * @throws SQLException
	 */
	public List<String> getUniversePeriodColumns() throws SQLException {
		List<String> columns = getTableColumnsStartingBy(UniverseParameters.UNIVERSE_TABLE_NAME, "PERIOD_");
		return columns;
	}

	/**
	 * 
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public String getUniverseColumnFormat(String columnName) throws Exception {
		return getColumnFormat(UniverseParameters.UNIVERSE_TABLE_NAME, columnName);
	}

	/**
	 * Is the given column empty in universe table?
	 * 
	 * @param columnName
	 * @return
	 */
	public boolean isUniverseColumnEmpty(String columnName, boolean isGrid) {
		return isColumnEmpty(UniverseParameters.UNIVERSE_TABLE_NAME, columnName, isGrid);
	}


	protected void createUniverseColumn(String columnName, String type) {
		String sql = "ALTER TABLE " + UniverseParameters.UNIVERSE_TABLE_NAME + " ADD COLUMN " + columnName + " " + type;
		Query query = this.session.createNativeQuery(sql);
		query.executeUpdate();
	}

	protected void dropUniverseColumn(String columnName) {
		String sql = "ALTER TABLE " + UniverseParameters.UNIVERSE_TABLE_NAME + " DROP COLUMN " + columnName;
		Query query = this.session.createNativeQuery(sql);
		query.executeUpdate();
	}

	/**
	 * Is table with the given name exist?
	 * 
	 * @param tableName Table name to check
	 * @return True if table exist. False otherwise.
	 */
	public boolean isTableExists(String tableName) {
		if (StringUtils.hasText(tableName)) {
			tableName = tableName.toUpperCase();
			tableName = tableName.startsWith(UniverseParameters.SCHEMA_NAME)
					? tableName.substring(UniverseParameters.SCHEMA_NAME.length())
					: tableName;

			String sql = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = '"
					+ UniverseParameters.SCHEMA_NAME.toLowerCase().substring(0,
							UniverseParameters.SCHEMA_NAME.length() - 1)
					+ "' AND tablename = '" + tableName.toLowerCase() + "')";
			try {
				Query query = this.session.createNativeQuery(sql);
				Object nbr = query.getSingleResult();
				if (nbr != null) {
					return (Boolean) nbr;
				}
			} catch (NoResultException ex) {
			} catch (Exception ex) {
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
		tableName = tableName.startsWith(UniverseParameters.SCHEMA_NAME)
				? tableName.substring(UniverseParameters.SCHEMA_NAME.length())
				: tableName;
		String sql = "SELECT upper(column_name) FROM information_schema.columns " + "WHERE table_schema = '"
				+ UniverseParameters.SCHEMA_NAME.toLowerCase().substring(0, UniverseParameters.SCHEMA_NAME.length() - 1)
				+ "'" + " AND table_name = '" + tableName.toLowerCase() + "' ORDER BY ordinal_position";
		Query query = this.session.createNativeQuery(sql);
		return query.getResultList();
	}

	/**
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTableColumns(String scheme, String tableName) throws SQLException {
		tableName = tableName.toUpperCase();
		scheme = scheme.toUpperCase();
		tableName = tableName.startsWith(scheme) ? tableName.substring(scheme.length()) : tableName;
		String sql = "SELECT upper(column_name) FROM information_schema.columns " + "WHERE table_schema = '"
				+ scheme.toLowerCase().substring(0, scheme.length() - 1) + "'" + " AND table_name = '"
				+ tableName.toLowerCase() + "' ORDER BY ordinal_position";
		Query query = this.session.createNativeQuery(sql);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> getMaterializedViewColumns(String scheme, String tableName) throws SQLException {
		tableName = tableName.toUpperCase();
		scheme = scheme.toUpperCase();
		tableName = tableName.startsWith(scheme) ? tableName.substring(scheme.length()) : tableName;
		String sql = "SELECT a.attname FROM pg_attribute a " + "JOIN pg_class t on a.attrelid = t.oid "
				+ "JOIN pg_namespace s on t.relnamespace = s.oid " + "WHERE a.attnum > 0 AND NOT a.attisdropped "
				+ "AND t.relname = '" + tableName.toLowerCase() + "' " + "AND s.nspname = '"
				+ scheme.toLowerCase().substring(0, scheme.length() - 1) + "' " + "ORDER BY a.attnum";
		Query query = this.session.createNativeQuery(sql);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> getTableColumnsStartingBy(String tableName, String columnPrefix) throws SQLException {
		tableName = tableName.toUpperCase();
		tableName = tableName.startsWith(UniverseParameters.SCHEMA_NAME)
				? tableName.substring(UniverseParameters.SCHEMA_NAME.length())
				: tableName;
		String sql = "SELECT upper(column_name) FROM information_schema.columns " + "WHERE table_schema = '"
				+ UniverseParameters.SCHEMA_NAME.toLowerCase().substring(0, UniverseParameters.SCHEMA_NAME.length() - 1)
				+ "'" + " AND table_name = '" + tableName.toLowerCase() + "' " + " AND column_name LIKE '"
				+ columnPrefix.toLowerCase() + "%' ORDER BY ordinal_position";
		Query query = this.session.createNativeQuery(sql);
		return query.getResultList();
	}

	private String getColumnFormat(String tableName, String columnName) {
		tableName = tableName.toUpperCase();
		tableName = tableName.startsWith(UniverseParameters.SCHEMA_NAME)
				? tableName.substring(UniverseParameters.SCHEMA_NAME.length())
				: tableName;
		String sql = "SELECT data_type FROM information_schema.columns " + "WHERE table_schema = '"
				+ UniverseParameters.SCHEMA_NAME.toLowerCase().substring(0, UniverseParameters.SCHEMA_NAME.length() - 1)
				+ "'" + " AND table_name = '" + tableName.toLowerCase() + "' " + " AND column_name = '"
				+ columnName.toLowerCase() + "'";
		Query query = this.session.createNativeQuery(sql);
		return (String) query.getSingleResult();
	}

	/**
	 * 
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public boolean isColumnEmpty(String tableName, String columnName, boolean isGrid) {
		tableName = tableName.toUpperCase();
		tableName = tableName.startsWith(UniverseParameters.SCHEMA_NAME)
				? tableName.substring(UniverseParameters.SCHEMA_NAME.length())
				: tableName;
		String sql = "SELECT count(1) FROM " + tableName + " WHERE " + columnName + " IS NOT NULL ";
		if (isGrid) {
			sql += " AND SOURCE_TYPE = '" + UniverseSourceType.INPUT_GRID + "'";
		}
		try {
			Query query = this.session.createNativeQuery(sql);
			Object nbr = query.getSingleResult();
			if (nbr != null) {
				return Long.valueOf(nbr.toString()) == 0;
			}
		} catch (NoResultException ex) {
		} catch (Exception ex) {
			log.error("Unable to check if table exist: " + tableName, ex);
		}
		return false;
	}

}
