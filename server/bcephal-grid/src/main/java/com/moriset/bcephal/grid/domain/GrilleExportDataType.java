/**
 * 
 */
package com.moriset.bcephal.grid.domain;

/**
 * @author Moriset
 *
 */
public enum GrilleExportDataType {

	EXCEL,
	CSV,
	JSON,
	PDF,
	PNG;
	
	public String getExtension() {
		return this == CSV ? ".csv" : (this == JSON ? ".json" : (this == EXCEL ? ".xlsx" :  (this == PDF ? ".pdf" : ".png")));
	}
	
}
