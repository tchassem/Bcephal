using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Planners;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Services
{
    public class SchedulerPlannerService : Service<SchedulerPlanner, SchedulerPlannerBrowserData>
    {
        public SchedulerPlannerService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "scheduler-planner";
            SocketResourcePath = "ws/scheduler-planner";
        }

        protected override SchedulerPlannerEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<SchedulerPlannerEditorData>(response, getJsonSerializerSettings());
        }
    }

    public static class ExtensionMethods
    {
        public static void AddSchedulerPlannerServices(this IServiceCollection service)
        {
            service.AddSingleton<SchedulerPlannerService>();
            service.AddSingleton<SchedulerPlannerLogService>();
            service.AddSingleton<SchedulerPlannerLogItemService>();
        }
    }
}
