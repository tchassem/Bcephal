using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Schedulers;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Pages
{
    
    public class AdminSchedulerBrowser_ : AbstractNewGridComponent<SchedulableObject, SchedulerBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = "Logs" ,ColumnWidth="45px",  ColumnName = "Logs", ColumnType = typeof(string)},
                        new {CaptionName = AppState["project"] ,ColumnWidth="100px",  ColumnName = nameof(SchedulerBrowserData.ProjectName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="100px",  ColumnName = nameof(SchedulerBrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CurrentlyExecuting"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.IsInEditMode), ColumnType = typeof(bool)},
                        new {CaptionName = AppState["cron.expression"] ,ColumnWidth="100px",  ColumnName = nameof(SchedulerBrowserData.Cron), ColumnType = typeof(string)},
                        new {CaptionName = AppState["last.execution.time"],ColumnWidth="100px", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["next.execution.time"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                       };

        [Inject]
        public SchedulerService schedulerService { get; set; }

        public string Type { get; set; } = SchedulerTypes.ALL;

        protected override Task<BrowserDataPage<SchedulerBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            SchedulerBrowserDataFilter schedulerBrowserDataFilter = new SchedulerBrowserDataFilter()
            {
                PageSize = filter.PageSize,
                OrderAsc = filter.OrderAsc,
                ShowAll = filter.ShowAll,
                AllowRowCounting = filter.AllowRowCounting,
                ObjectType = Type,
                ProjectCode = AppState.ProjectCode,
            };

           return schedulerService.Search<SchedulerBrowserData>(schedulerBrowserDataFilter);
        }

        private ObservableCollection<long?> BuildSelectedItems()
        {
            ObservableCollection<long?> ids = new();
            foreach (long id in GetSelectionDataItemsIds())
            {
                ids.Add(id);
            }
            return ids;
        }

        protected async void Stop()
        {
            SchedulerType sType = (SchedulerType)Enum.Parse(typeof(SchedulerType), Type);
            bool val = await schedulerService.stop(AppState.ProjectCode, sType, BuildSelectedItems());
            if (val)
            {
                await this.RefreshGrid_();
            }
        }

        public override ValueTask DisposeAsync()
        {
            AppState.ActivateSchedulingFileLoaderVisible = false;
            AppState.StopSchedulerHander -= Stop;
            AppState.CanStop = false;
            return base.DisposeAsync();
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            AppState.CanStop = true;
            AppState.CanRefresh = true && !AppState.IsDashboard;
            AppState.CanCreate = true && !AppState.IsDashboard;
        }

        protected override int ItemsCount => GridColumns.Length;

        protected override object GetFieldValue(SchedulerBrowserData item, int grilleColumnPosition)
        {
            if (grilleColumnPosition == 0)
            {
                return "Logs";
            }
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(SchedulerBrowserData.ObjectId);
        }
        protected override object KeyFieldValue(SchedulerBrowserData item)
        {
            return item.ObjectId;
        }
        
        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(SchedulerBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(SchedulerBrowserData dataItem)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {

            }
            return Task.CompletedTask;
        }

        protected async override Task OnRowUpdating(SchedulerBrowserData dataItem, SchedulerBrowserData newValues)
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

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.ActivateSchedulingFileLoaderVisible = true;
                AppState.StopSchedulerHander += Stop;            }
            return base.OnAfterRenderAsync(firstRender);
        }

        private object GetPropertyValue(SchedulerBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}
