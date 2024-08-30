using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Settings;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Pages.Setting
{
    // [RouteAttribute("/browser-incremental-numbers")]
    public partial class IncrementalNumberBrowser :  AbstractNewGridComponent<IncrementalNumber, IncrementalNumberBrowserData>
    {

        [Inject]
        public IncrementalNumberService IncrementalNumberService { get; set; }

        protected dynamic[] GridColumns => new[] {
                            new {CaptionName = AppState["Name"] ,ColumnWidth="15%",  ColumnName = nameof(IncrementalNumberBrowserData.Name), ColumnType = typeof(string)},
                            new {CaptionName = AppState["Increment"] ,ColumnWidth="6%", ColumnName = nameof(IncrementalNumberBrowserData.IncrementValue), ColumnType = typeof(long)},
                            new {CaptionName = AppState["InitialValue"] ,ColumnWidth="6%", ColumnName = nameof(IncrementalNumberBrowserData.InitialValue), ColumnType = typeof(long)},
                            new {CaptionName = AppState["MinimumValue"] ,ColumnWidth="6%", ColumnName = nameof(IncrementalNumberBrowserData.MinimumValue), ColumnType = typeof(long)},
                            new {CaptionName = AppState["MaximumValue"] ,ColumnWidth="7%", ColumnName = nameof(IncrementalNumberBrowserData.MaximumValue), ColumnType = typeof(long)},
                            new {CaptionName = AppState["Cycle"] ,ColumnWidth="6%", ColumnName = nameof(IncrementalNumberBrowserData.Cycle), ColumnType = typeof(bool)},
                            new {CaptionName = AppState["Size"] ,ColumnWidth="5%", ColumnName = nameof(IncrementalNumberBrowserData.Size), ColumnType = typeof(long)},
                            new {CaptionName = AppState["Prefix"] ,ColumnWidth="7%", ColumnName = nameof(IncrementalNumberBrowserData.Prefix), ColumnType = typeof(string)},
                            new {CaptionName = AppState["Suffix"] ,ColumnWidth="7%", ColumnName = nameof(IncrementalNumberBrowserData.Suffix), ColumnType = typeof(string)},
                            new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="9%", ColumnName = nameof(IncrementalNumberBrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                            new {CaptionName = AppState["CreationDate"] ,ColumnWidth="13%", ColumnName = nameof(IncrementalNumberBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                            new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="13%", ColumnName = nameof(IncrementalNumberBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(IncrementalNumberBrowserData item, string propName)
        {
            return item.GetType().GetProperty(propName).GetValue(item, null);
        }

        protected override object GetFieldValue(IncrementalNumberBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(IncrementalNumberBrowserData.Id);
        }

        protected override object KeyFieldValue(IncrementalNumberBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_INCREMENTAL_NUMBER;
        }

        protected override Task OnRowInserting(IncrementalNumberBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(IncrementalNumberBrowserData dataItem)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowUpdating(IncrementalNumberBrowserData dataItem, IncrementalNumberBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task<BrowserDataPage<IncrementalNumberBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return IncrementalNumberService.Search(filter);
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_INCREMENTAL_NUMBER;
            IsNavLink = true;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanCreate = true;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
