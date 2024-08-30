using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
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

    public partial class FileLoaderLogBrowser : AbstractNewGridComponent<FileLoaderLog, FileLoaderLog>
    {

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["DATE"] ,ColumnWidth="10%", ColumnName = nameof(FileLoaderLog.StartDate), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["User"] ,ColumnWidth="20%", ColumnName = nameof(FileLoaderLog.Username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["M/A"] ,ColumnWidth="5%", ColumnName = nameof(FileLoaderLog.Mode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["FileCount"],ColumnWidth="5%", ColumnName = nameof(FileLoaderLog.FileCount), ColumnType = typeof(int)},
                        new {CaptionName = AppState["ErrorFileCount"] ,ColumnWidth="5%", ColumnName = nameof(FileLoaderLog.ErrorFileCount), ColumnType = typeof(int)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="7%", ColumnName = nameof(FileLoaderLog.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Message"] ,ColumnWidth="auto", ColumnName = nameof(FileLoaderLog.Message), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(FileLoaderLog obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Parameter] public long? FileLoaderId_ { get; set; }
        [Parameter] public long? FileLoaderLogId_ { get; set; }
        [Parameter] public EventCallback<long?> FileLoaderLogId_Changed { get; set; }

        public long? FileLoaderLogIdBinding_
        {
            get { return FileLoaderLogId_; }
            set
            {
                FileLoaderLogId_ = value;
                if (FileLoaderLogId_Changed.HasDelegate)
                {
                    FileLoaderLogId_Changed.InvokeAsync(FileLoaderLogId_);
                }
            }
        }

        protected override void OnSelectedDataItemChanged(object selection)
        {
            if (selection != null)
            {
                base.OnSelectedDataItemChanged(selection);
                FileLoaderLogIdBinding_ = ((FileLoaderLog)selection).Id;
            }            
        }

        [Inject] public FileLoaderLogService FileLoaderLogService { get; set; }


        public virtual FileLoaderLogService GetService()
        {
            return FileLoaderLogService;
        }
        

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            if(AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.SourcingFileLoaderCreateAllowed && AppState.PrivilegeObserver.SourcingCreateAllowed)
            {
                AppState.CanCreate = true && !AppState.IsDashboard;
            }
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            SelectionMode = GridSelectionMode.Single;
        }

        protected override FileLoaderLog NewItem()
        {
            return null;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(FileLoaderLog item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(FileLoaderLog.Id);
        }
        protected override object KeyFieldValue(FileLoaderLog item)
        {
            FileLoaderLogIdBinding_ = item.Id;
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.NEW_LOAD_FILE;
        }
        protected override Task OnRowInserting(FileLoaderLog newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(FileLoaderLog dataItem)
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

        protected async override Task OnRowUpdating(FileLoaderLog dataItem, FileLoaderLog newValues)
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

        protected override Task<BrowserDataPage<FileLoaderLog>> SearchRows(BrowserDataFilter filter)
        {
            if (FileLoaderId_.HasValue)
            {
                filter.GroupId = FileLoaderId_;
                return GetService().Search(filter);
            }
            return Task.FromResult(new BrowserDataPage<FileLoaderLog>());
        }


   
        GrilleColumn GetItemsByPosition(int position)
        {
            foreach (var item in GridColumns)
            {
                if (item.Position == position)
                {
                    return item;
                }
            }
            return null;
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
           return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss",obj);
           
        }


        protected override void OnCustomHtmlElementDataCellDecoration(GridCustomizeElementEventArgs e)
        {
            if (GridElementType.DataCell.Equals(e.ElementType) && e.Column.Caption == GridColumns[GridColumns.Length - 2].CaptionName)
            {
                FileLoaderLog dataItem = (FileLoaderLog)e.Grid.GetDataItem(e.VisibleIndex);
                if (dataItem != null)
                {
                    if (RunStatus.ENDED.Equals(dataItem.RunStatus))
                    {
                        if (dataItem != null && dataItem.ErrorFileCount > 0)
                        {
                            e.Style += "background-color: #ff0000; color:white;";
                        }
                        else
                        {
                            e.Style += "background-color: #00945E; color:white;";
                        }
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
            if (GridElementType.DataCell.Equals(e.ElementType) && e.Column.Caption == GridColumns[GridColumns.Length - 3].CaptionName)
            {
                FileLoaderLog dataItem2 = (FileLoaderLog)e.Grid.GetDataItem(e.VisibleIndex);
                if (dataItem2 != null && dataItem2.ErrorFileCount > 0)
                {
                    e.Style += "font-size: .875em; font-weight: bold; color: #ffc107;";
                }
            }
        }

    }
}


