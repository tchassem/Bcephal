package com.moriset.bcephal.dashboard.service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.BarGrouping;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.Grouping;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.ScatterStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFAreaChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData.Series;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFPieChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.moriset.bcephal.dashboard.domain.ChartProperties;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.properties.ChartSerieColor;
import com.moriset.bcephal.dashboard.domain.properties.WebChartData;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
@NoArgsConstructor
public class PresentationChartService {

	@Autowired
	DashboardReportService dashboardReportService;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	ObjectMapper objectMapper;

	public XSLFChart buildChart(XMLSlideShow powerpoint, Long reportId, Map<String, Object> variableValues) {
		DashboardReport report = dashboardReportService.getById(reportId);
		if (report == null) {
			throw new BcephalException("Chart not found : " + reportId);
		}
		return buildChart(powerpoint, report, variableValues);
	}
	
	public XSLFChart buildChart(XMLSlideShow powerpoint, String name, Map<String, Object> variableValues) {
		DashboardReport report = dashboardReportService.getByName(name);
		if (report == null) {
			throw new BcephalException("Chart not found : " + name);
		}
		return buildChart(powerpoint, report, variableValues);
	}
	
	public XSLFChart buildChart(XMLSlideShow powerpoint, DashboardReport report, Map<String, Object> variableValues) {
		if (powerpoint == null) {
			throw new BcephalException("XMLSlideShow powerpoint is null");
		}
		XSLFChart chart = null;
		if (report != null) {
			List<VariableValue> variables = buildVariableValuesForFilter(variableValues);
			if(report.getAdminFilter() != null)	{
				report.getAdminFilter().setVariableValues(variables);
			}
			if(report.getUserFilter() != null)	{
				report.getUserFilter().setVariableValues(variables);
			}
			if(report.getGridAdminFilter() != null)	{
				report.getGridAdminFilter().setVariableValues(variables);
			}
			if(report.getGridUserFilter() != null)	{
				report.getGridUserFilter().setVariableValues(variables);
			}
			ArrayNode arrayNode = dashboardReportService.getRows(report, Locale.ENGLISH);			
			if (arrayNode != null) {
				try {
					return buildChart(powerpoint, report, arrayNode);
				} catch (Exception e) {
					log.error("Error {}", e);
					throw new BcephalException("Unable to build chart : " + e.getMessage());
				}
			}
		}
		return chart;
	}
	private GrilleExportData buildGrid(String reportName) {	
		GrilleExportData data = new GrilleExportData();
		data.setFilter(new GrilleDataFilter());
		data.getFilter().setGrid(grilleService.getByName(reportName));
		data.setType(GrilleExportDataType.EXCEL);
		return data;
	}
	
   public List<String> ExtractReportData(String reportName, XSSFWorkbook workbook, OutputStream os) throws Exception{
	   GrilleExportData data = buildGrid(reportName);
	   if(data.getFilter().getGrid() == null) {
		   return new ArrayList<>();
	   }
		return grilleService.exportWithXSSFWorkbook(data, workbook, os);
	}

