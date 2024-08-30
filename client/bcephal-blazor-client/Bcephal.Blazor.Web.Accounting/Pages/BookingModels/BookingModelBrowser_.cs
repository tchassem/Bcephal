using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.BookingModels
{
    [RouteAttribute("/browser-booking-model")]
    
    public class BookingModelBrowser_ : AbstractGridComponent<BookingModel, BrowserData>
    {

        [Inject]
        public BookingModelService BookingModelService { get; set; }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"].ToString(), ColumnWidth="40%",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        //new {CaptionName = AppState["Group"].ToString(), ColumnWidth="27%",  ColumnName = nameof(BookingModel.group), ColumnType = typeof(BGroup)},
                        new {CaptionName = AppState["CreationDate"].ToString(), ColumnWidth="20%",  ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime)},
                        new {CaptionName = AppState["ModificationDate"].ToString(), ColumnWidth="20%",  ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime)},
                        new {CaptionName = AppState["PageLoader.VisibleInShortcut"].ToString(), ColumnWidth="15%",  ColumnName = nameof(BookingModel.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override async Task OnInitializedAsync()
        {
            EditorRoute = Route.EDIT_BOOKING_MODEL;
            IsNavLink = true;
            NewButtonVisible = true;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            NewRowButtonVisible = false;
            AppState.CanRun = false;
            await base.OnInitializedAsync();
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.AccountingBookingModelCreateAllowed && AppState.PrivilegeObserver.AccountingBookingCreateAllowed)
            {
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            }
        }
        public override async ValueTask DisposeAsync()
        {

            AppState.CreateHander = null;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.AccountingBookingModelCreateAllowed && AppState.PrivilegeObserver.AccountingBookingCreateAllowed)
            {
                AppState.CanCreate = false;
            }
            await base.DisposeAsync();
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
            return Route.EDIT_BOOKING_MODEL;
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(BrowserData dataItem)
        {
            await BookingModelService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await BookingModelService.Delete(ids);
            }
        }

        protected async override Task OnRowUpdating(BrowserData dataItem, Dictionary<string, object> newValues)
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

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BrowserData> page_, DataSourceLoadOptionsBase options)
        {

            BrowserDataPage<BrowserData> page = await BookingModelService.Search(filter);

            foreach (BrowserData row in page.Items)
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
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
