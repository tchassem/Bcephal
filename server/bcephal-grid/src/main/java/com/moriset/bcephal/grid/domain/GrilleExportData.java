/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GrilleExportData {

	private GrilleDataFilter filter;
	
	private GrilleExportDataType type;
	
}