	private XSLFChart buildChart(XMLSlideShow powerpoint, DashboardReport report, ArrayNode arrayNode) throws Exception {
		
		ChartProperties chartProperties = report.getChartProperties();
		WebChartData webChartData = objectMapper.readValue(chartProperties.getWebLayoutData(), WebChartData.class);
		
		XSLFChart chart = powerpoint.createChart();
		chart.setTitleText(StringUtils.hasText(chartProperties.getTitle()) ? chartProperties.getTitle() : report.getName());
		
		XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
		XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
		leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
		
		List<String> arguments = new ArrayList<>();
		List<String> serieNames = new ArrayList<>();

		String argName = webChartData.getDefaultSerie().getArgumentAxis().getDimensionName();
		String argValueName = webChartData.getDefaultSerie().getValueAxis().getDimensionName();
		String argSerieName = webChartData.getDefaultSerie().getSerieAxis().getDimensionName();
		
		Iterator<JsonNode> it = arrayNode.iterator();
		while (it.hasNext()) {
			JsonNode body = it.next();
			report.getFields().forEach(x -> {
				if (StringUtils.hasText(x.getDimensionName())) {
					String val = body.get(x.getDimensionName()).asText();
					if (x.getDimensionName().equals(argName) && !arguments.contains(val)) {
						arguments.add(val);
					}
					if (x.getDimensionName().equals(argSerieName) && !serieNames.contains(val)) {
						serieNames.add(val);
					}
				}
			});
		}
		
		Collections.sort(arguments);
		Collections.sort(serieNames);
		int firstRow = arguments.size() <= 0 ? 0 : 1;
		String categoryDataRange = chart.formatRange(new org.apache.poi.ss.util.CellRangeAddress(firstRow, arguments.size(), 0, 0));
		XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromArray(arguments.toArray(new String[arguments.size()]), categoryDataRange, 0);
		
		XDDFChartData data = buildChartData(webChartData, chart, bottomAxis, leftAxis); 
		
		if(serieNames.size() == 1) {
			serieNames.add("");
		}
		int offset = 0;
		for (String serieValue : serieNames) {
			offset++;
			BigDecimal[] values = buildValues(arrayNode, arguments, argName, argSerieName, argValueName, serieValue, offset);
			
			XDDFNumericalDataSource<BigDecimal> valueData = buildSerie(chart, offset, values);
			XDDFChartData.Series serie = data.addSeries(categoryData, valueData);			
			buildSerieColor(serie, serieValue, webChartData);
			serie.setTitle(serieValue, chart.setSheetTitle(serieValue, offset));
			buildLegend(webChartData, chart, serieValue);
		}	
		
		chart.plot(data);
		return chart;
	}	
	
	private void buildLegend(WebChartData webChartData, XSLFChart chart, String serieValue) {
		if(webChartData.isShowLegend() && StringUtils.hasText(serieValue)) {
			XDDFTextBody textLegen = new XDDFTextBody(chart);			
			chart.getOrAddLegend().addEntry();
			chart.getOrAddLegend().getEntries().getLast().setTextBody(textLegen);
			chart.getOrAddLegend().getEntries().getLast().getTextBody().setText(serieValue);
			if("Right".equalsIgnoreCase(webChartData.getLegendHorizontalAlignment())) {
				chart.getOrAddLegend().setPosition(LegendPosition.RIGHT);
			}else if("Left".equalsIgnoreCase(webChartData.getLegendHorizontalAlignment())) {
				chart.getOrAddLegend().setPosition(LegendPosition.LEFT);
			}else if("Center".equalsIgnoreCase(webChartData.getLegendHorizontalAlignment())) {
				chart.getOrAddLegend().setPosition(LegendPosition.TOP);
			}else if("Bottom".equalsIgnoreCase(webChartData.getLegendHorizontalAlignment())) {
				chart.getOrAddLegend().setPosition(LegendPosition.BOTTOM);
			}
		}
	}
	
