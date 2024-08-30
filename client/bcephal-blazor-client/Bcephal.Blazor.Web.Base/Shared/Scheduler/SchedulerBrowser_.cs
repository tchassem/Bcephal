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

namespace Bcephal.Blazor.Web.Base.Shared.Scheduler
{
    public abstract class SchedulerBrowser_ : AbstractNewGridComponent<SchedulableObject, SchedulerBrowserData> 
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = "Logs" ,ColumnWidth="45px",  ColumnName = "Logs", ColumnType = typeof(string)},
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CurrentlyExecuting"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.IsInEditMode), ColumnType = typeof(bool)},
                        new {CaptionName = AppState["cron.expression"] ,ColumnWidth="100px",  ColumnName = nameof(SchedulerBrowserData.Cron), ColumnType = typeof(string)},
                        new {CaptionName = AppState["last.execution.time"],ColumnWidth="100px", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["next.execution.time"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                       };

        [Inject]
        public SchedulerService schedulerService { get; set; }

        private List<long> SelectedItems_ { get; set; }
        protected List<long> UnSelectedItems { get; set; }
        protected List<long> SelectedItems
        {
            get { return SelectedItems_; }
            set
            {
                SelectedItems_ = value;
            }
        }
        protected NewGridDataItem selectedItem { get; set; }

        public string Type { get; set; }
        
        protected  override Task<BrowserDataPage<SchedulerBrowserData>> SearchRows(BrowserDataFilter filter)
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

        protected async void Restart()
        {
            SchedulerType sType = (SchedulerType)Enum.Parse(typeof(SchedulerType), Type);
            await schedulerService.restart(AppState.ProjectCode, sType, BuildSelectedItems());
        }

        private ObservableCollection<long?> BuildSelectedItems()
        {
            ObservableCollection<long?> ids = new();
            foreach(long id in SelectedItems){
                ids.Add(id);
            }
            return ids;
        }

        protected async void Stop()
        {
            SchedulerType sType = (SchedulerType)Enum.Parse(typeof(SchedulerType), Type);
            await schedulerService.stop(AppState.ProjectCode, sType, BuildSelectedItems());
        }


        public override ValueTask DisposeAsync()
        {
            AppState.ActivateSchedulingFileLoaderVisible = false;
            AppState.RestartSchedulerHander -= Restart;
            AppState.CanRestart = false;
            AppState.StopSchedulerHander -= Stop;
            AppState.CanStop = false;
            //AppState.ActivateSchedulingFileLoaderHandler -= schedulingHandler;
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
            AppState.CanRestart = true && !AppState.IsDashboard;
            AppState.CanStop = true && !AppState.IsDashboard;
            AppState.CanRefresh = true && !AppState.IsDashboard;
            AppState.CanCreate = true && !AppState.IsDashboard;
        }

        protected override int ItemsCount => GridColumns.Length;

        protected override object GetFieldValue(SchedulerBrowserData item, int grilleColumnPosition)
        {
            if (grilleColumnPosition == 0) {
                return "Logs";
            }
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(SchedulerBrowserData.Id);
        }
        protected override object KeyFieldValue(SchedulerBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return ""; 
        }

        protected override Task OnRowInserting(SchedulerBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override  Task OnRowRemoving(SchedulerBrowserData dataItem)
        {
            return Task.CompletedTask;
        }

        protected override  Task OnRowRemoving(IReadOnlyList<object> ids)
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
              await  AppState.NavigateTo(link);
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
                AppState.RestartSchedulerHander += Restart;
                AppState.StopSchedulerHander += Stop;
                // AppState.ActivateSchedulingFileLoaderHandler += schedulingHandler;
            }
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
