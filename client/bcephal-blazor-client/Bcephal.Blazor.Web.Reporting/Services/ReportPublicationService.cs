using Bcephal.Blazor.Web.Sourcing.Services;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class ReportPublicationService : GrilleService
    {
        public ReportPublicationService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            //ResourcePath = "reporting/report/publication";
            ResourcePath = "reporting/grid";
        }
    }
}
