/**
 * 
 */
package com.moriset.bcephal.project.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
public class BackupProjectUtil {

	PostgresProperties postgresProperties;

	public BackupProjectUtil(PostgresProperties postgresProperties) {
		this.postgresProperties = postgresProperties;
	}

	/**
	 * 
	 * @param dumpFilePath
	 * @param properties
	 * @return
	 * @throws ImportProjectException
	 */
	public boolean importDb(String dumpFilePath, DataSourceProperties properties, String dbname)
			throws ImportProjectException {
		try {
			String host = PostgresProperties.DEFAULT_HOST;
			String port = PostgresProperties.DEFAULT_PORT;
			String userName = null;
			String userpwd = null;
			if (properties != null) {
				String url = properties.determineUrl();
				if (url.contains("//")) {
					url = url.substring(url.indexOf("//") + 2);
					host = url.substring(0, url.indexOf(":"));
					if (url.contains(":")) {
						url = url.substring(url.indexOf(":") + 1);
						if (url.contains("/")) {
							port = url.substring(0, url.indexOf("/"));
						} else {
							port = url;
						}
					}
				}
				if (host.contains("localhost")) {
					host = "127.0.0.1";
				}
				if (!StringUtils.hasText(dbname)) {
					dbname = properties.determineDatabaseName();
				}
				userName = properties.getUsername();
				userpwd = properties.getPassword();
			}
			log.info("Try to import DB : {}", dbname);
			if (createDb(dbname, userName, userpwd, host, port)) {
				String postgresRestoreCmdPath = postgresProperties.getRestore();
				log.info("Restore Bin Path : {}", postgresRestoreCmdPath);
				String opts = "";
				if(postgresProperties.getRestoreOpts() !=null && postgresProperties.getRestoreOpts().length > 0) {
					opts = String.join(",", postgresProperties.getRestoreOpts());
				}
				String[] command = { postgresRestoreCmdPath, "-h", host, "-p", port, "-U", userName, "-d", dbname, "-F", "c", dumpFilePath };
				if(org.springframework.util.StringUtils.hasText(opts)) {
					String[] command2 = { postgresRestoreCmdPath, "-h", host, "-p", port, "-U", userName, "-d", dbname,
							opts, "-F", "c", dumpFilePath };
					command = command2;
				}
				Process proc = exec(command, userpwd);
				PrintLog(proc);
				proc.waitFor();
				log.info("DB imported : {}", dbname);
				return true;
			}
		} catch (ImportProjectException e) {
			throw new ImportProjectException(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("DB Import error : {}", dbname, e);
		}
		return false;
	}

	/**
	 * 
	 * @param proc
	 * @throws IOException
	 * @throws ImportProjectException
	 */
	private void PrintLog(Process proc) throws IOException, ImportProjectException {
		if (proc == null) {
			return;
		}
		InputStream error = proc.getErrorStream();
		InputStream out = proc.getInputStream();
		StringWriter writer = new StringWriter();
		StringWriter outWriter = new StringWriter();
		IOUtils.copy(error, writer, Charset.forName("UTF-8"));
		IOUtils.copy(out, outWriter, Charset.forName("UTF-8"));
		String theString = writer.toString();
		String outString = outWriter.toString();
		if (StringUtils.hasText(outString)) {
			log.info("********************************* {}", outString);
		}
		if (StringUtils.hasText(theString)) {
			log.info("********************************* {}", theString);
			if (!theString.contains("ALTER SCHEMA public OWNER TO")) {
				throw new ImportProjectException(theString);
			}
		}
	}

	/**
	 * 
	 * @param dbname
	 * @param username
	 * @param password
	 * @param host
	 * @param port
	 * @return
	 * @throws ImportProjectException
	 */

	private boolean createDb(String dbname, String username, String password, String host, String port)
			throws ImportProjectException {
		try {			
			String postgresCreatedbCmdPath = postgresProperties.getCreatedb();
			log.info("Createdb Bin Path : {}", postgresCreatedbCmdPath);
			String[] command = { postgresCreatedbCmdPath, "-h", host, "-p", port, "-U", username, dbname };
			Process proc = exec(command, password);
			PrintLog(proc);
			proc.waitFor();
			return true;
		} catch (ImportProjectException e) {
			throw new ImportProjectException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param backupFilePath
	 * @param properties
	 * @return
	 * @throws ImportProjectException
	 */
	public String backup(String backupFilePath, String userName, String userpwd, String dbname, String url_)
			throws ImportProjectException {
		try {
			String postgresDumpCmdPath = postgresProperties.getDump();
			log.info("Dump Bin Path : {}", postgresDumpCmdPath);
			if(!org.springframework.util.StringUtils.hasText(postgresDumpCmdPath)) {
				throw new ImportProjectException("Dump Bin Path is NULL!");
			}
			String host = PostgresProperties.DEFAULT_HOST;
			String port = PostgresProperties.DEFAULT_PORT;
			String url = url_;
			if (url.contains("//")) {
				url = url.substring(url.indexOf("//") + 2);
				host = url.substring(0, url.indexOf(":"));
				if (url.contains(":")) {
					url = url.substring(url.indexOf(":") + 1);
					if (url.contains("/")) {
						port = url.substring(0, url.indexOf("/"));
					} else {
						port = url;
					}
				}
			}
			if (host.contains("localhost")) {
				host = "127.0.0.1";
			}
			String opts = "";
			if(postgresProperties.getDumpOpts() !=null && postgresProperties.getDumpOpts().length > 0) {
				opts = String.join(",", postgresProperties.getDumpOpts());
			}
			String[] command = { postgresDumpCmdPath, "-h", host, "-p", port, "-U", userName, "-d", dbname, "-F", "c", // "--no-owner",
																														// "-x",
					"-E", "UTF8", "-f", backupFilePath };
			if(org.springframework.util.StringUtils.hasText(opts)) {
				String[] command2 = { postgresDumpCmdPath, "-h", host, "-p", port, "-U", userName, "-d", dbname, opts, "-F", "c", "-E", "UTF8", "-f", backupFilePath };
				command = command2;
			}
			Process proc = exec(command, userpwd);
			PrintLog(proc);
			proc.waitFor();
			return backupFilePath;
		} catch (ImportProjectException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param command
	 * @param pwd
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private synchronized Process exec(final String[] command, final String pwd)
			throws IOException, InterruptedException {
		Process p;
		ProcessBuilder pb = new ProcessBuilder(command);
		try {
			final Map<String, String> env = pb.environment();
			env.put("PGPASSWORD", pwd);
			p = pb.start();
			return p;
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}

}
