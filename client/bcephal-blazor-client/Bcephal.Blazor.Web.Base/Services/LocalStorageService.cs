using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class LocalStorageService
    {
        private readonly IJSRuntime _js;

        public LocalStorageService(IJSRuntime js)
        {
            _js = js;
        }

        public async Task<string> GetFromLocalStorage(string key)
        {
            return await _js.InvokeAsync<string>("localStorage.getItem", key).AsTask();
        }

        public async Task SetLocalStorage(string key, object value)
        {
            await _js.InvokeVoidAsync("localStorage.setItem", key, value).AsTask();
        }
        public async Task DeleteLocalStorage(string key)
        {
            await _js.InvokeAsync<string>("localStorage.removeItem", key).AsTask();
        }
    }
}
