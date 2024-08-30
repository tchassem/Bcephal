using Bcephal.Blazor.Web.Administration.Pages.Profile;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Services
{
    public class ProfileDashboardService : Base.Services.Service<ProfileDashboard, ProfileDashboardEditorData>
    {
        public ProfileDashboardService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/dashboarding";
        }

        public async Task<ProfileDashboardEditorData> GetDashboards(string profileId)
        {
            string Uri = ResourcePath + "/profile-dashboard-editor-data/" + profileId;
            string response = await this.ExecuteGet(Uri);
            return JsonConvert.DeserializeObject<ProfileDashboardEditorData>(response, getJsonSerializerSettings());
        }

        public async Task<bool> Save(ListChangeHandler<ProfileDashboard> Dashboards, string profileId)
        {
            string Uri = ResourcePath + "/save-profile-dashboards/" + profileId;
            return bool.Parse(await ExecutePost(Uri, Dashboards));
            //return JsonConvert.DeserializeObject<ListChangeHandler<ProfileDashboard>>(response, getJsonSerializerSettings()); ;
        }
    }

    public static class ExtensionMethods
    {
        public static void AddAdministrationServices(this IServiceCollection service)
        {
            service.AddSingleton<ProfileDashboardService>();
            service.AddSingleton<ProfileService>();
            service.AddSingleton<UsersService>();
        }
    }
}
