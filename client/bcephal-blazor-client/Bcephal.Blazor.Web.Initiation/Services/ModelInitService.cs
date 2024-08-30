using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Initiation.Domain;
using Bcephal.Models.Base;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Services
{
    public class ModelInitService : Service<Model, BrowserData>
    {
        public ModelInitService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "initiation/model";
        }
        protected override ModelEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<ModelEditorData>(response, getJsonSerializerSettings());
        }

        public async Task<bool> CanDelete(string type, long id)
        {
            string responseMessage = await ExecuteGet(ResourcePath + "/can-delete/" + type + "/" + id);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }

    }

    public static class ExtensionMethods
    {
        public static void AddInitiationServices(this IServiceCollection service)
        {
            service.AddSingleton<ModelInitService>();
            service.AddSingleton<AccessRightService>();
            service.AddSingleton<ClipboardService>();
            service.AddSingleton<MeasuresService>();
            service.AddSingleton<PeriodService>();
        }
    }
}
