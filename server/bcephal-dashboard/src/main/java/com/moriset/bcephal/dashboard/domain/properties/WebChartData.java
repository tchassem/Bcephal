package com.moriset.bcephal.dashboard.domain.properties;

import lombok.Data;

@Data
public class WebChartData {
	 private String Title;
	 private String Subtitle;
	 private boolean ShowAxisTitles;
	 private String ValueAxisTitle;
	 private String ArgumentAxisTitle;
	 private AxisRange ArgumentAxisRange;
     private AxisRange ValueAxisRange;
     private String ArgumentAxisScrollBarPosition;
	 private String ArgumentAxisZoomAndPanMode;
	 private String ValueAxisZoomAndPanMode;
	 private boolean AllowTouchGestures ;
	 private boolean AllowMouseWheel ;
	 private boolean ArgumentAxisScrollBarVisible;

	 private boolean Rotated ;
	 private String LabelOverlap ;
	 private boolean ShowLegend ;
	 private boolean ShowLegendBorder ;
	 private boolean LegendAllowToggleSeries ;
	 private String LegendOrientation ;
	 private String LegendPosition ;
	 private String LegendHorizontalAlignment ;
	 private String LegendTitle ;
	 private String LegendSubtitle ;
	 private ChartSerie DefaultSerie ;

	 private ChartAxisType ValueAxisType ;
	 private ChartAxisType ArgumentAxisType ;
	 private ChartAxisDataType ValueAxisDataType ;
	 private ChartAxisDataType ArgumentAxisDataType ;
	 private AxisTickInterval ValAxisTickInterval ;
	 private AxisTickInterval ArgAxisTickInterval ;
	 private boolean ArgAxisGridLines ;
	 private boolean ValAxisGridLines ;
}
