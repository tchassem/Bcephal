using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class ReconciliationPrivilegeObserver
    {
        private readonly AppState AppState;
        public ReconciliationPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsReconciliation(uri))
            {
                return HasPrivilegeReconciliation(uri);
            }
            return false;
        }

        public bool IsReconciliation(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.Equals(Route.RECONCILIATION_FILTER_FORM)
                 || uri.Equals(Route.RECONCILIATION_FILTER_BROWSER)
                 || uri.StartsWith(Route.RECONCILIATION_AUTO_FORM)
                 || uri.StartsWith(Route.RECONCILIATION_AUTO_BROWSER)
                 || uri.Equals(Route.RECONCILIATION_SCHEDULED_AUTO_BROWSER));
        }

        private bool HasPrivilegeReconciliation(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.ReconciliationAllowed)
                {
                    if (CanCreate(uri))
                    {
                        if (Route.RECONCILIATION_FILTER_FORM.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationFilterCreateAllowed;
                        }
                        else  if(uri.StartsWith(Route.RECONCILIATION_AUTO_FORM))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed;
                        }
                    }
                    else
                    {
                        if (Route.RECONCILIATION_FILTER_BROWSER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationFilterEditAllowed || this.AppState.PrivilegeObserver.ReconciliationFilterViewAllowed || this.AppState.PrivilegeObserver.ReconciliationFilterCreateAllowed;
                        }
                        else if(Route.RECONCILIATION_AUTO_BROWSER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationAutoRecoEditAllowed || this.AppState.PrivilegeObserver.ReconciliationAutoRecoViewAllowed || this.AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed;
                        }
                        else if(Route.RECONCILIATION_SCHEDULED_AUTO_BROWSER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationAutoRecoSchedulerEditAllowed || this.AppState.PrivilegeObserver.ReconciliationAutoRecoSchedulerViewAllowed || this.AppState.PrivilegeObserver.ReconciliationAutoRecoSchedulerCreateAllowed;
                        }else if (uri.StartsWith(Route.RECONCILIATION_FILTER_FORM))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationFilterEditAllowed;
                        }
                        else if (uri.StartsWith(Route.RECONCILIATION_AUTO_FORM))
                        {
                            return this.AppState.PrivilegeObserver.ReconciliationAutoRecoEditAllowed || this.AppState.PrivilegeObserver.ReconciliationAutoRecoViewAllowed || this.AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed;
                        }
                    }
                   
                }
            }
            return false;
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri)
                && uri.Equals(Route.RECONCILIATION_FILTER_FORM)
                || uri.Equals(Route.RECONCILIATION_AUTO_FORM);
        }
    }
}
