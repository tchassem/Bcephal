using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class RepositoryService : ReportGridService
    {
        public RepositoryService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "billing/event-repository";
        }
    }
}