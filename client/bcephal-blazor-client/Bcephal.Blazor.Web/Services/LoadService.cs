using Bcephal.Blazor.Web.Sourcing.Services;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Services
{
    public class LoadService : GrilleService
    {
        public LoadService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/load";
        }
    }
}
