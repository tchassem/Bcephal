using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Dimensions;
using System.Linq;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System.Threading.Tasks;
using System.Collections.ObjectModel;
using Bcephal.Models.Grids;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts
{
    public partial class ReportingChartForm : Form<DashboardReport, BrowserData>
    {
        public override string LeftTitle { get => AppState["Chart"]; }

        public override bool usingUnitPane => false;
        public override string LeftTitleIcon { get => "bi-file-plus"; }

        protected override string DuplicateName()
        {
            return AppState["duplicate.reporting.chart.name", EditorData.Item.Name];
        }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        [Inject]
        public DashboardReportService DashboardReportService { get; set; }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedReportingChart;
                var second = AppState.PrivilegeObserver.CanEditReportingChart(EditorData.Item);
                return first || second;
            }
        }

        #region Input Parameters and properties section

        [Parameter]
        public long? ChartId { get; set; }

        public ListChangeHandler<DashboardReportField> FieldList
        {
            get
            {
                return EditorData.Item.FieldListChangeHandler;
            }
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
        public bool IsSmallScreen { get; set; }
        private string UserFilterKey = "ChartUserFilterKey";
        private string AdminFilterKey = "ChartAdminFilterKey";
        public int ActiveTabIndexFilter { get; set; } = 0;
        int ActiveTabIndex { get; set; } = 0;
        public string Filterstyle { get; set; } = "";

        #endregion


        #region Region reserved for Methods

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await  base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                AppState.PublishedHander += Publish;
                AppState.UnPublishedHander += UnPublish;
            }
            if (EditorData != null && EditorData.Item != null )
            {
                AppState.CanUnPublished = EditorData.Item.Published;
                AppState.CanPublished = !EditorData.Item.Published;
            }
        }

        public override string GetBrowserUrl { get => Route.BROWSER_REPORTING_CHART; set => base.GetBrowserUrl = value; }
        protected override void AfterInit(EditorData<DashboardReport> EditorData)
        {
            if (EditorData != null && EditorData.Item != null && string.IsNullOrWhiteSpace(EditorData.Item.ReportType))
            {
                EditorData.Item.DashboardReportType = DashboardItemType.CHART;
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

        protected override DashboardReportService GetService()
        {
            return DashboardReportService;
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.CanUnPublished = false;
            AppState.CanPublished = false;
            AppState.PublishedHander -= Publish;
            AppState.UnPublishedHander -= UnPublish;
            AppState.Update = false;
            AppState.CanLoad = false;
            await base.DisposeAsync();
        }

        private void ShowRightContent()
        {
            RefreshRightContent(RightContent_);
        }
        private void HideRightContent()
        {
            RefreshRightContent(null);
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            var filter = new EditorDataFilter();
            filter.SubjectType = DashboardItemType.CHART.Code;
            return filter;
        }
                    
        public void OnHierarchicalDataAdded(HierarchicalData data)
        {
            DimensionType type = DimensionType.ATTRIBUTE;
            if (data is Period)
            {
                type = DimensionType.PERIOD;
            }
            else if (data is Measure)
            {
                type = DimensionType.MEASURE;
            }
            var matches = FieldList.GetItems().Where(p => p.Type == type && p.DimensionId == data.Id);
            if (!matches.Any())
            {
                EditorData.Item
                    .AddField(new DashboardReportField()
                    {
                        DimensionId = data.Id,
                        Name = data.Name,
                        Type = type,
                        DimensionName = data.Name
                    });
                AppState.Update = true;
            }
            // StateHasChanged();
        } 
        
        public void OnFieldDeleted(DashboardReportField dim)
        {
            if (FieldList.GetItems().Contains(dim))
            {
                EditorData.Item.DeleteOrForgetField(dim);
                AppState.Update = true;
            }
        }

        #endregion
    }
}
