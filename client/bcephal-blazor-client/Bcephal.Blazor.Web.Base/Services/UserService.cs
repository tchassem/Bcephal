using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Bcephal.Models.Security;
using Microsoft.JSInterop;
using Newtonsoft.Json;


namespace Bcephal.Blazor.Web.Base.Services
{
    public class UserService : Service<User, object>
    {
        public static string DefaultLanguage  { get; set; }

        public static User User { get; set; }
        public UserService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            DefaultLanguage = "en";
        }

        public string UserName { get; set; }
        public async Task<string> GetUserName()
        {
            User user = await GetUser();
           
            if (user != null && !string.IsNullOrWhiteSpace(user.DefaultLanguage))
            {
                UserName = user.Name;
                User = user;
                user.DefaultLanguage = CultureInfo.CurrentCulture.TwoLetterISOLanguageName;
                DefaultLanguage = user.DefaultLanguage;
            }
            return UserName; 
        }


        public async Task<User> GetUser()
        {
            string uri = "users/connected-user-info";
            string userInfo = await ExecuteGet(uri);
            User user = JsonConvert.DeserializeObject<User>(userInfo);
            return user;
        }


        public async Task<string> GetStateId()
        {
            string uri = "state-id";
            await JSRuntime.InvokeVoidAsync("console.log", "call  GetStateId ! ");
            HttpResponseMessage response = await ExecuteRequest(uri, HttpMethod.Get);
            await JSRuntime.InvokeVoidAsync("console.log", "response.Headers : ", response.Headers);
            response.Headers.TryGetValues("state-id", out IEnumerable<string> ids);
            if (ids != null)
            {
                return ids.First();
            }
            return null;
        }
    }
}
