/**
 * 
 */
package com.moriset.bcephal.domain;

import com.moriset.bcephal.domain.dimension.DimensionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VariableBrowserData extends BrowserData {

    private DataSourceType dataSourceType;	
	private Long dataSourceId;
	private String dataSourceName;
	private DimensionType dimensionType;
	private Long dimensionId;
	
	public VariableBrowserData(Variable persistent) {
		super(persistent);
		setDataSourceId(persistent.getDataSourceId());
		setDataSourceType(persistent.getDataSourceType());
		setDimensionId(persistent.getDimensionId());
		setDimensionType(persistent.getDimensionType());
		setDataSourceName(persistent.getDataSourceName());
	}
}
