using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Invoices
{
    //[RouteAttribute("/billing/current-run-status")]
    public class CurrentRunBrowser_ : AbstractGridComponent<BillingModelLog, BrowserData>, IDisposable
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"],ColumnWidth="100px",  ColumnName = nameof(BillingModelLog.BillingName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["BillingType"] ,ColumnWidth="100px", ColumnName = nameof(BillingModelLog.BillingTypeName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User"] ,ColumnWidth="100px", ColumnName = nameof(BillingModelLog.Username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Mode"] ,ColumnWidth="100px", ColumnName = nameof(BillingModelLog.Mode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="100px", ColumnName = nameof(BillingModelLog.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["End"] ,ColumnWidth="100px", ColumnName = nameof(BillingModelLog.EndDate), ColumnType = typeof(DateTime)},
                    };

        protected override int ItemsCount => GridColumns.Length;


        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }


        [Inject]
        public CurrentRunService CurrentRunservice { get; set; }


        private BrowserDataPage<BillingModelLog> pages { get; set; }

        public void Dispose()
        {
            AppState.Hander = null;
        }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            
        }
        protected override BrowserData NewItem()
        {
            return null;
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
            return Route.EDIT_GRID;
        }
        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }


        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BrowserData> page_, DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<BrowserData> page = await CurrentRunservice.Search(filter);
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

        protected override Task OnRowRemoving(BrowserData dataItem)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowUpdating(BrowserData dataItem, Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override Task OnRowRemoving(List<long> id)
        {
            return Task.CompletedTask;
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}
