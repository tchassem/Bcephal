using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExpress.Blazor;
using DevExtreme.AspNet.Data;
using DevExtreme.AspNet.Data.ResponseModel;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Base.Shared.Grille;

namespace Bcephal.Blazor.Web.Base.Shared.AbstractComponent
{
    public abstract partial class AbstractGridComponent<P, C> : ComponentBase, IAsyncDisposable where P : Persistent where C : DataForEdit
    {
        #region parameter

        [CascadingParameter]
        public Error Error { get; set; }

        [Parameter]
        public EditorData<P> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<P>> EditorDataChanged { get; set; }

        [Parameter]
        public Func<List<long>, List<long>, Task> OnSelectionChangeHandler_ { get; set; }

        [Parameter]
        public Func<Task> DoHandlerAfterRefresh_ { get; set; }
        protected bool IsAfterRefresh { get; set; } = false;

        [Parameter]
        public bool UsingManualyData { get; set; } = false;

        #endregion

        #region internal propeties

        protected bool PageSizeSelector { get; set; } = true;
        protected bool ShowPager { get; set; } = true;
        protected int PagerVisibleNumericButtonCount { get; set; } = 5;
        protected int PagerSwitchToInputBoxButtonCount { get; set; } = 20;
        protected DataGridSelectionMode SelectionMode { get; set; } = DataGridSelectionMode.OptimizedMultipleSelection;
        protected DataGridColumnResizeMode ColumnResizeMode { get; set; } = DataGridColumnResizeMode.Component;
        protected DataGridEditMode CurrentEditMode { get; set; } = DataGridEditMode.EditForm;
        protected bool NewButtonVisible { get; set; } = false;
        [Parameter]
        public bool DuplicateButtonVisible { get; set; } = false;
        
        protected bool NewRowButtonVisible { get; set; } = false;
        protected bool ClearFilterButtonVisible { get; set; } = false;
        protected bool DeleteButtonVisible { get; set; } = false;
        protected bool DeleteAllButtonVisible { get; set; } = true;
        protected bool CanRefresh { get; set; } = true;
        protected bool CanCreate { get; set; } = true;
        protected bool EmptyLineVisible { get; set; } = true;
        protected bool EditButtonVisible { get; set; } = false;
        protected bool ToolbarEditButtonVisible { get; set; } = false;
        protected bool ChooserToolbarVisible { get; set; } = true;
        protected bool ShowFilterButtonVisible { get; set; } = true;
        protected bool ShowSelectionColumnVisible { get; set; } = true;
        protected bool ShowInColumnChooser { get; set; } = true;

        protected bool ComandCulumnvisible { get; set; } = true;
        protected bool ShowsAll_ { get; set; } = true;

        protected bool ShowsCommandColumn { get; set; } = false;

        protected bool AllowFilter { get; set; } = true;
        protected bool displayItem => EditButtonVisible || NewButtonVisible || DeleteButtonVisible;

        [Parameter]
        public bool AllowToDisplayCustomPager { get; set; } = true;
        protected bool AllowAllRow { get; set; } = false;


        protected bool AllowAllRow_
        {
            get => AllowAllRow;
            set
            {
                AllowAllRow = value;
                if (value)
                {
                    page_PageSize = -1;
                   // DxDataGridRef.PageSize = -1;
                    //DxDataGridRef.ShowPager = false;
                }
                else
                {
                    page_PageSize = 25;
                   // DxDataGridRef.ShowPager = true;
                }
               
                AppState.MainLayoutShouldRenderTrue();
                RefreshGirdAfterChecked();
                //RefreshGirdAfterCheckedRow();
            }
        }
        //private async void RefreshGirdAfterCheckedRow()
        //{
        //    var param = new Dictionary<string, object>() { };
        //    if (CustomHeaderRender != null)
        //    {
        //        param.Add("PageSize", page_PageSize);
        //        param.Add("DataNavigationMode", DataGridNavigationMode.ShowAllDataRows);
        //    }
        //    await DxDataGridRef.SetParametersAsync(ParameterView.FromDictionary(param));
        //    RefreshGirdAfterChecked();
        //}
        private void RefreshGirdAfterChecked()
        {
            string lastKeyName = KeyName;
            string separator = "____";
            if (KeyName.Contains(separator))
            {
                lastKeyName = KeyName.Substring(0, KeyName.IndexOf(separator));
            }
            string key = Guid.NewGuid().ToString("d");
            KeyName = $"{lastKeyName}{separator}{key}";
            RefreshGird();
        }


        protected bool ShowPagerCountItem { get; set; } = true;

        protected bool AllowRowCounting_ { get=> AllowRowCounting; 
            set {
                AllowRowCounting = value;
                RefreshGirdAfterChecked();
            } 
        }

        protected bool AllowRowCounting { get; set; } = false;
        protected bool CanSetRowCounting { get; set; } = false;

        #endregion

        #region inject properties

        [Inject]
        public IJSRuntime JSRuntime { get; set; }
        [Inject]
        protected AppState AppState { get; set; }

        [Inject]
        protected LocalStorageService LocalStorageService { get; set; }
        #endregion

        #region other properties
        public string EditorRoute { get; set; }

        [Parameter]
        public IEnumerable<C> ManualyData { get; set; } = new List<C>();

        [Parameter]
        public string KeyName { get; set; } = "AbstractGridComponent";

        protected DxDataGrid<C> DxDataGridRef { get; set; }
        protected DxDataGridSelectionColumn DxDataGridSelectionColumnRef { get; set; }
        protected List<AbstractGridDataItem> ColumnsDatas { get; set; }

        private List<long> SelectedItems_ { get; set; }
        protected List<long> UnSelectedItems { get; set; }
        protected List<long> SelectedItems
        {
            get { return SelectedItems_; }
            set
            {
                SelectedItems_ = value;
            }
        }
        protected C selectedItem { get; set; }
        protected bool Enabled { get; set; } = true;

