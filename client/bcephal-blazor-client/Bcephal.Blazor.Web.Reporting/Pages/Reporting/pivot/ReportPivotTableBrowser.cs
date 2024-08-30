using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.pivot
{
   //[RouteAttribute("/browser-report-pivot-table")]
    public class ReportPivotTableBrowser : AbstractNewGridComponent<DashboardReport, BrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="48%", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="25%", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="25%", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        protected override string getCommandColumnWidth()
        {
            return "2%";
        }

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }


        [Inject]
        public DashboardReportService DashboardReportService { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_REPORT_PIVOT_TABLE;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
        }


        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (AppState.PrivilegeObserver.CanCreatedReportingPivotTable)
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
            if (AppState.PrivilegeObserver.CanCreatedReportingPivotTable)
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
            return nameof(BrowserData.Id);
        }

        protected override object KeyFieldValue(BrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_REPORT_PIVOT_TABLE;
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
                await  AppState.NavigateTo(link);
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
            filter.ReportType = DashboardItemType.PIVOT_TABLE.Code;
            return DashboardReportService.Search(filter);
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
