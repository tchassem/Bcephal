using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Bcephal.Models.Profiles;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Services
{
    public class ProfileService : Service<Profile, ProfileBrowserData>
    {
        public ProfileService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/profiles";
        }

        protected override ProfileEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<ProfileEditorData>(response, getJsonSerializerSettings());
        }
    }
}
