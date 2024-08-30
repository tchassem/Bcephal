using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;
using DevExtreme.AspNet.Data;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Services.Mocks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Dashboards;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts
{
   // [RouteAttribute("browser-reporting-chart/")]
    public partial class BrowserReportingChart : AbstractNewGridComponent<DashboardReport, BrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="25%", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"],ColumnWidth="20%", ColumnName = nameof(BrowserData.Group), ColumnType = typeof(BGroup)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="20%", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="20%", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="15%", ColumnName = nameof(BrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public DashboardReportService DashboardReportService { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_REPORTING_CHART;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (AppState.PrivilegeObserver.CanCreatedReportingChart)
            {
                AppState.CanCreate = true && !AppState.IsDashboard;
                AppState.CanRefresh = true && !AppState.IsDashboard;
                CanCreate = true && !AppState.IsDashboard;
                CanRefresh = true && !AppState.IsDashboard;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            if (AppState.PrivilegeObserver.CanCreatedReportingChart)
            {
                AppState.CanCreate = false;
                AppState.CanRefresh = false;
            }
            return base.DisposeAsync();
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }
        protected override string KeyFieldName()
        {
            return nameof(DashboardReport.Id);
        }

        protected override object KeyFieldValue(BrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_REPORTING_CHART;
        }

        protected override Task OnRowInserting(BrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected async override Task OnRowUpdating(BrowserData dataItem, BrowserData newValues)
        {
            try
            {
                string link = NavLinkURI();
                if (link.Trim().EndsWith("/"))
                {
                    link += dataItem.Id;
                }
                else
                {
                    link += "/" + dataItem.Id;
                }
                await AppState.NavigateTo(link);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected override async Task OnRowRemoving(BrowserData dataItem)
        {
            await DashboardReportService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((BrowserData)obj).Id.Value).ToList();
                await DashboardReportService.Delete(idss);
            }
        }
    

        protected override Task<BrowserDataPage<BrowserData>> SearchRows(BrowserDataFilter filter)
        {
            filter.ReportType = DashboardItemType.CHART.Code;
            return DashboardReportService.Search(filter);
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }

}
