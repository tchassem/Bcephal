using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Settings;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Services
{
    public class IncrementalNumberService : Service<IncrementalNumber, IncrementalNumberBrowserData>
    {

        public IncrementalNumberService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "settings/incremental-number";
        }
    }
}