	private XDDFChartData buildChartData(WebChartData webChartData, XSLFChart chart, XDDFCategoryAxis bottomAxis,
			XDDFValueAxis leftAxis) {
		
		if (!StringUtils.hasText(webChartData.getDefaultSerie().getType())) {
			webChartData.getDefaultSerie().setType("BAR");
		}
		
		if (webChartData.getDefaultSerie().getType().toUpperCase().contains("BAR")) {
			XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
			XDDFBarChartData bar = (XDDFBarChartData) data;
			bar.setBarDirection(BarDirection.COL);
			if (webChartData.getDefaultSerie().getType().toUpperCase().equals("STACKEDBAR")) {				
				bar.setBarGrouping(BarGrouping.STACKED);
			}
			else if (webChartData.getDefaultSerie().getType().toUpperCase().equals("FULLSTACKEDBAR")) {				
				bar.setBarGrouping(BarGrouping.PERCENT_STACKED);
			}
			return data;
		}
		else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("LINE")) {
			XDDFChartData data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
			XDDFLineChartData bar = (XDDFLineChartData) data;			
			if (webChartData.getDefaultSerie().getType().toUpperCase().equals("STACKEDLINE")) {
				bar.setGrouping(Grouping.STACKED);
			}
			else if (webChartData.getDefaultSerie().getType().toUpperCase().equals("FULLSTACKEDLINE")) {
				bar.setGrouping(Grouping.PERCENT_STACKED);
			}
			return data;
		}
		else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("AREA")) {
			XDDFChartData data = chart.createData(ChartTypes.AREA, bottomAxis, leftAxis);
			XDDFAreaChartData bar = (XDDFAreaChartData) data;			
			if (webChartData.getDefaultSerie().getType().toUpperCase().equals("STACKEDAREA")) {
				bar.setGrouping(Grouping.STACKED);
			}
			else if (webChartData.getDefaultSerie().getType().toUpperCase().equals("FULLSTACKEDAREA")) {
				bar.setGrouping(Grouping.PERCENT_STACKED);
			}
			return data;
		}
		else if (webChartData.getDefaultSerie().getType().toUpperCase().equals("SCATTER")) {
			XDDFChartData data = chart.createData(ChartTypes.SCATTER, bottomAxis, leftAxis);
			XDDFScatterChartData bar = (XDDFScatterChartData) data;	
			bar.setStyle(ScatterStyle.SMOOTH_MARKER);
			return data;
		}		
		else if (webChartData.getDefaultSerie().getType().toUpperCase().equals("PIE")) {
			XDDFChartData data = chart.createData(ChartTypes.PIE, bottomAxis, leftAxis);
			XDDFPieChartData bar = (XDDFPieChartData) data;	
			return bar;
		}
		
		else {
			XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
			XDDFBarChartData bar = (XDDFBarChartData) data;
			bar.setBarDirection(BarDirection.COL);
			return data;
		}
		
