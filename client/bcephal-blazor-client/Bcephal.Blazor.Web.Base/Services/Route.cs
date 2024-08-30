using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class Route
    {
        public static string ABOUT = "about/";
        public static string INDEX = "index/";
        public static string PROJECT_BROWSER = "project-browser/";
        public static string PROJECT = "projects/";
        public static string HOME_PAGE = "home/";
        public static string FUNCTIONALITIES = "functionalities/";
        public static string DASHBOARD = "dashboard/";
        public static string MODEL = "model/";
        public static string EDIT_GRID = "edit-grille/";
        public static string BILLING_TEMPLATE = "billing-template/";
        public static string RECONCILIATION_LOG = "reconcialiation/log";
        public static string BROWSER_GRID = "browser-grille/";
        public static string EDIT_PERIOD = "period/filter";
        public static string EDIT_MEASURE = "measure/filter";
        public static string EDIT_ACCESS_RIGHT = "attribute/filter";
        public static string LIST_FILES_LOADER = "projects/file-loaders";
        public static string NEW_DYNAMIC_FORM = "dynamic-form";
        public static string List_DYNAMIC_BROWSER = "dynamic-browser";
        public static string NEW_LOAD_FILE = "new-load-file/";
        public static string BILLING_RUN_OUTCOME = "billing/run-outcome/";
        public static string BILLING_TEMPLATE_LIST = "template/list";
        public static string LIST_INVOICE = "billing/list-invoice/";
        public static string LIST_CREDIT_NOTE = "billing/list-credit-note/";
        public static string BILLING_MODEL_BROWSER = "billing/model-browser/";
        public static string BILLING_MODEL_FORM = "billing/model-form/";
        public static string BILLING_MODEL_INVOICE = "billing/invoice/edit/";
        public static string SCHEDULED_MODEL_BROWSER = "billing/scheduled-browser/";
        public static string RECONCILIATION_AUTO_FORM = "reconciliation/auto-reco/";
        public static string RECONCILIATION_AUTO_BROWSER = "reconciliation/auto-reco-browser/";
        public static string CURRENT_BILLING_RUN_STATUS = "billing/current-run-status/";
        public static string BILLING_EVENT_REPOSITORY = "billing-event-repository/";
        public static string CLIENT_REPOSITORY = "client-repository/";
        public static string BILLING_JOIN = "billing-join/";
        public static string SETTINGS_CONFIGURATION = "settings/configuration/";
        public static string EDIT_REPORTING_GRID = "edit-reporting-grid/";
        public static string BROWSER_REPORTING_GRID = "browser-reporting-grid/"; 
        public static string BROWSER_REPORT = "reporting/report/list";
        public static string BROWSER_REPORT_PUBLICATION = "reporting/report/publication/list";

        public static string RECONCILIATION_FILTER_FORM = "reconciliation-filter-form/";
        public static string RECONCILIATION_RUN_AUTO = "reconciliation-run-auto/";
        public static string RECONCILIATION_SCHEDULED_AUTO_BROWSER = "reconciliation-scheduled-auto-reco/";
        public static string RECONCILIATION_SCHEDULED_LOG_AUTO_BROWSER = "reconciliation-scheduled-auto-reco-log/";
        public static string RECONCILIATION_AUTO_RECO_LOG = "reconciliation-auto-reco-log/";
        public static string RECONCILIATION_FILTER_BROWSER = "reconciliation-filter-browser/";
        public static string BROWSER_REPORTING_CHART = "browser-reporting-chart/";
        public static string EDIT_REPORTING_CHART = "edit-reporting-chart/";
        public static string EDIT_REPORTING_JOIN_GRID = "reporting-join-grid/edit";
        public static string BROWSER_REPORTING_JOIN_GRID = "reporting-join-grid/list";
        public static string EDIT_REPORTING_JOIN = "reporting-join/edit";
        public static string BROWSER_REPORTING_JOIN = "reporting-join/list";
        public static string EDIT_REPORT_DASHBOARD = "edit-report-dashboard/";
        public static string BROWSER_REPORT_DASHBOARD = "browser-report-dashboard/";
        public static string LOAD_LOAD_TABLE = "load/load-table";
        public static string LOAD_CLEAR_TABLE = "load/clear-table";
        public static string EDIT_REPORT_ALARM = "edit-alarm/";
        public static string BROWSER_REPORT_ALARM = "browser-report-alarm/";
        public static string BROWSER_REPORT_SCHEDULED_ALARM = "browser-report-scheduled-alarm/";
        public static string BROWSER_FILES_LOADER_LOGS = "browser-files-loader-logs/";
        public static string BROWSER_SCHEDULED_FILES_LOADER = "browser-scheduled-files-loader/";
        public static string BROWSER_SCHEDULED_LOG_FILES_LOADER = "browser-scheduled-files-loader-log/";
        public static string EDIT_TRANSFORMATION_ROUTINE = "edit-transformation-routine/";
        public static string BROWSER_TRANSFORMATION_ROUTINE = "browser-transformation-routine/";
        public static string BROWSER_SCHEDULED_TRANSFORMATION_TREE = "browser-scheduler-transformation-tree/";
        public static string BROWSER_SCHEDULED_ROUTINE = "browser-scheduler-routine/";
        public static string BROWSER_SCHEDULED_BOOKING = "browser-scheduler-booking/";

        public static string TEST_GRID_LAYOUT = "test/grid-layout/";

        public static string EDIT_INCREMENTAL_NUMBER = "edit-incremental-number/";
        public static string BROWSER_INCREMENTAL_NUMBER = "browser-incremental-numbers/";
        public static string EDIT_SPOT = "edit-spot";
        public static string BROWSER_SPOT = "browser-spot/";

        public static string BROWSER_REPORT_PIVOT_TABLE = "browser-report-pivot-table/";
        public static string EDIT_REPORT_PIVOT_TABLE = "edit-report-pivot-table/";

        public static string BROWSER_SPREAD_SHEET = "spread-sheet-browser/";
        public static string EDIT_ARCHIVE_LOG = "archive-log-form/";
        public static string BROWSER_ARCHIVE_LOG = "archive-log-browser/";
        public static string ARCHIVE_BROWSER = "archive-browser/";
        public static string ARCHIVE_CONFIGURATION_BROWSER = "archive-configuration-browser/";
        public static string CLIENT_FORM = "client-form/";
        public static string BROWSER_CLIENT = "client-browser/";

        public static string MEASURE_TREEVIEW = "edit-measure/";
        public static string PERIOD_TREEVIEW = "edit-period/";
        public static string MODEL_TREEVIEW = "edit-model/";
        public static string EDIT_ARCHIVE_CONFIGURATION = "edit-archive-config/";
        
        public static string BROWSER_BOOKING_MODEL = "browser-booking-model/";
        public static string EDIT_BOOKING_MODEL = "booking-model-form/";
        public static string BROWSER_BOOKING_MODEL_LOG = "browser-booking-model-log/";
        public static string EDIT_BOOKING_MODEL_LOG = "edit-booking-model-log/";
        public static string USER_FORM = "user-form/";
        public static string BROWSER_USER = "user-browser/";
        public static string PROFIL_LIST = "profil/list/";
        public static string PROFIL_EDIT = "profil/edit/";
        public static string ACCESS_RIGHTS = "access-rights/";

        public static string INPUT_SPREADSHEET = "input/spreadsheet/";
        public static string EXPORT_PROJECT = "export-cur-project/";
        public static string POSTING_LIST = "posting/list/";
        public static string POSTING_EDIT = "posting/edit/";
        public static string POSTING_REPO = "posting/repositories/";

        public static string PROJECT_BACKUP_BROWSER = "projects/archives-browser/";
        public static string BROWSER_SMS = "browser-sms/";
        public static string BROWSER_EMAIL = "browser-email/";
        public static string BROWSER_MESSAGE_LOG = "message-log-browser/";
        public static string EDIT_MESSAGE_TEST = "message-test-form/";
        public static string ADMIN_SCHEDULER_BROWSER = "admin-scheduler-browser/";

        public static string BROWSER_JOIN_LOG = "browser-join-grid-log/";

        public static string SCHEDULED_PLANNER_BROWSER = "scheduler/planner-browser/";
        public static string SCHEDULED_PLANNER = "scheduler/planner/";
        public static string SCHEDULED_PLANNER_LOGS = "scheduler-planner/logs/";
        public static string SCHEDULED_PLANNER_OPERATION = "scheduler/planner-operation/";
        public static string FILE_MANAGER_BROWSER = "file-manager/";
    }
}
