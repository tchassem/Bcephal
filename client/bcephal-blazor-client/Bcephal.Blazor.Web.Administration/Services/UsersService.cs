using Bcephal.Models.Users;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Services
{
   public class UsersService : Base.Services.Service<User, User>
    {
        public UsersService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "users";
        }
    }


}
