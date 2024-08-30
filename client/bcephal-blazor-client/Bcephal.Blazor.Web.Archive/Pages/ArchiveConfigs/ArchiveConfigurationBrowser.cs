using Bcephal.Blazor.Web.Archive.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Pages.ArchiveConfigs
{
    //[RouteAttribute("/archive-configuration-browser/")]
    public class ArchiveConfigurationBrowser : AbstractNewGridComponent<ArchiveConfig, ArchiveConfigBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(ArchiveConfigBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="100px",  ColumnName = nameof(ArchiveConfigBrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(ArchiveConfigBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="100px", ColumnName = nameof(ArchiveConfigBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(ArchiveConfigBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public ArchiveConfigurationService ArchiveConfigService { get; set; }

        public virtual ArchiveConfigurationService GetService()
        {
            return ArchiveConfigService;
        }


        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_ARCHIVE_CONFIGURATION;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            AppState.CanRun = false;
            AppState.CanRefresh = true;
            DeleteButtonVisible = true;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.DataManagementArchiveConfigCreateAllowed && AppState.PrivilegeObserver.DataManagementArchiveConfigCreateAllowed)
            //{
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            //}
        }

        public override async ValueTask DisposeAsync()
        {

            AppState.CreateHander = null;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.DataManagementArchiveConfigCreateAllowed && AppState.PrivilegeObserver.DataManagementArchiveConfigCreateAllowed)
            //{
                AppState.CanCreate = false;
            //}
            await base.DisposeAsync();
        }

        protected override object GetFieldValue(ArchiveConfigBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(ArchiveConfigBrowserData.Id);
        }
        protected override object KeyFieldValue(ArchiveConfigBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_ARCHIVE_CONFIGURATION;
        }

        protected override Task OnRowInserting(ArchiveConfigBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(ArchiveConfigBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((ArchiveConfigBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(ArchiveConfigBrowserData dataItem, ArchiveConfigBrowserData newValues)
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

        protected override  Task<BrowserDataPage<ArchiveConfigBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return GetService().Search(filter);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}
