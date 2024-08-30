using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Accounting;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.Postings
{
    [RouteAttribute("/posting/list")]
    public partial class PostingBrowser : AbstractGridComponent<Posting, PostingBrowserData>
    {

        [Inject]
        public PostingService PostingService { get; set; }

        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Posting.Id"], ColumnWidth="20%", ColumnName = nameof(PostingBrowserData.Id), ColumnType = typeof(string)},
            new {CaptionName = AppState["Posting.ValueDate"], ColumnWidth="20%", ColumnName = nameof(PostingBrowserData.ValueDate), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["Posting.EntryDate"], ColumnWidth="20%", ColumnName = nameof(PostingBrowserData.EntryDate), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["Posting.Balance"], ColumnWidth="25%", ColumnName = nameof(PostingBrowserData.balance), ColumnType = typeof(decimal)},
            new {CaptionName = AppState["Posting.Status"], ColumnWidth="15%", ColumnName = nameof(PostingBrowserData.status), ColumnType = typeof(string)}
        };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(PostingBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override void OnInitialized()
        {
            EditorRoute = Route.POSTING_EDIT;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            NewRowButtonVisible = false;
            AppState.CanRun = false;
            AppState.CanValidate = true && !AppState.IsDashboard;
            AppState.ValidateHandler -= PostingValidation;
            AppState.CanReset = true && !AppState.IsDashboard;
            AppState.ResetHandler -= PostingResetValidation;
            base.OnInitialized();
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.DeleteHandler += CustomDelete;
                OnSelectionChangeHandler_ += ChoosePostingItems;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            AppState.DeleteHandler -= CustomDelete;
            OnSelectionChangeHandler_ -= ChoosePostingItems;

            AppState.CanValidate = false;
            AppState.ValidateHandler += PostingValidation;
            AppState.CanReset = false;
            AppState.ResetHandler += PostingResetValidation;
            return base.DisposeAsync();
        }

        public void CustomDelete()
        {
            if (CkeckDeleteCondition())
            {
                DeletionTitle = AppState["DeletionTitle"];
                if (SelectedItems.Count == 1)
                {
                    DeleteMessage = AppState["SureToDeleteSelectedItem", SelectedItems[0]];// GetFieldValue(SelectedItem, 0) + " ?";
                }
                else
                {
                    DeleteMessage = AppState["SureToDeleteAllSelectedItems"];
                }
                DeleteConfirmationPopup = true;
                this.Refresh();
            }
        }

        protected override async void DeleteAction()
        {
            if (SelectedItems.Count == 1)
            {
                await OnRowRemoving(new List<long>() { SelectedItems[0] });
            }
            else
            {
                await OnRowRemoving(SelectedItems);
            }
            AfterDelete();
            DeleteConfirmationPopup = false;
        }

        protected override object GetFieldValue(PostingBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(BrowserData.Id);
        }
        protected override object KeyFieldValue(PostingBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.POSTING_EDIT;
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(PostingBrowserData dataItem)
        {
            await PostingService.Delete(new List<long>() { dataItem.Id.Value });
            await Refresh();
        }
        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await PostingService.Delete(ids);
            }
            await Refresh();
        }

        protected async override Task OnRowUpdating(PostingBrowserData dataItem, Dictionary<string, object> newValues)
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

        protected override bool CkeckDeleteCondition()
        {
            return SelectedItems.Count > 0;
        }

        protected override async void AfterDelete()
        {
            UnSelectedItems.Clear();
            SelectedItems.Clear();
            DxDataGridRef.ClearSelection();
            AppState.CanDelete = false;
            await Refresh();
        }

        private Task ChoosePostingItems(List<long> SelectedItems, List<long> UnSelectedItems)
        {
            if (SelectedItems.Count > 0)
            {
                AppState.CanDelete = true;
            }
            else if (SelectedItems.Count == 0)
            {
                AppState.CanDelete = false;
            }
            return Task.CompletedTask;
        }

        private async void PostingValidation()
        {
            bool val = await PostingService.ResetValidation(SelectedItems);
            if(val)
            {
                await this.Refresh();
            }
        }

        private async void PostingResetValidation()
        {
            bool val = await PostingService.ResetValidation(SelectedItems);
            if (val)
            {
                await this.Refresh();
            }
        }

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<PostingBrowserData> page_, DataSourceLoadOptionsBase options)
        {

            BrowserDataPage<PostingBrowserData> page = await PostingService.Search(filter);

            foreach (PostingBrowserData row in page.Items)
            {
                page_.Items.Add(row);
            }
            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.PageLastItem;
            page_.PageSize = page.PageSize;
            page_.TotalItemCount = page.TotalItemCount;
        }
        protected override Task BuildFilter(BrowserDataFilter filter, DataSourceLoadOptionsBase options)
        {
            filter.ColumnFilters = null;
            filter.Criteria = null;
            if (options != null)
            {
                if (options.Sort != null)
                {
                    foreach (var sortItem in options.Sort)
                    {
                        //filter.OrderAsc = sortItem.Selector;
                    }
                }
                if (options.Filter != null)
                {
                    filter.ColumnFilters = null;
                    foreach (IList<object> filterItem in options.Filter)
                    {
                        object ObCriteria = filterItem.ElementAt(2);
                        if (ObCriteria != null)
                        {
                            filter.Criteria = ObCriteria.ToString();
                        }
                    }
                }
            }
            return Task.CompletedTask;
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
