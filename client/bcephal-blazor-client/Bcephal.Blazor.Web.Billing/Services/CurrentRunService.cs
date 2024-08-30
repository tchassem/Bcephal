using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Billing.Services
{
    public class CurrentRunService : Service<BillingModelLog, BrowserData>
    {

        public CurrentRunService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/current-run-status";
        }


    }
}
