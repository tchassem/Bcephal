using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Services
{
    public class DynamicFormService : Service<Grille, object>
    {

        public DynamicFormService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/grid";
        }

    }
}
