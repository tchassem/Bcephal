using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    class ReportingPrivilegeObserver
    {
        private readonly AppState AppState;
        public ReportingPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsReporting(uri))
            {
                return HasPrivilegeReporting(uri);
            }
            return false;
        }

        private bool HasPrivilegeReporting(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.ReportingAllowed)
                {
                    if (CanCreate(uri))
                    {
                        if (Route.EDIT_REPORTING_GRID.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingReportGridCreateAllowed;
                        }
                        else if (Route.EDIT_REPORT_PIVOT_TABLE.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingPivotTableCreateAllowed;
                        }
                        else if (Route.EDIT_REPORTING_CHART.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingChartCreateAllowed;
                        }
                        else if (Route.EDIT_REPORTING_JOIN_GRID.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingJoinGridCreateAllowed;
                        }
                    }
                    else
                    {
                        if (Route.BROWSER_REPORTING_GRID.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingReportGridEditAllowed || this.AppState.PrivilegeObserver.ReportingReportGridCreateAllowed || this.AppState.PrivilegeObserver.ReportingReportGridViewAllowed;
                        }
                        else if(Route.BROWSER_REPORT_PIVOT_TABLE.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingPivotTableEditAllowed || this.AppState.PrivilegeObserver.ReportingPivotTableCreateAllowed  || this.AppState.PrivilegeObserver.ReportingPivotTableViewAllowed;
                        }
                        else if(Route.BROWSER_REPORTING_CHART.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingChartEditAllowed || this.AppState.PrivilegeObserver.ReportingChartCreateAllowed || this.AppState.PrivilegeObserver.ReportingChartViewAllowed;
                        }
                        else if(Route.BROWSER_REPORTING_JOIN_GRID.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReportingJoinGridEditAllowed || this.AppState.PrivilegeObserver.ReportingJoinGridCreateAllowed  || this.AppState.PrivilegeObserver.ReportingJoinGridViewAllowed;
                        }
                        if (uri.StartsWith(Route.EDIT_REPORTING_GRID))
                        {
                            return this.AppState.PrivilegeObserver.ReportingReportGridEditAllowed || this.AppState.PrivilegeObserver.ReportingReportGridCreateAllowed || this.AppState.PrivilegeObserver.ReportingReportGridViewAllowed;
                        }
                        else
                      if (uri.StartsWith(Route.EDIT_REPORT_PIVOT_TABLE))
                        {
                            return this.AppState.PrivilegeObserver.ReportingPivotTableEditAllowed || this.AppState.PrivilegeObserver.ReportingPivotTableCreateAllowed || this.AppState.PrivilegeObserver.ReportingPivotTableViewAllowed;
                        }
                        else
                      if (uri.StartsWith(Route.EDIT_REPORTING_CHART))
                        {
                            return this.AppState.PrivilegeObserver.ReportingChartEditAllowed || this.AppState.PrivilegeObserver.ReportingChartCreateAllowed || this.AppState.PrivilegeObserver.ReportingChartViewAllowed;
                        }
                        else
                      if (uri.StartsWith(Route.EDIT_REPORTING_JOIN_GRID))
                        {
                            return this.AppState.PrivilegeObserver.ReportingJoinGridEditAllowed || this.AppState.PrivilegeObserver.ReportingJoinGridCreateAllowed || this.AppState.PrivilegeObserver.ReportingJoinGridViewAllowed;
                        }
                    }
                   
                }
            }
            return false;
        }

        public bool IsReporting(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.StartsWith(Route.EDIT_REPORTING_GRID)
                 || uri.Equals(Route.BROWSER_REPORTING_GRID)
                 || uri.StartsWith(Route.EDIT_REPORT_PIVOT_TABLE)
                 || uri.Equals(Route.BROWSER_REPORT_PIVOT_TABLE)
                 || uri.StartsWith(Route.EDIT_REPORTING_CHART)
                 || uri.Equals(Route.BROWSER_REPORTING_CHART)
                 || uri.StartsWith(Route.EDIT_REPORTING_JOIN_GRID)
                 || uri.Equals(Route.BROWSER_REPORTING_JOIN_GRID));
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.Equals(Route.EDIT_REPORTING_GRID)
                 || uri.Equals(Route.EDIT_REPORT_PIVOT_TABLE)
                 || uri.Equals(Route.EDIT_REPORTING_CHART)
                 || uri.Equals(Route.EDIT_REPORTING_JOIN_GRID));
        }
    }
}