//		if (StringUtils.hasText(webChartData.getDefaultSerie().getType())) {
//			if (webChartData.getDefaultSerie().getType().toUpperCase().contains("LINE")) {
//				data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
//				// XDDFLineChartData line = (XDDFLineChartData) data;
//				// line.setBarDirection(BarDirection.COL);
//			} else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("PIE")) {
//				data = chart.createData(ChartTypes.PIE, bottomAxis, leftAxis);
//				// XDDFPieChartData pie = (XDDFPieChartData) data;
//				// pie.setBarDirection(BarDirection.COL);
//			} else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("AREA")) {
//				data = chart.createData(ChartTypes.AREA, bottomAxis, leftAxis);
//				XDDFAreaChartData area = (XDDFAreaChartData) data;
//				// area.setBarDirection(BarDirection.COL);
//			} else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("DOUGHNUT")) {
//				data = chart.createData(ChartTypes.DOUGHNUT, bottomAxis, leftAxis);
//				// XDDFDoughnutChartData doughnut = (XDDFDoughnutChartData) data;
//				// doughnut.setBarDirection(BarDirection.COL);
//			} else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("RADAR")) {
//				data = chart.createData(ChartTypes.RADAR, bottomAxis, leftAxis);
//				// XDDFRadarChartData radar = (XDDFRadarChartData) data;
//				// radar.setBarDirection(BarDirection.COL);
//			} else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("SCATTER")) {
//				data = chart.createData(ChartTypes.SCATTER, bottomAxis, leftAxis);
//				// XDDFScatterChartData scatter = (XDDFScatterChartData) data;
//				// scatter.setBarDirection(BarDirection.COL);
//			} else if (webChartData.getDefaultSerie().getType().toUpperCase().contains("SURFACE")) {
//				data = chart.createData(ChartTypes.SURFACE, bottomAxis, leftAxis);
//				// XDDFSurfaceChartData surface = (XDDFSurfaceChartData) data;
//				// surface.setBarDirection(BarDirection.COL);
//			}
//		}
//		return data;
	}
	
	private XDDFNumericalDataSource<BigDecimal> buildSerie(XSLFChart chart,  int col, BigDecimal[] values) {
		String valuesDataRange = chart.formatRange(new CellRangeAddress(1, values.length, col, col));
		XDDFNumericalDataSource<BigDecimal> valueData = XDDFDataSourcesFactory
				.fromArray(values, valuesDataRange, col);
		return valueData;
	}
	
	
	
	private BigDecimal[] buildValues(ArrayNode arrayNode,List<String> arguments,String argName, String argSerieName, String argValueName, 
			String argSerieTitle, int col) {
		BigDecimal[] values = new BigDecimal[arguments.size()];
		Iterator<JsonNode> it = arrayNode.iterator();
		
		while (it.hasNext()) {
			JsonNode body = it.next();
			if (body.has(argName) && body.has(argSerieName)) {
				String argNode = body.get(argName).asText();
				String serieNode = body.get(argSerieName).asText();
				int index = arguments.indexOf(argNode);
				BigDecimal value = BigDecimal.ZERO;
				if (argNode != null && serieNode != null && serieNode.equals(argSerieTitle)) {
					try {
						value = values[index];
						if(value == null) {
							value = BigDecimal.ZERO;
						}
						BigDecimal val = new BigDecimal(body.get(argValueName).asDouble());
						value = value.add(val);
					} catch (Exception e) {
						
					}
					values[index] = value;
				}					
			}
		}
		return values;
	}

	private void buildSerieColor(Series serie, String serieValue, WebChartData webChartData) {
		if(webChartData.getDefaultSerie() != null && serieValue != null) {
			ChartSerieColor color = webChartData.getDefaultSerie().getColor(serieValue);
			if(color != null) {
//				Color c = Color.decode(color.getColor());
//				XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(c.getRed(), c.getGreen(), c.getBlue()));				
//				serie.setFillProperties(fill);				
			}
		}
	}
	
	
	protected List<VariableValue> buildVariableValuesForFilter(Map<String, Object> variableValues) {
		List<VariableValue> values = new ArrayList<>();
		if(variableValues != null) {
			for(String name : variableValues.keySet()) {
				Object obj = variableValues.get(name);
				if(obj != null) {
					if(obj instanceof Number) {
						values.add(VariableValue.builder().name(name).decimalValue(BigDecimal.valueOf(((Number)obj).doubleValue())).build());
					}
					else if(obj instanceof BigDecimal) {
						values.add(VariableValue.builder().name(name).decimalValue((BigDecimal)obj).build());
					}
					else if(obj instanceof String) {
						values.add(VariableValue.builder().name(name).stringValue((String)obj).build());
					}
					else if(obj instanceof Date) {
						values.add(VariableValue.builder().name(name).periodValue((Date)obj).build());
					}
					else if(obj instanceof VariableIntervalPeriod) {
						values.add(VariableValue.builder()
								.name(name)
								.periodValue(((VariableIntervalPeriod)obj).getStart())
								.end(((VariableIntervalPeriod)obj).getEnd())
								.build());
					}
				}
			}
		}
		return values;
	}
		
	
	
	
	
	
	

	
	public XSLFChart ReplaceChartWorkbook(XMLSlideShow powerPoint, XSLFChart oldchart, String name, Map<String, Object> variableValues) {
		DashboardReport report = dashboardReportService.getByName(name);
		if (report == null) {
			throw new BcephalException("Chart not found : " + name);
		}
		return ReplaceChartWorkbook(powerPoint, oldchart, report, variableValues);
		
	}

	
	public XSLFChart ReplaceChartWorkbook(XMLSlideShow powerPoint, XSLFChart oldchart, DashboardReport report, Map<String, Object> variableValues) {
		if (oldchart == null) {
			throw new BcephalException("XSLFChart oldchart is null");
		}
		XSLFChart chart = null;
		if (report != null) {
			List<VariableValue> variables = buildVariableValuesForFilter(variableValues);
			if(report.getAdminFilter() != null)	{
				report.getAdminFilter().setVariableValues(variables);
			}
			if(report.getUserFilter() != null)	{
				report.getUserFilter().setVariableValues(variables);
			}
			if(report.getGridAdminFilter() != null)	{
				report.getGridAdminFilter().setVariableValues(variables);
			}
			if(report.getGridUserFilter() != null)	{
				report.getGridUserFilter().setVariableValues(variables);
			}
			ArrayNode arrayNode = dashboardReportService.getRows(report, Locale.ENGLISH);			
			if (arrayNode != null) {
				try {
				return	ReplaceChartWorkbook(powerPoint, oldchart, report, arrayNode);
				} catch (Exception e) {
					log.error("Error {}", e);
					throw new BcephalException("Unable to build chart : " + e.getMessage());
				}finally {
					
				}
			}
		}
		return chart;
	}
	
	
	
