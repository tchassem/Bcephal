/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridColumn;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UnionGridFilter extends GrilleDataFilter {

	@Enumerated(EnumType.STRING)
	private JoinGridType type;
	
	private UnionGrid unionGrid;
	
	private List<UnionGridColumn> unionGridColumns;
		
	@JsonIgnore
	private List<AbstractSmartGrid<?>> smartGrids;
	
	public UnionGridFilter(){
		
	}
	
	public UnionGridFilter(DimensionDataFilter filter){
		super(filter);
	}
	
	@JsonIgnore
	public boolean isUniverse() {
		return type == JoinGridType.GRID;
	}
	
	@JsonIgnore
	public boolean isUnion() {
		return type == JoinGridType.UNION_GRID;
	}
	
	
	
	
}
