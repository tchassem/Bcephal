using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Grille
{
    //[RouteAttribute("/browser-grille")]
    public partial class GridBrowser : AbstractNewGridComponent<Models.Grids.Grille, GrilleBrowserData>
    {

        [Inject]
        public GrilleService GrilleService { get; set; }
        public bool Editable => AppState.PrivilegeObserver.CanCreatedSourcingInputGrid;
        public virtual GrilleService GetService()
        {
            return GrilleService;
        }
        protected virtual  void CanCreate_()
        {
            if (Editable)
            {
                AppState.CanCreate = true && !AppState.IsDashboard;
                AppState.CanRefresh = true && !AppState.IsDashboard;
                CanCreate = true && !AppState.IsDashboard;
                CanRefresh = true && !AppState.IsDashboard;
            }
        }

        protected virtual void DisposeCreate()
        {
           AppState.CanCreate = false;
           AppState.CanRefresh = false;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                CanCreate_();
                AppState.PublishedHander += Publish;
                AppState.ResetPublicationHandler += ResetPublication;
                AppState.RefreshPublicationHandler += RefreshPublication;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        private async void Publish()
        {
            List<long> ids = GetSelectionDataItemsIds_(item => ((GrilleBrowserData)item).Published.HasValue && !((GrilleBrowserData)item).Published.Value).ToList();
            if (ids.Count() > 0)
            {
                await GetService().Publish(ids)
                    .ContinueWith(async t =>
                    {
                        SelectedDataItemsChanged(new List<GrilleBrowserData>() { });
                        await RefreshGrid_();
                    });
            }
        }

        private async void ResetPublication()
        {
            List<long> ids = GetSelectionDataItemsIds_(item => ((GrilleBrowserData)item).Published.HasValue && ((GrilleBrowserData)item).Published.Value).ToList();
            if (ids.Count() > 0)
            {
                await GetService().ResetPublication(ids)
                .ContinueWith(async t =>
                {
                    SelectedDataItemsChanged(new List<GrilleBrowserData>() { });
                    await RefreshGrid_();
                });
            }
        }
        private async void RefreshPublication()
        {
            List<long> ids = GetSelectionDataItemsIds_(item => ((GrilleBrowserData)item).Published.HasValue && ((GrilleBrowserData)item).Published.Value).ToList();
            if (ids.Count() > 0)
            {
                await GetService().RefreshPublication(ids)
                .ContinueWith(async t =>
                {
                    SelectedDataItemsChanged(new List<GrilleBrowserData>() { });
                    await RefreshGrid_();
                });
            }
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_GRID;
            NewButtonVisible = false;
            CanRefresh = true && !AppState.IsDashboard;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
            IsNavLink = true;
        }

        protected override void OnSelectedDataItemChanged(object selectedDataItem)
        {
            base.OnSelectedDataItemChanged(selectedDataItem);
            bool canDoAction = selectedDataItem != null;
            var publishItems = SelectedDataItems.Where(item => ((GrilleBrowserData)item).Published.HasValue && !((GrilleBrowserData)item).Published.Value);
            bool Published = canDoAction && publishItems.Any();
            var unpublishItems = SelectedDataItems.Where(item => ((GrilleBrowserData)item).Published.HasValue && ((GrilleBrowserData)item).Published.Value);
            bool restPublishedPublished = canDoAction && unpublishItems.Any();
            AppState.CanPublished = Published;
            AppState.CanResetPublication = restPublishedPublished;
            AppState.CanRefreshPublication = restPublishedPublished;
        }

        public override ValueTask DisposeAsync()
        {
            DisposeCreate();
            AppState.CanPublished = false;
            AppState.PublishedHander -= Publish;
            AppState.CanResetPublication = false;
            AppState.CanRefreshPublication = false;
            AppState.ResetPublicationHandler -= ResetPublication;
            AppState.RefreshPublicationHandler -= RefreshPublication;
            return base.DisposeAsync();
        }

        protected override GrilleBrowserData NewItem()
        {
            return null;
        }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="20%", ColumnName = nameof(GrilleBrowserData.Name), ColumnType = typeof(string),IsNavLink= false},
                        new {CaptionName = AppState["Status"],ColumnWidth="5%" , ColumnName = nameof(GrilleBrowserData.Status), ColumnType = typeof(string),IsNavLink= false},
                        new {CaptionName = AppState["Editable"],ColumnWidth="5%" , ColumnName = nameof(GrilleBrowserData.Editable), ColumnType = typeof(bool),IsNavLink= false},
                        new {CaptionName = AppState["Published"],ColumnWidth="5%" , ColumnName = nameof(GrilleBrowserData.Published), ColumnType = typeof(bool),IsNavLink= false},
                        new {CaptionName = AppState["VisibleInShortcut"],ColumnWidth="20%" , ColumnName = nameof(GrilleBrowserData.VisibleInShortcut), ColumnType = typeof(bool),IsNavLink= false},
                        new {CaptionName = AppState["CreationDate"],ColumnWidth="25%" , ColumnName = nameof(GrilleBrowserData.CreationDateTime), ColumnType = typeof(DateTime?),IsNavLink= false},
                        new {CaptionName = AppState["ModificationDate"],ColumnWidth="25%" , ColumnName = nameof(GrilleBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?),IsNavLink= false},
                    };

        protected override int ItemsCount => GridColumns.Length;
        private object GetPropertyValue(GrilleBrowserData item, string propName)
        {
            return item.GetType().GetProperty(propName).GetValue(item, null);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(GrilleBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override async void CheckedChanged(GrilleBrowserData item, int position, bool value)
        {
            if (AppState["Editable"].Equals(GridColumns[position].CaptionName))
            {
                await GetService().SetEditable(item.Id.Value, value);
                //item.Editable = value;
                await RefreshGrid_();
            }
        }

        protected override string KeyFieldName()
        {
            return nameof(Bcephal.Models.Grids.Grille.Id);
        }

        protected override object KeyFieldValue(GrilleBrowserData item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.EDIT_GRID;
        }

        protected override Task OnRowInserting(GrilleBrowserData dataItem)
        {
            
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((GrilleBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected override async Task OnRowRemoving(GrilleBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }

        protected async override Task OnRowUpdating(GrilleBrowserData dataItem, GrilleBrowserData newValues)
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

        protected override Task<BrowserDataPage<GrilleBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return GetService().Search<GrilleBrowserData>(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
