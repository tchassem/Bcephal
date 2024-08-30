using Bcephal.Blazor.Web.Base.Services;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Services
{
    public class DashboardService : Service<Models.Dashboards.Dashboard, Models.Dashboards.Dashboard>
    {
        public DashboardService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "dashboarding";
        }

        protected override async Task<bool> CheckDuplicateObject(Models.Dashboards.Dashboard item)
        {
            Models.Dashboards.Dashboard reconciliationModel = await getByName(item.Name);
            return reconciliationModel == null || !(item.Id.HasValue && reconciliationModel.Id.Value == item.Id.Value) ? false : true;
        }

    }

   
    public static class ExtensionMethods
    {
        public static void AddDashboardServices(this IServiceCollection service)
        {
            service.AddSingleton<AlarmService>();
            service.AddSingleton<DashboardProfileService>();
            service.AddSingleton<DashboardService>();
        }
    }
}
