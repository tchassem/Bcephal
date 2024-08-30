using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Reconciliation;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Services
{
    public class AutoRecoService : Service<AutoReco, RecoBrowserData>
    {
        public AutoRecoService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "reconciliation/auto-reco";
            this.SocketResourcePath = "/ws/reconciliation/auto-reco";
        }
        protected override AutoRecoEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<AutoRecoEditorData>(response, getJsonSerializerSettings());
        }

        protected override async Task<bool> CheckDuplicateObject(AutoReco item)
        {
            AutoReco autoReco = await getByName(item.Name);
            return autoReco == null || !(item.Id.HasValue && autoReco.Id.Value == item.Id.Value) ? false : true;
        }

        public async Task<ReconciliationModelColumns> GetModelColumns(long modelId)
        {
            String response = await this.ExecuteGet(ResourcePath + "/model-columns/" + modelId);
            ReconciliationModelColumns item = JsonConvert.DeserializeObject<ReconciliationModelColumns>(response);
            return item;
        }

    }
}
