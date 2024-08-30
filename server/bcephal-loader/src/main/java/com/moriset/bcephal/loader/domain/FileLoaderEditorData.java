/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderEditorData extends EditorData<FileLoader> {

	private List<BrowserData> grids;
	
	private List<BrowserData> materializedGrids;

	private List<BrowserData> tables;
	
	private List<Nameable> routines = new ArrayList<Nameable>();
	
	private List<String> dateFormats;
	
	private List<Nameable> templates;

}
