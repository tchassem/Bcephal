/**
 * 
 */
package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.VariableReference;
import com.moriset.bcephal.grid.domain.VariableReferenceCondition;
import com.moriset.bcephal.grid.service.form.Reference;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DimensionDataFilter extends GrilleDataFilter {

	private Long dimensionId;
	private DimensionType dimensionType;
    private UniverseFilter userFilter;	
	private UniverseFilter adminFilter;
	private Reference reference;
	private DashboardItemVariableData variableReference;
		
	public DimensionDataFilter(){
		
	}
	
	public DimensionDataFilter(VariableReference ref, DimensionType dimensionType){
		setDimensionId(ref.getDimensionId() != null ? ref.getDimensionId() : ref.getSourceId());
		setDimensionType(dimensionType);
		setDataSourceId(ref.getDataSourceId());
		setDataSourceType(ref.getDataSourceType().GetDataSource());
		
		reference = new Reference();
		reference.setDataSourceId(ref.getDataSourceId());
		reference.setDataSourceType(ref.getDataSourceType());
		reference.setColumnId(ref.getSourceId());
		reference.setFormula(ref.getFormula());
//		for(VariableReferenceCondition cond : ref.getConditions()) {
//			reference.getConditions().add(new ReferenceCondition(cond));
//		}
		
		variableReference = new DashboardItemVariableData();
		variableReference.setDataSourceId(ref.getDataSourceId());
		variableReference.setDataSourceType(ref.getDataSourceType());
		variableReference.setSourceId(ref.getSourceId());
		variableReference.setDimensionId(ref.getDimensionId());
		variableReference.setFormula(ref.getFormula());
		variableReference.setUniqueValue(ref.isUniqueValue());
		for(VariableReferenceCondition cond : ref.getConditions()) {
			variableReference.getItems().add(new DashboardItemVariableReferenceConditionData(cond));
		}
	}
	
	
	
	
	
	
}
