package com.moriset.bcephal.dashboard.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "PivotTableProperties")
@Table(name = "BCP_PIVOTTABLE_PROPERTIES")
@Data
@EqualsAndHashCode(callSuper = false)
public class PivotTableProperties extends DashboardReportProperties {

	private static final long serialVersionUID = -6273220529634769028L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_pivot_prop_seq")
	@SequenceGenerator(name = "dashboard_pivot_prop_seq", sequenceName = "dashboard_pivot_prop_seq	", initialValue = 1, allocationSize = 1)
	private Long id;

	@Override
	public PivotTableProperties copy() {
		PivotTableProperties copy = new PivotTableProperties();
		copy.setWebLayoutData(getWebLayoutData());
		copy.setTitle(getTitle());
		return copy;
	}

}
