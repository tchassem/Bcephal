using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    //[RouteAttribute("/reconciliation-auto-reco-log")]
    public class LogAutoRecoBrowser : AbstractGridComponent<AutoReco, RecoBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="13%",  ColumnName = nameof(RecoBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Reconciliation.RecoType"] ,ColumnWidth="10%", ColumnName = nameof(RecoBrowserData.RecoType), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User"] ,ColumnWidth="13%", ColumnName = nameof(RecoBrowserData.User), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Mode"] ,ColumnWidth="5%", ColumnName = nameof(RecoBrowserData.Mode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="7%", ColumnName = nameof(RecoBrowserData.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDateTime"] ,ColumnWidth="auto", ColumnName = nameof(RecoBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDateTime"] ,ColumnWidth="auto", ColumnName = nameof(RecoBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["End"] ,ColumnWidth="7%", ColumnName = nameof(RecoBrowserData.End), ColumnType = typeof(string)},

                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(RecoBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public AutoRecoLogService AutoRecoLogService { get; set; }

        public virtual AutoRecoLogService GetService()
        {
            return AutoRecoLogService;
        }
        public override ValueTask DisposeAsync()
        {
            AppState.Hander = null;
            AppState.ActivateSchedulingLogAutoRecoVisible = false;
            AppState.ActivateSchedulingLogAutoRecoHandler -= schedulingHandler;
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
            NewRowButtonVisible = false;
            AppState.ActivateSchedulingLogAutoRecoInit = await GetService().CanStart();
        }

        private async void schedulingHandler()
        {
            if (AppState.ActivateSchedulingLogAutoReco_)
            {
                await GetService().start();
                AppState.Refresh();
            }
            else
            {
                await GetService().stop();
                AppState.Refresh();
            }
        }


        protected override RecoBrowserData NewItem()
        {
            return null;
        }

        //protected override Type GetColumnTyepAt(int position)
        //{
        //    return GridColumns[position].ColumnType;
        //}


        protected override object GetFieldValue(RecoBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(RecoBrowserData.Id);
        }
        protected override object KeyFieldValue(RecoBrowserData item)
        {

            return item.Id;
        }



        protected override string NavLinkURI()
        {
            return "";
        }
        //protected override string GetColumnWidth(int position)
        //{
        //    return GridColumns[position].ColumnWidth;
        //}

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(RecoBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await GetService().Delete(ids);
            }
        }

        protected async override Task OnRowUpdating(RecoBrowserData dataItem, Dictionary<string, object> newValues)
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

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<RecoBrowserData> page_, DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<RecoBrowserData> page = await GetService().Search(filter);

            foreach (RecoBrowserData row in page.Items)
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

        protected async override Task OnAddNewClick()
        {
            try
            {
                await AppState.NavigateTo(NavLinkURI());
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

        //protected override string GetColumnNameAt(int position)
        //{
        //    return GridColumns[position].ColumnName;
        //}

        //protected override string GetCaptionNameAt(int position)
        //{
        //    return GridColumns[position].CaptionName;
        //}

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanRefresh = true && !AppState.IsDashboard;
                AppState.ActivateSchedulingLogAutoRecoVisible = true && !AppState.IsDashboard;
                AppState.ActivateSchedulingLogAutoRecoHandler += schedulingHandler;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
