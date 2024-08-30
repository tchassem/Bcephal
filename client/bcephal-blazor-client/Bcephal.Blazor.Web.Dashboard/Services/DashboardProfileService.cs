using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Profiles;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Services
{
    public class DashboardProfileService : Service<ProfileDashboard, DashboardProfileEditorData>
    {
        public DashboardProfileService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/profiles";
        }

        public async Task<DashboardProfileEditorData> GetProfiles(long? DashboardId)
        {
            string Uri = ResourcePath + "/profile-dashboard-editor-data/" + DashboardId;
            string response = await this.ExecuteGet(Uri);
            return JsonConvert.DeserializeObject<DashboardProfileEditorData>(response, getJsonSerializerSettings());
        }

        public async Task<bool> Save(ListChangeHandler<ProfileDashboard> Profiles, string DashboardId)
        {
            string Uri = ResourcePath + "/save-profile-dashboards/" + DashboardId;
            return bool.Parse(await ExecutePost(Uri, Profiles));
        }

    }
}
