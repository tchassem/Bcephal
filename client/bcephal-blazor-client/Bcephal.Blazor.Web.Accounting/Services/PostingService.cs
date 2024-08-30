using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Accounting;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Bcephal.Models.Grids.Filters;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Services
{
    public class PostingService : Service<Posting, PostingBrowserData>
    {
        public PostingService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/accounting/posting";
        }

        protected override PostingEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<PostingEditorData>(response, getJsonSerializerSettings());
        }

        //public async Task<BrowserDataPage<PostingBrowserData>> Search(BrowserDataFilter filter)
        //{
        //    string response = await this.ExecutePost(ResourcePath + "/search", filter);
        //    BrowserDataPage<PostingBrowserData> page = JsonConvert.DeserializeObject<BrowserDataPage<PostingBrowserData>>(response, getJsonSerializerSettings());
        //    return page;
        //}

        public async Task<Posting> Validation(long postingId)
        {
            Posting posting = await this.getById(postingId);
            return await Validation(posting); ;
        }

        public async Task<Posting> Validation(Posting posting)
        {
            string result = await this.ExecutePost(ResourcePath + "/validate", posting);
            Posting post = JsonConvert.DeserializeObject<Posting>(result, getJsonSerializerSettings());
            return post;
        }

        public async Task<bool> Validation(List<long> PostingIds)
        {
            return bool.Parse(await this.ExecutePost(ResourcePath + "/validate-postings", PostingIds));
        }

        public async Task<bool> ResetValidation(long postingId)
        {
            return await ResetValidation(new List<long>() { postingId });
        }

        public async Task<PostingEditorData> ResetValidation(Posting posting)
        {
            string response = await this.ExecutePost(ResourcePath + "/reset", posting);
            return DeserialiazeEditorData(response);
        }

        public async Task<bool> ResetValidation(List<long> PostingIds)
        {
            return bool.Parse(await this.ExecutePost(ResourcePath + "/reset-postings", PostingIds));
        }
    }
}
