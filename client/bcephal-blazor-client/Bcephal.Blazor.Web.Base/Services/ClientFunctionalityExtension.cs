using Bcephal.Blazor.Web.Base.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Clients
{
    public static class ClientFunctionalityExtension
    {
        public static string Name(this ClientFunctionality clientFunctionality, AppState appState)
        {
            if(appState == null)
            {
                return clientFunctionality.Code;
            }
            return appState[clientFunctionality.Code];
        }
    }
}
