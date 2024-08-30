using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
    
    public class ClientStorage
    {
        readonly IJSRuntime JSRuntime;
        public ClientStorage(IJSRuntime jsRuntime)
        {
            JSRuntime = jsRuntime;
        }
        public  void SetCookie(string key, string value, int days = 365)
        {
             ((IJSInProcessRuntime)JSRuntime).InvokeVoid("blazorCulture.WriteCookie", key, value, days);
        }

        public Task<string> GetCookie(string key)
        {
            return JSRuntime.InvokeAsync<string>("blazorCulture.ReadCookie",key).AsTask();
        }

    }
    
}
