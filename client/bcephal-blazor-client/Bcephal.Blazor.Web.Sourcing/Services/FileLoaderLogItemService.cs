using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Loaders;
using Microsoft.JSInterop;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Sourcing.Services
{
    public class FileLoaderLogItemService : Service<FileLoaderLogItem, FileLoaderLogItem>
    {

        public FileLoaderLogItemService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/file-loader-log-item";
        }
    }

  
}
