using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Planners;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Services
{
    public class SchedulerPlannerLogItemService : Service<SchedulerPlannerLogItem, SchedulerPlannerLogItemBrowserData>
    {

        public SchedulerPlannerLogItemService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "scheduler-planner/log-item";
        }
    }
}
