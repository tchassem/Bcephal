package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class MaterializedGridCopyData {
	
	@JsonIgnore
	private MaterializedGridDataFilter filter;
	
	private Long targetGridId;
	
	private boolean createNewGrid;
	
	private String newGridName;
	
	private boolean identifyColumnByName;
	
	private boolean deleteOriginalRows;
	
}
