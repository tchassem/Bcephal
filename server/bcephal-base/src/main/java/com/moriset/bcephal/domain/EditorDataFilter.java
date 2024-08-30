/**
 * 
 */
package com.moriset.bcephal.domain;

import com.moriset.bcephal.domain.filters.ColumnFilter;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class EditorDataFilter {
	
	private DataSourceType dataSourceType;	
	private Long dataSourceId;
	private Long id;
	private Long secondId;
	private String subjectType;
	private boolean showAllMeasureTypes;
	private boolean showPeriodWithIntervalls;
	private boolean showModelWithAttributes;
	private boolean showModelWithTargetCustom;
	private boolean newData;

	
	private ColumnFilter ColumnFilters;
	
	public EditorDataFilter() {
		this.showAllMeasureTypes = false;
		this.showPeriodWithIntervalls = false;
		this.showModelWithAttributes = true;
		this.showModelWithTargetCustom = true;
		this.newData = false;
		this.dataSourceType = DataSourceType.UNIVERSE;
	}
	
	public EditorDataFilter(Long id) {
		this.id = id;
		this.showAllMeasureTypes = false;
		this.showPeriodWithIntervalls = false;
		this.showModelWithAttributes = true;
		this.showModelWithTargetCustom = true;
		this.newData = false;
		this.dataSourceType = DataSourceType.UNIVERSE;
	}

}
