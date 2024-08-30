/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import com.moriset.bcephal.grid.service.UnionGridFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UnionGridExportData {

	private UnionGridFilter filter;
	
	private GrilleExportDataType type;
	
}
