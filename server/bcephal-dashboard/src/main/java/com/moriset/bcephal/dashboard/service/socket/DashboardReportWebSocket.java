/**
 * 
 */
package com.moriset.bcephal.dashboard.service.socket;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.service.DashboardReportService;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.AbstractExportGrilleWebSocket;
import com.moriset.bcephal.service.MainObjectService;

/**
 * @author Moriset
 *
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardReportWebSocket extends AbstractExportGrilleWebSocket {

	@Autowired
	DashboardReportService dashboardReportService;
	
	@Override
	protected MainObjectService<? extends MainObject, BrowserData> getService(){
		return  dashboardReportService;
	}
	
	protected void buildGrid() {
		DashboardReport report = dashboardReportService.getById(getData().getFilter().getGrid().getId());
		List<DashboardReportField> fields = report.getFieldListChangeHandler().getItems();
		Collections.sort(fields, new Comparator<DashboardReportField>() {
			@Override
			public int compare(DashboardReportField field1, DashboardReportField field2) {
				return field1.getPosition() - field2.getPosition();
			}
		});
		GrilleDataFilter filter = dashboardReportService.buildGrilleDataFilter(report, fields);
		filter.getGrid().setType(GrilleType.DASHBOARD_REPORT);
		getData().setFilter(filter);
	}
	
}
