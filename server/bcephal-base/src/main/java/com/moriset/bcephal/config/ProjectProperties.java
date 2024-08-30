package com.moriset.bcephal.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "bcephal.projects")
@Component
@Data
public class ProjectProperties {

	private String baseDir;
	private String archiveDirName;
	private String etlDirName;
	private String backupDirName;
	private String billTemplateDirName;
	
	public String getClientsPath(String client) {
		return Path.of(baseDir, client).toString();
	}
	
	public String getProjectsPath(String client, String project) {
		return Path.of(getClientsPath(client), project).toString();
	}
	
	public String getArchivesPath(String client, String project) {
		return getPath(client, project, archiveDirName);		
	}
	
	public String getEtlPath(String client, String project) {
		return getPath(client, project, etlDirName);		
	}
	
	public String getBackupsPath(String client, String project) {
		return getPath(client, project, backupDirName);		
	}
	
	public String getBillTemplatesPath(String client, String project) {
		return getPath(client, project, billTemplateDirName);		
	}
	
	
	private String getPath(String client, String project, String dirName) {
		return Path.of(getProjectsPath(client, project), dirName).toString();		
	}
	
}
