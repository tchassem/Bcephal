using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
   public class AdministrationPrivilegeObserver
    {

        private readonly AppState AppState;
        public AdministrationPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsAdministration(uri))
            {
                return HasPrivilegeAdministration(uri);
            }
            return false;
        }

        private bool HasPrivilegeAdministration(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.AdministrationAllowed)
                {
                    if (CanCreate(uri))
                    {
                        if(Route.CLIENT_FORM.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationClientCreateAllowed;
                        }
                        else if (Route.PROFIL_EDIT.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationProfileCreateAllowed;
                        }
                        else if (uri.Equals(Route.USER_FORM))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationUserCreateAllowed; ;
                        }
                       
                    }
                    else
                    {
                        if (Route.BROWSER_CLIENT.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationClientEditAllowed || this.AppState.PrivilegeObserver.AdministrationClientViewAllowed || this.AppState.PrivilegeObserver.AdministrationClientCreateAllowed;
                        }
                        else if (Route.PROFIL_LIST.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationProfileEditAllowed || this.AppState.PrivilegeObserver.AdministrationProfileViewAllowed || this.AppState.PrivilegeObserver.AdministrationProfileCreateAllowed;
                        }
                        else  if (uri.Equals(Route.BROWSER_USER))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationUserEditAllowed || this.AppState.PrivilegeObserver.AdministrationUserViewAllowed || this.AppState.PrivilegeObserver.AdministrationUserCreateAllowed;
                        }
                        else if (Route.ADMIN_SCHEDULER_BROWSER.Equals(uri))
                        {
                            return true;
                           // return this.AppState.PrivilegeObserver.AdministrationSchedulerEditAllowed || this.AppState.PrivilegeObserver.AdministrationSchedulerViewAllowed || this.AppState.PrivilegeObserver.AdministrationSchedulerCreateAllowed;
                        }
                        if (uri.StartsWith(Route.CLIENT_FORM))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationClientEditAllowed || this.AppState.PrivilegeObserver.AdministrationClientViewAllowed || this.AppState.PrivilegeObserver.AdministrationUserCreateAllowed;
                        }
                        else if (uri.StartsWith(Route.USER_FORM))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationUserEditAllowed || this.AppState.PrivilegeObserver.AdministrationUserViewAllowed || this.AppState.PrivilegeObserver.AdministrationUserCreateAllowed;
                        }
                        else if (uri.StartsWith(Route.PROFIL_EDIT))
                        {
                            return this.AppState.PrivilegeObserver.AdministrationProfileEditAllowed || this.AppState.PrivilegeObserver.AdministrationProfileViewAllowed || this.AppState.PrivilegeObserver.AdministrationUserCreateAllowed;
                        }
                    }
                   
                }
            }
            return false;
        }

        public bool IsAdministration(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.Equals(Route.CLIENT_FORM)
                 || uri.Equals(Route.BROWSER_CLIENT)
                 || uri.StartsWith(Route.USER_FORM)
                 || uri.StartsWith(Route.BROWSER_USER)
                 || uri.StartsWith(Route.PROFIL_EDIT)
                 || uri.Equals(Route.PROFIL_LIST)
                 || uri.Equals(Route.ADMIN_SCHEDULER_BROWSER));
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri)
                && uri.Equals(Route.CLIENT_FORM)
                || uri.Equals(Route.USER_FORM)
                || uri.Equals(Route.PROFIL_EDIT);
        }

    }
}
