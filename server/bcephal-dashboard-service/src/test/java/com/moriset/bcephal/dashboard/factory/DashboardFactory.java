package com.moriset.bcephal.dashboard.factory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.dashboard.domain.Dashboard;
import com.moriset.bcephal.dashboard.domain.DashboardItem;
import com.moriset.bcephal.dashboard.domain.DashboardItemType;
import com.moriset.bcephal.dashboard.domain.DashboardLayout;
import com.moriset.bcephal.domain.BGroup;

public class DashboardFactory {
	
	
	public static List<Dashboard> buildDashboards(){
		List<Dashboard> dashboards = new ArrayList<>();
		dashboards.add(buildACQ001OverviewReco());
		dashboards.add(buildBILL001BillingOverview());
		dashboards.add(buildEOM001EPBIssuing());
		dashboards.add(buildEOM002VIBAcquiring());
		dashboards.add(buildHPG001RecoHomepage());
		dashboards.add(buildINV001BillingOverview());
		dashboards.add(buildINV100BillingEventConsistencyCheck());
		dashboards.add(buildISS001OverviewReco());
		dashboards.add(buildREC002ISSUINGR02Reco());
		dashboards.add(buildREC003ISSUINGR03Reco());
		dashboards.add(buildREC004ISSUINGR04Reco());
		dashboards.add(buildREC102ACQUIRINGR02Reco());
		dashboards.add(buildREC103ACQUIRINGR03Reco());
		dashboards.add(buildREC104ACQUIRINGR04Reco());
		dashboards.add(buildSCH001SchemeInvoiceOverview());
		dashboards.add(buildSYS001QuickAccessToDataSourcesAndReport());
		dashboards.add(buildTRX001OverviewIssuingctivity());
		return dashboards;
	}
	
