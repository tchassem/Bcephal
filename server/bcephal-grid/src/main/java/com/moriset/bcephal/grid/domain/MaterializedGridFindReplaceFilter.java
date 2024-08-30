/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterializedGridFindReplaceFilter {

	private MaterializedGridDataFilter filter;

	private MaterializedGridColumn column;
    
	private boolean ignoreCase;
	
	private boolean replaceOnlyValuesCorrespondingToCondition;

	private String operation;

	private String criteria;
	private BigDecimal measureCriteria;

	private String replaceValue;
	private BigDecimal measureReplaceValue;
	
}
