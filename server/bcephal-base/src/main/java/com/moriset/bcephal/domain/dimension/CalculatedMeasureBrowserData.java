package com.moriset.bcephal.domain.dimension;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CalculatedMeasureBrowserData extends BrowserData {

	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private String dataSource;
	
	
	public CalculatedMeasureBrowserData() {
		super();
	}
	
	public CalculatedMeasureBrowserData(CalculatedMeasure measure) {
		super(measure);
		this.dataSourceType = measure.getDataSourceType();
	}
	
}
