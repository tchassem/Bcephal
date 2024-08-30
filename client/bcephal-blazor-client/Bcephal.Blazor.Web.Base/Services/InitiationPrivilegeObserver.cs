using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    class InitiationPrivilegeObserver
    {
        private readonly AppState AppState;
        public InitiationPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsInitiation(uri))
            {
                return HasPrivilegeInitiation(uri);
            }
            return false;
        }

        private bool HasPrivilegeInitiation(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.InitiationAllowed)
                {
                    if (Route.MEASURE_TREEVIEW.Equals(uri))
                    {
                        return this.AppState.PrivilegeObserver.InitiationMeasureAllowed;
                    }
                    else if (Route.PERIOD_TREEVIEW.Equals(uri))
                    {
                        return this.AppState.PrivilegeObserver.InitiationPeriodAllowed;
                    }
                    else if (Route.MODEL_TREEVIEW.Equals(uri))
                    {
                        return this.AppState.PrivilegeObserver.InitiationModelAllowed;
                    }
                }
            }
            return false;
        }

        public bool IsInitiation(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) && (uri.Equals(Route.MEASURE_TREEVIEW) || uri.Equals(Route.PERIOD_TREEVIEW) || uri.Equals(Route.MODEL_TREEVIEW));
        }
    }
}
