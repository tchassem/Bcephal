using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Accounting;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.Postings
{
    public partial class PostingEntryGrid : AbstractGridComponent<Posting, PostingEntry>
    {
        [Inject]
        public PostingService PostingService { get; set; }

        [Parameter]
        public Action<Posting> EditorDataPostingHandler { get; set; }

        [Parameter]
        public EventCallback<ObservableCollection<PostingEntry>> CompileAmountEventCallback { get; set; }

        [Parameter]
        public List<Account> Accounts { get; set; }

        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["AccountId"], ColumnWidth="25%", ColumnName = nameof(PostingEntry.AccountId), ColumnType = typeof(string)},
            new {CaptionName = AppState["account.name"], ColumnWidth="40%", ColumnName = nameof(PostingEntry.AccountName), ColumnType = typeof(string)},
            new {CaptionName = AppState["Posting.EntryAmount"], ColumnWidth="20%", ColumnName = nameof(PostingEntry.Amount), ColumnType = typeof(decimal)},
            new {CaptionName = AppState["Posting.Sign"], ColumnWidth="15%", ColumnName = nameof(PostingEntry.sign), ColumnType = typeof(string)}
        };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(PostingEntry obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override void OnInitialized()
        {
            ShowFilterRow = false;
            EditButtonVisible = EditorData.Item.Status == PostingStatus.DRAFT ? true : false;
            base.OnInitialized();
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.DeleteHandler += CustomDelete;
                OnSelectionChangeHandler_ += ChooseEntriesItems;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            AppState.DeleteHandler -= CustomDelete;
            OnSelectionChangeHandler_ -= ChooseEntriesItems;
            return base.DisposeAsync();
        }

        public void CustomDelete()
        {
            SelectedItem = EditorData.Item.entryListChangeHandler.Items.Where(x => x.EditPosition == SelectedItems[0]).FirstOrDefault();
            if (CkeckDeleteCondition()) {
                DeletionTitle = AppState["DeletionTitle"];
                if(SelectedItems.Count == 1)
                {
                    DeleteMessage = AppState["SureToDeleteSelectedItem", GetFieldValue(SelectedItem, 0)];
                }
                else if(EditorData.Item.entryListChangeHandler.Items.Count > SelectedItems.Count)
                {
                    DeleteMessage = AppState["SureToDeleteAllSelectedItems"];
                }
                else
                {
                    DeleteMessage = AppState["SureToDeleteAllItem"];
                }
                DeleteConfirmationPopup = true;
            }
        }

        protected override async void DeleteAction()
        {
            if (SelectedItems.Count == 1)
            {
                await OnRowRemoving(SelectedItem);
            }
            else
            {
                await OnRowRemoving(SelectedItems);
            }
            AfterDelete();
            DeleteConfirmationPopup = false;
        }

        protected override object GetFieldValue(PostingEntry item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(PostingEntry.EditPosition);
        }
        protected override object KeyFieldValue(PostingEntry item)
        {
            return item.EditPosition;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        private async Task AddOrUpdateData(PostingEntry dataItem, Dictionary<string, object> newValues)
        {   if (newValues.Count > 0)
            {
                if (newValues.ContainsKey(nameof(PostingEntry.AccountId)))
                {
                    newValues.TryGetValue(nameof(PostingEntry.AccountId), out object AccountId);
                    dataItem.AccountId = AccountId != null ? AccountId.ToString() : null;
                } 
                else if (newValues.ContainsKey(nameof(PostingEntry.AccountName)))
                {
                    newValues.TryGetValue(nameof(PostingEntry.AccountName), out object AccountName);
                    dataItem.AccountName = AccountName != null ? AccountName.ToString() : null;
                } 
                else if (newValues.ContainsKey(nameof(PostingEntry.Amount)))
                {
                    newValues.TryGetValue(nameof(PostingEntry.Amount), out object Amount);
                    dataItem.Amount = (decimal?)Amount;
                } 
                else
                {
                    newValues.TryGetValue(nameof(PostingEntry.sign), out object sign);
                    dataItem.Sign = sign != null ? PostingSign.GetByCode(sign.ToString()) : null;
                }

                if (dataItem.IsPersistent)
                {
                    EditorData.Item.UpdateEntry(dataItem);
                }
                else
                {
                    EditorData.Item.DeleteOrForgetEntry(dataItem);
                    EditorData.Item.AddEntry(dataItem);
                }
                await CompileAmountEventCallback.InvokeAsync(EditorData.Item.entryListChangeHandler.Items);
                await EditorDataChanged.InvokeAsync(EditorData);
                await Refresh();
            }
            else
            {
                if (dataItem.IsPersistent)
                {
                    EditorData.Item.UpdateEntry(dataItem);
                }
                else
                {
                    EditorData.Item.DeleteOrForgetEntry(dataItem);
                    EditorData.Item.AddEntry(dataItem);
                }
                await CompileAmountEventCallback.InvokeAsync(EditorData.Item.entryListChangeHandler.Items);
                await EditorDataChanged.InvokeAsync(EditorData);
                await Refresh();
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
            AppState.Save();
            AppState.Update = false;
            AppState.CanDelete = false;
            await Refresh();
        }

        protected override async Task OnRowRemoving(PostingEntry dataItem)
        {
            EditorData.Item.DeleteOrForgetEntry(EditorData.Item.entryListChangeHandler.Items.Where(e => e.EditPosition == dataItem.EditPosition).First());
            await CompileAmountEventCallback.InvokeAsync(EditorData.Item.entryListChangeHandler.Items);
            await EditorDataChanged.InvokeAsync(EditorData);
        }

        protected override async Task OnRowRemoving(List<long> positions)
        {
            if (positions != null && positions.Count > 0)
            {
                foreach (var position in positions)
                {
                    EditorData.Item.DeleteOrForgetEntry(EditorData.Item.entryListChangeHandler.Items.Where(e => e.EditPosition == position).First());
                }
                await CompileAmountEventCallback.InvokeAsync(EditorData.Item.entryListChangeHandler.Items);
                await EditorDataChanged.InvokeAsync(EditorData);
            }
            await Refresh();
        }

        protected override Task OnRowUpdating(PostingEntry dataItem, Dictionary<string, object> newValues)
        {
            return AddOrUpdateData(dataItem, newValues);
        }

        protected override Task SearchRows(BrowserDataFilter filter, BrowserDataPage<PostingEntry> page_, DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<PostingEntry> page = new BrowserDataPage<PostingEntry>(); // await PostingService.Search(filter);
            if(EditorData != null && EditorData.Item != null)
            {
                foreach (PostingEntry row in EditorData.Item.entryListChangeHandler.Items)
                {
                    row.EditPosition = page_.Items.Count;
                    page_.Items.Add(row);
                }
            }
           
            page_.Items.Add(new PostingEntry() { EditPosition = page_.Items.Count });

            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.PageLastItem;
            page_.PageSize = page.PageSize;
            page_.TotalItemCount = page.TotalItemCount;
            return Task.CompletedTask;
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

        private Task ChooseEntriesItems(List<long> SelectedItems, List<long> UnSelectedItems)
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

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new CustomGridData(GridColumns[Position], Position);
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

        protected override RenderFragment GetEditData(PostingEntry DataItem, AbstractGridDataItem GridDataItem)
        {
            return GetEditData_(DataItem, GridDataItem);
        }

    }

    public class CustomGridData : GridDataItem
    {
        public CustomGridData(Object GridColumns, int position) : base (GridColumns, position)
        {

        }

        public override bool CanEditColumn
        {
            get
            {
                return false;
            }
        }
    }
}
