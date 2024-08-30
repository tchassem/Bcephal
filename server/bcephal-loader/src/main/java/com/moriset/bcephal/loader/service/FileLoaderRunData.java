/**
 * 
 */
package com.moriset.bcephal.loader.service;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.loader.domain.FileLoader;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class FileLoaderRunData {

	private Long id;

	private FileLoader loader;

	private String operationCode;
	
	private List<String> repositories;
	
	private List<String> files;

	@JsonIgnore
	private List<File> fileDatas;

	private RunModes mode;

	@JsonIgnore
	public boolean isManual() {
		return mode == RunModes.M;
	}

	@JsonIgnore
	public boolean isAutomatic() {
		return mode == RunModes.A;
	}

}
