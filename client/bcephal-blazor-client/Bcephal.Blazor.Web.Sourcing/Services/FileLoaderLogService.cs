using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Loaders;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Sourcing.Services
{
    public class FileLoaderLogService : Service<FileLoaderLog, FileLoaderLog>
    {
        public FileLoaderLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/file-loader-log";
        }
    }
}