private XSLFChart ReplaceChartWorkbook(XMLSlideShow powerPoint,  XSLFChart oldchart, DashboardReport report, ArrayNode arrayNode) throws Exception {
		
		ChartProperties chartProperties = report.getChartProperties();
		WebChartData webChartData = objectMapper.readValue(chartProperties.getWebLayoutData(), WebChartData.class);
		
		
		List<String> arguments = new ArrayList<>();
		List<String> serieNames = new ArrayList<>();

		String argName = webChartData.getDefaultSerie().getArgumentAxis().getDimensionName();
		String argValueName = webChartData.getDefaultSerie().getValueAxis().getDimensionName();
		String argSerieName = webChartData.getDefaultSerie().getSerieAxis().getDimensionName();
		
		Iterator<JsonNode> it = arrayNode.iterator();
		while (it.hasNext()) {
			JsonNode body = it.next();
			report.getFields().forEach(x -> {
				if (StringUtils.hasText(x.getDimensionName())) {
					String val = body.get(x.getDimensionName()).asText();
					if (x.getDimensionName().equals(argName) && !arguments.contains(val)) {
						arguments.add(val);
					}
					if (x.getDimensionName().equals(argSerieName) && !serieNames.contains(val)) {
						serieNames.add(val);
					}
				}
			});
		}
		
		Collections.sort(arguments);
		Collections.sort(serieNames);
		
		XSLFChart chart = oldchart;
		
		chart.setTitleText(StringUtils.hasText(chartProperties.getTitle()) ? chartProperties.getTitle() : report.getName());
		
		
		List<XDDFChartData> datas = chart.getChartSeries();
		
		XDDFChartData bar = datas.get(0);
		
		int firstRow = arguments.size() <= 0 ? 0 : 1;
		String categoryDataRange = chart.formatRange(new org.apache.poi.ss.util.CellRangeAddress(firstRow, arguments.size(), 0, 0));
		XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromArray(arguments.toArray(new String[arguments.size()]), categoryDataRange, 0);
		
		int countBar = bar.getSeriesCount();
		int offset = countBar - 1;
		while(offset >= 0) {			
			bar.removeSeries(offset);
			offset--;
		}
		offset = 0;
		for (String serieValue : serieNames) {
			offset++;
			BigDecimal[] values = buildValues(arrayNode, arguments, argName, argSerieName, argValueName, serieValue, offset);
			XDDFNumericalDataSource<BigDecimal> valueData = buildSerie(chart, offset, values);
			XDDFChartData.Series serie = bar.addSeries(categoryData, valueData);
			serie.setTitle(serieValue, chart.setSheetTitle(serieValue, offset));
		}	
	     chart.plot(datas.get(0));
		return chart;
	}
}
