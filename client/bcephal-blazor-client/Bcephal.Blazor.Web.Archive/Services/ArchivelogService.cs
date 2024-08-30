using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Archives;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using Microsoft.JSInterop;

namespace Bcephal.Blazor.Web.Archive.Services
{
    public class ArchiveLogService : Service<ArchiveLog, ArchiveLogBrowserData>
    {
        public ArchiveLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "archive/log";
        }
    }
}
