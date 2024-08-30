using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
  public  class SourcingPrivilegeObserver
    {
        private readonly AppState AppState;
        public SourcingPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsSourcing(uri))
            {
                return HasPrivilegeSourcing(uri);
            }
            return false;
        }

        private bool HasPrivilegeSourcing(string uri)
        {

            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.SourcingAllowed)
                {

                    if (CanCreate(uri))
                    {

                        if (Route.EDIT_GRID.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SourcingInputGridCreateAllowed;
                        }
                        else
                            if (Route.NEW_LOAD_FILE.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SourcingFileLoaderCreateAllowed;
                        }
                        else
                            if (!string.IsNullOrWhiteSpace(uri) && (uri.StartsWith(Route.NEW_DYNAMIC_FORM)))
                        {
                            return false;
                        }
                        else
                            if (Route.EDIT_SPOT.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SourcingSpotCreateAllowed;
                        }
                    }
                    else
                    {
                        if (Route.BROWSER_GRID.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SourcingInputGridEditAllowed || this.AppState.PrivilegeObserver.SourcingInputGridViewAllowed || this.AppState.PrivilegeObserver.SourcingInputGridCreateAllowed;
                        }
                        else
                            if (Route.LIST_FILES_LOADER.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SourcingFileLoaderViewAllowed || this.AppState.PrivilegeObserver.SourcingFileLoaderEditAllowed || this.AppState.PrivilegeObserver.SourcingFileLoaderCreateAllowed;
                        }
                        else
                            if (!string.IsNullOrWhiteSpace(uri) && uri.StartsWith(Route.List_DYNAMIC_BROWSER))
                        {
                            return false;
                        }
                        else
                            if (Route.BROWSER_SPOT.Equals(uri))
                        {
                            return this.AppState.PrivilegeObserver.SourcingSpotViewAllowed  || this.AppState.PrivilegeObserver.SourcingSpotEditAllowed || this.AppState.PrivilegeObserver.SourcingSpotCreateAllowed;
                        }else
                        if (uri.StartsWith(Route.EDIT_GRID))
                        {
                            return this.AppState.PrivilegeObserver.SourcingInputGridEditAllowed || this.AppState.PrivilegeObserver.SourcingInputGridViewAllowed || this.AppState.PrivilegeObserver.SourcingInputGridCreateAllowed;
                        }
                        else
                        if (uri.StartsWith(Route.NEW_DYNAMIC_FORM))
                        {
                            return false;
                        }
                        else
                        if (uri.StartsWith(Route.NEW_LOAD_FILE))
                        {
                            return this.AppState.PrivilegeObserver.SourcingFileLoaderEditAllowed || this.AppState.PrivilegeObserver.SourcingFileLoaderViewAllowed || this.AppState.PrivilegeObserver.SourcingFileLoaderCreateAllowed; ;
                        }
                        else
                        if (uri.StartsWith(Route.EDIT_SPOT))
                        {
                            return this.AppState.PrivilegeObserver.SourcingSpotEditAllowed || this.AppState.PrivilegeObserver.SourcingSpotViewAllowed || this.AppState.PrivilegeObserver.SourcingSpotCreateAllowed; ;
                        }
                    }
                }
            }
            return false;
        }

        public bool IsSourcing(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.StartsWith(Route.EDIT_SPOT)
                 || uri.Equals(Route.BROWSER_SPOT)
                 || uri.StartsWith(Route.NEW_DYNAMIC_FORM)
                 || uri.StartsWith(Route.List_DYNAMIC_BROWSER)
                 || uri.StartsWith(Route.NEW_LOAD_FILE)
                 || uri.Equals(Route.LIST_FILES_LOADER)
                 || uri.Equals(Route.BROWSER_GRID)
                 || uri.StartsWith(Route.EDIT_GRID));
        }

        public bool CanCreate(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.Equals(Route.EDIT_SPOT)
                 || uri.StartsWith(Route.NEW_DYNAMIC_FORM)
                 || uri.Equals(Route.NEW_LOAD_FILE)
                 || uri.Equals(Route.EDIT_GRID));
        }
    }
}