        private CustomDxToolbarItem DxToolbarItemRefDelete { get; set; }

        protected bool EnabledDelete_;
        protected bool EnabledDelete
        {
            get
            {
                return EnabledDelete_;
            }
            set
            {
                EnabledDelete_ = value && SelectedItems != null && SelectedItems.Count() > 1;
                if (DxToolbarItemRefDelete != null)
                {
                    DxToolbarItemRefDelete.Refresh(EnabledDelete_);
                }
            }
        }

        protected bool ShowFilterRow { get; set; } = true;
        private int page_CurrentPage_;
        private int page_CurrentPage
        {
            get { 
                return page_CurrentPage_; 
            }
            set
            {
                page_CurrentPage_ = value;
                PageIndexChanged(value);
            }
        }

        protected int page_PageSize = 25;
        protected readonly BrowserDataFilter filter = new BrowserDataFilter();
        protected readonly IEnumerable<int> PageSize = new List<int>() { 10, 25, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000 };
        protected readonly IDictionary<string, object> attributes = new Dictionary<string, object>()
        {
            { "PagerAutoHideNavButtons", false },
        };
        protected string GridId { get; set; } = Guid.NewGuid().ToString("d");
         protected string CheckBoxTotalPageId { get; set; } = Guid.NewGuid().ToString("d");
        public ElementReference CheckBoxTotalPageControl { get; set; }
        protected bool IsNavLink { get; set; } = false;

        protected bool DeleteConfirmationPopup { get; set; } = false;
        bool IsConfirmation { get; set; } = true;

        protected virtual string GridRowClass { get; set; } = "grid-row";

        protected long? CurrentProjectId { get; set; }

        protected string DeleteContext { get; set; } = "";

        protected virtual string DeletionTitle { get; set; }

        protected string DeleteMessage { get; set; } = "";

        #endregion

        #region abstract method

        protected abstract int ItemsCount { get; }
        protected abstract Task SearchRows(BrowserDataFilter filter, BrowserDataPage<C> page_, DataSourceLoadOptionsBase options);

        protected abstract AbstractGridDataItem GetGridDataItem(int Position);

        protected virtual string ColumnChoserWidth { get { return ItemsCount < 3 ? "2.5%" : ItemsCount < 7 ? "3%" : "35px"; } }
        protected virtual string getCommandColumnWidth()
        {
            return "5%";
        }
        protected abstract object GetFieldValue(C item, int grilleColumnPosition);
        protected abstract Task OnRowRemoving(C dataItem);
        protected abstract Task OnRowRemoving(List<long> ids);
        protected abstract Task OnRowUpdating(C dataItem, Dictionary<string, object> newValues);
        protected abstract Task OnRowInserting(Dictionary<string, object> newValues);
        protected abstract string KeyFieldName();
        protected abstract object KeyFieldValue(C item);
        protected abstract string NavLinkURI();

        protected virtual RenderFragment CustomHeaderRender => null;
        protected virtual RenderFragment EditFormTemplate => null;

        protected virtual C NewItem()
        {
            return default;
        }
        protected virtual Task BuildFilter(BrowserDataFilter filter, DataSourceLoadOptionsBase options)
        {
            return Task.CompletedTask;
        }
        protected virtual string getPopupEditFormHeaderTextLabel()
        {
            return "EDIT.FORM";
        }

        protected virtual void CheckedChanged(C item, int position, bool value)
        {

        }
        #endregion

        #region override method

