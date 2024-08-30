using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Microsoft.JSInterop;
using System;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Services
{
    public class AttributeValueModelService : Service<Attribute, BrowserData>
    {
        public AttributeValueModelService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "b-group";
            SocketResourcePath = "ws/sourcing";
        }
    }
}
