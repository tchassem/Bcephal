using Bcephal.Blazor.Web.Sourcing.Services;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class ReportService : GrilleService
    {
        public ReportService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/grid";
        }
    }
}
