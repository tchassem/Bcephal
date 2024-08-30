using Bcephal.Models.Security;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class AboutService :Service<User, object>
    {

        public static string ErrorMessage { get; set; } = "";

        public AboutService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "about";
        }


        public async Task<string> GetBuidNumber()
        {
            string response = await this.ExecuteGet(ResourcePath + "/build-number");
            return response;
        }

    }
}
