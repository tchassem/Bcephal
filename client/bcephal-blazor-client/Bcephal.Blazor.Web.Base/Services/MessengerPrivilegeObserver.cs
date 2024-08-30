using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class MessengerPrivilegeObserver
    {

        private readonly AppState AppState;
        public MessengerPrivilegeObserver(AppState AppState)
        {
            this.AppState = AppState;
        }
        public bool HasPrivilege(string uri)
        {
            if (IsMessenger(uri))
            {
                return HasPrivilegeMessenger(uri);
            }
            return false;
        }

        private bool HasPrivilegeMessenger(string uri)
        {
            if (this.AppState.PrivilegeObserver != null)
            {
                if (this.AppState.PrivilegeObserver.MessengerAllowed)
                {
                    if (Route.BROWSER_MESSAGE_LOG.Equals(uri))
                    {
                        return this.AppState.PrivilegeObserver.MessengerLogAllowed;
                    }

                }
            }
            return false;
        }

        public bool IsMessenger(string uri)
        {
            return !string.IsNullOrWhiteSpace(uri) &&
                  (uri.Equals(Route.BROWSER_MESSAGE_LOG));
        }
    }
}
