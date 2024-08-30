using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Services
{
    public class ArchiveConfigurationService : Service<ArchiveConfig, ArchiveConfigBrowserData>
    {
        public ArchiveConfigurationService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "archive/configuration";
            this.SocketResourcePath = "/ws/archive";
        }
        protected override async Task<bool> CheckDuplicateObject(ArchiveConfig item)
        {
            ArchiveConfig archiveConfig = await getByName(item.Name);
            return archiveConfig == null || !(item.Id.HasValue && archiveConfig.Id.Value == item.Id.Value) ? false : true;
        }
    }
}
