using System.Net.Http;
using Bcephal.Blazor.Web.Base.Services;
using Microsoft.JSInterop;

namespace Bcephal.Blazor.Web.Billing.Services
{
    public class BilleRunService : Service<Bcephal.Models.Billing.BillingRunOutcome, object>
    {

        public BilleRunService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/run";
        }


    }
}
