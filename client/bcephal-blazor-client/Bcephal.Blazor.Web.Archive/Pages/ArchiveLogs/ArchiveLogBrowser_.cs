using Bcephal.Blazor.Web.Archive.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExpress.Blazor;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Pages.ArchiveLogs
{
    public class ArchiveLogBrowser_ : AbstractNewGridComponent<Models.Archives.ArchiveLog, ArchiveLogBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="80px",  ColumnName = nameof(ArchiveLogBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Action"] ,ColumnWidth="80px",  ColumnName = nameof(ArchiveLogBrowserData.Action), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User"],ColumnWidth="80px", ColumnName = nameof(ArchiveLogBrowserData.Username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="80px",  ColumnName = nameof(ArchiveLogBrowserData.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"],ColumnWidth="80px", ColumnName = nameof(ArchiveLogBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Message"] ,ColumnWidth="160px",  ColumnName = nameof(ArchiveLogBrowserData.Message), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(ArchiveLogBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override void OnHtmlDataCellDecoration(GridCustomizeCellDisplayTextEventArgs eventArgs)
        {
        }

        [Inject]
        public ArchiveLogService ArchiveLogService { get; set; }

        public virtual ArchiveLogService GetService()
        {
            return ArchiveLogService;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = false;
            EditButtonVisible = false;
            AppState.CanCreate = false;
            AppState.CanRefresh = true;
        }

        protected override object GetFieldValue(ArchiveLogBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(ArchiveLogBrowserData.Id);
        }

        protected override object KeyFieldValue(ArchiveLogBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(ArchiveLogBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(ArchiveLogBrowserData dataItem)
        {
            await ArchiveLogService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((ArchiveLogBrowserData)obj).Id.Value).ToList();
                await ArchiveLogService.Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(ArchiveLogBrowserData dataItem, ArchiveLogBrowserData newValues)
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

        protected override  Task<BrowserDataPage<ArchiveLogBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return GetService().Search(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}
