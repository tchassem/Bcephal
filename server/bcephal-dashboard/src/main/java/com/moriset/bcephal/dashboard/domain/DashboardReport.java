/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "DashboardReport")
@Table(name = "BCP_DASHBOARD_REPORT")
@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardReport extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4135917041758928631L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_report_seq")
	@SequenceGenerator(name = "dashboard_report_seq", sequenceName = "dashboard_report_seq	", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient
	private String dataSourceName;

	@Enumerated(EnumType.STRING)
	private DashbordReportType reportType;

	private String tableName;

	private String sql;

	private boolean published;

	private boolean useAllDimensions;
	
	private boolean showDynamicFilter;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "userFilter")
	private UniverseFilter userFilter;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "adminFilter")
	private UniverseFilter adminFilter;

	@Transient
	private UniverseFilter gridUserFilter;
	@Transient
	private UniverseFilter gridAdminFilter;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "othersDimensions")
	private UniverseFilter othersDimensions;


	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "dynamicFilter")
	private UniverseDynamicFilter dynamicFilter;
	
	@Transient
	private UniverseDynamicFilter currentDynamicFilter;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "chartProperties")
	private ChartProperties chartProperties;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "pivotTableProperties")
	private PivotTableProperties pivotTableProperties;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "dashboardReport")
	private List<DashboardReportField> fields;

	@Transient
	private ListChangeHandler<DashboardReportField> fieldListChangeHandler;

	public DashboardReport() {
		this.fieldListChangeHandler = new ListChangeHandler<DashboardReportField>();
		this.useAllDimensions = true;
		this.dataSourceType = DataSourceType.UNIVERSE;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<DashboardReportField> fields) {
		this.fields = fields;
		this.fieldListChangeHandler.setOriginalList(this.fields);
	}

	@PostLoad
	public void initListChangeHandler() {
		this.fields.forEach(x -> {
		});
		this.fieldListChangeHandler.setOriginalList(this.fields);
	}

	@JsonIgnore
	public DashboardReport copy() {
		DashboardReport copy = new DashboardReport();
		copy.setName(getName());
		copy.setDataSourceId(getDataSourceId());
		copy.setDataSourceType(getDataSourceType());
		copy.setDataSourceName(getDataSourceName());
		copy.setDescription(getDescription());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setGroup(getGroup());
		copy.setDataSourceType(dataSourceType);		
		copy.setDataSourceId(dataSourceId);		
		copy.setDataSourceName(dataSourceName);
		copy.setReportType(reportType);
		//copy.setTableName(tableName);
		//copy.setSql(sql);
		copy.setPublished(false);

		copy.setUseAllDimensions(useAllDimensions);		
		copy.setShowDynamicFilter(showDynamicFilter);
		copy.setUserFilter(userFilter != null ? userFilter.copy() : null);
		copy.setAdminFilter(adminFilter != null ? adminFilter.copy() : null);
		copy.setOthersDimensions(othersDimensions != null ? othersDimensions.copy() : null);
		copy.setDynamicFilter(dynamicFilter != null ? dynamicFilter.copy() : null);
		copy.setChartProperties(chartProperties != null ? chartProperties.copy() : null);
		copy.setPivotTableProperties(pivotTableProperties != null ? pivotTableProperties.copy() : null);
				
		for (DashboardReportField field : this.getFieldListChangeHandler().getItems()) {
			if (field == null) continue;
			DashboardReportField copyField = field.copy();
			copy.getFieldListChangeHandler().addNew(copyField);
		}
		return copy;
	}

}
