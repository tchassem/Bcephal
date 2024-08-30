using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class SettingsPrivilegeObserver
    {
        private readonly AppState AppState;
        public SettingsPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsSettings(uri))
            {
                return HasPrivilegeSettings(uri);
            }
            return false;
        }

        private bool HasPrivilegeSettings(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.SettingsAllowed)
                {
                    if(CanCreate(uri))
                    {
                        if (Route.EDIT_INCREMENTAL_NUMBER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SettingsIncrementalNumberCreateAllowed;
                        }
                        else
                        if (Route.SETTINGS_CONFIGURATION.Equals(uri))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        if (Route.BROWSER_INCREMENTAL_NUMBER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SettingsIncrementalNumberEditAllowed || this.AppState.PrivilegeObserver.SettingsIncrementalNumberViewAllowed;
                        }
                        else if (uri.StartsWith(Route.EDIT_INCREMENTAL_NUMBER))
                        {
                            return this.AppState.PrivilegeObserver.SettingsIncrementalNumberEditAllowed;
                        }
                       
                    }
                    
                }
            }
            return false;
        }

        public bool IsSettings(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.StartsWith(Route.EDIT_INCREMENTAL_NUMBER)
                 || uri.Equals(Route.BROWSER_INCREMENTAL_NUMBER)
                 || uri.Equals(Route.SETTINGS_CONFIGURATION));
        }

        public bool CanCreate(string uri)
       {
            return !string.IsNullOrWhiteSpace(uri) &&
                (uri.Equals(Route.EDIT_INCREMENTAL_NUMBER) 
                || uri.Equals(Route.SETTINGS_CONFIGURATION));
       }
    }
}
