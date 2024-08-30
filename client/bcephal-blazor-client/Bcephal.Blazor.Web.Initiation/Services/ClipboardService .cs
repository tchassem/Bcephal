using Microsoft.JSInterop;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Services
{
    public class ClipboardService
    {
        private readonly IJSRuntime IjsRuntime;

        public ClipboardService(IJSRuntime jsRuntime)
        {
            IjsRuntime = jsRuntime;
        }

        public ValueTask<string> ReadTextAsync()
        {
            return IjsRuntime.InvokeAsync<string>("navigator.clipboard.readText");
        }

        public ValueTask WriteTextAsync(string text)
        {
            return IjsRuntime.InvokeVoidAsync("navigator.clipboard.writeText", text);
        }
    }
}
