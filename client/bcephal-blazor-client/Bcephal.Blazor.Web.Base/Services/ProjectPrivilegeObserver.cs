using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class ProjectPrivilegeObserver
    {
        private readonly AppState AppState;

        public ProjectPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }

        public bool HasPrivilege(string uri)
        {
            if (IsProject(uri))
            {
                return HasPrivilegeProject(uri);
            }
            return false;
        }

        private bool HasPrivilegeProject(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.ProjectAllowed)
                {
                    if (Route.PROJECT_BACKUP_BROWSER.Equals(uri))
                    {
                        return this.AppState.PrivilegeObserver.ProjectBackupAllowed;
                    }
                    else
                        if (Route.ACCESS_RIGHTS.Equals(uri))
                    {
                        return this.AppState.PrivilegeObserver.ProjectAccessRightAllowed;
                    }
                    else if (!string.IsNullOrWhiteSpace(uri))
                    {
                        if (uri.StartsWith(Route.PROJECT_BROWSER))
                        {
                            return this.AppState.PrivilegeObserver.ProjectAllowed;
                        }
                    }
                }
            }
            return false;
        }



        public bool IsProject(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.Equals(Route.PROJECT_BROWSER)
                 || uri.Equals(Route.PROJECT_BACKUP_BROWSER)
                 || uri.StartsWith(Route.ACCESS_RIGHTS)
                 || uri.StartsWith(Route.PROJECT_BROWSER));
        }
    }
}
