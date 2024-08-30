using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class DashboardItemType
    {
        public static DashboardItemType EMPTY_TYPE = new DashboardItemType("EMPTY_TYPE", "Empty type");

        public static DashboardItemType CHART = new DashboardItemType("CHART", "Charts");
        public static DashboardItemType DRILL_DOWN_GRID = new DashboardItemType("DRILL_DOWN_GRID", "Drill down grids");
        public static DashboardItemType PIVOT_TABLE = new DashboardItemType("PIVOT_TABLE", "Pivot tables");
        public static DashboardItemType REPORT_GRID = new DashboardItemType("REPORT_GRID", "Report Grids");


        public static DashboardItemType PROJECT_BROWSER = new DashboardItemType("PROJECT_BROWSER", "Project list");
        public static DashboardItemType PROJECT_BACKUP_BROWSER = new DashboardItemType("PROJECT_BACKUP_BROWSER", "Project list backup");
        public static DashboardItemType INPUT_GRID_BROWSER = new DashboardItemType("INPUT_GRID_BROWSER", "Input grids list");
        public static DashboardItemType REPORT_GRID_BROWSER = new DashboardItemType("REPORT_GRID_BROWSER", "Report Grids list");
        public static DashboardItemType TRANSFORMATION_TREE_BROWSER = new DashboardItemType("TRANSFORMATION_TREE_BROWSER", "Tranformation tree list");
        public static DashboardItemType FORM_BROWSER = new DashboardItemType("FORM_BROWSER", "Form list");
        public static DashboardItemType LOADER_BROWSER = new DashboardItemType("LOADER_BROWSER", "File Loader list");
        public static DashboardItemType BROWSER_SPOT = new DashboardItemType("BROWSER_SPOT", "List spot");
        public static DashboardItemType AUTO_RECO_BROWSER = new DashboardItemType("AUTO_RECO_BROWSER", "Auto reco list");
        public static DashboardItemType MANUAL_RECO_BROWSER = new DashboardItemType("MANUAL_RECO_BROWSER", "Manual reco list");
        public static DashboardItemType BILLING_MODEL_BROWSER = new DashboardItemType("BILLING_MODEL_BROWSER", "billing model list");
        public static DashboardItemType BOOKING_BROWSER = new DashboardItemType("BOOKING_BROWSER", "Booking list");
        public static DashboardItemType DASHBOARD_HOME_PAGE = new DashboardItemType("DASHBOARD_HOME_PAGE", "Dashboard home page");
        public static DashboardItemType DASHBOARD_BROWSER = new DashboardItemType("DASHBOARD_BROWSER", "Dashboard list");
        public static DashboardItemType HOME_PAGE = new DashboardItemType("HOME_PAGE", "Home page");
        public static DashboardItemType PIVOT_TABLE_BROWSER = new DashboardItemType("PIVOT_TABLE_BROWSER", "Pivot table list");
        public static DashboardItemType CHART_BROWSER = new DashboardItemType("CHART_BROWSER", "Chart list");
        public static DashboardItemType BROWSER_TRANSFORMATION_ROUTINE = new DashboardItemType("BROWSER_TRANSFORMATION_ROUTINE", "Transformation routine list");
        
        public static DashboardItemType BROWSER_SCHEDULED_TRANSFORMATION_TREE = new DashboardItemType("BROWSER_SCHEDULED_TRANSFORMATION_TREE", "Transformation scheduled tree list");
        public static DashboardItemType BROWSER_SCHEDULED_ROUTINE = new DashboardItemType("BROWSER_SCHEDULED_ROUTINE", "Transformation routine scheduled list");
        public static DashboardItemType BROWSER_REPORT_PUBLICATION = new DashboardItemType("BROWSER_REPORT_PUBLICATION", "Report publication list");
        public static DashboardItemType BROWSER_REPORTING_JOIN = new DashboardItemType("BROWSER_REPORTING_JOIN", "Reporting join list");
        public static DashboardItemType BROWSER_REPORTING_JOIN_LOG = new DashboardItemType("BROWSER_REPORTING_JOIN_LOG", "Join log list");
        public static DashboardItemType BROWSER_REPORT_ALARM = new DashboardItemType("BROWSER_REPORT_ALARM", "Report alarm list");
        public static DashboardItemType BROWSER_REPORT_SCHEDULED_ALARM = new DashboardItemType("BROWSER_REPORT_SCHEDULED_ALARM", "Report scheduled alarm list");
        //public static DashboardItemType RECONCILIATION_FILTER_BROWSER = new DashboardItemType("RECONCILIATION_FILTER_BROWSER", "Reconciliation filter list");
        //public static DashboardItemType RECONCILIATION_AUTO_BROWSER = new DashboardItemType("RECONCILIATION_AUTO_BROWSER", "Reconciliation auto list");
        public static DashboardItemType SCHEDULED_MODEL_BROWSER = new DashboardItemType("SCHEDULED_MODEL_BROWSER", "Billing model scheduled list");
        public static DashboardItemType BILLING_TEMPLATE_LIST = new DashboardItemType("BILLING_TEMPLATE_LIST", "Billing template list");
        public static DashboardItemType LIST_INVOICE = new DashboardItemType("LIST_INVOICE", "Invoice list");
        public static DashboardItemType LIST_CREDIT_NOTE = new DashboardItemType("LIST_CREDIT_NOTE", "Credit note list");
        public static DashboardItemType POSTING_LIST = new DashboardItemType("POSTING_LIST", "Posting list");

        public static DashboardItemType BROWSER_BOOKING_MODEL = new DashboardItemType("BROWSER_BOOKING_MODEL", "Booking model list");
        public static DashboardItemType BROWSER_SCHEDULED_BOOKING = new DashboardItemType("BROWSER_SCHEDULED_BOOKING", "Scheduled booking list");
        public static DashboardItemType BROWSER_BOOKING_MODEL_LOG = new DashboardItemType("BROWSER_BOOKING_MODEL_LOG", "Booking model log list");
        public static DashboardItemType ARCHIVE_BROWSER = new DashboardItemType("ARCHIVE_BROWSER", "Archive list");
        public static DashboardItemType BROWSER_ARCHIVE_LOG = new DashboardItemType("BROWSER_ARCHIVE_LOG", "Archive log list");
        public static DashboardItemType ARCHIVE_CONFIGURATION_BROWSER = new DashboardItemType("ARCHIVE_CONFIGURATION_BROWSER", "Archive configuration list");
        public static DashboardItemType BROWSER_MESSAGE_LOG = new DashboardItemType("BROWSER_MESSAGE_LOG", "Message log list");
        public static DashboardItemType BROWSER_CLIENT = new DashboardItemType("BROWSER_CLIENT", "Client list");
        public static DashboardItemType BROWSER_USER = new DashboardItemType("BROWSER_USER", "User list");
        public static DashboardItemType PROFIL_LIST = new DashboardItemType("PROFIL_LIST", "Profil list");
        public static DashboardItemType ADMIN_SCHEDULER_BROWSER = new DashboardItemType("ADMIN_SCHEDULER_BROWSER", "Admin scheduler list");
        public static DashboardItemType BROWSER_INCREMENTAL_NUMBER = new DashboardItemType("BROWSER_INCREMENTAL_NUMBER", "Incremental number list");

       
            
        public String Code { get; protected set; }
        public String Label { get; protected set; }

        public DashboardItemType(String code, String Label)
        {
            this.Label = Label;
            this.Code = code;
        }

        public override string ToString()
        {
            return this.Label;
        }

        public bool IsChart()
        {
            return this == DashboardItemType.CHART;
        }

        public bool IsPivotTable()
        {
            return this == DashboardItemType.PIVOT_TABLE;
        }

        public bool IsDrillDownGrid()
        {
            return this == DashboardItemType.DRILL_DOWN_GRID;
        }

        public bool IsGrid()
        {
            return this == DashboardItemType.REPORT_GRID;
        }

        public bool IsProjectBrowser()
        {
            return this == DashboardItemType.PROJECT_BROWSER;
        }
        public bool IsProjectBackupBrowser()
        {
            return this == DashboardItemType.PROJECT_BACKUP_BROWSER;
        }

        public bool IsInputGridBrowser()
        {
            return this == DashboardItemType.INPUT_GRID_BROWSER;
        }

        public bool IsReportGridBrowser()
        {
            return this == DashboardItemType.REPORT_GRID_BROWSER;
        }

        public bool IsTransformationTreeBrowser()
        {
            return this == DashboardItemType.TRANSFORMATION_TREE_BROWSER;
        }

        public bool IsFormBrowser()
        {
            return this == DashboardItemType.FORM_BROWSER;
        }

        public bool IsLoaderBrowser()
        {
            return this == DashboardItemType.LOADER_BROWSER;
        }
        public bool IsSpotBrowser()
        {
            return this == DashboardItemType.BROWSER_SPOT;
        }

        public bool IsAutoRecoBrowser()
        {
            return this == DashboardItemType.AUTO_RECO_BROWSER;
        }

        public bool IsManualRecoBrowser()
        {
            return this == DashboardItemType.MANUAL_RECO_BROWSER;
        }

        public bool IsBillingModelBrowser()
        {
            return this == DashboardItemType.BILLING_MODEL_BROWSER;
        }
        public bool IsBookingBrowser()
        {
            return this == DashboardItemType.BOOKING_BROWSER;
        }
        public bool IsDashboardHomePage()
        {
            return this == DashboardItemType.DASHBOARD_HOME_PAGE;
        }
        public bool IsHomePage()
        {
            return this == DashboardItemType.HOME_PAGE;
        }

        public bool IsDashboardBrowser() => this == DashboardItemType.DASHBOARD_BROWSER;
        public bool IsPivotTableBrowser() => this == DashboardItemType.PIVOT_TABLE_BROWSER;      
        public bool IsChartBrowser() => this == DashboardItemType.CHART_BROWSER;
        public bool IsEmptyType() => this == DashboardItemType.EMPTY_TYPE;
        public bool IsBrowserScheduledTransformationTreeType() => this == DashboardItemType.BROWSER_SCHEDULED_TRANSFORMATION_TREE;
        public bool IsBrowserTransformationRoutine() => this == DashboardItemType.BROWSER_TRANSFORMATION_ROUTINE;
        public bool IsBrowserScheduledTransformationRoutine() => this == DashboardItemType.BROWSER_SCHEDULED_ROUTINE;
        public bool IsBrowserReportPublication() => this == DashboardItemType.BROWSER_REPORT_PUBLICATION; 
        public bool IsBrowserReportJoin() => this == DashboardItemType.BROWSER_REPORTING_JOIN;
        public bool IsBrowserJoinLog() => this == DashboardItemType.BROWSER_REPORTING_JOIN_LOG;
        public bool IsBrowserReportAlarm() => this == DashboardItemType.BROWSER_REPORT_ALARM;
        public bool IsBrowserReportScheduledAlarm() => this == DashboardItemType.BROWSER_REPORT_SCHEDULED_ALARM;
        //public bool IsBrowserReconciliationFilter() => this == DashboardItemType.RECONCILIATION_FILTER_BROWSER;
        //public bool IsBrowserReconciliationAuto() => this == DashboardItemType.RECONCILIATION_AUTO_BROWSER;
        public bool IsBrowserScheduledBillingModel() => this == DashboardItemType.SCHEDULED_MODEL_BROWSER;
        public bool IsBrowserBillingTemplate() => this == DashboardItemType.BILLING_TEMPLATE_LIST;
        public bool IsBrowserInvoice() => this == DashboardItemType.LIST_INVOICE;
        public bool IsBrowserCreditNote() => this == DashboardItemType.LIST_CREDIT_NOTE;
        public bool IsBrowserPosting() => this == DashboardItemType.POSTING_LIST;

        public bool IsBrowserBookingModel() => this == DashboardItemType.BROWSER_BOOKING_MODEL;
        public bool IsBrowserScheduledBooking() => this == DashboardItemType.BROWSER_SCHEDULED_BOOKING;
        public bool IsBrowserBookingModelLog() => this == DashboardItemType.BROWSER_BOOKING_MODEL_LOG;
        public bool IsBrowserArchive() => this == DashboardItemType.ARCHIVE_BROWSER;
        public bool IsBrowserArchiveLog() => this == DashboardItemType.BROWSER_ARCHIVE_LOG;
        public bool IsBrowserArchiveConfiguration() => this == DashboardItemType.ARCHIVE_CONFIGURATION_BROWSER;
        public bool IsBrowserMessageLog() => this == DashboardItemType.BROWSER_MESSAGE_LOG;
        public bool IsBrowserClient() => this == DashboardItemType.BROWSER_CLIENT;
        public bool IsBrowserUser() => this == DashboardItemType.BROWSER_USER;
        public bool IsBrowserProfil() => this == DashboardItemType.PROFIL_LIST;
       public bool IsBrowserAdminScheduler() => this == DashboardItemType.ADMIN_SCHEDULER_BROWSER;
        public bool IsBrowserIncrementalNumber() => this == DashboardItemType.BROWSER_INCREMENTAL_NUMBER;




        public static DashboardItemType GetByCode(String code)
        {
            if (DashboardItemType.CHART.Code.Equals(code)) return DashboardItemType.CHART;
            if (DashboardItemType.PIVOT_TABLE.Code.Equals(code)) return DashboardItemType.PIVOT_TABLE;
            if (DashboardItemType.DRILL_DOWN_GRID.Code.Equals(code)) return DashboardItemType.DRILL_DOWN_GRID;
            if (DashboardItemType.REPORT_GRID.Code.Equals(code)) return DashboardItemType.REPORT_GRID;

            if (DashboardItemType.PROJECT_BROWSER.Code.Equals(code)) return DashboardItemType.PROJECT_BROWSER;
            if (DashboardItemType.PROJECT_BACKUP_BROWSER.Code.Equals(code)) return DashboardItemType.PROJECT_BACKUP_BROWSER;
            if (DashboardItemType.INPUT_GRID_BROWSER.Code.Equals(code)) return DashboardItemType.INPUT_GRID_BROWSER;
            if (DashboardItemType.REPORT_GRID_BROWSER.Code.Equals(code)) return DashboardItemType.REPORT_GRID_BROWSER;
            if (DashboardItemType.TRANSFORMATION_TREE_BROWSER.Code.Equals(code)) return DashboardItemType.TRANSFORMATION_TREE_BROWSER;
            if (DashboardItemType.FORM_BROWSER.Code.Equals(code)) return DashboardItemType.FORM_BROWSER;
            if (DashboardItemType.LOADER_BROWSER.Code.Equals(code)) return DashboardItemType.LOADER_BROWSER;
            if (DashboardItemType.BROWSER_SPOT.Code.Equals(code)) return DashboardItemType.BROWSER_SPOT;
            if (DashboardItemType.AUTO_RECO_BROWSER.Code.Equals(code)) return DashboardItemType.AUTO_RECO_BROWSER;
            if (DashboardItemType.MANUAL_RECO_BROWSER.Code.Equals(code)) return DashboardItemType.MANUAL_RECO_BROWSER;
            if (DashboardItemType.BILLING_MODEL_BROWSER.Code.Equals(code)) return DashboardItemType.BILLING_MODEL_BROWSER;
            if (DashboardItemType.BOOKING_BROWSER.Code.Equals(code)) return DashboardItemType.BOOKING_BROWSER;
            if (DashboardItemType.DASHBOARD_HOME_PAGE.Code.Equals(code)) return DashboardItemType.DASHBOARD_HOME_PAGE;
            if (DashboardItemType.HOME_PAGE.Code.Equals(code)) return DashboardItemType.HOME_PAGE;
            if (DashboardItemType.DASHBOARD_BROWSER.Code.Equals(code)) return DashboardItemType.DASHBOARD_BROWSER;
            if (DashboardItemType.PIVOT_TABLE_BROWSER.Code.Equals(code)) return DashboardItemType.PIVOT_TABLE_BROWSER;
            if (DashboardItemType.CHART_BROWSER.Code.Equals(code)) return DashboardItemType.CHART_BROWSER;
            if (DashboardItemType.EMPTY_TYPE.Code.Equals(code)) return DashboardItemType.EMPTY_TYPE;
            if (DashboardItemType.BROWSER_SCHEDULED_TRANSFORMATION_TREE.Code.Equals(code)) return DashboardItemType.BROWSER_SCHEDULED_TRANSFORMATION_TREE;
            if (DashboardItemType.BROWSER_TRANSFORMATION_ROUTINE.Code.Equals(code)) return DashboardItemType.BROWSER_TRANSFORMATION_ROUTINE;
            if (DashboardItemType.BROWSER_SCHEDULED_ROUTINE.Code.Equals(code)) return DashboardItemType.BROWSER_SCHEDULED_ROUTINE;
            if (DashboardItemType.BROWSER_REPORT_PUBLICATION.Code.Equals(code)) return DashboardItemType.BROWSER_REPORT_PUBLICATION;
            if (DashboardItemType.BROWSER_REPORTING_JOIN.Code.Equals(code)) return DashboardItemType.BROWSER_REPORTING_JOIN;
            if (DashboardItemType.BROWSER_REPORTING_JOIN_LOG.Code.Equals(code)) return DashboardItemType.BROWSER_REPORTING_JOIN_LOG;
            if (DashboardItemType.BROWSER_REPORT_ALARM.Code.Equals(code)) return DashboardItemType.BROWSER_REPORT_ALARM;
            if (DashboardItemType.BROWSER_REPORT_SCHEDULED_ALARM.Code.Equals(code)) return DashboardItemType.BROWSER_REPORT_SCHEDULED_ALARM;
            //if (DashboardItemType.RECONCILIATION_FILTER_BROWSER.Code.Equals(code)) return DashboardItemType.RECONCILIATION_FILTER_BROWSER;
            //if (DashboardItemType.RECONCILIATION_AUTO_BROWSER.Code.Equals(code)) return DashboardItemType.RECONCILIATION_AUTO_BROWSER;
            if (DashboardItemType.SCHEDULED_MODEL_BROWSER.Code.Equals(code)) return DashboardItemType.SCHEDULED_MODEL_BROWSER;
            if (DashboardItemType.BILLING_TEMPLATE_LIST.Code.Equals(code)) return DashboardItemType.BILLING_TEMPLATE_LIST;
            if (DashboardItemType.LIST_INVOICE.Code.Equals(code)) return DashboardItemType.LIST_INVOICE;
            if (DashboardItemType.LIST_CREDIT_NOTE.Code.Equals(code)) return DashboardItemType.LIST_CREDIT_NOTE;
            if (DashboardItemType.POSTING_LIST.Code.Equals(code)) return DashboardItemType.POSTING_LIST;

            if (DashboardItemType.BROWSER_BOOKING_MODEL.Code.Equals(code)) return DashboardItemType.BROWSER_BOOKING_MODEL;
            if (DashboardItemType.BROWSER_SCHEDULED_BOOKING.Code.Equals(code)) return DashboardItemType.BROWSER_SCHEDULED_BOOKING;
            if (DashboardItemType.BROWSER_BOOKING_MODEL_LOG.Code.Equals(code)) return DashboardItemType.BROWSER_BOOKING_MODEL_LOG;
            if (DashboardItemType.ARCHIVE_BROWSER.Code.Equals(code)) return DashboardItemType.ARCHIVE_BROWSER;
            if (DashboardItemType.BROWSER_ARCHIVE_LOG.Code.Equals(code)) return DashboardItemType.BROWSER_ARCHIVE_LOG;
            if (DashboardItemType.ARCHIVE_CONFIGURATION_BROWSER.Code.Equals(code)) return DashboardItemType.ARCHIVE_CONFIGURATION_BROWSER;
            if (DashboardItemType.BROWSER_MESSAGE_LOG.Code.Equals(code)) return DashboardItemType.BROWSER_MESSAGE_LOG;
            if (DashboardItemType.BROWSER_CLIENT.Code.Equals(code)) return DashboardItemType.BROWSER_CLIENT;
            if (DashboardItemType.PROFIL_LIST.Code.Equals(code)) return DashboardItemType.PROFIL_LIST;
            if (DashboardItemType.ADMIN_SCHEDULER_BROWSER.Code.Equals(code)) return DashboardItemType.ADMIN_SCHEDULER_BROWSER;
            if (DashboardItemType.BROWSER_INCREMENTAL_NUMBER.Code.Equals(code)) return DashboardItemType.BROWSER_INCREMENTAL_NUMBER;
            if (DashboardItemType.BROWSER_USER.Code.Equals(code)) return DashboardItemType.BROWSER_USER;
            return null;
        }
    }
}