        protected Task Refresh()
        {
            if (DxDataGridRef != null)
            {
                return DxDataGridRef.Refresh();
            }
            return Task.CompletedTask;
        }
        protected virtual async Task OnAddNewClick()
        {
            try
            {
                C item = NewItem();
                if (DxDataGridRef != null && item != null)
                {
                    await DxDataGridRef.StartRowEdit(item);
                    if (!Enabled)
                    {
                        ChangeToolbarEnabled(true);
                    }
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }
        protected virtual async Task OnEditClick()
        {
            try
            {
                if (DxDataGridRef != null)
                {
                    await DxDataGridRef.StartRowEdit(SelectedItem);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        protected virtual async Task OnDeleteClick()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (DxDataGridRef != null && SelectedItems != null && SelectedItems.Count > 0)
                {
                    await OnRowRemoving(SelectedItems);
                    await Refresh();
                    SelectedItems.Clear();
                    EnabledDelete = false;
                    ChangeToolbarEnabled(false);
                }
                AppState.HideLoadingStatus();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        protected virtual async void OnRowDbClick(DataGridRowClickEventArgs<C> args)
        {
               await Task.CompletedTask;
        }

        void ChangeToolbarEnabled(bool enabled)
        {
            Enabled = enabled;
            StateHasChanged();
        }

        void OnShowFilterRow(ToolbarItemClickEventArgs e)
        {
            ShowFilterRow = !ShowFilterRow;
        }

        public C SelectedItem
        {
            get
            {
                return selectedItem;
            }
            set
            {
                selectedItem = value;
                InvokeAsync(StateHasChanged);
            }
        }


        public void PageIndexChanged(int page)
        {
            filter.Page = page + 1;
            filter.PageSize = DxDataGridRef.PageSize;
            ShowPagerWidgetsCheckbox();
            this.StateHasChanged();
        }


        private void ShowPagerWidgetsCheckbox()
        {
            // --- RenderFormContentRef.StateHasChanged_();
            RenderFormContentRef.Refresh(BuildChecBoxTotalPage);
        }

        protected virtual void OnSingleSelectionChanged(C selection)
        {
            SelectedItem = selection;
        }

        private List<long> GetSelectAllKeys()
        {
            List<long> items = new();
            if (loadResult.data is ObservableCollection<C>)
            {
                ObservableCollection<C> itemsDatas = (ObservableCollection<C>)loadResult.data;
                if (itemsDatas != null)
                {
                    foreach (var item in itemsDatas)
                    {
                        try
                        {
                            var obj = KeyFieldValue(item);
                            if (obj != null)
                            {
                                long.TryParse(obj.ToString(), out long id);
                                items.Add(id);
                            }
                        }
                        catch { }
                    }
                }
            }
            return items;
        }
        private List<long> GetSelectItem(List<long> unSelectIds)
        {
            List<long> items = new();
            if(unSelectIds != null && loadResult.data is ObservableCollection<C>)
            {
                ObservableCollection<C> itemsDatas = (ObservableCollection<C>)loadResult.data;
                if (itemsDatas != null)
                {
                    foreach (var item in itemsDatas)
                    {
                        try
                        {
                            var obj = KeyFieldValue(item);
                            if (obj != null)
                            {
                                long.TryParse(obj.ToString(), out long id);
                                if (!unSelectIds.Contains(id))
                                {
                                    items.Add(id);
                                }
                            }
                        }
                        catch { }
                    }
                }
            }
            return items;
        }

        //private dynamic GetPropertyValue(object item, string propName)
        //{
        //    var defaultFlags = BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.IgnoreCase;
        //    return item.GetType().GetProperty(propName, defaultFlags).GetValue(item, null);
        //}

    protected async Task OnSelectionChanged(DataGridSelection<C> selection)
        {
            if (SelectedItems == null)
            {
                SelectedItems = new List<long>();
            }
            if (UnSelectedItems == null)
            {
                UnSelectedItems = new List<long>();
            }
            bool SelectModeAll = !selection.SelectedKeysStored.Any() && SelectedItems.Count == 0;
            bool SelectModeAllAfter = !selection.SelectedKeysStored.Any() && !selection.UnselectedKeysStored.Any()
                                    && UnSelectedItems.Any();
            bool SelectModeAllbefore = !selection.SelectedKeysStored.Any() && selection.UnselectedKeysStored.Any()
                                    && SelectedItems.Any();

            var selectedKeys = await selection.SelectedKeys;
            var SelectedKeysMatchingFilter = await selection.SelectedKeysMatchingFilter;
            //await JSRuntime.InvokeVoidAsync("console.log", "SelectedKeys", selectedKeys,
            //     "SelectedKeysMatchingFilter", SelectedKeysMatchingFilter,
            //     "SelectedKeysStored", selection.SelectedKeysStored,
            //    "UnselectedKeysStored",  selection.UnselectedKeysStored);
            //select all items
            bool SelectModeAllRoot = selectedKeys.Any() && !selection.SelectedKeysStored.Any();
            // empty selection
            bool UnSelectModeAllRoot = !selectedKeys.Any() && !selection.SelectedKeysStored.Any() && !selection.UnselectedKeysStored.Any();
            if (UnSelectModeAllRoot)
            {
                UnSelectedItems.Clear();
                UnSelectedItems.AddRange(SelectedItems);
                SelectedItems.Clear();
            }
            else
                if (SelectModeAllRoot)
            {
                SelectedItems.Clear();
                UnSelectedItems.Clear();
                foreach (var obj in selectedKeys)
                {
                    long.TryParse(obj.ToString(), out long value);
                    SelectedItems.Add(value);
                }
                foreach (var obj in selection.UnselectedKeysStored)
                {
                    long.TryParse(obj.ToString(), out long value);
                    UnSelectedItems.Add(value);
                }
            }
            else
            if (!SelectModeAll && !SelectModeAllbefore)
            {
                UnSelectedItems.Clear();
                UnSelectedItems.AddRange(SelectedItems);
                SelectedItems.Clear();
                foreach (var obj in selection.SelectedKeysStored)
                {
                    long.TryParse(obj.ToString(), out long value);
                    SelectedItems.Add(value);
                    UnSelectedItems.Remove(value);
                }
            }
            else
            if (!SelectModeAllbefore)
            {

                SelectedItems.Clear();
                if (SelectModeAllAfter)
                {
                    UnSelectedItems.Clear();
                }
                SelectedItems.AddRange(GetSelectItem(UnSelectedItems));
                UnSelectedItems.Clear();
                foreach (var obj in selection.UnselectedKeysStored)
                {
                    long.TryParse(obj.ToString(), out long value);
                    UnSelectedItems.Add(value);
                    SelectedItems.Remove(value);
                }

            }
            else
            {
                foreach (var obj in selection.UnselectedKeysStored)
                {
                    long.TryParse(obj.ToString(), out long value);
                    UnSelectedItems.Add(value);
                    SelectedItems.Remove(value);
                }
            }          
            
            EnabledDelete = true;

            if (OnSelectionChangeHandler_ != null && (SelectedItems.Count >= 0 || UnSelectedItems.Count >= 0))
            {
                await OnSelectionChangeHandler_?.Invoke(SelectedItems, UnSelectedItems);
            }
        }

        private async void OnSelectionChangeHandlerInvoke(List<long>  SelectedItems, List<long>  UnSelectedItems)
        {
            if (OnSelectionChangeHandler_ != null && (SelectedItems.Count > 0 || UnSelectedItems.Count > 0))
            {
              await  OnSelectionChangeHandler_?.Invoke(SelectedItems, UnSelectedItems);
            }
        }

        #endregion

        #region protected method
        protected string GetEditTextFieldName(CellEditContext cellEditContext, AbstractGridDataItem GridDataItem)
        {
            object value_ = GetFieldValue((C)cellEditContext.DataItem, GridDataItem.Position);
            string value = null;
            if (value_ != null)
            {
                value = value_.ToString();
            }
            if (cellEditContext.EditedValues.ContainsKey(GridDataItem.ColumnName))
            {
                object els;
                cellEditContext.EditedValues.TryGetValue(GridDataItem.ColumnName, out els);
                if (els != null)
                {
                    value = els.ToString();
                }
            }
            return value;
        }

        protected string GetEditTextFieldName(C DataItem, AbstractGridDataItem GridDataItem)
        {
            object value_ = GetFieldValue(DataItem, GridDataItem.Position);
            string value = null;
            if (value_ != null)
            {
                value = value_.ToString();
            }
            if (EditedValues.ContainsKey(GridDataItem.ColumnName))
            {
                object els;
                EditedValues.TryGetValue(GridDataItem.ColumnName, out els);
                if (els != null)
                {
                    value = els.ToString();
                }
            }
            return value;
        }

        protected virtual decimal? GetEditDecimalFieldName(CellEditContext cellEditContext, AbstractGridDataItem GridDataItem)
        {
            try
            {
                object value_ = GetFieldValue((C)cellEditContext.DataItem, GridDataItem.Position);
                decimal? value__ = null;
                if (value_ != null && !string.IsNullOrEmpty(value_.ToString()))
                {
                    decimal.TryParse(value_.ToString(), out decimal value);
                    value__ = value;
                }
                if (cellEditContext.EditedValues.ContainsKey(GridDataItem.ColumnName))
                {
                    object els;
                    cellEditContext.EditedValues.TryGetValue(GridDataItem.ColumnName, out els);
                    if (els != null)
                    {
                        decimal.TryParse(els.ToString(), out decimal value);
                        value__ = value;
                    }
                }
                return value__;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            return null;
        }

        protected virtual decimal? GetEditDecimalFieldName(C DataItem, AbstractGridDataItem GridDataItem)
        {
            try
            {
                object value_ = GetFieldValue(DataItem, GridDataItem.Position);
                decimal? value__ = null;
                if (value_ != null && !string.IsNullOrEmpty(value_.ToString()))
                {
                    decimal.TryParse(value_.ToString(), out decimal value);
                    value__ = value;
                }
                if (EditedValues.ContainsKey(GridDataItem.ColumnName))
                {
                    object els;
                    EditedValues.TryGetValue(GridDataItem.ColumnName, out els);
                    if (els != null)
                    {
                        decimal.TryParse(els.ToString(), out decimal value);
                        value__ = value;
                    }
                }
                return value__;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            return null;
        }

        protected virtual RenderFragment GetEditData(C DataItem, AbstractGridDataItem GridDataItem)
        {
            return null;
        }

        protected virtual DateTime? GetEditDateTimeFieldName(CellEditContext cellEditContext, AbstractGridDataItem GridDataItem)
        {
            try
            {
                object value_ = GetFieldValue((C)cellEditContext.DataItem, GridDataItem.Position);
                DateTime? date__ = null;
                if (value_ != null && !string.IsNullOrEmpty(value_.ToString()))
                {

                    DateTime.TryParse(value_.ToString(), out DateTime date);
                    date__ = date;
                }
                if (cellEditContext.EditedValues.ContainsKey(GridDataItem.ColumnName))
                {
                    object els;
                    cellEditContext.EditedValues.TryGetValue(GridDataItem.ColumnName, out els);
                    if (els != null)
                    {
                        DateTime.TryParse(els.ToString(), out DateTime date);
                        date__ = date;
                    }
                }
                return date__;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            return null;
        }


            protected virtual DateTime? GetEditDateTimeFieldName(C DataItem, AbstractGridDataItem GridDataItem)
        {
            try
            {
                object value_ = GetFieldValue(DataItem, GridDataItem.Position);
                DateTime? date__ = null;
                if (value_ != null && !string.IsNullOrEmpty(value_.ToString()))
                {

                    DateTime.TryParse(value_.ToString(), out DateTime date);
                    date__ = date;
                }
                if (EditedValues.ContainsKey(GridDataItem.ColumnName))
                {
                    object els;
                    EditedValues.TryGetValue(GridDataItem.ColumnName, out els);
                    if (els != null)
                    {
                        DateTime.TryParse(els.ToString(), out DateTime date);
                        date__ = date;
                    }
                }
                return date__;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            return null;
        }

        protected LoadResult loadResult { get; set; } = new LoadResult();


        [JSInvokable("RefreshGirdFromJSRow")]
        public void RefreshGirdFromJSRow(bool value)
        {
            AllowAllRow_ = value;
        }

        [JSInvokable("RefreshGirdFromJS")]
        public void RefreshGirdFromJS(bool value)
        {
            AllowRowCounting_ = value;
        }
        private DotNetObjectReference<AbstractGridComponent<P,C>> DotNetObjectReference_;
        private RenderFormContent RenderFormContentRef { get; set; }

        protected async Task<LoadResult> LoadCustomData(DataSourceLoadOptionsBase options, CancellationToken cancellationToken)
        {
            if (!UsingManualyData)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    filter.ShowAll = AllowAllRow;
                    if (options.Take > 0)
                    {
                        filter.PageSize = options.Take;
                    }
                    else
                        if (options.Take == -1)
                    {
                        filter.ShowAll = true;
                    }

                    filter.AllowRowCounting = AllowRowCounting;
                    
                    loadResult.data = new ObservableCollection<C>();
                    await BuildFilter(filter, options);
                    BrowserDataPage<C> page_ = await SearchRows_(options);
                    if (AllowAllRow)
                    {
                        page_.PageSize = -1;
                    }
                    if (page_ != null)
                    {
                        loadResult.data = page_.Items;
                        loadResult.groupCount = page_.PageCount;
                        page_PageSize = page_.PageSize;
                        loadResult.totalCount = page_.TotalItemCount;
                    }
                    else
                    {
                        loadResult.data = new ObservableCollection<C>();
                        loadResult.groupCount = 1;
                        loadResult.totalCount = 0;
                    }
                    if (((IEnumerable<C>)loadResult.data).Count() == 0 && EditButtonVisible && EmptyLineVisible)
                    {
                        ObservableCollection<C> datas = new ObservableCollection<C>();
                        datas.Add(NewItem());
                        loadResult.data = datas;
                        loadResult.totalCount = 1;
                    }
                    if (IsAfterRefresh)
                    {
                        await DoHandlerAfterRefresh_?.Invoke();
                    }
                    AppState.HideLoadingStatus();
                    //await InvokeAsync(StateHasChanged);
                    //CanSetRowCounting = true;
                    if(RenderFormContentRef != null)
                    {
                        await RenderFormContentRef.StateHasChanged_();
                    }
                    if(DotNetObjectReference_ != null && AllowToDisplayCustomPager)
                    {
                        await JSRuntime.InvokeVoidAsync("setRowsCountOfGrid", GridId, CheckBoxTotalPageId, CheckBoxTotalPageControl, DotNetObjectReference_).AsTask();
                    }
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                   // await InvokeAsync(StateHasChanged);
                }
            }
            else
            {
                var dat = GetManualyDatas();
                loadResult.data = dat;
                loadResult.groupCount = 1;
                loadResult.totalCount = dat.Count();
            }
            return loadResult;
        }

        protected virtual IEnumerable<C> GetManualyDatas()
        {
            return ManualyData;
        }
        #endregion

        #region private method
        protected async virtual void NavigateTo(object id)
        {
            string uri = NavLinkURI().TrimEnd();
            if (string.IsNullOrWhiteSpace(uri))
            {
                return;
            }
            uri = uri.TrimEnd();
            if (!uri.EndsWith("/"))
            {
                uri = uri + "/";
            }
            await AppState.NavigateTo(uri + id);
        }

        protected virtual string GetOpenTabLink(object id)
        {
            if (!string.IsNullOrWhiteSpace(NavLinkURI()))
            {
                string uri = NavLinkURI().TrimEnd();
                if (string.IsNullOrWhiteSpace(uri))
                {
                    return Route.HOME_PAGE;
                }
                uri = uri.TrimEnd();
                if (!uri.EndsWith("/"))
                {
                    uri = uri + "/";
                }
                string opentaburi = uri + id;
                return opentaburi;
            }
            else
            {
                return "";
            }

        }

        protected async Task<BrowserDataPage<C>> SearchRows_(DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<C> page_ = new BrowserDataPage<C>();
            if (ItemsCount > 0)
            {
                await SearchRows(filter, page_, options);
            }
            return page_;
        }

        private void EditGridCell(CellEditContext cellEditContext, string column, object value)
        {
            if (cellEditContext.EditedValues.ContainsKey(column))
            {
                cellEditContext.EditedValues.Remove(column);
            }
            cellEditContext.EditedValues.Add(column, value);
        }

        protected void EditGridCell(C cellEditContext, string column, object value)
        {
            if (EditedValues.ContainsKey(column))
            {
                EditedValues.Remove(column);
            }
            EditedValues.Add(column, value);
        }

        private async void OnRowRemoving_(C dataItem)
        {
            try
            {
                await OnRowRemoving(dataItem);
                await InvokeAsync(StateHasChanged);
                await Refresh();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        private void CancelDeletion()
        {
            DeleteConfirmationPopup = false;
        }

        protected virtual bool CkeckDeleteCondition()
        {
            return SelectedItem != null && DeleteContext.Equals(AppState["Delete"]);
        }

        protected virtual void AfterDelete()
        {
            
        }
        protected virtual void DeleteAction()
        {
            if(CkeckDeleteCondition())
            {
                OnRowRemoving_((C)SelectedItem);
            }
            else if(DeleteContext.Equals(AppState["DeleteAll"]))
            {
                DeleteAll();
            }
            AfterDelete();
            DeleteConfirmationPopup = false;
        }
        private async void OnRowUpdating_(C dataItem, Dictionary<string, object> newValues)
        {
            try
            {
                await OnRowUpdating(dataItem, newValues);
                await InvokeAsync(StateHasChanged);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        private async void OnRowInserting_(Dictionary<string, object> newValues)
        {
            try
            {
                await OnRowInserting(newValues);
                await InvokeAsync(StateHasChanged);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        protected override Task OnInitializedAsync()
        {
            if (AllowToDisplayCustomPager)
            {
                DotNetObjectReference_ = DotNetObjectReference.Create(this);
            }
            return base.OnInitializedAsync();
        }
        #endregion

        #region init and dispose

        public override Task SetParametersAsync(ParameterView parameters)
        {
            attributes.Remove("grid-bcephal-id");
            attributes.Add("grid-bcephal-id", GridId);
            return base.SetParametersAsync(parameters);
        }
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                if (CanRefresh)
                {
                    AppState.CanRefresh = true;
                    AppState.RefreshHander += RefreshGird;
                }
                if (CanCreate)
                {
                    AppState.CreateHander += Create;
                }                
            }
            //if (CanSetRowCounting)
            // {
                //   await JSRuntime.InvokeVoidAsync("setRowsCountOfGrid", GridId,loadResult.totalCount, AppState["total.rows.count"].ToString());
                //    await JSRuntime.InvokeVoidAsync("setRowsCountOfGrid", GridId, CheckBoxTotalPageControl, DotNetObjectReference.Create(this)).AsTask();
               //   CanSetRowCounting = false;
               // await JSRuntime.InvokeVoidAsync("setRowsCountOfGrid", GridId, CheckBoxTotalPageId, CheckBoxTotalPageControl, DotNetObjectReference.Create(this)).AsTask();
           // }
            await base.OnAfterRenderAsync(firstRender);
        }

        
        private void RefreshGird()
        {
             Refresh();
        }

        protected async virtual void Create()
        {
            if (!String.IsNullOrEmpty(EditorRoute))
            {
                await AppState.NavigateTo(EditorRoute);
            }
        }

        public virtual ValueTask DisposeAsync()
        {
            if (CanRefresh)
            {
                AppState.CanRefresh = false;
                AppState.RefreshHander -= RefreshGird;
            }

            if (CanCreate)
            {
                AppState.CanCreate = false;
                AppState.CreateHander -= Create;
            }
            
            return ValueTask.CompletedTask;
        }
        #endregion

        #region text decoration
        protected virtual void OnHtmlRowDecoration(DataGridHtmlRowDecorationEventArgs<C> eventArgs)
        {
            // if (eventArgs.VisibleIndex % 2 == 1)
            // eventArgs.CssClass = "bc-grid-row-style";


            //if (eventArgs.DataItem != null && eventArgs.DataItem.Quantity > largeOrder)
            //    eventArgs.CssClass = " table-warning font-weight-bold";
            //else
            //    eventArgs.Attributes.Add("data-low-price", "");
            OnHtmlRowDecorationContextMenu(eventArgs);
        }

        protected virtual void OnHtmlDataCellDecoration(DataGridHtmlDataCellDecorationEventArgs<C> eventArgs)
        {
            //eventArgs.CssClass += " border-0";
            //if (eventArgs.FieldName == nameof(Invoice.OrderId))
            //{
            //    if (eventArgs.RowVisibleIndex % 2 == 1)
            //        eventArgs.Style += " background-color: rgb(169, 148, 200); color: black;";
            //    else
            //        eventArgs.Style += " background-color: rgb(210, 198, 233); color: black;";
            //    if (eventArgs.DataItem.Quantity > largeOrder)
            //    {
            //        eventArgs.CssClass += " font-weight-bold";
            //    }
            //}
            
        }
        #endregion

        #region header template
        

        protected virtual string GridCssClass()
        {
            return "custom-grid";
        }

        //private DxDataGrid<C> DxDataGridRef_
        //{
        //    get => DxDataGridRef;
        //    set
        //    {
        //        DxDataGridRef = value;
        //        if (DxDataGridRef != null)
        //        {
        //            setParamsViewGrid();
        //        }
        //    }
        //}

        //private async void setParamsViewGrid()
        //{
        //    var param = new Dictionary<string, object>() { };
        //    if (CustomHeaderRender != null)
        //    {
        //        param.Add("HeaderTemplate", addHeaderRender);
        //    }
        //    await DxDataGridRef.SetParametersAsync(ParameterView.FromDictionary(param));
        //}

        protected virtual string FormatDoubleCellValue(string format, Object obj)
        {
            if (obj == null)
            {
                return null;
            }
            try
            {
                double.TryParse(obj.ToString(), out double obj_);
                string str = obj_.ToString(format);
                return str;
            }
            catch
            {
                try
                {
                    DateTime.TryParse(obj.ToString(), out DateTime obj_);
                    return obj_.ToString(format, CultureInfo.InvariantCulture);
                }
                catch{}
            }
            return obj.ToString();
        }

        protected virtual string FormatDateCellValue(string format, Object obj)
        {
            if (obj == null)
            {
                return null;
            }
            try
            {
                DateTime.TryParse(obj.ToString(), out DateTime obj_);
                return obj_.ToString(format);
            }
            catch{}
            return obj.ToString();
        }

        protected virtual void setCellEditContext(CellEditContext context_)
        {

        }
        #endregion

        #region context menu to link

        DxContextMenu ContextMenuOpenLinkRef;

        private string currentUrl { get; set; }
        private string currentId { get; set; }

        private void OpenLing(MouseEventArgs args, C item, string url,string id)
        {
            selectedItem = item;
            currentUrl = url;
            currentId = id;
            ContextMenuOpenLinkRef.ShowAsync(args);
        }
        private Task OnItemClickOpenLinkOnNewTab(ContextMenuItemClickEventArgs args)
        {
            string text = args.ItemInfo.Text;
            if (text.Equals(AppState["Open"]))
            {
                NavigateTo(currentId);
            }
            else
           if (text.Equals(AppState["OpenOnNewTab"]))
            {
                AppState.StartLinkInNewTab(currentUrl);
            }
            return Task.CompletedTask;
        }

        #endregion


        #region context Menu

        protected virtual void SetEdite()
        {
            EditedValues = new Dictionary<string, object>();
            // InitCustomEdit(EditedValues);

            selectedItem.IsInEditMode = true;
            // DxDataGridRef.StartRowEdit((C)selectedItem);
        }

        DxContextMenu ContextMenuRef;
        private bool IsInEditMode { get; set; } = false;
        protected bool IsNewRow { get; set; } = false;
        Dictionary<string, object> EditedValues { get; set; }
        protected bool UsingDefaultCustomTemplate { get; set; } = true;

       protected virtual RenderFragment CustomContextMenuRender { get; }

        protected virtual Task OnItemClick(ContextMenuItemClickEventArgs args)
        {
            string text = args.ItemInfo.Text;
            DeleteContext = text;
            IsNewRow = false;
            if (string.IsNullOrWhiteSpace(DeletionTitle))
            {
                DeletionTitle = AppState["DeletionTitle"];
            }
            
            if (text.Equals(AppState["New"]))
            {
                EditedValues = new Dictionary<string, object>();
                IsNewRow = true;
                selectedItem = NewItem();
                selectedItem.IsInEditMode = true;
                DxDataGridRef.StartRowEdit(null);
                //DxDataGridRef.SetDataRowSelected(selectedItem, true);
            }
            else
           if (text.Equals(AppState["Edit"]))
            {
                SetEdite();
            }
            if (text.Equals(AppState["Duplicate"]))
            {
                Duplicate(SelectedItems);
            }
            else
            if (text.Equals(AppState["Delete"]))
            {
                DeleteMessage = AppState["SureToDeleteSelectedItem", GetFieldValue(SelectedItem, 0)];
                DeleteConfirmationPopup = true;               
            }
            else if(text.Equals(AppState["DeleteAll"]))
            {
                DeleteMessage = AppState["SureToDeleteAllItem"];
                DeleteConfirmationPopup = true;
            }
            return Task.CompletedTask;
        }

        protected virtual void Duplicate(List<long> selectedItems)
        {

        }

        void OnHtmlRowDecorationContextMenu(DataGridHtmlRowDecorationEventArgs<C> eventArgs)
        {
            eventArgs.Attributes["oncontextmenu"] = EventCallback.Factory.Create(this, (Func<MouseEventArgs, Task>)onContextMenu);
            async Task onContextMenu(MouseEventArgs e)
            {
                selectedItem = eventArgs.DataItem;                
                await ContextMenuRef.ShowAsync(e);
            }
            eventArgs.Attributes["onclick"] = EventCallback.Factory.Create(this, (Func<MouseEventArgs, Task>)onClick);
            async Task onClick(MouseEventArgs e)
            {
              await  onClick_(e, eventArgs.DataItem) ;
            }
        }

        protected  virtual Task onClick_(MouseEventArgs e, C item)
        {
            return Task.CompletedTask;
        }

        private List<long> GetAllIds()
        {
            List<long> items = new();
            if(loadResult != null && loadResult.data != null)
            {
                if(loadResult.data is ObservableCollection<C>)
                {
                    ObservableCollection<C> datas = (ObservableCollection<C>)loadResult.data;
                    foreach(C item in datas)
                    {
                        object value = KeyFieldValue(item);
                        try
                        {
                            long.TryParse(value.ToString(), out long id);
                            items.Add(id);
                        }
                        catch { }                       
                    }
                }
                else
                if (loadResult.data is List<C>)
                {
                    List<C> datas = (List<C>)loadResult.data;
                    foreach (C item in datas)
                    {
                        object value = KeyFieldValue(item);
                        try
                        {
                            long.TryParse(value.ToString(), out long id);
                            items.Add(id);
                        }
                        catch { }
                    }
                }
            }
            return items;
        }

        protected async void DeleteAll()
        {
            try
            {
                AppState.ShowLoadingStatus();
                List<long> Items = GetAllIds();
                if (DxDataGridRef != null && Items.Count > 0)
                {
                    await OnRowRemoving(Items);
                    await Refresh();
                    EnabledDelete = false;
                    ChangeToolbarEnabled(false);
                }
                AppState.HideLoadingStatus();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        
        protected async Task EnterHandleValidSubmit(KeyboardEventArgs args, C DataItem)
        {
            await Task.Yield();
            if (args != null && !string.IsNullOrWhiteSpace(args.Code) && args.Code.Contains("Enter"))
            {
                await HandleValidSubmit(DataItem);
                DataItem.IsInEditMode = false;
            }
            else
            if(args != null && !string.IsNullOrWhiteSpace(args.Code) && args.Code.Equals("Escape"))
            {
                await OnCancelButtonClick(DataItem);
            }
        }

        protected async Task EnterHandleValidSubmit_(FocusEventArgs args, C DataItem)
        {
            await Task.Yield();
            if (args != null)
            {
                await HandleValidSubmit(DataItem);
                DataItem.IsInEditMode = false;
            }
            //else
            //if (args != null && !string.IsNullOrWhiteSpace(args.Code) && args.Code.Equals("Escape"))
            //{
            //    await OnCancelButtonClick(DataItem);
            //}
        }

        protected virtual async Task HandleValidSubmit(C DataItem)
        {
            try
            {
                if (IsNewRow)
                {
                    await OnRowInserting(EditedValues);
                }
                else
                {
                    await OnRowUpdating(DataItem, EditedValues);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }


        #endregion



        #region edit formcontext

        protected CustomCellEditContext CustomCellEditContext_ { get; set; } = null;
        protected class CustomCellEditContext
        {
            public Error Error { get; set; }
            public CustomCellEditContext(C dataItem, Dictionary<string, object> EditedValues, Func<C, int, object> func, Error Error, bool IsNewRow)
            {
                DataItem = dataItem;
                this.IsNewRow = IsNewRow;
                this.EditedValues = EditedValues;
                GetFieldValueFunc = func;
                this.Error = Error;
                Items = new();
            }

            protected Func<C, int, object> GetFieldValueFunc;

            public C DataItem { get; set; }
            public List<dynamic> Items { get; set; }
            public Dictionary<string, object> EditedValues { get; set; }
            public bool IsNewRow { get; set; }

            public object columnType { get; set; }
            public string caption { get; set; }
            public string columnWidth { get; set; }
            public Action StateHasChanged { get; set; }

            public string GetEditTextFieldName(AbstractGridDataItem item)
            {
                object value_ = GetFieldValueFunc?.Invoke(DataItem, item.Position);
                string value = null;
                if (value_ != null)
                {
                    value = value_.ToString();
                }
                if (EditedValues.ContainsKey(item.ColumnName))
                {
                    object els;
                    EditedValues.TryGetValue(item.ColumnName, out els);
                    if (els != null)
                    {
                        value = els.ToString();
                    }
                }
                return value;
            }

            public decimal? A { get; set; }
            public virtual decimal? GetEditDecimalFieldName(AbstractGridDataItem item)
            {
                try
                {
                    object value_ = GetFieldValueFunc?.Invoke(DataItem, item.Position);
                    decimal? value__ = null;
                    if (value_ != null && !string.IsNullOrEmpty(value_.ToString()))
                    {
                        decimal.TryParse(value_.ToString(), out decimal value);
                        value__ = value;
                    }
                    if (EditedValues.ContainsKey(item.ColumnName))
                    {
                        object els;
                        EditedValues.TryGetValue(item.ColumnName, out els);
                        if (els != null)
                        {
                            decimal.TryParse(els.ToString(), out decimal value);
                            value__ = value;
                        }
                    }
                    return value__;
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                    StateHasChanged();
                }
                return null;
            }

            public virtual DateTime? GetEditDateTimeFieldName(AbstractGridDataItem item)
            {
                try
                {
                    object value_ = GetFieldValueFunc?.Invoke(DataItem, item.Position);
                    DateTime? date__ = null;
                    if (value_ != null && !string.IsNullOrEmpty(value_.ToString()))
                    {

                        DateTime.TryParse(value_.ToString(), out DateTime date);
                        date__ = date;
                    }
                    if (EditedValues.ContainsKey(item.ColumnName))
                    {
                        object els;
                        EditedValues.TryGetValue(item.ColumnName, out els);
                        if (els != null)
                        {
                            DateTime.TryParse(els.ToString(), out DateTime date);
                            date__ = date;
                        }
                    }
                    return date__;
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                    StateHasChanged();
                }
                return null;
            }

            public void EditGridCell(AbstractGridDataItem item, object value)
            {
                if (EditedValues.ContainsKey(item.ColumnName))
                {
                    EditedValues.Remove(item.ColumnName);
                }
                EditedValues.Add(item.ColumnName, value);
            }
        }

        protected virtual CustomCellEditContext GetCustomCellEditContext(C dataItem, Dictionary<string, object> EditedValues, bool isNew = false)
        {
            return new CustomCellEditContext(dataItem, EditedValues, GetFieldValue, Error, isNew);
        }


        protected virtual Task OnRowEditStarting(C item)
        {
            Dictionary<string, object> EditedValues = new Dictionary<string, object>();
            InitCustomEdit(EditedValues);
            CustomCellEditContext_ = GetCustomCellEditContext(item, EditedValues);
            CustomCellEditContext_.StateHasChanged += () => { InvokeAsync(StateHasChanged); };
            return Task.CompletedTask;
        }

        protected virtual Task OnCancelButtonClick(C item)
        {
            item.IsInEditMode = false;
            return Task.CompletedTask;
        }

        protected virtual async Task OnCancelButtonClick()
        {
            await DxDataGridRef.CancelRowEdit();
            CustomCellEditContext_ = null;
        }
        protected virtual async Task HandleValidSubmit()
        {
            try
            {
                if (CustomCellEditContext_.IsNewRow)
                {
                    await OnRowInserting(CustomCellEditContext_.EditedValues);
                }
                else
                {
                    await OnRowUpdating(CustomCellEditContext_.DataItem, CustomCellEditContext_.EditedValues);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            finally
            {
                await Refresh();
                await DxDataGridRef.CancelRowEdit();
            }
          
        }

        private void InitCustomEdit(Dictionary<string, object> EditedValues)
        {
            for (int Position = 0; Position < ItemsCount; Position++)
            {
                EditedValues.Add(ColumnsDatas[Position].ColumnName, null);
            }
        }

        protected virtual async void OnRowInsertStarting()
        {
           await OnRowEditStarting(NewItem());
        }
        #endregion

    }

    public abstract class AbstractGridDataItem
    {
        public int Position { get; set; }

        private object Value { get; set; }

        public Action<AbstractGridDataItem, object> EditAction { get; set; }

        public AbstractGridDataItem(int position)
        {
            this.Position = position;
        }
        public abstract string ColumnName { get; }
        public abstract string CaptionName { get; }
        public virtual string ColumnWidth
        {
            get
            {
                return "100px";
            }
        }
        public virtual bool Visible
        {
            get
            {
                return true;
            }
        }
        public virtual DataGridFixedStyle GridFixedStyle
        {
            get {
                    return DataGridFixedStyle.None;
                }
        }

        public virtual string CommandColumnWidth
        {
            get
            {
                return "5%";
            }
        }
        public virtual bool CanEditColumn
        {
            get
            {
                return true;
            }
        }
        public abstract Type ColumnType { get; }
        public virtual string ColumnFormat
        {
            get
            {
                return null;
            }
        }

        public virtual string ValueString
        {
            get
            {
                if (Value == null)
                {
                    return null;
                }
                return Value.ToString();
            }
            set
            {
                Value = value;
                EditAction?.Invoke(this, Value);
            }
        }

        public virtual DateTime? ValueDate
        {
            get
            {
                if (Value == null)
                {
                    return null;
                }
                try
                {
                    DateTime.TryParse(Value.ToString(), out DateTime val);
                    return val;
                }
                catch
                {
                    return null;
                }
            }
            set
            {
                Value = value;
                EditAction?.Invoke(this, Value);
            }
        }

        public virtual decimal? ValueDecimal
        {
            get
            {
                if (Value == null)
                {
                    return null;
                }
                try
                {
                    decimal.TryParse(Value.ToString(), out decimal val);
                    return val;
                }
                catch
                {
                    return null;
                }
            }
            set
            {
                Value = value;
                EditAction?.Invoke(this, Value);
            }
        }
    }

    public class GridDataItem : AbstractGridDataItem
    {
        object GridColumns;

        public GridDataItem(object GridColumns, int position) : base(position)
        {
            this.GridColumns = GridColumns;
        }
        private object GetPropertyValue(string propName)
        {
            return GridColumns.GetType().GetProperty(propName).GetValue(GridColumns, null);
        }
        public override string CaptionName
        {
            get
            {
                if (GridColumns == null)
                {
                    return null;
                }
                return (string)GetPropertyValue("CaptionName");
            }
        }

        public override string ColumnName
        {
            get

            {
                if (GridColumns == null)
                {
                    return null;
                }
                return (string)GetPropertyValue("ColumnName");
            }
        }

        public override Type ColumnType
        {
            get

            {
                if (GridColumns == null)
                {
                    return typeof(string);
                }
                return (Type)GetPropertyValue("ColumnType");
            }
        }

        public override string ColumnWidth
        {
            get

            {
                if (GridColumns == null)
                {
                    return null;
                }
                return (string)GetPropertyValue("ColumnWidth");
            }
        }
    }

}
