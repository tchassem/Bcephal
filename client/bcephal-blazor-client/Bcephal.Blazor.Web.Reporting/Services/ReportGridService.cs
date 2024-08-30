using Bcephal.Blazor.Web.Sourcing.Services;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class ReportGridService : GrilleService
    {
        public ReportGridService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "reporting/grid";
        }
    }

    public static class ExtensionMethods
    {
        public static void AddReportingServices(this IServiceCollection service)
        {
            service.AddSingleton<BillingTemplateService>();
            service.AddSingleton<ReportGridService>();
            service.AddSingleton<ReportPublicationService>();
            service.AddSingleton<ReportService>();
            service.AddSingleton<RepositoryService>();
            service.AddSingleton<DashboardReportService>();
            service.AddSingleton<SpreadSheetService>();
            service.AddSingleton<JoinService>();
            service.AddSingleton<JoinLogService>();
        }
    }
}
