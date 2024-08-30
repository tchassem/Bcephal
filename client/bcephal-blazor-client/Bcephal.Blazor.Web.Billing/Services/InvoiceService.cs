using System.Net.Http;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Invoices;
using Microsoft.JSInterop;
using Newtonsoft.Json;

namespace Bcephal.Blazor.Web.Billing.Services
{
    public class InvoiceService : Service<Invoice, InvoiceBrowserData>
    {
        public InvoiceService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/invoice";
        }
    }
}
