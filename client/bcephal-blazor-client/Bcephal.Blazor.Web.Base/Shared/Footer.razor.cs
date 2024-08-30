using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
    public partial class Footer : ComponentBase
    {
        [Inject] private AppState AppState { get; set; }
        [Inject] private IJSRuntime JSRuntime { get; set; }

        [CascadingParameter] public Error Error { get; set; }
        private ConcurrentDictionary<string, RenderFragment> templates { get; set; } = new();


        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                AppState.ProgressBarsHander -= AddProgressBar;
                AppState.ProgressBarsHander += AddProgressBar;
                AppState.RefreshFooterHandler += StateHasChanged;
            }
        }


        public Task AddProgressBar(SocketJS webSocket)
        {
            templates.TryAdd(webSocket.Id, RenderProgressBar(webSocket));
            StateHasChanged();
            return Task.CompletedTask;
        }


        public Task RemoveProgressBar(string key)
        {
            var element = templates.Where(select => select.Key == key);
            if (element != null && element.Count() > 0)
            {
                templates.TryRemove(element.FirstOrDefault());
                StateHasChanged();
            }
            return Task.CompletedTask;
        }


        

        private async void Stop(SocketJS webSocket)
        {
            try{ 
                webSocket.send("STOP");
                await  RemoveProgressBar(webSocket.Id);
            }
            catch(Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        public async ValueTask DisposeAsync()
        {
            await JSRuntime.InvokeVoidAsync("console.log", "call dispose from footer : ");
            GC.SuppressFinalize(this);
            AppState.RefreshFooter();
        }
    }
}
