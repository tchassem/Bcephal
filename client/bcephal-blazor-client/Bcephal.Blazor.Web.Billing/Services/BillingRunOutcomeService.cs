using System.Net.Http;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing;
using Microsoft.JSInterop;


namespace Bcephal.Blazor.Web.Billing.Services
{
    public class BillingRunOutcomeService : Service<Persistent, BillingRunOutcome>
    {

        public BillingRunOutcomeService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/run-outcome";
            this.SocketResourcePath = "/ws/billing";
        }

    }
}
