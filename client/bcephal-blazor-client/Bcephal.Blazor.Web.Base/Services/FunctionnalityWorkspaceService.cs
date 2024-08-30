using Bcephal.Models.Functionalities;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class FunctionnalityWorkspaceService : Service<FunctionalityWorkspace, FunctionalityWorkspace>
    {
        public static FunctionalityWorkspace FunctionalityWorkspace { get; set; }

        public FunctionnalityWorkspaceService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {

        }
    

        public async Task<FunctionalityWorkspace> GetFunctionalityWorkspace(string id)
        {
            string uri = "functionalities/functionality-workspace/"+id;

            string responseMessage = await ExecuteGet(uri);
            try
            {
                FunctionalityWorkspace = JsonConvert.DeserializeObject<FunctionalityWorkspace>(responseMessage);

            }
            catch (Exception e)
            {
                await JSRuntime.InvokeVoidAsync("console.log", "message d'exception: ", e.Message);
            }

            return FunctionalityWorkspace;

        }




    }


}
