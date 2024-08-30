using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
   public partial class DashboardPage
    {
        [Parameter]
        public string projectId { get; set; }
        [CascadingParameter]
        public Error Error { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public DashboardService DashboardService { get; set; }

        ObservableCollection<Models.Dashboards.Dashboard> Dashboards { get; set; }

        private Models.Dashboards.Dashboard DashBoardBinding { get; set; }


        protected override async Task OnInitializedAsync()
        {
            await GetDashBoard_();
            await base.OnInitializedAsync();
        }
        private async Task GetDashBoard_()
        {
            try
            {
                AppState.ShowLoadingStatus(AppState.LoadingTypeSender.DASHBOARD);
                BrowserDataPage<Models.Dashboards.Dashboard> page = await DashboardService.Search(new() {ProfileId = AppState.ProfilId });
                Dashboards = page.Items;
                if (Dashboards.Count > 0 && DashBoardBinding == null)
                {
                    DashBoardBinding = Dashboards[0];
                }
                AppState.CanRenderDashboard = Dashboards.Count > 0;
                AppState.CanRefresh = true;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                Dashboards = new ObservableCollection<Models.Dashboards.Dashboard>();
            }
            finally
            {
                AppState.HideLoadingStatus(AppState.LoadingTypeSender.DASHBOARD);
                StateHasChanged();
            }
        }

        private async void GetDashBoard()
        {
            await GetDashBoard_();
        }


        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                AppState.DashboardHander += SelectedDashdoard;
                AppState.RefreshHander += GetDashBoard;
                AppState.EnableClientAndProfile();               
            }
             
        }


        public ValueTask DisposeAsync()
        {
            AppState.DashboardHander -= SelectedDashdoard;
            AppState.RefreshHander -= GetDashBoard;
            AppState.CanRenderDashboard = false;
            AppState.CanRefresh = false;
            AppState.DesableClientAndProfile();
            return ValueTask.CompletedTask;
        }
    }
}
