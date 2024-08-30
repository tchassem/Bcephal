package com.moriset.bcephal.dashboard.factory;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.domain.DashbordReportType;
import com.moriset.bcephal.dashboard.domain.PivotTableProperties;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.UniverseFilter;;

public class PivotTableFactory {
	
	public static DashboardReport BuildPivot1() {
		DashboardReport pivot = new DashboardReport();
		pivot.setDataSourceName("BIL010 MA volume per client per invoice pivot");
		pivot.setDataSourceId(47L);
		pivot.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		pivot.setAdminFilter(new UniverseFilter());
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1547L, "Check","Check", 4, DimensionType.MEASURE));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(3L, "Measure 2","Measure 2", 3, DimensionType.CALCULATED_MEASURE));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1543L, "Client ID","Client ID", 1, DimensionType.ATTRIBUTE));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1744L, "Entry date","Entry date",0, DimensionType.PERIOD));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1544L, "Posting amount","Posting amount", 2, DimensionType.MEASURE));
		pivot.setPivotTableProperties(buildPivotTableProperties(null, "{\"Fields\":[{\"Type\":\"CALCULATED_MEASURE\",\"FieldGroup\":\"ROW\",\"PeriodGrouping\":null,\"DimensionName\":\"Entry date\",\"Caption\":\"Entry date\",\"Position\":0},{\"Type\":\"CALCULATED_MEASURE\",\"FieldGroup\":\"COLUMN\",\"PeriodGrouping\":null,\"DimensionName\":\"Client ID\",\"Caption\":\"Client ID\",\"Position\":0},{\"Type\":\"MEASURE\",\"FieldGroup\":\"COMMON\",\"PeriodGrouping\":null,\"DimensionName\":\"Posting amount\",\"Caption\":\"Posting amount\",\"Position\":0},{\"Type\":\"CALCULATED_MEASURE\",\"FieldGroup\":\"COMMON\",\"PeriodGrouping\":null,\"DimensionName\":\"Measure 2\",\"Caption\":\"Measure 2\",\"Position\":0}],\"ShowFieldHeaders\":true,\"ShowGrandTotals\":true,\"ShowPager\":true}"));
		pivot.setReportType(DashbordReportType.PIVOT_TABLE);
		pivot.setName("Pivot Grid 1");
		return pivot;
	}

	
	public static DashboardReport BuildPivot2() {
		DashboardReport pivot = new DashboardReport();
		pivot.setDataSourceType(DataSourceType.UNIVERSE);
		pivot.setReportType(DashbordReportType.PIVOT_TABLE);
		pivot.setName("Pivot Grid ");
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1L, "Measure 1","Measure 1", 3, DimensionType.CALCULATED_MEASURE));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(2L,"Unique Report ID","Unique Report ID", 2, DimensionType.ATTRIBUTE));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1L,"Value date","Value date", 1, DimensionType.PERIOD));
		pivot.getFieldListChangeHandler().addNew(BuildDashboardReportField(1L,"Posting amount","Posting amount", 0, DimensionType.MEASURE));
		pivot.setPivotTableProperties(buildPivotTableProperties("{\\\"Fields\\\":[],\\\"ShowFieldHeaders\\\":true,\\\"ShowGrandTotals\\\":true,\\\"ShowPager\\\":true}", null));
		pivot.setReportType(DashbordReportType.PIVOT_TABLE);
		
		return pivot;
	}
	
	public static List<DashboardReport> BuildAllPivot() {
		List<DashboardReport> items = new ArrayList<>();
		items.add(BuildPivot1());
		items.add(BuildPivot2());
		return items;
	}
	
	public static DashboardReportField BuildDashboardReportField(Long dimensionId ,String dimensionName, String name, int position,DimensionType type) {
		
		DashboardReportField item = new DashboardReportField();
		item.setDimensionId(dimensionId);
		item.setDimensionName(dimensionName);
		item.setPosition(position);
		item.setName(name);
		item.setPosition(position);
		item.setType(type);
		return item;
	}
	
	public static PivotTableProperties buildPivotTableProperties(String title, String webLayoutData) {
		PivotTableProperties properties = new PivotTableProperties();
		properties.setTitle(title);;
		properties.setWebLayoutData(webLayoutData);
		return properties;
	}
}
