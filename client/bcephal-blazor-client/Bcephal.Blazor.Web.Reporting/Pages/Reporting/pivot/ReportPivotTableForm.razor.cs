using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.pivot
{
   public partial class ReportPivotTableForm : Form<DashboardReport, BrowserData>
    {
        [Inject]
        public DashboardReportService DashboardReportService { get; set; }

        public override bool usingUnitPane => false;

        public bool IsSmallScreen { get; set; }

        protected override DashboardReportService GetService()
        {
            return DashboardReportService;
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.report.pivot.table.name", EditorData.Item.Name];
        }
        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedReportingPivotTable;
                var second = AppState.PrivilegeObserver.CanEditReportingPivotTable(EditorData.Item);
                return first || second;
            }
        }
        public override string GetBrowserUrl { get => Route.BROWSER_REPORT_PIVOT_TABLE; set => base.GetBrowserUrl = value; }
        protected override EditorDataFilter getEditorDataFilter()
        {
            var filter = new EditorDataFilter();
            filter.SubjectType = DashboardItemType.PIVOT_TABLE.Code;
            return filter;
        }

        public override string LeftTitle { get { return AppState["Pivot.Table"]; } }

        public override string LeftTitleIcon { get { return "bi-file-plus"; } }


        private string UserFilterKey = "PivotUserFilterKey";
        private string AdminFilterKey = "PivotAdminFilterKey";

        public int ActiveTabIndexFilter { get; set; } = 0;
        public string Filterstyle { get; set; } = "";

        private void RefreshDesignContent()
        {
            RefreshRightContent(RightContent_);
        }
        private void RefreshDataContent()
        {
            RefreshRightContent(null);
        }

        protected override void AfterInit(EditorData<DashboardReport> EditorData)
        {
            if(EditorData != null && EditorData.Item != null && string.IsNullOrWhiteSpace(EditorData.Item.ReportType))
            {
                EditorData.Item.ReportType = DashboardItemType.PIVOT_TABLE.Code;
            }
            if (EditorData != null && EditorData.Item != null && EditorData.Item.UserFilter == null)
            {
                EditorData.Item.UserFilter = new();
            }
            if (EditorData != null && EditorData.Item != null && EditorData.Item.AdminFilter == null)
            {
                EditorData.Item.AdminFilter = new();
            }
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.PublishedHander += Publish;
                AppState.UnPublishedHander += UnPublish;
            }
            if(EditorData != null && EditorData.Item != null)
            {
                AppState.CanUnPublished = EditorData.Item.Published;
                AppState.CanPublished = !EditorData.Item.Published;
            }
            return base.OnAfterRenderAsync(firstRender);
        }
        private void Publish()
        {
            EditorData.Item.Published = true;
            save();
        }
           private void UnPublish()
        {
            EditorData.Item.Published = false;
            save();
        }

        public override ValueTask DisposeAsync()
        {
            AppState.PublishedHander -= Publish;
            AppState.UnPublishedHander -= UnPublish;
            AppState.CanUnPublished = false;
            AppState.CanPublished = false;
            base.DisposeAsync();
            return ValueTask.CompletedTask;
        }

        public UniverseFilter UserFilterBinding
        {
            get { return EditorData.Item.UserFilter; }
            set
            {
                EditorData.Item.UserFilter = value;
                AppState.Update = true;
            }
        }

        public UniverseFilter AdminFilterBinding
        {
            get { return EditorData.Item.AdminFilter; }
            set
            {
                EditorData.Item.AdminFilter = value;
                AppState.Update = true;
            }
        }
    }
}
