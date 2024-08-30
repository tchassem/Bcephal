using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Services
{
    public class ArchiveServices: Service<Bcephal.Models.Archives.Archive, ArchiveBrowserData>
    {
        public ArchiveServices(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "archive";
            this.SocketResourcePath = "/ws/archive";
        }

        protected override async Task<bool> CheckDuplicateObject(Models.Archives.Archive item)
        {
            Models.Archives.Archive archive = await getByName(item.Name);
            return archive == null || !(item.Id.HasValue && archive.Id.Value == item.Id.Value) ? false : true;
        }

    }

    public static class ExtensionMethods
    {
        public static void AddArchiveServices(this IServiceCollection service)
        {
            service.AddSingleton<ArchiveServices>();
            service.AddSingleton<ArchiveLogService>();
            service.AddSingleton<ArchiveConfigurationService>();
        }
    }
}
