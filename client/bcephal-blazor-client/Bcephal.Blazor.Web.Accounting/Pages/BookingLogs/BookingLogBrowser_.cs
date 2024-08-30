using Bcephal.Blazor.Web.Accounting.Services;
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

namespace Bcephal.Blazor.Web.Accounting.Pages.BookingLogs
{
    [RouteAttribute("/browser-booking-model-log/")]
    public class BookingLogBrowser_ : AbstractGridComponent<BookingModelLog, BookingModelLogBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["#"] ,ColumnWidth="100px",  ColumnName = nameof(BookingModelLogBrowserData.modelOid), ColumnType = typeof(int)},
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(BookingModelLogBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User.name"] ,ColumnWidth="100px",  ColumnName = nameof(BookingModelLogBrowserData.user), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Mode"] ,ColumnWidth="100px",  ColumnName = nameof(BookingModelLogBrowserData.mode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="100px",  ColumnName = nameof(BookingModelLogBrowserData.status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(BookingModelLogBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="100px", ColumnName = nameof(BookingModelLogBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["End"] ,ColumnWidth="100px", ColumnName = nameof(BookingModelLogBrowserData.endDate), ColumnType = typeof(DateTime?)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BookingModelLogBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public BookingModelLogService BookingModelLogService { get; set; }

        protected override object GetFieldValue(BookingModelLogBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(BookingModelLogBrowserData.Id);
        }

        protected override object KeyFieldValue(BookingModelLogBrowserData item)
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

        protected override async Task OnRowRemoving(BookingModelLogBrowserData dataItem)
        {
            await BookingModelLogService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await BookingModelLogService.Delete(ids);
            }
        }

        protected override Task OnRowUpdating(BookingModelLogBrowserData dataItem, Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BookingModelLogBrowserData> page_, DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<BookingModelLogBrowserData> page = await BookingModelLogService.Search(filter);

            foreach (BookingModelLogBrowserData row in page.Items)
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
    }
}
