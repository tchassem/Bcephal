using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.Grille;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Report.Grid
{
    public partial class ReportingForm : GrilleForm
    {
        [Inject]
        ReportGridService ReportGridService { get; set; }

        public override string GetBrowserUrl { get => Route.BROWSER_REPORTING_GRID; set => base.GetBrowserUrl = value; }

        public override string LeftTitle { get { return AppState["ReportGrid"]; } }

        public override string LeftTitleIcon { get { return "bi-file-plus"; } }
        protected override string DuplicateName()
        {
            return AppState["duplicate.reporting.name", EditorData.Item.Name];
        }
        protected override ReportGridService GetService()
        {
            return ReportGridService;
        }

        public override bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedReportingReportGrid;
                var second = AppState.PrivilegeObserver.CanEditReportingReportGrid(EditorData.Item);
                return first || second;
            }
        }
    }
}
