/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JoinFilter extends GrilleDataFilter {

	private Join join;
	
	private List<JoinColumn> joinColumns;
	
	private GrilleType gridType;
	
	private Long debitCreditAttributeId;
	
	@JsonIgnore
	private List<AbstractSmartGrid<?>> smartGrids;
	
	@JsonIgnore
	private Map<String, Object> variableValues;
	
	public JoinFilter(){
		gridType = GrilleType.INPUT;
		this.variableValues = new HashMap<>();
	}
	
}
