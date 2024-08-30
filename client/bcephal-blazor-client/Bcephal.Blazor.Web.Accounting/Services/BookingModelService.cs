using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Accounting.Services
{
    public class BookingModelService : Service<BookingModel, BrowserData>
    {
        public BookingModelService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "accounting/booking-model";
        }
    }

    public static class ExtensionMethods
    {
        public static void AddAccountingServices(this IServiceCollection service)
        {
            service.AddSingleton<BookingModelService>();
            service.AddSingleton<BookingModelLogService>();
            service.AddSingleton<PostingRepositoryService>();
            service.AddSingleton<PostingService>();
        }
    }
}
