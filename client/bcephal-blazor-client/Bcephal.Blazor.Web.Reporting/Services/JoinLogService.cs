using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Joins;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class JoinLogService : Service<JoinLog, JoinLogBrowserData>
    {
        public JoinLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "join-log";
        }
    }
}
