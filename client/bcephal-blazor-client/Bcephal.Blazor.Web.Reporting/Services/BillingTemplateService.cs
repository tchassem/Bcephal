using System.Net.Http;
using Microsoft.JSInterop;
using Bcephal.Models.Billing;
using Bcephal.Models.Base;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Billing.Invoices;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class BillingTemplateService :Service<BillTemplate, BillingTemplateBrowserData>
    {

        public BillingTemplateService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/template";
        }
    }
}
