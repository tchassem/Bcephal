using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Sourcing.Services;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
        public class ReconciliationRunAutoBrowser : AbstractNewGridComponent<AutoReco, RecoBrowserData>
        {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"],ColumnWidth="15%", ColumnName = nameof(RecoBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="10%", ColumnName = nameof(RecoBrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CurrentlyExecuting"],ColumnWidth="10%", ColumnName = nameof(RecoBrowserData.CurrentlyExecuting), ColumnType = typeof(bool)},
                        new {CaptionName = AppState["CreationDate"],ColumnWidth="auto", ColumnName = nameof(RecoBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"],ColumnWidth="auto", ColumnName = nameof(RecoBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="10%", ColumnName = nameof(RecoBrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        [Inject]
        public AutoRecoService ReconciliationRunAutoService { get; set; }

        public override ValueTask DisposeAsync()
        {
            AppState.Hander = null;
            AppState.CreateHander = null;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed && AppState.PrivilegeObserver.ReconciliationCreateAllowed)
            {
                AppState.CanCreate = false;
            }
            return base.DisposeAsync();
        }

        protected override async Task OnInitializedAsync()
        {
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed && AppState.PrivilegeObserver.ReconciliationCreateAllowed)
            {
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            }
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;   
            EditButtonVisible = false;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed && AppState.PrivilegeObserver.ReconciliationCreateAllowed)
            {
                DeleteButtonVisible = true && !AppState.IsDashboard;
            }
        }

        protected override RecoBrowserData NewItem()
        {
            return null;
        }

        protected override string KeyFieldName()
        {
            return nameof(RecoBrowserData.Id);
        }
        
        protected override object KeyFieldValue(RecoBrowserData item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.RECONCILIATION_RUN_AUTO;
        }

        protected override Task OnRowInserting(RecoBrowserData newValues)
        {
            return Task.CompletedTask;
        }


        protected override Task<BrowserDataPage<RecoBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return ReconciliationRunAutoService.Search(filter);
        }


        protected override async Task OnRowRemoving(RecoBrowserData dataItem)
        {
         await ReconciliationRunAutoService.Delete(new List<long>() { dataItem.Id.Value });
        }

      protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((RecoBrowserData)obj).Id.Value).ToList();
                await ReconciliationRunAutoService.Delete(idss);
            }
        }

        protected override Task OnRowUpdating(RecoBrowserData dataItem, RecoBrowserData newValues)
        {
            string link = NavLinkURI();
            return Task.CompletedTask;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }
        protected override object GetFieldValue(RecoBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

     
        private object GetPropertyValue(RecoBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
