using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;

using Microsoft.AspNetCore.Components;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Pages.Project
{
    public partial class HomePage : IAsyncDisposable
    {
        [Inject]  private AppState AppState { get; set; }

        public override async Task SetParametersAsync(ParameterView parameters)
        {
            await base.SetParametersAsync(parameters);
            if (AppState.ClientId.HasValue && AppState.ProfilId.HasValue)
            {
                await AppState.InitUserWorkspace();
            }
        }

        async Task InitApplicationWorkspace()
        {
            AppState.ShowLoadingStatus();
            await AppState.InitApp();            
            AppState.HideLoadingStatus();
        }
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                await InitApplicationWorkspace();
            }
            await base.OnAfterRenderAsync(firstRender);
        }

        public ValueTask DisposeAsync()
        {
            return ValueTask.CompletedTask;
        }
    }
}
