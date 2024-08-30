using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Forms;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Form.Services
{
    public class FormDataService : Service<FormData, object[]>
    {

        public FormDataService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "form/data";
        }
        protected override FormDataEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<FormDataEditorData>(response, getJsonSerializerSettings());
        }
    }

    public static class ExtensionMethods
    {
        public static void AddFormServices(this IServiceCollection service)
        {
            service.AddSingleton<FormDataService>();
        }
    }
}
