using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Services.Mocks;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Invoices;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Services
{
    public class BillingCreditNoteService : Service<Invoice, InvoiceBrowserData>
    {
        public BillingCreditNoteService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/credit-note";
        }
    }
}
