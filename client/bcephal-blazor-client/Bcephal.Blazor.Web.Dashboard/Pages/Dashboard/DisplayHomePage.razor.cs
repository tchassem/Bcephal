using Bcephal.Blazor.Web.Base.Services;
using Microsoft.AspNetCore.Components;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
   public partial class DisplayHomePage 
    {
        [Parameter]
        public string projectId { get; set; }
        [Inject]
        public AppState AppState { get; set; }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            AppState.RefreshHomePageHander -= StateHasChanged;
            AppState.RefreshHomePageHander += StateHasChanged;
            return base.OnAfterRenderAsync(firstRender);
        }
    }
}
