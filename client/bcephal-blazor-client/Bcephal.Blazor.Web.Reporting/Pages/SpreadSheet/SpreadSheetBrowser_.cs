using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.SpreadSheet
{
    public class SpreadSheetBrowser_ : AbstractGridComponent<Models.Sheets.SpreadSheet, BrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public SpreadSheetService SpreadSheetService { get; set; }


        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            NewRowButtonVisible = false;
            AppState.CanRun = true && !AppState.IsDashboard;
            AppState.CanRefresh = true && !AppState.IsDashboard;
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
            return "";
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(BrowserData dataItem)
        {
            await SpreadSheetService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await SpreadSheetService.Delete(ids);
            }
        }

        protected override Task OnRowUpdating(BrowserData dataItem, Dictionary<string, object> newValues)
        {

            return Task.CompletedTask;
        }

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BrowserData> page_, DataSourceLoadOptionsBase options)
        {

            BrowserDataPage<BrowserData> page = await SpreadSheetService.Search(filter);

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
    }
}
