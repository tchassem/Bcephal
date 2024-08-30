using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Services;
using Bcephal.Blazor.Web.Routing;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Blazor.Web.Form.Services;
using Bcephal.Blazor.Web.Archive.Services;
using Bcephal.Blazor.Web.Initiation.Services;
using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Setting.Services;
using Bcephal.Blazor.Web.Planification.Services;
using Bcephal.Blazor.Web.Messenger.Services;
using Bcephal.Blazor.Web.Scheduling.Services;
//using Bcephal.Blazor.Web.FileManager.Services;

namespace Bcephal.Blazor.Web
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            var builder = WebAssemblyHostBuilder.CreateDefault(args);
            builder.RootComponents.Add<App>("#app");
            builder.Logging.SetMinimumLevel(LogLevel.Error);
            builder.Services.AddBaseServices();
            builder.Services.AddConfigHttpClientServices(builder.Build(), builder.HostEnvironment.BaseAddress, builder.HostEnvironment.IsProduction());
            builder.Services.AddSingleton<LoadService>();
            builder.Services.AddSingleton<IncrementalNumberService>();
            builder.Services.AddSingleton<ClientService>();
            builder.Services.AddSingleton<RouteManager>();
            builder.Services.AddSingleton<AboutService>();
            builder.Services.AddInitiationServices();
            builder.Services.AddSettingServices();
            builder.Services.AddSourcingServices();
            builder.Services.AddReportingServices();
            builder.Services.AddReconciliationServices();
            builder.Services.AddPlanificationServices();
            builder.Services.AddFormServices();
            builder.Services.AddDashboardServices();                       
            builder.Services.AddBillingServices();            
            builder.Services.AddDevExpressBlazor();         
            builder.Services.AddArchiveServices();
            builder.Services.AddAccountingServices();
            builder.Services.AddAdministrationServices();            
            builder.Services.AddMessengerServices();
            builder.Services.AddSchedulerPlannerServices();
            //builder.Services.AddFileManagerServices();
            await builder.Build().SetDefaultCulture();
            await builder.Build().RunAsync();
        }
    }
}