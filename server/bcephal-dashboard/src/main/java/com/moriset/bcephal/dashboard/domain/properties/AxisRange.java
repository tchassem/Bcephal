package com.moriset.bcephal.dashboard.domain.properties;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;

import lombok.Data;
@Data
public class AxisRange {

	private DimensionType Type ;
	public double MinDecimalValue ;
	public double MaxDecimalValue ;
	public PeriodValue MinPeriodValue ;
	public PeriodValue MaxPeriodValue ;
}
	 

