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
public class FindReplaceFilter {

	private GrilleDataFilter filter;

	private GrilleColumn column;
    
	private boolean ignoreCase;
	
	private boolean replaceOnlyValuesCorrespondingToCondition;

	private String operation;

	private String criteria;
	private BigDecimal measureCriteria;

	private String replaceValue;
	private BigDecimal measureReplaceValue;
	
}
