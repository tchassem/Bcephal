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
    public abstract  class SchedulerLogBrowser_ : AbstractGridComponent<SchedulableObject, SchedulerLogBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="100px",  ColumnName = nameof(SchedulerLogBrowserData.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.CreationDate), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["end.date.time"],ColumnWidth="100px", ColumnName = nameof(SchedulerLogBrowserData.EndDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["message"] ,ColumnWidth="100px", ColumnName = nameof(SchedulerLogBrowserData.Message), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Detail"] ,ColumnWidth="100px", ColumnName = nameof(SchedulerLogBrowserData.Details), ColumnType = typeof(string)},
                       };

        [Inject]
        public SchedulerLogService schedulerLogService { get; set; }

        public string Type { get; set; }

        [Parameter]
        public long? Id { get; set; }
        protected async override Task SearchRows(BrowserDataFilter filter, BrowserDataPage<SchedulerLogBrowserData> page_, DataSourceLoadOptionsBase options)
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

            BrowserDataPage<SchedulerLogBrowserData> page = await schedulerLogService.Search<SchedulerLogBrowserData>(schedulerBrowserDataFilter);
            foreach (SchedulerLogBrowserData row in page.Items)
            {
                page_.Items.Add(row);
            }
            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.PageLastItem;
            page_.PageSize = page.PageSize;
            page_.TotalItemCount = page.TotalItemCount;
        }



        private ObservableCollection<long?> BuildSelectedItems()
        {
            ObservableCollection<long?> ids = new();
            foreach (long id in SelectedItems)
            {
                ids.Add(id);
            }
            return ids;
        }

        public override ValueTask DisposeAsync()
        {
            AppState.ActivateSchedulingFileLoaderVisible = false;
            AppState.CanRestart = false;
            //AppState.RestartSchedulerHander -=  ;
            return base.DisposeAsync();
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            NewRowButtonVisible = false;
            AppState.CanRestart = false;
            AppState.CanStop = false;
            AppState.CanRefresh = true && !AppState.IsDashboard;
        }

        protected override int ItemsCount => GridColumns.Length;

        protected override object GetFieldValue(SchedulerLogBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);

        }

        protected override string KeyFieldName()
        {
            return nameof(SchedulerLogBrowserData.Id);
        }
        protected override object KeyFieldValue(SchedulerLogBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(SchedulerLogBrowserData dataItem)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {

            }
            return Task.CompletedTask;
        }

        protected async override Task OnRowUpdating(SchedulerLogBrowserData dataItem, Dictionary<string, object> newValues)
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

        protected async override Task OnAddNewClick()
        {
            try
            {
              await  AppState.NavigateTo(NavLinkURI());
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected override Task BuildFilter(BrowserDataFilter filter, DataSourceLoadOptionsBase options)
        {
            filter.ColumnFilters = null;
            filter.Criteria = null;
            if (options != null)
            {
                if (options.Sort != null)
                {
                    foreach (var sortItem in options.Sort)
                    {
                        //filter.OrderAsc = sortItem.Selector;
                    }
                }
                if (options.Filter != null)
                {
                    filter.ColumnFilters = null;
                    foreach (IList<object> filterItem in options.Filter)
                    {
                        object ObCriteria = filterItem.ElementAt(2);
                        if (ObCriteria != null)
                        {
                            filter.Criteria = ObCriteria.ToString();
                        }
                    }
                }
            }
            return Task.CompletedTask;
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.ActivateSchedulingFileLoaderVisible = true && !AppState.IsDashboard;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        private object GetPropertyValue(SchedulerLogBrowserData obj, string propName)
        {

            return obj.GetType().GetProperty(propName).GetValue(obj, null);

        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }




    }
}
