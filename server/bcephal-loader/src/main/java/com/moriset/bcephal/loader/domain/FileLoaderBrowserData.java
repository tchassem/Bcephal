/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-6
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class FileLoaderBrowserData extends BrowserData {
	
	private boolean confirmAction;
	
	public FileLoaderBrowserData(FileLoader fileLoader) {
		super(fileLoader);
		setConfirmAction(fileLoader.isConfirmAction());
	}

}
