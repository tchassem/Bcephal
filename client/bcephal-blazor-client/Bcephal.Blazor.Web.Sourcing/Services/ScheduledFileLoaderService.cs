using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Loaders;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Services
{
    public class ScheduledFileLoaderService : Service<FileLoader, FileLoaderBrowserData>
    {
        public ScheduledFileLoaderService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing-scheduler/file-loader-scheduler";
        }

        public async Task<bool> CanStart()
        {
            string response = await this.ExecutePost(ResourcePath + "/can-start", new BrowserDataFilter());
            bool page = JsonConvert.DeserializeObject<bool>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<bool> start()
        {
            string response = await this.ExecutePost(ResourcePath + "/start", new BrowserDataFilter());
            bool page = JsonConvert.DeserializeObject<bool>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<bool> stop()
        {
            string response = await this.ExecutePost(ResourcePath + "/stop", new BrowserDataFilter());
            bool page = JsonConvert.DeserializeObject<bool>(response, getJsonSerializerSettings());
            return page;
        }
    }
}
