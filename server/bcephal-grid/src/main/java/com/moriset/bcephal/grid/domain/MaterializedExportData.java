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
public class MaterializedExportData {

	private MaterializedGridDataFilter filter;
	
	private GrilleExportDataType type;
	
}
