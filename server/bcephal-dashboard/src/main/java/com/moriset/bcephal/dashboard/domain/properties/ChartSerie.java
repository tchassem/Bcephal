package com.moriset.bcephal.dashboard.domain.properties;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.domain.VariableValue;

import lombok.Data;
@Data
public class ChartSerie {
	 private String Color ;
	 private List<ChartSerieColor> Colors ;
	 private DashboardReportField ValueAxis ;
	 private DashboardReportField ArgumentAxis ;
	 private DashboardReportField SerieAxis ;
	 private String Name ;
	 private boolean IsDefault ;
	 private boolean IsVisible ;
	 private boolean ShowLabel ;
	 private boolean DoRankingSerie ; 
	 private boolean ShowTooltip ; 
	 private String Type ;
	 private boolean AddCustomValueAxis ;
	 private boolean ShowCustomValueAxisTitle ;
	 private String CustomValueAxisTitle ;
	 private String CustomValueAxisAlignment ;
	 private ChartSerieFilter SerieFilter ;
	 private VariableValue SerieVariableValue ;
	 private VariableValue ArgumentVariableValue ;
	 private VariableValue ValueVariableValue ;
	 private String LinkFunctionalityCode ;
	 private long LinkObjectId ;
	 private boolean OpenLinkObjectOnNewTab ;
	 private boolean IsActiveLink ; 
	 
	 @JsonIgnore
	 public ChartSerieColor getColor(String serie) {
		 if(Colors != null) {
			 for(ChartSerieColor color : Colors) {
				 if(serie.equals(color.getSerie())) {
					 return color;
				 }
			 }
		 }
		 return null;
	 }

}