	public static Dashboard buildACQ001OverviewReco() {
		Dashboard item = new Dashboard();
		item.setName("ACQ001 Overview reco");
		item.setCreationDate(Timestamp.valueOf("2023-04-07 14:01:01"));
		item.setModificationDate(Timestamp.valueOf("2023-04-07 14:01:06"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_4x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("Acquiring", Timestamp.valueOf("2023-04-07 14:01:06"), Timestamp.valueOf("2023-04-07 14:01:01")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC205 ACQUIRING Pending MT942", DashboardItemType.CHART, true, 13l, 9, "REC205 ACQUIRING Pending MT942"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List reconciliation filters", DashboardItemType.RECONCILIATION_FILTER_BROWSER, true, null, 10, "List reconciliation filters"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 11, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC102 ACQUIRING R02 Pending SA", DashboardItemType.CHART, true, 9l, 0, "REC102 ACQUIRING R02 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC112 ACQUIRING R02 Pending MA", DashboardItemType.CHART, true, 12l, 1, "REC112 ACQUIRING R02 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC103 ACQUIRING R03 Pending MA", DashboardItemType.CHART, true, 10l, 3, "REC103 ACQUIRING R03 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC104 ACQUIRING R04 Pending SA", DashboardItemType.CHART, true, 11l, 4, "REC104 ACQUIRING R04 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC107 ACQUIRING Pending MA", DashboardItemType.CHART, true, 14l, 6, "REC107 ACQUIRING Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC117 ACQUIRING Pending REC", DashboardItemType.CHART, true, 17l, 7, "REC117 ACQUIRING Pending REC"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC117 ACQUIRING Pending REC", DashboardItemType.CHART, true, 17l, 7, "REC117 ACQUIRING Pending REC"));
		return item;
	}
	public static Dashboard buildBILL001BillingOverview() {
		Dashboard item = new Dashboard();
		item.setName("BILL001 Billing overview");
		item.setCreationDate(Timestamp.valueOf("2023-04-20 08:33:05"));
		item.setModificationDate(Timestamp.valueOf("2023-08-10 12:42:58"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.HORIZONTAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("Acquiring", Timestamp.valueOf("2023-04-07 14:01:06"), Timestamp.valueOf("2023-04-07 14:01:01")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List joins", DashboardItemType.BROWSER_REPORTING_JOIN, true, null, 3, "List joins"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List invoices", DashboardItemType.LIST_INVOICE, true, null, 5, "List invoices"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 6, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 7, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List scheduler", DashboardItemType.BROWSER_SCHEDULER_PLANNER, true, null, 4, "List scheduler"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("INV100 Billing event list per status", DashboardItemType.CHART, true, 28l, 0, "INV100 Billing event list per status"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("INV200 Invoice per client per status", DashboardItemType.CHART, true, 30l, 1, "INV200 Invoice per client per status"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		return item;
	}
	public static Dashboard buildEOM001EPBIssuing() {
		Dashboard item = new Dashboard();
		item.setName("EOM001 EPB Issuing");
		item.setCreationDate(Timestamp.valueOf("2023-10-03 17:39:03"));
		item.setModificationDate(Timestamp.valueOf("2023-10-03 17:40:03"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(null);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("EOM900 EPB Issuing", DashboardItemType.REPORT_GRID, true, 250l, 0, "EOM900 EPB Issuing"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List report grids", DashboardItemType.REPORT_GRID_BROWSER, true, null, 2, "List report grids"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 3, "Empty type"));
		return item;
	}
	public static Dashboard buildEOM002VIBAcquiring() {
		Dashboard item = new Dashboard();
		item.setName("EOM002 VIB Acquiring");
		item.setCreationDate(Timestamp.valueOf("2023-10-03 17:40:11"));
		item.setModificationDate(Timestamp.valueOf("2023-10-03 17:41:49"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(null);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List report grids", DashboardItemType.REPORT_GRID_BROWSER, true, null, 2, "List report grids"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 3, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("EOM910 EPB Acquiring", DashboardItemType.REPORT_GRID, true, 251l, 0, "EOM910 EPB Acquiring"));
		return item;
	}
	public static Dashboard buildHPG001RecoHomepage() {
		Dashboard item = new Dashboard();
		item.setName("HPG001 Reco homepage");
		item.setCreationDate(Timestamp.valueOf("2023-03-24 08:25:37"));
		item.setModificationDate(Timestamp.valueOf("2023-09-24 08:41:19"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_2x2);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("ACQUIRING - Settlement advisement evolution", DashboardItemType.CHART, true, 8l, 3, "TRX101 Acquiring - Settlement advisement"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("ISSUING - Settlement advisement evolution", DashboardItemType.CHART, true, 1l, 2, "TRX001 Settlement advisement"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List tasks", DashboardItemType.BROWSER_TASK, true, null, 1, "List tasks"));
		return item;
	}
	public static Dashboard buildINV001BillingOverview() {
		Dashboard item = new Dashboard();
		item.setName("INV001 Billing overview");
		item.setCreationDate(Timestamp.valueOf("2023-08-10 12:36:36"));
		item.setModificationDate(Timestamp.valueOf("2023-10-27 11:44:12"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(null);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("ACQUIRING - Settlement advisement evolution", DashboardItemType.CHART, true, 8l, 3, "TRX101 Acquiring - Settlement advisement"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("ISSUING - Settlement advisement evolution", DashboardItemType.CHART, true, 1l, 2, "TRX001 Settlement advisement"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List tasks", DashboardItemType.BROWSER_TASK, true, null, 1, "List tasks"));
		return item;
	}
	public static Dashboard buildINV100BillingEventConsistencyCheck() {
		Dashboard item = new Dashboard();
		item.setName("INV100 Billing event consistency check");
		item.setCreationDate(Timestamp.valueOf("2023-08-18 11:31:41"));
		item.setModificationDate(Timestamp.valueOf("2023-10-11 17:42:24"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "CHK200 Billing amount total  per billing group", DashboardItemType.REPORT_GRID, true, 220l, 3, "CHK200 Billing amount total  per billing group"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "INV100 Billing event list per status", DashboardItemType.CHART, true, 28l, 4, "INV100 Billing event list per status"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "CHK300 MCI PML Invoice total amount - per month", DashboardItemType.REPORT_GRID, true, 222l, 6, "CHK300 MCI PML Invoice total amount - per month"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "CHK100 Billing event amount", DashboardItemType.REPORT_GRID, true, 193l, 0, "CHK100 Billing event amount"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "CHK100 Billing event amount - details per client", DashboardItemType.REPORT_GRID, true, 226l, 1, "CHK100 Billing event amount - details per client"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "CHK350 Affiliate invoice - total per month", DashboardItemType.REPORT_GRID, true, 224l, 7, "CHK350 Affiliate invoice - total per month"));
		return item;
	}
	public static Dashboard buildISS001OverviewReco() {
		Dashboard item = new Dashboard();
		item.setName("ISS001 Overview reco");
		item.setCreationDate(Timestamp.valueOf("2023-04-15 06:22:21"));
		item.setModificationDate(Timestamp.valueOf("2023-04-26 15:43:41"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_4x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC017 ISSUING R7 Pending REC", DashboardItemType.CHART, true, 17l, 7, "REC017 ISSUING R7 Pending REC"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 10, "List automatic reconciliations"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 11, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC200 ISSUING Pending MT942", DashboardItemType.CHART, true, 5l, 9, "REC200 ISSUING Pending MT942"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC002 ISSUING R02 Pending SA", DashboardItemType.CHART, true, 2l, 0, "REC002 ISSUING R02 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC012 ISSUING R02 Pending MA", DashboardItemType.CHART, true, 3l, 1, "REC012 ISSUING R02 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC003 ISSUING R03 Pending MA", DashboardItemType.CHART, true, 4l, 3, "REC003 ISSUING R03 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC004 ISSUING R04 Pending SA", DashboardItemType.CHART, true, 6l, 4, "REC004 ISSUING R04 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC007 ISSUING R7 Pending MA", DashboardItemType.CHART, true, 16l, 6, "REC007 ISSUING R7 Pending MA"));
		return item;
	}
	public static Dashboard buildREC002ISSUINGR02Reco() {
		Dashboard item = new Dashboard();
		item.setName("REC002 ISSUING R02 Reco");
		item.setCreationDate(Timestamp.valueOf("2023-03-26 14:31:35"));
		item.setModificationDate(Timestamp.valueOf("2023-04-26 15:42:44"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC201 R02 Pending SA", DashboardItemType.CHART, true, 2l, 0, "REC201 R02 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC202 R02 Pending MA", DashboardItemType.CHART, true, 3l, 3, "REC202 R02 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 4, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 6, "List automatic reconciliations"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("HPG201 R02 Reco of the day", DashboardItemType.REPORT_GRID, true, 18l, 7, "HPG201 R02 Reco of the day"));
		return item;
	}
	public static Dashboard buildREC003ISSUINGR03Reco() {
		Dashboard item = new Dashboard();
		item.setName("REC003 ISSUING R03 Reco");
		item.setCreationDate(Timestamp.valueOf("2023-03-26 14:58:50"));
		item.setModificationDate(Timestamp.valueOf("2023-04-26 15:45:18"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC301 R03 Pending MA", DashboardItemType.CHART, true, 4l, 0, "REC301 R03 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC302 R03 Pending MT942", DashboardItemType.CHART, true, 5l, 3, "REC302 R03 Pending MT942"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 4, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("HPG301 R03 Reco of the day", DashboardItemType.REPORT_GRID, true, 19l, 7, "HPG301 R03 Reco of the day"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 6, "List automatic reconciliations"));
		return item;
	}
	public static Dashboard buildREC004ISSUINGR04Reco() {
		Dashboard item = new Dashboard();
		item.setName("REC004 ISSUING R04 Reco");
		item.setCreationDate(Timestamp.valueOf("2023-03-26 15:16:30"));
		item.setModificationDate(Timestamp.valueOf("2023-04-26 15:47:00"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC401 R04 Pending SA", DashboardItemType.CHART, true, 6l, 0, "REC401 R04 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC900 Pending MT942", DashboardItemType.CHART, true, 5l, 3, "REC900 Pending MT942"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("HPG401 R04 Reco of the day", DashboardItemType.REPORT_GRID, true, 20l, 7, "HPG401 R04 Reco of the day"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 4, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 6, "List automatic reconciliations"));
		return item;
	}
	public static Dashboard buildREC102ACQUIRINGR02Reco() {
		Dashboard item = new Dashboard();
		item.setName("REC102 ACQUIRING R02 Reco");
		item.setCreationDate(Timestamp.valueOf("2023-04-07 15:34:28"));
		item.setModificationDate(Timestamp.valueOf("2023-04-26 15:47:47"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC102 ACQUIRING R02 Pending SA", DashboardItemType.CHART, true, 9l, 0, "REC102 ACQUIRING R02 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem( "REC112 ACQUIRING R02 Pending MA", DashboardItemType.CHART, true, 12l, 3, "REC112 ACQUIRING R02 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 4, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC102 ACQUIRING R02 Reco of the day", DashboardItemType.REPORT_GRID, true, 50l, 7, "REC102 ACQUIRING R02 Reco of the day"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 6, "List automatic reconciliations"));
		return item;
	}
	public static Dashboard buildREC103ACQUIRINGR03Reco() {
		Dashboard item = new Dashboard();
		item.setName("REC103 ACQUIRING R03 Reco");
		item.setCreationDate(Timestamp.valueOf("2023-04-07 15:54:14"));
		item.setModificationDate(Timestamp.valueOf("2023-04-26 15:48:23"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC103 ACQUIRING R03 Pending MA", DashboardItemType.CHART, true, 10l, 0, "REC103 ACQUIRING R03 Pending MA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC205 ACQUIRING Pending MT942", DashboardItemType.CHART, true, 13l, 3, "REC205 ACQUIRING Pending MT942"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 4, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 6, "List automatic reconciliations"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC103 ACQUIRING R03 Reco of the day", DashboardItemType.REPORT_GRID, true, 51l, 7, "REC103 ACQUIRING R03 Reco of the day"));
		return item;
	}
	public static Dashboard buildREC104ACQUIRINGR04Reco() {
		Dashboard item = new Dashboard();
		item.setName("REC104 ACQUIRING R04 Reco");
		item.setCreationDate(Timestamp.valueOf("2023-04-07 15:57:52"));
		item.setModificationDate(Timestamp.valueOf("2023-07-28 07:55:58"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 5, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC104 ACQUIRING R04 Pending SA", DashboardItemType.CHART, true, 11l, 0, "REC104 ACQUIRING R04 Pending SA"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC205 ACQUIRING Pending MT942", DashboardItemType.CHART, true, 13l, 3, "REC205 ACQUIRING Pending MT942"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 4, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("REC104 ACQUIRING R04 Reco of the day", DashboardItemType.REPORT_GRID, true, 52l, 7, "REC104 ACQUIRING R04 Reco of the day"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List automatic reconciliations", DashboardItemType.RECONCILIATION_AUTO_BROWSER, true, null, 6, "List automatic reconciliations"));
		return item;
	}
	public static Dashboard buildSCH001SchemeInvoiceOverview() {
		Dashboard item = new Dashboard();
		item.setName("SCH001 Scheme invoice overview");
		item.setCreationDate(Timestamp.valueOf("2023-04-28 08:42:56"));
		item.setModificationDate(Timestamp.valueOf("2023-04-28 08:52:29"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.HORIZONTAL_2x2);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("SCH001 Scheme invoice overview - service code", DashboardItemType.CHART, true, 25l, 0, "SCH001 Scheme invoice overview - service code"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, "Empty type"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("SCH100 PML Invoice", DashboardItemType.REPORT_GRID, true, 103l, 2, "SCH100 PML Invoice"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("SCH102 Affiliate invoice", DashboardItemType.REPORT_GRID, true, 104l, 3, "SCH102 Affiliate invoice"));
		return item;
	}
	public static Dashboard buildSYS001QuickAccessToDataSourcesAndReport() {
		Dashboard item = new Dashboard();
		item.setName("SYS001 Quick access to data sources and report");
		item.setCreationDate(Timestamp.valueOf("2023-04-28 08:15:41"));
		item.setModificationDate(Timestamp.valueOf("2023-04-28 08:16:50"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(null);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List input grids", DashboardItemType.INPUT_GRID_BROWSER, true, null, 0, "List input grids"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List materialized grid", DashboardItemType.MATERIALIZED_BROWSER, true, null, 1, "List materialized grid"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List report grids", DashboardItemType.REPORT_GRID_BROWSER, true, null, 2, "List report grids"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("List joins", DashboardItemType.BROWSER_REPORTING_JOIN, true, null, 3, "List joins"));
		return item;
	}
	public static Dashboard buildTRX001OverviewIssuingctivity() {
		Dashboard item = new Dashboard();
		item.setName("TRX001 Overview Issuing activity");
		item.setCreationDate(Timestamp.valueOf("2023-04-16 09:07:04"));
		item.setModificationDate(Timestamp.valueOf("2023-04-16 09:09:24"));
		item.setAllowRefreshFrequency(false);
		item.setDocumentCount(0);
		item.setLayout(DashboardLayout.VERTICAL_3x3);
		item.setPublished(false);
		item.setRefreshFrequency(0);
		item.setVisibleInShortcut(true);
		item.setProfilId(null);
		item.setRefreshFrequencyUnit(null);
		item.setDefaultItem(buildDashboardItem("Item",DashboardItemType.EMPTY_TYPE, true, null, 0, null));
		//item.setGroup(buildBGroup("DEFAULT", Timestamp.valueOf("2023-03-24 06:58:18"), Timestamp.valueOf("2023-03-24 06:58:18")));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("TRX210 ISSUING MA per member - MCI On Us", DashboardItemType.CHART, true, 20l, 5, "TRX210 ISSUING MA per member - MCI On Us"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("TRX001 ISSUING Settlement advisement", DashboardItemType.CHART, true, 1l, 0, "TRX001 ISSUING Settlement advisement"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("TRX200 ISSUING MA per bank - Maestro", DashboardItemType.CHART, true, 10l, 3, "TRX200 ISSUING MA per bank - Maestro"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("TRX205 ISSUING MA per member - MCI Off Us", DashboardItemType.CHART, true, 19l, 4, "TRX205 ISSUING MA per member - MCI Off Us"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("TRX300 ISSUING - REC MST per account", DashboardItemType.CHART, true, 21l, 6, "TRX300 ISSUING - REC MST per account"));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 2, ""));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 8, ""));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 1, ""));
		item.getItemsListChangeHandler().addNew(buildDashboardItem("", DashboardItemType.EMPTY_TYPE, false, null, 7, ""));
		return item;
	}

	
	public static BGroup buildBGroup(String nameGroup, java.sql.Timestamp modificationDate, java.sql.Timestamp creationDate) {
		BGroup group = new BGroup();
		group.setName(nameGroup);
		group.setVisibleInShortcut(true);
		group.setModificationDate(modificationDate);
		group.setCreationDate(creationDate);
		return group;
		
	}
	
	public static DashboardItem buildDashboardItem(String nameDashboardItem,DashboardItemType dashboardItemType,boolean visible,Long itemId,int position,String itemName) {
		DashboardItem dashboardItem = new DashboardItem();
		dashboardItem.setName(nameDashboardItem);
		dashboardItem.setItemType(dashboardItemType);
		dashboardItem.setItemId(itemId);
		dashboardItem.setPosition(position);
		dashboardItem.setItemName(itemName);
		dashboardItem.setShowTitleBar(true);
		dashboardItem.setVisible(visible);
		dashboardItem.setShowBorder(true);
		dashboardItem.setNewTab(false);
		return dashboardItem;
	}

}
