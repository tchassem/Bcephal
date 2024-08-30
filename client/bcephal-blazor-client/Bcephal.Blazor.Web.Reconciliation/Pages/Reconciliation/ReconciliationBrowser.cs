﻿using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    //[RouteAttribute("/reconciliation-filter-browser")]
    public class ReconciliationBrowser : AbstractNewGridComponent<ReconciliationModel, BrowserData>
    {
        [Inject]
        private ReconciliationModelService ReconciliationModelService { get; set; }
        protected dynamic[] ReconciliationModelColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="25%", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="auto", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="auto", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="20%", ColumnName = nameof(BrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        protected override int ItemsCount => ReconciliationModelColumns.Length;
        private object GetPropertyValue(BrowserData item, string propName)
        {
            return item.GetType().GetProperty(propName).GetValue(item, null);
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.RECONCILIATION_FILTER_FORM;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (AppState.PrivilegeObserver.CanCreatedReconciliationFilter)
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
            if (AppState.PrivilegeObserver.CanCreatedReconciliationFilter)
            {
                AppState.CanCreate = false;
                AppState.CanRefresh = false;
            }
            return base.DisposeAsync();
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(ReconciliationModelColumns[Position], Position);
        }
       

        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, ReconciliationModelColumns[grilleColumnPosition].ColumnName);
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
            return Route.RECONCILIATION_FILTER_FORM;
        }
        protected override Task OnRowInserting(BrowserData newValues)
        {
            return Task.CompletedTask;
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
                await  AppState.NavigateTo(link);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
        protected override async Task OnRowRemoving(BrowserData dataItem)
        {
            await ReconciliationModelService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((BrowserData)obj).Id.Value).ToList();
                await ReconciliationModelService.Delete(idss);
            }
        }

        protected override  Task<BrowserDataPage<BrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return ReconciliationModelService.Search(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

    }
}
