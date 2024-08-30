using Bcephal.Models.Base;
using Bcephal.Models.Schedulers;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
   public class SchedulerLogService :Service<SchedulableObject, SchedulerLogBrowserData>
    {
        public SchedulerLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "scheduler/logs";
        }
    }
}
