using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Data;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class DashboardReportService : Service<DashboardReport, BrowserData>
    {
        public DashboardReportService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "dashboarding/report";
        }

        public async Task<ObservableCollection<Dictionary<string, object>>> GetRows(DashboardReport dReport)
        {
            // await JSRuntime.InvokeVoidAsync("console.log", " dReport => ", dReport);
            string response = await this.ExecutePost(ResourcePath + "/rows", dReport);
            // await JSRuntime.InvokeVoidAsync("console.log", " data => ", response);
            return JsonConvert.DeserializeObject<ObservableCollection<Dictionary<string, object>>>(response);
        }

        public async Task<ObservableCollection<JObject>> GetRows_(DashboardReport dReport)
        {
            string response = await this.ExecutePost(ResourcePath + "/rows", dReport);
            return JsonConvert.DeserializeObject<ObservableCollection<JObject>>(response);
        }
    }
}
