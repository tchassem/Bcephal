using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class DashboardPrivilegeObserver
    {
        private readonly AppState AppState;

        public DashboardPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }

        public bool HasPrivilege(string uri)
        {
            if (IsDashboard(uri))
            {
                return HasPrivilegeDashboard(uri);
            }
            return false;
        }

        public bool IsDashboard(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.StartsWith(Route.EDIT_REPORT_DASHBOARD)
                 || uri.Equals(Route.BROWSER_REPORT_DASHBOARD)
                 || uri.StartsWith(Route.EDIT_REPORT_ALARM)
                 || uri.Equals(Route.BROWSER_REPORT_ALARM))
                 || uri.Equals(Route.BROWSER_REPORT_SCHEDULED_ALARM);
        }

        private bool HasPrivilegeDashboard(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.DashboardingAllowed)
                {
                    if (CanCreate(uri))
                    {
                        if (Route.EDIT_REPORT_DASHBOARD.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DashboardingDashboardCreateAllowed && this.AppState.PrivilegeObserver.DashboardingCreateAllowed;
                        }
                        else if(Route.EDIT_REPORT_ALARM.Equals(uri))
                        {
                            return (this.AppState.PrivilegeObserver.DashboardingAlarmCreateAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmSchedulerCreateAllowed)
                                && this.AppState.PrivilegeObserver.DashboardingCreateAllowed;
                        }
                       
                    }
                    else
                    {
                        if (Route.BROWSER_REPORT_DASHBOARD.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DashboardingDashboardEditAllowed || this.AppState.PrivilegeObserver.DashboardingDashboardViewAllowed || this.AppState.PrivilegeObserver.DashboardingDashboardCreateAllowed;
                        }
                        else if(Route.BROWSER_REPORT_ALARM.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DashboardingAlarmEditAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmViewAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmCreateAllowed;
                        }
                        else if(Route.BROWSER_REPORT_SCHEDULED_ALARM.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DashboardingAlarmSchedulerEditAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmViewAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmCreateAllowed;
                        }else                        
                        if (uri.StartsWith(Route.EDIT_REPORT_DASHBOARD))
                        {
                            return this.AppState.PrivilegeObserver.DashboardingDashboardEditAllowed || this.AppState.PrivilegeObserver.DashboardingDashboardViewAllowed  || this.AppState.PrivilegeObserver.DashboardingDashboardCreateAllowed;
                        }
                        else if (uri.StartsWith(Route.EDIT_REPORT_ALARM))
                        {
                            return this.AppState.PrivilegeObserver.DashboardingAlarmEditAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmViewAllowed || this.AppState.PrivilegeObserver.DashboardingAlarmCreateAllowed;
                                
                        }
                    }                   
                }
            }
            return false;
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) && 
                uri.Equals(Route.EDIT_REPORT_DASHBOARD)
                || uri.Equals(Route.EDIT_REPORT_ALARM);
        }
    }
}
