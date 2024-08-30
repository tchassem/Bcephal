
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Loaders;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    //[RouteAttribute("/projects/file-loaders")]
    public class ListFileLoaders : AbstractNewGridComponent<Models.Loaders.FileLoader, FileLoaderBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"], ColumnWidth="20%", ColumnName = nameof(FileLoaderBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"], ColumnWidth="25%", ColumnName = nameof(FileLoaderBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"], ColumnWidth="25%", ColumnName = nameof(FileLoaderBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["FileSeparator"], ColumnWidth="10%", ColumnName = nameof(FileLoaderBrowserData.FileSeparator), ColumnType = typeof(string)},
                        new {CaptionName = AppState["VisibleInShortcut"], ColumnWidth="20%", ColumnName = nameof(FileLoaderBrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
        };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(FileLoaderBrowserData obj, string propName) { 
            return obj.GetType().GetProperty(propName).GetValue(obj, null); 
        }

        public bool Editable => AppState.PrivilegeObserver.CanCreatedSourcingFileLoader;

        [Inject] public FileLoaderService FileLoaderService { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.NEW_LOAD_FILE;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
            IsNavLink = true; 
          
            AppState.CanRefresh = true && !AppState.IsDashboard;
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                AppState.CanCreate = Editable && !AppState.IsDashboard;
            }
        }

        public async override ValueTask DisposeAsync()
        {
            if (AppState.CanCreate)
            {
                AppState.CanCreate = false;
            }
            AppState.CanRefresh = false;
            await base.DisposeAsync();
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(FileLoaderBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(FileLoaderBrowserData.Id);
        }

        protected override object KeyFieldValue(FileLoaderBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.NEW_LOAD_FILE;
        }

        protected override Task OnRowInserting(FileLoaderBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected async override Task OnRowUpdating(FileLoaderBrowserData dataItem, FileLoaderBrowserData newValues)
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

        protected override Task OnRowRemoving(FileLoaderBrowserData dataItem)
        {
            return FileLoaderService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected async override  Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((FileLoaderBrowserData)obj).Id.Value).ToList();
                await FileLoaderService.Delete(idss);
            }
        }

        protected override  Task<BrowserDataPage<FileLoaderBrowserData>> SearchRows(BrowserDataFilter filter)
        {
           return FileLoaderService.Search(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }

}
