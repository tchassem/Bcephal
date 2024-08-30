package com.moriset.bcephal.dashboard.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "ChartProperties")
@Table(name = "BCP_CHART_PROPERTIES")
@Data
@EqualsAndHashCode(callSuper = false)
public class ChartProperties extends DashboardReportProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1628870906437338677L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_chart_prop_seq")
	@SequenceGenerator(name = "dashboard_chart_prop_seq", sequenceName = "dashboard_chart_prop_seq	", initialValue = 1, allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING)
	private DashboardReportChartType chartType;

	@Enumerated(EnumType.STRING)
	private DashboardReportChartDispositionType chartDispositionType;

	@Override
	public ChartProperties copy() {
		ChartProperties copy = new ChartProperties();
		copy.setChartType(getChartType());
		copy.setChartDispositionType(getChartDispositionType());
		copy.setWebLayoutData(getWebLayoutData());
		copy.setTitle(getTitle());
		return copy;
	}

}
