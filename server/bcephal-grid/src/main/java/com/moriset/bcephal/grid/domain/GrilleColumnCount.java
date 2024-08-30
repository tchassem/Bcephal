/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Emmanuel Emmeni
 *
 */
@Data 
@EqualsAndHashCode(callSuper = false)
public class GrilleColumnCount {
	
	private BigDecimal sumItems;
	
	private long countItems;
	
	private BigDecimal averageItems;
	
	private BigDecimal maxItem;
	
	private BigDecimal minItem;
	
	public GrilleColumnCount() {
		sumItems = BigDecimal.ZERO;
		countItems = 0;
		averageItems = BigDecimal.ZERO;
		maxItem = BigDecimal.ZERO;
		minItem = BigDecimal.ZERO;		
	}
}
