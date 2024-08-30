/**
 * 
 */
package com.moriset.bcephal.multitenant.jpa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DataBaseUtils {

	DataSourceProperties properties;
	DataSource source;
	
	HikariProperties hikariProperties;
	
	/**
	 * Builds JDBC URL for the given database.
	 * 
	 * @param dbName
	 * @return JDBC URL
	 */
	public String buildUrl(String dbName) {
		String url = properties.getUrl();
		if (!StringUtils.hasLength(url)) {
			url = "jdbc:postgresql://localhost:5433";
		}
		if (!url.endsWith("/")) {
			url = url.concat("/");
		}
		String dbName_ = dbName;
		if (dbName.length() > 1 && dbName_.startsWith("/")) {
			dbName_ = dbName_.substring(1);
		}
		url = url.concat(dbName_);
		return url;
	}
	
	
	public DataSource buildDataSource(String dbName) {					
		try {
			String url = buildUrl(dbName);		
			HikariDataSource source = DataSourceBuilder.create(this.getClass().getClassLoader())
					.type(HikariDataSource.class).url(url).username(properties.getUsername())
					.password(properties.getPassword()).build();

			source.setPoolName(dbName + "_" + System.currentTimeMillis());
			if (hikariProperties != null) {
				if (hikariProperties.getIdleTimeout() != null) {
					source.setIdleTimeout(hikariProperties.getIdleTimeout());
				} 
				if (hikariProperties.getMaximumPoolSize() != null) {
					source.setMaximumPoolSize(hikariProperties.getMaximumPoolSize());
				} 
				if (hikariProperties.getMinimumIdle() != null) {
					source.setMinimumIdle(hikariProperties.getMinimumIdle());
				} 
				if (hikariProperties.getValidationTimeout() != null) {
					source.setValidationTimeout(hikariProperties.getValidationTimeout());
				}	
				if (hikariProperties.getInitializationFailTimeout() != null) {
					source.setInitializationFailTimeout(hikariProperties.getInitializationFailTimeout());
				} 
				if (hikariProperties.getConnectionTimeout() != null) {
					source.setConnectionTimeout(hikariProperties.getConnectionTimeout());
				} 
				if (hikariProperties.getConnectionTestQuery() != null) {
					source.setConnectionTestQuery(hikariProperties.getConnectionTestQuery());
				} 
				if (hikariProperties.getMaxLifetime() != null) {
					source.setMaxLifetime(hikariProperties.getMaxLifetime());
				} 
				if (hikariProperties.getLeakDetectionThleshold() != null) {
					source.setLeakDetectionThreshold(hikariProperties.getLeakDetectionThleshold());
				}
			}
			
//			for (Object key_ : hikariProperties.keySet()) {
//				String key = (String) key_;				
//				if (key.equalsIgnoreCase("idle-timeout") && hikariProperties.get(key) != null) {
//					source.setIdleTimeout(Long.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("maximum-pool-size") && hikariProperties.get(key) != null) {
//					source.setMaximumPoolSize(Integer.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("minimum-idle") && hikariProperties.get(key) != null) {
//					source.setMinimumIdle(Integer.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("validation-timeout") && hikariProperties.get(key) != null) {
//					source.setValidationTimeout(Long.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("initialization-fail-timeout") && hikariProperties.get(key) != null) {
//					source.setInitializationFailTimeout(Long.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("connection-timeout") && hikariProperties.get(key) != null) {
//					source.setConnectionTimeout(Long.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("connection-test-query") && hikariProperties.get(key) != null) {
//					source.setConnectionTestQuery(hikariProperties.get(key).toString());
//				} else if (key.equalsIgnoreCase("max-lifetime") && hikariProperties.get(key) != null) {
//					source.setMaxLifetime(Long.valueOf(hikariProperties.get(key).toString().trim()));
//				} else if (key.equalsIgnoreCase("leak-detection-thleshold") && hikariProperties.get(key) != null) {
//					source.setLeakDetectionThreshold(Long.valueOf(hikariProperties.get(key).toString().trim()));
//				}
//			}
			return source;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}
	
	
	

	/**
	 * Check the existence of the database.
	 * 
	 * @param dbName The database name
	 * @return TRUE if the database exists, FALSE otherwise.
	 * @throws SQLException
	 */
	public boolean isDbExists(String dbName) throws SQLException {
		log.debug("Checking the existence of the database : {}", dbName);
		Connection conn = source.getConnection();
		try {
			log.trace("Connected to the PostgreSQL server successfully.");
			String sql = "SELECT count(1) from pg_database WHERE datname='" + dbName + "';";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			int count = rs.getInt(1);
			log.debug("The database '{}' exists : {}", dbName, count > 0);
			return count > 0;
		} finally {
			close(conn);
		}
	}
	
	private synchronized void close(final Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.debug("Unable to disconnect from postgres data base.", e);
			}
		}
	}

	/**
	 * Creates a database if not exists.
	 * 
	 * @param dbName The new database name
	 * @return TRUE If the database did not exist and was created successfully.
	 *         FALSE otherwise.
	 * @throws SQLException
	 */
	public boolean createsDbIfNotExists(String dbName) throws SQLException {
		log.debug("Create database if not exists : {}", dbName);
		if (!isDbExists(dbName)) {
			createDb(dbName);
			return true;
		}
		return false;
	}

	/**
	 * Creates a new database.
	 * 
	 * @param dbName The new database name
	 * @throws SQLException
	 */
	public void createDb(String dbName) throws SQLException {
		log.debug("Database creation : {}", dbName);
		Connection conn = source.getConnection();
		try {
			log.trace("Connected to the PostgreSQL server successfully.");
			String sql = "CREATE DATABASE \"" + dbName + "\"";
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			log.debug("Database successfully created : {}", dbName);
		} finally {
			close(conn);
		}
	}

	/**
	 * Drops database.
	 * 
	 * @param dbName The database to drop.
	 * @throws SQLException
	 */
	public void dropDb(String dbName) throws SQLException {
		log.debug("Database dropping : {}", dbName);
		Connection conn = source.getConnection();
		try {
			log.trace("Connected to the PostgreSQL server successfully.");
			String sql = "DROP DATABASE IF EXISTS \"" + dbName + "\"";
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			log.debug("Database successfully dropped : {}", dbName);
		} finally {
			close(conn);
		}
	}

	/**
	 * Renames database.
	 * 
	 * @param dbName  current name
	 * @param newName New name
	 * @throws SQLException
	 */
	public void renameDb(String dbName, String newName) throws SQLException {
		log.debug("Database renaming fom '{}' to '{}'", dbName, newName);
		Connection conn = source.getConnection();
		try {
			log.trace("Connected to the PostgreSQL server successfully.");
			String sql = "ALTER DATABASE \"" + dbName + "\" RENAME TO \"" + newName + "\"";
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			log.debug("Database successfully rename from '{}' to '{}'", dbName, newName);
		} finally {
			close(conn);
		}
	}

	/**
	 * Close all connections to the given database.
	 * 
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public boolean closeAllConnexionsToDb(String dbName) throws Exception {
		log.debug("Closing all connexion to database : {}", dbName);
		Connection conn = source.getConnection();
		try {
			log.trace("Connected to the PostgreSQL server successfully.");
			String sql = "SELECT pg_terminate_backend (pid) FROM pg_stat_activity WHERE datname='" + dbName + "'";
			Statement stmt = conn.createStatement();
			boolean response = stmt.execute(sql);
			log.debug("Database '{}' successfully closed : {}", dbName, response);
			return response;
		} finally {
			close(conn);
		}
	}

	public long countConnectionOfDB(String dbName) throws Exception {
		log.debug("Count connexion to database   : {}", dbName);
		Connection conn = source.getConnection();
		try {
			log.trace("Connected to the PostgreSQL server successfully.");
			String sql = "SELECT sum(numbackends) FROM pg_stat_database where datname ='" + dbName + "'";
			Statement stmt = conn.createStatement();
			boolean response = stmt.execute(sql);
			log.debug("Database '{}' successfully closed : {}", dbName, response);
			long count = -1;
			ResultSet result = stmt.getResultSet();
			if (result.next()) {
				count = result.getLong(1);
			}
			return count;
		} finally {
			close(conn);
		}
	}

	public void updateDbSchema(DataSource source, String... updateFilesLocations) {
		log.debug("Try to update data base schema : {}", source);
		Flyway flyway = Flyway.configure()
				.dataSource(source)
				.locations(updateFilesLocations)
				.outOfOrder(true)
				.load();
		flyway.repair();
		flyway.migrate();
		log.debug("Data base {} : schema successfully updated!", source);
	}

}
