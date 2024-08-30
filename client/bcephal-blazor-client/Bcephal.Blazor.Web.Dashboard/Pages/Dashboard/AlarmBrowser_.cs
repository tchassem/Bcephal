using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Alarms;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
    public class AlarmBrowser_ : AbstractNewGridComponent<Alarm, BrowserData>
    {
        [Inject]
        public AlarmService AlarmService { get; set; }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        };

        protected override int ItemsCount => GridColumns.Length;
        public virtual AlarmService GetService()
        {
            return AlarmService;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_REPORT_ALARM;
            IsNavLink = true;
            ClearFilterButtonVisible = false;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.DashboardingAlarmCreateAllowed && AppState.PrivilegeObserver.DashboardingCreateAllowed)
            //{
                DeleteButtonVisible = true;
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            //}
        }
        public override async ValueTask DisposeAsync()
        {
            AppState.Hander = null;
            AppState.CreateHander = null;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.DashboardingAlarmCreateAllowed && AppState.PrivilegeObserver.DashboardingCreateAllowed)
            //{
                AppState.CanCreate = false;
            //}
            AppState.CanRefresh = true;
            await base.DisposeAsync();
        }

        protected override BrowserData NewItem()
        {
            return null;
        }

        protected override string KeyFieldName()
        {
            return nameof(Alarm.Id);
        }
        protected override object KeyFieldValue(BrowserData item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.EDIT_REPORT_ALARM;
        }
        protected override Task OnRowInserting(BrowserData newValues)
        {
            return Task.CompletedTask;
        }


        protected override  Task<BrowserDataPage<BrowserData>> SearchRows(BrowserDataFilter filter)
        {            
            return GetService().Search(filter);
        }

        protected override Task OnRowRemoving(BrowserData dataItem)
        {
            return GetService().Delete(new List<long>() { dataItem.Id.Value });
        }

        protected async  override Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((BrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }
        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }


        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }
        protected override Task OnRowUpdating(BrowserData dataItem, BrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

    }

}
