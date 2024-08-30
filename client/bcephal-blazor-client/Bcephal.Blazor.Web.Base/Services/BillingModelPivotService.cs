
using Bcephal.Models.Billing.Model;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class BillingModelPivotService : Service<BillingModelPivot, object>
    {

        public BillingModelPivotService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/pivot";
        }

    }
}
