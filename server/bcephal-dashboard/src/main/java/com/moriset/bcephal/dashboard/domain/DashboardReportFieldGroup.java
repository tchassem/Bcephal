/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum DashboardReportFieldGroup {

	ROW(1), COLUMN(2), COMMON(3), FILTER(4), GROUP(5);

	public int position;

	private DashboardReportFieldGroup(int position) {
		this.position = position;
	}

}
