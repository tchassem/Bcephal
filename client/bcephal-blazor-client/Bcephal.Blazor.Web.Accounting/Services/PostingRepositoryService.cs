using Bcephal.Blazor.Web.Sourcing.Services;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Services
{
    public class PostingRepositoryService : GrilleService
    {
        public PostingRepositoryService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "posting/grid";
        }
    }
}
