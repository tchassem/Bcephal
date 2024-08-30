using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Blazor.Web.Sourcing.Pages.Grille;
using Microsoft.AspNetCore.Components;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Report
{
   public class ReportPublicationBrowser: GridBrowser
    {
        [Inject] ReportPublicationService ReportPublicationService { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = "";
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            AppState.CanRun = false;
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

        public override ReportPublicationService GetService()
        {
            return ReportPublicationService;
        }

        protected override string NavLinkURI()
        {
             return "#";
        }
    }
}