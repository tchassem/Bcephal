using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Profiles;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Services
{
    public class AccessRightService : Service<ProfileProject, AccessRightEditorData>
    {
        public AccessRightService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/profiles";
        }

        public async Task<AccessRightEditorData> GetProfileProjects(long? ProjectCode)
        {
            string Uri = ResourcePath + "/access-right-editor-data/" + ProjectCode.Value;
            string response = await this.ExecuteGet(Uri);
            return JsonConvert.DeserializeObject<AccessRightEditorData>(response, getJsonSerializerSettings());
        }

        public async Task<AccessRightEditorData> SaveAccessRight(ListChangeHandler<ProfileProject> ProfileProjects, string ProjectCode)
        {
            string Uri = ResourcePath + "/save-access-rights/" + ProjectCode;
            string resJson = await ExecutePost(Uri, ProfileProjects);
            return JsonConvert.DeserializeObject<AccessRightEditorData>(resJson, getJsonSerializerSettings());
        }
    }
}
