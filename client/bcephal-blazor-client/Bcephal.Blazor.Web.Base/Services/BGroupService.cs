using Bcephal.Models.Base;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class BGroupService : Service<BGroup, BrowserData>
    {
        public BGroupService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "b-group";
            SocketResourcePath = "ws/sourcing";
        }
    }
}
