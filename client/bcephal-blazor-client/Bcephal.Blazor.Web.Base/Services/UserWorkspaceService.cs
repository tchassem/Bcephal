using Bcephal.Models.Projects;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class UserWorkspaceService : Service<UserWorkspace, object>
    {
        public static UserWorkspace UserWorkspace { get; set; }

     
        public event Action OnChange;             

        public UserWorkspaceService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {

        }
    

        public async Task<UserWorkspace> GetUserWorkspace()
        {
            string uri = "projects/user-workspace";

            string responseMessage = await ExecuteGet(uri);

            try
            {
                UserWorkspace = JsonConvert.DeserializeObject<UserWorkspace>(responseMessage);

            }
            catch (Exception e)
            {
                await JSRuntime.InvokeVoidAsync("console.log", "message d'exception: ", e.Message);
            }

            return UserWorkspace;

        }


        public async Task<UserWorkspace> GetUserWorkspaceByClient(long? subscriptionId)
        {
            string baseUri = "projects/user-workspace";
            string uri = $"{baseUri}/{subscriptionId}";
        
            string responseMessage = await ExecuteGet(uri);
            try
            {
                UserWorkspace = JsonConvert.DeserializeObject<UserWorkspace>(responseMessage);
              //  await JSRuntime.InvokeVoidAsync("console.log", "Result of getting userworkspace : ", UserWorkspace);

            }
            catch (Exception e)
            {
                await JSRuntime.InvokeVoidAsync("console.log", "message d'exception: ", e.Message);
            }

            return UserWorkspace;

        }

        public void SetUserWorkspace(UserWorkspace userwkspace)
        {
            UserWorkspace = userwkspace;
            NotifyStateChanged();
        }
        private void NotifyStateChanged() => OnChange?.Invoke();

    }


}
