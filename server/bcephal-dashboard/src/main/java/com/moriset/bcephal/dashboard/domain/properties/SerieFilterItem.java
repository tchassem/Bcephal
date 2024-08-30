package com.moriset.bcephal.dashboard.domain.properties;

import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.PeriodGranularity;

import lombok.Data;
@Data
public class SerieFilterItem {
	 private DashboardReportField Field;
	 private FilterVerb FilterVerb;
	 private String OpenBracket;
	 private String CloseBracket;

	 private String Operator;
	 private String Comparator;
	 private String Value;
	 private String Sign;
	 private int Number;
	 private PeriodGranularity Granularity;
}
