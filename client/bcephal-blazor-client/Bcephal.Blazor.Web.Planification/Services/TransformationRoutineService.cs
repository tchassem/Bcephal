using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Routines;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Planification.Services
{
    public class TransformationRoutineService : Service<TransformationRoutine, BrowserData>
    {
        public TransformationRoutineService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "planification/routine";
            this.SocketResourcePath = "/ws/planification/run-routine";
        }
    }

    public static class ExtensionMethods
    {
        public static void AddPlanificationServices(this IServiceCollection service)
        {
            service.AddSingleton<TransformationRoutineService>();
        }
    }
}
