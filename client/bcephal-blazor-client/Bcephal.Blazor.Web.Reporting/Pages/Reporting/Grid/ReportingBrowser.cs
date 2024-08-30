using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Blazor.Web.Sourcing.Pages.Grille;
using Microsoft.AspNetCore.Components;
using System.Threading.Tasks;
using System;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Grid
{
    //[RouteAttribute("browser-reporting-grid/")]
    public class ReportingBrowser: GridBrowser
    {
        [Inject]  ReportGridService ReportGridService { get; set; }

        public override ReportGridService GetService()
        {
            return ReportGridService;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_REPORTING_GRID;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
            AppState.CanRefresh = true && !AppState.IsDashboard;
        }

        protected override void CanCreate_()
        {
            if (AppState.PrivilegeObserver.CanCreatedReportingReportGrid)
            {
                AppState.CanCreate = true && !AppState.IsDashboard;
                AppState.CanRefresh = true && !AppState.IsDashboard;
                CanCreate = true && !AppState.IsDashboard;
                CanRefresh = true && !AppState.IsDashboard;
            }
        }

        protected override void DisposeCreate()
        {
            AppState.CanCreate = false;
            AppState.CanRefresh = false;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_REPORTING_GRID;
        }

    }
}
