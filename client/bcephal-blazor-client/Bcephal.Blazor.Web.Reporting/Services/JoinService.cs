using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Services
{
    public class JoinService : Service<Join, JoinBrowserData>
    {
        public JoinService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/join";
            this.SocketResourcePath = "/ws/join";
        }
        protected override JoinEditorData DeserialiazeEditorData(string response)
        {
            JoinEditorData JoinEditorData = JsonConvert.DeserializeObject<JoinEditorData>(response, getJsonSerializerSettings());
            return JoinEditorData;
        }
        public async Task<IEnumerable<Nameable>> FindGridColumns(long GridId)
        {
            string response = await ExecuteGet(ResourcePath + "/grid-colum/" + GridId);
            return JsonConvert.DeserializeObject<IEnumerable<Nameable>>(response, getJsonSerializerSettings());
        }
    }
}
