using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Loaders;
using DevExpress.Blazor;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{

    public partial  class FileLoaderLogItemBrowser : AbstractNewGridComponent<FileLoaderLogItem, FileLoaderLogItem>
    {

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["File"] ,ColumnWidth="35%",  ColumnName = nameof(FileLoaderLogItem.File), ColumnType = typeof(string)},
                        new {CaptionName = AppState["LineCount"] ,ColumnWidth="5%", ColumnName = nameof(FileLoaderLogItem.LineCount), ColumnType = typeof(int)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="7%", ColumnName = nameof(FileLoaderLogItem.RunStatus), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Message"],ColumnWidth="auto", ColumnName = nameof(FileLoaderLogItem.Message), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(FileLoaderLogItem obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        public long? FileLoaderLogId_ { get; set; }

        [Parameter]
        public EventCallback<long?> FileLoaderLogIdChanged { get; set; }

        [Parameter]
        public long? FileLoaderLogId
        {
            get
            {
                return FileLoaderLogId_;
            }
            set
            {
                FileLoaderLogId_ = value;
                AppState.ShowLoadingStatus();
                InvokeAsync(RefreshGrid_);
                AppState.HideLoadingStatus();
            }
        }

        [Inject]
        public FileLoaderLogItemService FileLoaderLogItemService { get; set; }

        public virtual FileLoaderLogItemService GetService()
        {
            return FileLoaderLogItemService;
        }
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
            DeleteButtonVisible = false;
            SelectionMode = DevExpress.Blazor.GridSelectionMode.Single;

        }
        protected override FileLoaderLogItem NewItem()
        {
            return null;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(FileLoaderLogItem item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(FileLoaderLogItem.Id);
        }
        protected override object KeyFieldValue(FileLoaderLogItem item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
          
            return null;
        }
        protected override Task OnRowInserting(FileLoaderLogItem newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(FileLoaderLogItem dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((FileLoaderBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(FileLoaderLogItem dataItem, FileLoaderLogItem newValues)
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

        protected override  Task<BrowserDataPage<FileLoaderLogItem>> SearchRows(BrowserDataFilter filter)
        {
            if (FileLoaderLogId.HasValue)
            {
                filter.GroupId = FileLoaderLogId;
                return GetService().Search(filter);
            }
            return Task.FromResult(new BrowserDataPage<FileLoaderLogItem>());
        }


        protected override void OnCustomHtmlElementDataCellDecoration(GridCustomizeElementEventArgs e)
        {
            if (GridElementType.DataCell.Equals(e.ElementType) && e.Column.Caption == GridColumns[GridColumns.Length - 2].CaptionName)
            {
                FileLoaderLogItem dataItem = (FileLoaderLogItem)e.Grid.GetDataItem(e.VisibleIndex);
                if (dataItem != null)
                {

                    if (RunStatus.ENDED.Equals(dataItem.RunStatus))
                    {
                        //#00945E #9ACD32
                        e.Style += "background-color: #00945E; color:white;";
                    }
                    if (RunStatus.IN_PROGRESS.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: #3395ff; color:white;";
                    }
                    if (RunStatus.ERROR.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: #ff0000; color:white;";
                    }
                    if (RunStatus.STOPPED.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: ccb1b4; color:white;";
                    }
                }
            }            
        }
    }
}
