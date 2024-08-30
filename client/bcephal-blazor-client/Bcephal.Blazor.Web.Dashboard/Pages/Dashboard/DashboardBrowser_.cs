using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
    public class DashboardBrowser_ : AbstractNewGridComponent<Models.Dashboards.Dashboard, Models.Dashboards.Dashboard>
    {

        [Inject]
        public DashboardService DashboardService { get; set; }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="15%",  ColumnName = nameof(Models.Dashboards.Dashboard.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="10%", ColumnName = nameof(Models.Dashboards.Dashboard.group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="15%", ColumnName = nameof(Models.Dashboards.Dashboard.VisibleInShortcut), ColumnType = typeof(bool)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="auto", ColumnName = nameof(Models.Dashboards.Dashboard.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="auto", ColumnName = nameof(Models.Dashboards.Dashboard.ModificationDateTime), ColumnType = typeof(DateTime?)}
                    };

        protected override int ItemsCount => GridColumns.Length;

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_REPORT_DASHBOARD;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;            
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.DashboardingCreateAllowed && AppState.PrivilegeObserver.DashboardingDashboardCreateAllowed)
            //{
                DeleteButtonVisible = true;
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            //}
            AppState.CanRefresh = true && !AppState.IsDashboard;
            
        }
        public override async ValueTask DisposeAsync()
        {

            AppState.CreateHander = null;
            AppState.Hander = null;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.DashboardingCreateAllowed)
            {
                AppState.CanCreate = false;
            }
            await base.DisposeAsync();
        }

        protected override string KeyFieldName()
        {
            return nameof(Models.Dashboards.Dashboard.Id);
        }
        protected override object KeyFieldValue(Models.Dashboards.Dashboard item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.EDIT_REPORT_DASHBOARD;
        }
        protected override Task OnRowInserting(Models.Dashboards.Dashboard newValues)
        {
            return Task.CompletedTask;
        }


        protected override Task<BrowserDataPage<Models.Dashboards.Dashboard>> SearchRows(BrowserDataFilter filter)
        {
            return DashboardService.Search(filter);
        }

        protected override Task OnRowRemoving(Models.Dashboards.Dashboard dataItem)
        {
            return DashboardService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override Task OnRowUpdating(Models.Dashboards.Dashboard dataItem, Models.Dashboards.Dashboard newValues)
        {
            return Task.CompletedTask;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(Models.Dashboards.Dashboard item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }


        private object GetPropertyValue(Models.Dashboards.Dashboard obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected async override Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((Models.Dashboards.Dashboard)obj).Id.Value).ToList();
                await DashboardService.Delete(idss);
            }
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }

}
