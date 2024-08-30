/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.utils.FileType;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class FileLoaderColumnDataBuilder {

	private int sheetIndex;

	private String sheetName;

	private String separator;

	private Long gridId;

	private FileType fileType;
	
	private Long templateId;

	private boolean hasHeader;

	private int headerRowcount;
}
