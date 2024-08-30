using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    class TransformationPrivilegeObserver
    {
        private readonly AppState AppState;
        public TransformationPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsTransformation(uri))
            {
                return HasPrivilegeTransformation(uri);
            }
            return false;
        }

        private bool HasPrivilegeTransformation(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.TransformationAllowed)
                {
                    if (CanCreate(uri))
                    {
                        if (Route.EDIT_TRANSFORMATION_ROUTINE.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.TransformationRoutineCreateAllowed;
                        }
                    }
                    else
                    {
                        if (Route.BROWSER_TRANSFORMATION_ROUTINE.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.TransformationRoutineEditAllowed || this.AppState.PrivilegeObserver.TransformationRoutineViewAllowed || this.AppState.PrivilegeObserver.TransformationRoutineCreateAllowed;
                        }
                        if (Route.BROWSER_SCHEDULED_ROUTINE.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.TransformationRoutineSchedulerEditAllowed || this.AppState.PrivilegeObserver.TransformationRoutineSchedulerViewAllowed || this.AppState.PrivilegeObserver.TransformationRoutineSchedulerCreateAllowed;
                        }
                        else if (uri.StartsWith(Route.EDIT_TRANSFORMATION_ROUTINE))
                        {
                            return this.AppState.PrivilegeObserver.TransformationRoutineEditAllowed || this.AppState.PrivilegeObserver.TransformationRoutineCreateAllowed;
                        }
                       
                    }
                }
            }
            return false;
        }

        public bool IsTransformation(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.StartsWith(Route.EDIT_TRANSFORMATION_ROUTINE)
                 || uri.Equals(Route.BROWSER_TRANSFORMATION_ROUTINE)
                 || uri.Equals(Route.BROWSER_SCHEDULED_ROUTINE));
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) && uri.Equals(Route.EDIT_TRANSFORMATION_ROUTINE);
        }
    }
}
