
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    //[RouteAttribute("reporting-join/list")]
    public class GridJoinBrowser : AbstractNewGridComponent<Join, JoinBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Name"], ColumnWidth="35%", ColumnName = nameof(JoinBrowserData.Name), ColumnType = typeof(long)},
            new {CaptionName = AppState["CreationDate"], ColumnWidth="auto", ColumnName = nameof(JoinBrowserData.CreationDate), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["ModificationDate"], ColumnWidth="auto", ColumnName = nameof(JoinBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
        };

        [Inject]
        public JoinService JoinService { get; set; }

        public virtual JoinService GetService()
        {
            return JoinService;
        }

        protected override Task OnInitializedAsync()
        {
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
            DeleteAllButtonVisible = true;
            DeleteButtonVisible = true;
            EditorRoute = Route.EDIT_REPORTING_JOIN;
            SelectionMode = DevExpress.Blazor.GridSelectionMode.Multiple;
            CurrentEditMode = DevExpress.Blazor.GridEditMode.PopupEditForm;
            return base.OnInitializedAsync();
        }


        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (AppState.PrivilegeObserver.CanCreatedReportingJoinGrid)
            {
                AppState.CanCreate = true && !AppState.IsDashboard;
                AppState.CanRefresh = true && !AppState.IsDashboard;
                CanCreate = true && !AppState.IsDashboard;
                CanRefresh = true && !AppState.IsDashboard;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            if (AppState.PrivilegeObserver.CanCreatedReportingJoinGrid)
            {
                AppState.CanCreate = false;
                AppState.CanRefresh = false;
            }
            return base.DisposeAsync();
        }

        protected override int ItemsCount => GridColumns.Length;


        private object GetPropertyValue(JoinBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override object GetFieldValue(JoinBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(JoinLogBrowserData.Id);
        }

        protected override object KeyFieldValue(JoinBrowserData item)
        {
            return item.Id;
        }
        protected override Task OnRowInserting(JoinBrowserData newValues)
        {
            throw new NotImplementedException();
        }

        protected override async Task OnRowRemoving(JoinBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((JoinBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected override async Task OnRowUpdating(JoinBrowserData dataItem, JoinBrowserData editModel)
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

        protected override Task<BrowserDataPage<JoinBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return GetService().Search(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_REPORTING_JOIN;
        }
    }
}

