/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import com.moriset.bcephal.grid.service.JoinFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JoinExportData {

	private JoinFilter filter;
	
	private GrilleExportDataType type;
	
}
