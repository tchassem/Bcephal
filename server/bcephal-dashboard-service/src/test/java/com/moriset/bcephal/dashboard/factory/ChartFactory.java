package com.moriset.bcephal.dashboard.factory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;

import com.moriset.bcephal.dashboard.domain.ChartProperties;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.DashboardReportChartDispositionType;
import com.moriset.bcephal.dashboard.domain.DashboardReportChartType;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.domain.DashboardReportFieldProperties;
import com.moriset.bcephal.dashboard.domain.DashbordReportType;
import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilter;
import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilterItem;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.FilterItemType;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.service.InitiationService;


public class ChartFactory {
	
	public static List<DashboardReport> buildCharts() throws Exception {
		List<DashboardReport> items = new ArrayList<>();
		
		return items;
	}

	public static DashboardReport buildBILL001BillingEventPerType(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("BILL001 Billing event per type");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-20 08:25:12"));
		report.setModificationDate(Timestamp.valueOf("2023-05-16 21:04:28"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"Billing event per type\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":29,\"DimensionName\":\"Billing amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Billing amount\",\"Id\":66},\"ArgumentAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":124,\"DimensionName\":\"Billing event status\",\"Position\":3,\"Properties\":null,\"Name\":\"Billing event status\",\"Id\":null},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":133,\"DimensionName\":\"Billing group 1\",\"Position\":2,\"Properties\":null,\"Name\":\"Billing group 1\",\"Id\":68},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));

		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Billing event type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Invoice", null));
		report.getDynamicFilter()
				.addItem(buildUniverseDynamicFilterItem(null, "﻿Billing event ID", DimensionType.ATTRIBUTE,
						"﻿Billing event ID", 0, PeriodGranularity.DAY, null,
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null), 
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null)));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Billing amount", "Billing amount",
				0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Billing event date",
				"Billing event date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Billing group 1", "Billing group 1", 2,
				null, null, DimensionType.ATTRIBUTE));

		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Billing event status", "Billing event status", 3,
				null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildBILL002InvoiceStatusPerClient(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("BILL002 Invoice status per client");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-20 08:25:12"));
		report.setModificationDate(Timestamp.valueOf("2023-05-16 21:04:28"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
               "{\"Title\":\"Issued invoice per client per status\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":false,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":23,\"DimensionName\":\"Invoice amount (excl VAT)\",\"Position\":0,\"Properties\":null,\"Name\":\"Invoice amount (excl VAT)\",\"Id\":null},\"ArgumentAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":119,\"DimensionName\":\"Invoice status\",\"Position\":1,\"Properties\":null,\"Name\":\"Invoice status\",\"Id\":71},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":110,\"DimensionName\":\"Client ID\",\"Position\":3,\"Properties\":null,\"Name\":\"Client ID\",\"Id\":73},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getMeasureFilter().addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Invoice amount (excl VAT)", DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, "<>", 0, BigDecimal.ZERO));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Invoice amount (excl VAT)", "Invoice amount (excl VAT)",
				0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Invoice status",
				"Invoice status", 1, null, null, DimensionType.ATTRIBUTE ));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "invoice date", "invoice date", 2,
				null, null, DimensionType.PERIOD));

		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Client ID", "Client ID", 3,
				null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildREC002ISSUINGR02PendingSA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC002 ISSUING R02 Pending SA ");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-02-28 14:26:39"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:19:54"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"R2 Pending SA \",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":83},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":5},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":6},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Settlement Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing", null));
		report.getAdminFilter().getMeasureFilter()
				.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R2",
						DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, ">", 0, BigDecimal.ZERO));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC003ISSUINGR03PendingMA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC003 ISSUING R03 Pending MA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-03-26 14:54:28"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:26:44"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"R03 Pending MA\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":null},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":11},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":12},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Member Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing", null));
		report.getAdminFilter().getMeasureFilter()
		.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R3",
				DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, ">", 0, BigDecimal.ZERO));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC004ISSUINGR04PendingSA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC004 ISSUING R04 Pending SA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-03-26 15:11:01"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:28:49"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"R04 Pending SA\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":85},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":17},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":18},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Settlement Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing", null));
		report.getAdminFilter().getMeasureFilter()
				.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R4",
						DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, ">", 0, BigDecimal.ZERO));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC007ISSUINGR7PendingMA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC007 ISSUING R7 Pending MA ");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 08:11:52"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:29:29"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING R7 Pending MA \",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":86},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":5,\"DimensionName\":\"Scheme Date\",\"Position\":1,\"Properties\":null,\"Name\":\"Scheme Date\",\"Id\":47},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":48},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R07",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						null, AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Issuing", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Member Advisement", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC012ISSUINGR02PendingMA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC012 ISSUING R02 Pending MA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-03-26 14:29:42"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:30:11"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"R02 Pending MA\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":87},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":8},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":9},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Member Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing", null));
		report.getAdminFilter().getMeasureFilter()
		.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R2",
				DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, "<>", 0, null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC017ISSUINGR7PendingREC(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC017 ISSUING R7 Pending REC");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 08:14:32"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:30:56"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING R7 Pending REC\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":null},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":2,\"DimensionName\":\"Processor date\",\"Position\":2,\"Properties\":null,\"Name\":\"Processor date\",\"Id\":52},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":51},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R07",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						null, null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "REC Type Name",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Member Settlement account", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Processor date",
				"Processor date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 0, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC102ACQUIRINGR02PendingSA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC102 ACQUIRING R02 Pending SA ");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-04 13:44:08"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:31:46"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ACQUIRING R02 Pending SA \",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":89},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":26},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":27},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Acquiring", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Settlement Advisement", null));
		report.getAdminFilter().getMeasureFilter()
		.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R2",
				DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, "<>", 0, null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC103ACQUIRINGR03PendingMA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC103 ACQUIRING R03 Pending MA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-07 14:34:39"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:32:21"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
             "{\"Title\":\"ACQUIRING R03 Pending MA\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":90},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":29},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":30},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Acquiring", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Member Advisement", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC104ACQUIRINGR04PendingSA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC104 ACQUIRING R04 Pending SA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-07 14:55:55"));
		report.setModificationDate(Timestamp.valueOf("2023-05-30 17:40:17"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
			"{\"Title\":\"ACQUIRING R04 Pending SA\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":82},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":32},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":33},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Acquiring", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Settlement Advisement", null));
		report.getAdminFilter().getMeasureFilter()
		.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R4",
				DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, "<>", 0, null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC107ACQUIRINGPendingMA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC107 ACQUIRING Pending MA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 08:05:02"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:33:01"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ACQUIRING R7 pending MA \",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":91},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":5,\"DimensionName\":\"Scheme Date\",\"Position\":0,\"Properties\":null,\"Name\":\"Scheme Date\",\"Id\":40},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":42},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R07",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, null, null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Acquiring", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Member Advisement", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Date",
				"Scheme Date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC112ACQUIRINGR02PendingMA(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC112 ACQUIRING R02 Pending MA");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-07 15:00:13"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:33:35"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ACQUIRING R02 Pending MA\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":92},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":35},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":36},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Acquiring", null));
		report.getAdminFilter().getAttributeFilter()
		.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
				DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Member Advisement", null));
		report.getAdminFilter().getMeasureFilter()
		.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Remaining amount R2",
				DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, "<>", 0, null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC117ACQUIRINGPendingREC(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC117 ACQUIRING Pending REC");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 08:08:39"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:34:07"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ACQUIRING R7 Pending REC\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":36,\"DimensionName\":\"Check\",\"Position\":2,\"Properties\":null,\"Name\":\"Check\",\"Id\":93},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":5,\"DimensionName\":\"Scheme Date\",\"Position\":1,\"Properties\":null,\"Name\":\"Scheme Date\",\"Id\":44},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":45},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R07",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, null, AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Acquiring",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "REC Type Name",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null,
						"Member Settlement account", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Date",
				"Scheme Date", 0, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Check", "Check", 2, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC200ISSUINGPendingMT942(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC200 ISSUING Pending MT942");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-03-26 14:56:38"));
		report.setModificationDate(Timestamp.valueOf("2023-04-07 15:32:19"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"Pending MT942\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":2,\"DimensionName\":\"Financial amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Financial amount\",\"Id\":13},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":14},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":15},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Bank Account N°",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"BE66210096446243", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R01",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, null,
						AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R03",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, null,
						AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R04",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 3, null, null,
						AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R05",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 4, null, null,
						AttributeOperator.NULL));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 2, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Financial amount",
				"Financial amount", 0, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREC205ACQUIRINGPendingMT942(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REC205 ACQUIRING Pending MT942");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-06-16 15:04:39"));
		report.setModificationDate(Timestamp.valueOf("2023-04-07 15:07:24"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ACQUIRING Pending MT942\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":2,\"DimensionName\":\"Financial amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Financial amount\",\"Id\":37},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":38},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":39},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedBar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Bank Account N°",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"BE24001870998038", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R01",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, null,
						AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R03",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, null,
						AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R04",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 3, null, null,
						AttributeOperator.NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "R05",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 4, null, null,
						AttributeOperator.NULL));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Scheme Platform ID",
				"Scheme Platform ID", 2, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Financial amount",
				"Financial amount", 0, null, null, DimensionType.MEASURE));
		return report;
	}
	
	public static DashboardReport buildREP001ISSUINGHighestMemberAdvisementVsPrefunding(
			InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("REP001 ISSUING Highest member advisement vs prefunding");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-06-16 15:04:39"));
		report.setModificationDate(Timestamp.valueOf("2023-04-07 15:07:24"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"Prefunding vs Member advisement amount\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":false,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":19},\"ArgumentAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":33,\"DimensionName\":\"Member bank name\",\"Position\":2,\"Properties\":null,\"Name\":\"Member bank name\",\"Id\":21},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":2,\"DimensionName\":\"Advisement ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Advisement ID\",\"Id\":null},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"Bar\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Member Bank ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, null,
						AttributeOperator.NOT_NULL));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, null,
						AttributeOperator.NOT_NULL));
		report.getAdminFilter().getMeasureFilter()
				.addItem(buildMeasureFilterItem(DataSourceType.UNIVERSE, initiationService, "Posting amount",
						DimensionType.MEASURE, FilterVerb.AND, FilterItemType.FREE, ">", 0, BigDecimal.ZERO));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Posting amount",
				"Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Member bank name",
				"Member bank name", 1, null, null, DimensionType.ATTRIBUTE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Advisement ID",
				"Advisement ID", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildSCH001SchemeInvoiceOverviewServiceCode(
			InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("SCH001 Scheme invoice overview - service code");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-28 08:39:32"));
		report.setModificationDate(Timestamp.valueOf("2023-04-28 08:53:07"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"SCH001 Scheme invoice overview - service code\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":47,\"DimensionName\":\"CHARGE\",\"Position\":0,\"Properties\":null,\"Name\":\"CHARGE\",\"Id\":79},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Invoice date\",\"Position\":1,\"Properties\":null,\"Name\":\"Invoice date\",\"Id\":80},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":37,\"DimensionName\":\"SERVICE_CODE_DESCRIPTION\",\"Position\":2,\"Properties\":null,\"Name\":\"SERVICE_CODE_DESCRIPTION\",\"Id\":81},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":null,\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.setAdminFilter(new UniverseFilter());
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "CHARGE",
				"CHARGE", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Invoice date",
				"Invoice date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "SERVICE_CODE_DESCRIPTION",
				"SERVICE_CODE_DESCRIPTION", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildTRX001ISSUINGSettlementAdvisement(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("TRX001 ISSUING Settlement advisement");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-03-24 08:26:55"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 07:52:07"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING - Settlement advisement evolution per settlement platform\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":1},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":2},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":3},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedArea\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Settlement Advisement",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, 
						"Issuing",
						null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Posting amount", "Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService,
				"Scheme Platform ID", "Scheme Platform ID", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildTRX101ACQUIRINGSettlementAdvisement(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("TRX101 ACQUIRING Settlement advisement");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-07 13:17:08"));
		report.setModificationDate(Timestamp.valueOf("2023-05-30 17:41:28"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ACQUIRING - Settlement advisement evolution per settlement platform\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":22},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":23},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":69,\"DimensionName\":\"Scheme Platform ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Scheme Platform ID\",\"Id\":24},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"StackedArea\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Settlement Advisement",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, 
						"Acquiring",
						null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(
				buildDashboardReportField(initiationService, "Posting amount", "Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService,
				"Scheme Platform ID", "Scheme Platform ID", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildTRX200ISSUINGMAPerBankMaestro(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("TRX200 ISSUING MA per bank - Maestro");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 08:55:05"));
		report.setModificationDate(Timestamp.valueOf("2023-06-05 12:28:32"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING MA per bank - Maestro\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":53},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":54},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":31,\"DimensionName\":\"Member Bank ID\",\"Position\":2,\"Properties\":null,\"Name\":\"Member Bank ID\",\"Id\":95},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"Line\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Member Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Maestro Off-Us",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Scheme Platform ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing", null));
		report.getDynamicFilter()
				.addItem(buildUniverseDynamicFilterItem(null, "Member Bank ID", DimensionType.ATTRIBUTE,
						"Member Bank ID", 0, PeriodGranularity.DAY, null,
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
						buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null)));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Posting amount",
				"Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Member Bank ID",
				"Member Bank ID", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildTRX205ISSUINGMAPerMemberMCIOffUs(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("TRX205 ISSUING MA per member - MCI Off Us");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 08:58:51"));
		report.setModificationDate(Timestamp.valueOf("2023-06-1 20:42:53"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING MA per member - MCI Off Us\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":56},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":57},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":33,\"DimensionName\":\"Member bank name\",\"Position\":2,\"Properties\":null,\"Name\":\"Member bank name\",\"Id\":58},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"Line\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Member Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Issuing",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Scheme Platform ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "MasterCard Off-Us", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Posting amount",
				"Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Member Bank ID",
				"Member Bank ID", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildTRX210ISSUINGMAPerMemberMCIOnUs(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("TRX210 ISSUING MA per member - MCI On Us");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 09:01:00"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:44:34"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING MA per member - MCI Off Us\",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":56},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":1,\"DimensionName\":\"Value date\",\"Position\":1,\"Properties\":null,\"Name\":\"Value date\",\"Id\":57},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":33,\"DimensionName\":\"Member bank name\",\"Position\":2,\"Properties\":null,\"Name\":\"Member bank name\",\"Id\":58},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"Line\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Advisement ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null,
						"Member Advisement", null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 2, null, "Issuing",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "Scheme Platform ID",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "MasterCard Off-Us", null));
		report.setDynamicFilter(new UniverseDynamicFilter());
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Posting amount",
				"Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Value date",
				"Value date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Member Bank ID",
				"Member Bank ID", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static DashboardReport buildTRX300ISSUINGRECMSTPerAccount(InitiationService initiationService) {
		DashboardReport report = new DashboardReport();
		report.setName("TRX300 ISSUING - REC MST per account ");
		report.setReportType(DashbordReportType.CHART);
		report.setDataSourceType(DataSourceType.UNIVERSE);
		report.setCreationDate(Timestamp.valueOf("2023-04-16 09:03:16"));
		report.setModificationDate(Timestamp.valueOf("2023-06-01 20:53:42"));
		report.setChartProperties(buildChartProperties(DashboardReportChartDispositionType.COLUMNS_AS_SERIES,
				DashboardReportChartType.FULL_STACKED_BAR,
				"{\"Title\":\"ISSUING - REC MST per account \",\"Subtitle\":null,\"ShowAxisTitles\":true,\"ValueAxisTitle\":null,\"ArgumentAxisTitle\":null,\"Rotated\":false,\"LabelOverlap\":null,\"ShowLegend\":true,\"ShowLegendBorder\":true,\"LegendAllowToggleSeries\":true,\"LegendOrientation\":\"Vertical\",\"LegendPosition\":\"Outside\",\"LegendHorizontalAlignment\":\"Right\",\"LegendTitle\":null,\"LegendSubtitle\":null,\"DefaultSerie\":{\"Color\":null,\"ValueAxis\":{\"Type\":\"MEASURE\",\"DimensionId\":1,\"DimensionName\":\"Posting amount\",\"Position\":0,\"Properties\":null,\"Name\":\"Posting amount\",\"Id\":62},\"ArgumentAxis\":{\"Type\":\"PERIOD\",\"DimensionId\":2,\"DimensionName\":\"Processor date\",\"Position\":1,\"Properties\":null,\"Name\":\"Processor date\",\"Id\":63},\"SerieAxis\":{\"Type\":\"ATTRIBUTE\",\"DimensionId\":86,\"DimensionName\":\"REC Account name\",\"Position\":3,\"Properties\":null,\"Name\":\"REC Account name\",\"Id\":65},\"Name\":null,\"IsDefault\":true,\"IsVisible\":true,\"ShowLabel\":false,\"ShowTooltip\":true,\"Type\":\"Line\",\"AddCustomValueAxis\":false,\"ShowCustomValueAxisTitle\":false,\"CustomValueAxisTitle\":null,\"CustomValueAxisAlignment\":null,\"SerieFilter\":{\"ItemList\":[],\"ItemsCount\":0},\"Id\":null},\"ChartSerieList\":[]}"));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "PML type",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 1, null, "Issuing",
						null));
		report.getAdminFilter().getAttributeFilter()
				.addItem(buildAttributeFilterItem(DataSourceType.UNIVERSE, initiationService, "REC Type Name",
						DimensionType.ATTRIBUTE, FilterVerb.AND, FilterItemType.FREE, false, 0, null, "Member Settlement account", null));
		report.getDynamicFilter()
		.addItem(buildUniverseDynamicFilterItem(null, "REC Account ID", DimensionType.ATTRIBUTE,
				"REC Account ID", 0, PeriodGranularity.DAY, null,
				buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null),
				buildPeriodValue(PeriodOperator.SPECIFIC, null, "+", 0, PeriodGranularity.DAY, null)));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Posting amount",
				"Posting amount", 0, null, null, DimensionType.MEASURE));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "Processor date",
				"Processor date", 1, null, null, DimensionType.PERIOD));
		report.getFieldListChangeHandler().addNew(buildDashboardReportField(initiationService, "REC Account name",
				"REC Account name", 2, null, null, DimensionType.ATTRIBUTE));
		return report;
	}
	
	public static ChartProperties buildChartProperties(DashboardReportChartDispositionType chartDispositionType, DashboardReportChartType chartType,
			String webLayoutData) {
		ChartProperties properties = new ChartProperties();
		properties.setChartDispositionType(chartDispositionType);
		properties.setChartType(chartType);
		properties.setWebLayoutData(webLayoutData);
		return properties;
	}
	
	public static DashboardReportField buildDashboardReportField(InitiationService initiationService, String dimensionName, String name, int position, DashboardReportFieldProperties properties, String tableName, DimensionType type) {
		DashboardReportField props = new DashboardReportField();
		
		Dimension dimension = initiationService.getDimension(type, dimensionName, false, null, null);
		Assertions.assertThat(dimension).isNotNull();
		props.setDimensionId((Long) dimension.getId());
		props.setDimensionName(dimensionName);
		props.setName(name);
		props.setPosition(position);
		props.setProperties(properties);
		props.setTableName(tableName);
		props.setType(type);
		return props;
	}
	
	public static DashboardReportFieldProperties buildDashboardReportField(String dimensionFunction, DimensionFormat format, PeriodValue from, PeriodValue to, Boolean useNet) {
		DashboardReportFieldProperties properties = new DashboardReportFieldProperties();
		properties.setDimensionFunction(dimensionFunction);
		properties.setFormat(format);
		properties.setFromDateValue(from);
		properties.setToDateValue(to);
		properties.setUsedNetCreditDebit(useNet);
		return properties;
	}
	
	public static DimensionFormat buildDimensionFormat(int nbr) {
		DimensionFormat format = new DimensionFormat();
		format.setNbrOfDecimal(nbr);
		return format;
	}
	
	public static PeriodValue buildPeriodValue(PeriodOperator dateOperator,Date dateValue,String dateSign,int dateNumber,PeriodGranularity dateGranularity,String variableName) {
		PeriodValue periodeValue = new PeriodValue();
		periodeValue.setDateOperator(dateOperator);
		periodeValue.setDateGranularity(dateGranularity);
		periodeValue.setDateNumber(dateNumber);
		periodeValue.setDateSign(dateSign);
		periodeValue.setDateValue(dateValue);
		periodeValue.setVariableName(variableName);
		return periodeValue;
	}
	
	public static MeasureFilterItem buildMeasureFilterItem(DataSourceType dataSourceType, InitiationService initiationService, String dimensionName, DimensionType dimensionType,
			FilterVerb filterVerb, FilterItemType itemType, String operator, int position, BigDecimal value) {
		MeasureFilterItem item = new MeasureFilterItem();
		Dimension dimension = initiationService.getDimension(dimensionType, dimensionName, false, null, null);
		Assertions.assertThat(dimension).isNotNull();
		item.setDataSourceType(dataSourceType);
		item.setDimensionId((Long) dimension.getId());
		item.setDimensionName(dimensionName);
		item.setDimensionType(dimensionType);
		item.setFilterVerb(filterVerb);
		item.setItemType(itemType);
		item.setOperator(operator);
		item.setPosition(position);
		item.setValue(value);
		return item;
	}
	
	public static AttributeFilterItem buildAttributeFilterItem(DataSourceType dataSourceType, InitiationService initiationService, String dimensionName, DimensionType dimensionType,
			FilterVerb filterVerb, FilterItemType itemType, Boolean useLink, int position, String variable, String value, AttributeOperator operator) {
		AttributeFilterItem item = new AttributeFilterItem();
		Dimension dimension = initiationService.getDimension(dimensionType, dimensionName, false, null, null);
		Assertions.assertThat(dimension).isNotNull();
		item.setDataSourceType(dataSourceType);
		item.setDimensionId((Long) dimension.getId());
		item.setDimensionName(dimensionName);
		item.setDimensionType(dimensionType);
		item.setFilterVerb(filterVerb);
		item.setItemType(itemType);
		item.setUseLink(useLink);
		item.setPosition(position);
		item.setVariables(variable);
		item.setValue(value);
		item.setOperator(operator);
		return item;
	}
	
	public static PeriodFilterItem buildPeriodFilterItem(DataSourceType dataSourceType, Long dataSourceId, InitiationService initiationService, String dimensionName, DimensionType dimensionType,
			FilterVerb filterVerb, FilterItemType itemType, String sign, int position, int number, String variable, PeriodGranularity granularity, Date value, PeriodOperator operator) {
		PeriodFilterItem item = new PeriodFilterItem();
		
		Dimension dimension = initiationService.getDimension(dimensionType, dimensionName, false, null, null);
		Assertions.assertThat(dimension).isNotNull();
		item.setDataSourceType(dataSourceType);
		item.setDataSourceId(dataSourceId);
		item.setDimensionId((Long)dimension.getId());
		item.setDimensionName(dimensionName);
		item.setDimensionType(dimensionType);
		item.setFilterVerb(filterVerb);
		item.setItemType(itemType);
		item.setSign(sign);
		item.setPosition(position);
		item.setNumber(number);
		item.setVariables(variable);
		item.setGranularity(granularity);
		item.setValue(value);
		item.setOperator(operator);
		return item;
	}
	
	public static UniverseDynamicFilterItem buildUniverseDynamicFilterItem(InitiationService initiationService, String dimensionName, DimensionType dimensionType,
			String label, int position, PeriodGranularity granularity, String values, PeriodValue from, PeriodValue to) {
		UniverseDynamicFilterItem item = new UniverseDynamicFilterItem();
		Dimension dimension = initiationService.getDimension(dimensionType, dimensionName, false, null, null);
		Assertions.assertThat(dimension).isNotNull();
		item.setDimensionId((Long) dimension.getId());
		item.setDimensionName(dimensionName);
		item.setDimensionType(dimensionType);
		item.setLabel(label);
		item.setPosition(position);
		item.setGranularity(granularity);
		item.setValues(values);
		item.setStartDateValue(from);
		item.setEndDateValue(to);
		return item;
	}
	
	public static UniverseFilter buildUFilter(AttributeFilterItem attributeItem, MeasureFilterItem measureItem, PeriodFilterItem periodItem) {
		UniverseFilter filter = new UniverseFilter();
		
		AttributeFilter attributeFilter = new AttributeFilter();
		attributeFilter.getItemListChangeHandler().addNew(attributeItem);
		filter.setAttributeFilter(attributeFilter);
		
		MeasureFilter measureFilter = new MeasureFilter();
		measureFilter.getItemListChangeHandler().addNew(measureItem);
		filter.setMeasureFilter(measureFilter);
		
		PeriodFilter periodFilter = new PeriodFilter();
		periodFilter.getItemListChangeHandler().addNew(periodItem);
		filter.setPeriodFilter(periodFilter);
		return filter;
	}
}
