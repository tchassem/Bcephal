package com.moriset.bcephal.dashboard.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardBrowserData extends BrowserData {

	private boolean defaultDashboard;
	
	public DashboardBrowserData(Dashboard dashboard) {
		super(dashboard);
	}
	
	public DashboardBrowserData(Dashboard dashboard, boolean defaultDashboard) {
		this(dashboard);
		setDefaultDashboard(defaultDashboard);
	}
	
}
