using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Services
{
    public class ReconciliationLogService: Service<ReconciliationLog, ReconciliationLog>
    {
        public ReconciliationLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "reconciliation/model";
        }


        public  async Task<BrowserDataPage<ReconciliationLog>> SearchLogs(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/logs-search", filter);
            BrowserDataPage<ReconciliationLog> page = JsonConvert.DeserializeObject<BrowserDataPage<ReconciliationLog>>(response, getJsonSerializerSettings());
            return page;
        }

    }
}
