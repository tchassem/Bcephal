package com.moriset.bcephal.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.filters.PeriodGrouping;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "DashboardReportFieldProperties")
@Table(name = "BCP_DASHBOARD_REPORT_FIELD_PROPERTIES")
@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardReportFieldProperties extends Persistent {
	
	private static final long serialVersionUID = 8125372581370005799L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_report_field_properties_seq")
	@SequenceGenerator(name = "dashboard_report_field_properties_seq", sequenceName = "dashboard_report_field_properties_seq	", initialValue = 1, allocationSize = 1)
	private Long id;

	private boolean usedNetCreditDebit;
	
	@Embedded	
	private DimensionFormat format;
	
	private PeriodGrouping groupBy;
	
	private String dimensionFunction;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="fromDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="fromDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="fromDateVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="fromDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="fromDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="fromDateGranularity"))
	})
	@Embedded	
	private PeriodValue fromDateValue;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="toDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="toDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="toDateVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="toDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="toDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="toDateGranularity"))
	})
	@Embedded	
	private PeriodValue toDateValue;

	
	public DashboardReportFieldProperties() {
		format = new DimensionFormat();
		fromDateValue = new PeriodValue();
		toDateValue = new PeriodValue();
	}
	
	
	public void setFormat(DimensionFormat format) {
		if(format != null) {
			this.format = format;
		}
	}
	
	public void setFromDateValue(PeriodValue fromDateValue) {
		if(fromDateValue != null) {
			this.fromDateValue = fromDateValue;
		}
	}
	
	public void setToDateValue(PeriodValue toDateValue) {
		if(toDateValue != null) {
			this.toDateValue = toDateValue;
		}
	}
	
	@JsonIgnore
	public DashboardReportFieldProperties copy() {
		DashboardReportFieldProperties copy = new DashboardReportFieldProperties();
		copy.setUsedNetCreditDebit(usedNetCreditDebit);;
		copy.setFormat(format);
		copy.setGroupBy(groupBy);
		copy.setDimensionFunction(dimensionFunction);
		copy.setFromDateValue(fromDateValue);
		copy.setToDateValue(toDateValue);
		return copy;
	}
	
}
