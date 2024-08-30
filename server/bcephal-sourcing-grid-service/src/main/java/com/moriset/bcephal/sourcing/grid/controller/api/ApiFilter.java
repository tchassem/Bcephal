package com.moriset.bcephal.sourcing.grid.controller.api;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;

import lombok.Data;

@Data
public class ApiFilter {
	
	DimensionType dimensionType;
	
	String dimensionName;
	
	String operator;
	
	String stringValue;
	
	BigDecimal decimalValue;
	
	Date dateValue;
	
	@JsonIgnore
	Dimension dimension;
	
	@JsonIgnore
	public boolean isAttribute() {
		return this.dimensionType == DimensionType.ATTRIBUTE;
	}

	@JsonIgnore
	public boolean isPeriod() {
		return this.dimensionType == DimensionType.PERIOD;
	}

	@JsonIgnore
	public boolean isMeasure() {
		return this.dimensionType == DimensionType.MEASURE;
	}
}
