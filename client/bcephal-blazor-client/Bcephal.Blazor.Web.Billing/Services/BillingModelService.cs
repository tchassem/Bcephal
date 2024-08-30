using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Services
{
    public class BillingModelService : Service<BillingModel, BrowserData>
    {
        public BillingModelService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/model";
            SocketResourcePath = "/ws/billing/run";
        }

        public async Task<BrowserDataPage<BillingModel>> GetAllMockAsync()
        {
            await Task.Delay(40);
            ObservableCollection<BillingModel> l = new ObservableCollection<BillingModel>();
            for (int i = 1; i <= 60; i++)
            {
                l.Add(new BillingModel { Id = i, Name = $"BillingModel {i}", group = new BGroup($"Group {(i%2 + 1)}"), CreationDate = DateTime.Now.AddDays((i - 1)).ToString(), ModificationDate = DateTime.Now.AddDays((i + 1)).ToString(), VisibleInShortcut = (i%3 == 0) });
            }

            
            return new BrowserDataPage<BillingModel>()
            {
                PageSize = 10,
                PageFirstItem = 1,
                PageLastItem = 10,
                TotalItemCount = l.Count(),
                CurrentPage = 1,
                Items = l
            };
        }

        public IEnumerable<KeyValuePair<string, string>> GetMockInvoicePivotList()
        {
            return new Dictionary<string, string>()
            {
                { "key1", "Pivot 1" },
                { "key2", "Pivot 2" },
                { "key3", "Pivot 3" },
                { "key4", "Pivot 4" },
                { "key5", "Pivot 5" },
            };
        }

        protected override BillingModelEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<BillingModelEditorData>(response, getJsonSerializerSettings());
        }
    }


    public static class ExtensionMethods
    {
        public static void AddBillingServices(this IServiceCollection service)
        {
            service.AddSingleton<BillingModelService>();
            service.AddSingleton<BillingRunOutcomeService>();
            service.AddSingleton<BilleRunService>();
            service.AddSingleton<CurrentRunService>();
            service.AddSingleton<InvoiceService>();
            service.AddSingleton<ClientRepositoryService>();
            service.AddSingleton<BillingJoinService>();
            service.AddSingleton<BillingCreditNoteService>();
        }
    }
}
