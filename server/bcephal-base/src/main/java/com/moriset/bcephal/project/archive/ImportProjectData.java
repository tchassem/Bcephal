package com.moriset.bcephal.project.archive;

import java.util.Locale;

import lombok.Data;

@Data
public class ImportProjectData {
	private String filePath;

	private String newProjectName;

	private boolean overwriteExistingProject;

	private boolean renameExistingProject;

	private boolean renameProjectToImport;

	private Long clientId;
	private Long profileId;

	private Locale locale;

	public ImportProjectData() {
		this.overwriteExistingProject = false;
		this.renameExistingProject = false;
		this.renameProjectToImport = false;
	}
}
