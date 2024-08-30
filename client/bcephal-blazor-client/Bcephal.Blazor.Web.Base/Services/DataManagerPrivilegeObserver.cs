using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
     public class DataManagerPrivilegeObserver
    {
        private readonly AppState AppState;

        public DataManagerPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }

        public bool HasPrivilege(string uri)
        {
            if (IsDataManager(uri))
            {
                return HasPrivilegeDataManager(uri);
            }
            return false;
        }

        public bool IsDataManager(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.StartsWith(Route.EDIT_ARCHIVE_CONFIGURATION)
                 || uri.Equals(Route.ARCHIVE_CONFIGURATION_BROWSER)
                 || uri.Equals(Route.ARCHIVE_BROWSER)
                 || uri.Equals(Route.BROWSER_ARCHIVE_LOG));
        }

        private bool HasPrivilegeDataManager(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.DataManagementAllowed)
                {
                    if (CanCreate(uri))
                    {
                        if (Route.EDIT_ARCHIVE_CONFIGURATION.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.CanCreatedDataManagementArchiveConfig;
                        }
                    }
                    else
                    {
                        if (Route.ARCHIVE_CONFIGURATION_BROWSER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DataManagementArchiveConfigEditAllowed || this.AppState.PrivilegeObserver.DataManagementArchiveConfigViewAllowed || this.AppState.PrivilegeObserver.CanCreatedDataManagementArchiveConfig;
                        }else  if(Route.ARCHIVE_BROWSER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DataManagementArchiveEditAllowed || this.AppState.PrivilegeObserver.DataManagementArchiveViewAllowed || this.AppState.PrivilegeObserver.CanCreatedDataManagementArchiveConfig;
                        }
                        else if(Route.BROWSER_ARCHIVE_LOG.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.DataManagementArchiveLogEditAllowed || this.AppState.PrivilegeObserver.DataManagementArchiveLogViewAllowed || this.AppState.PrivilegeObserver.CanCreatedDataManagementArchiveConfig;
                        }
                        else if (uri.StartsWith(Route.EDIT_ARCHIVE_CONFIGURATION))
                        {
                            return this.AppState.PrivilegeObserver.DataManagementArchiveConfigEditAllowed;
                        }
                    }
                   
                   
                }
            }
            return false;
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) && uri.Equals(Route.EDIT_ARCHIVE_CONFIGURATION);
        }
    }
}
