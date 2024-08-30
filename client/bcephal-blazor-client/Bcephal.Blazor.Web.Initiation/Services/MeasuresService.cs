using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Initiation.Domain;
using Bcephal.Models.Base;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Services
{
  public  class MeasuresService : Service<Measure, BrowserData>
    {
        public MeasuresService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "initiation/measure";
        }


        public async Task<bool> CanDelete(long id)
        {
            string responseMessage = await ExecuteGet(ResourcePath + "/can-delete/" + id);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }


    }
}
