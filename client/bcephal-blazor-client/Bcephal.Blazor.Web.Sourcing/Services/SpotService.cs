using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Spot;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Services
{
    public class SpotService : Service<Models.Spot.Spot , BrowserData>
    {
        public SpotService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/spot";
        }

        public async virtual Task<decimal?> Evaluate(Models.Spot.Spot spot)
        {
            if (spot == null)
            {
                return 0;
            }
            string response = await this.ExecutePost(ResourcePath + "/evaluate-spot", spot);
            decimal? amount = JsonConvert.DeserializeObject<decimal?>(response);
            return amount;
        }

        protected override async Task<bool> CheckDuplicateObject(Models.Spot.Spot item)
        {
            Models.Spot.Spot spot = await getByName(item.Name);
            return spot == null || !(item.Id.HasValue && spot.Id.Value == item.Id.Value) ? false : true;
        }

        protected override SpotEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<SpotEditorData>(response, getJsonSerializerSettings());
        }

    }

}
