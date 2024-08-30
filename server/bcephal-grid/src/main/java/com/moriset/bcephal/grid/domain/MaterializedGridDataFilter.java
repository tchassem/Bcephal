package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MaterializedGridDataFilter extends BrowserDataFilter {

	private MaterializedGrid grid;
	private List<MaterializedGridColumn> columns;
	
	private List<Long> ids;
	private List<Long> exportColumnIds;
	private boolean exportAllColumns;
	
	private Long recoAttributeId;
	
	private Long freezeAttributeId;
	
	private Long noteAttributeId;
	
	private GrilleRowType rowType;
	
	private boolean conterpart;
	
	private boolean credit;
	
	private boolean debit;
		
	@JsonIgnore
	private String creditValue;
	
	@JsonIgnore
	private String debitValue;
	
	@JsonIgnore
	private Attribute debitCreditAttribute;
	
	@JsonIgnore
	private UniverseFilter filter;
	
	
		
	public MaterializedGridDataFilter() {
		exportAllColumns = true;
		columns = new ArrayList<>();
	}

}
