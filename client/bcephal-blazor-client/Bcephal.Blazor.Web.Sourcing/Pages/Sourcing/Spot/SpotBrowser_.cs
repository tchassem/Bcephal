using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.Spot
{
   // [RouteAttribute("/browser-spot")]
    public class SpotBrowser_ : AbstractNewGridComponent<Models.Spot.Spot, BrowserData>, IDisposable
    {
        protected dynamic[] SpotColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="20%", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="20%", ColumnName = nameof(BrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="auto", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="auto", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                    };

        protected override int ItemsCount => SpotColumns.Length;

        [Inject]
        public SpotService SpotService { get; set; }

        public void Dispose()
        {
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.SourcingSpotCreateAllowed && AppState.PrivilegeObserver.SourcingCreateAllowed)
            //{
                AppState.CanCreate = false;
            //}
        }

        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, SpotColumns[grilleColumnPosition].ColumnName);
        }

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(SpotColumns[Position], Position);
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
            
            return Route.EDIT_SPOT;
        }

        protected override Task OnRowInserting(BrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(BrowserData dataItem)
        {

            await SpotService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected async override  Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((BrowserData)obj).Id.Value).ToList();
                await SpotService.Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(BrowserData dataItem, BrowserData newValues)
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

        protected override  Task<BrowserDataPage<BrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return SpotService.Search(filter);
          
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
        public bool Editable => AppState.PrivilegeObserver.CanCreatedSourcingSpot;
        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_SPOT;
            DeleteButtonVisible = true;
            AppState.CanRefresh = true && !AppState.IsDashboard;

            if (Editable)
            {
                AppState.CanCreate = true && !AppState.IsDashboard;
                IsNavLink = true;
            }
        }

        //protected override Task OnAfterRenderAsync(bool firstRender)
        //{
        //    if (firstRender && AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.SourcingSpotCreateAllowed && AppState.PrivilegeObserver.SourcingCreateAllowed)
        //    {
        //        AppState.CanCreate = true;
        //    }
        //    return base.OnAfterRenderAsync(firstRender);
        //}

        public async override ValueTask DisposeAsync()
        {
            AppState.CanCreate = false;
            await base.DisposeAsync();
        }
    }
}
