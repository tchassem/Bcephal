using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Settings;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Setting.Services
{
    public class ParameterService : Service<Parameter, BrowserData>
    {

        public ParameterService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "settings/configuration";
        }

        protected override ParameterEditorData DeserialiazeEditorData(string response)
        {
            ParameterEditorData parameterEditorData = JsonConvert.DeserializeObject<ParameterEditorData>(response, getJsonSerializerSettings());
            return parameterEditorData;
        }

        public async Task<ParameterEditorData>  BuildAutomatically(string code)
        {
            string response = await this.ExecutePost(ResourcePath + "/build-automatically", code);
            ParameterEditorData parameterEditorData = DeserialiazeEditorData(response);
            return parameterEditorData;
        }

        public async override Task<Parameter> Save(Parameter item)
        {
            if (item == null)
            {
                return item;
            }
            String response = await this.ExecutePost(ResourcePath + "/save", item);
            item = JsonConvert.DeserializeObject<Parameter>(response);
            return item;
        }

    }

    public static class ExtensionMethods
    {
        public static void AddSettingServices(this IServiceCollection service)
        {
            service.AddSingleton<ParameterService>();
        }
    }
}
