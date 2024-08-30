/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "DashboardReportField")
@Table(name = "BCP_DASHBOARD_REPORT_FIELD")
@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardReportField extends Persistent {

	private static final long serialVersionUID = -2526737260888734438L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_report_fiel_seq")
	@SequenceGenerator(name = "dashboard_report_fiel_seq", sequenceName = "dashboard_report_fiel_seq	", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dashboardReport")
	private DashboardReport dashboardReport;

	@Enumerated(EnumType.STRING)
	private DimensionType type;

	private Long DimensionId;

	private String DimensionName;

	private int position;

	private String name;

	private String sql;

	private String tableName;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "properties_id")
	private DashboardReportFieldProperties properties;

	@JsonIgnore
	public DashboardReportField copy() {
		DashboardReportField copy = new DashboardReportField();
		copy.setType(getType());
		copy.setPosition(getPosition());
		copy.setSql(getSql());
		copy.setTableName(getTableName());
		copy.setName(getName());
		copy.setType(getType());
		copy.setDimensionId(getDimensionId());
		copy.setDimensionName(getDimensionName());
		if(getProperties() != null) {
			DashboardReportFieldProperties propertiesCopy = (DashboardReportFieldProperties)getProperties().copy();
			copy.setProperties(propertiesCopy);
		}
		return copy;
	}

}
