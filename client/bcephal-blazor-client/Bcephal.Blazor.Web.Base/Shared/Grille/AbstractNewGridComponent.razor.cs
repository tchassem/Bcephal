using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared.Component;
using System.ComponentModel;
using System.Diagnostics;
using Bcephal.Blazor.Web.Base.Shared.Grille;
using DevExpress.Data.ODataLinq;
using System.Reflection;
using Microsoft.AspNetCore.Components.CompilerServices;

namespace Bcephal.Blazor.Web.Base.Shared.AbstractComponent
{
    public abstract partial class AbstractNewGridComponent<P, C> : ComponentBase, IAsyncDisposable where P : Persistent where C : DataForEdit
    {
        #region inject properties

        [Inject] public IJSRuntime JSRuntime { get; set; }
        [Inject] protected AppState AppState { get; set; }

        #endregion

        #region parameter

        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public EditorData<P> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<P>> EditorDataChanged { get; set; }
        [Parameter] public Func<List<long>, Task> OnSelectionChangeHandler_ { get; set; }
        [Parameter] public Func<Task> DoHandlerAfterRefresh_ { get; set; }
        [Parameter] public bool UsingManualyData { get; set; } = false;
        [Parameter] public bool DuplicateButtonVisible { get; set; } = false;
        [Parameter] public bool AllowToDisplayCustomPager { get; set; } = true;
        [Parameter] public ObservableCollection<C> ManualyData { get; set; } = new ObservableCollection<C>();
        [Parameter] public string KeyName { get; set; } = "AbstractNewGridComponent";
        [Parameter] public Func<RenderFragment> CustomHeaderRenderHandler { get; set; }

        private bool CanDisplayHeader { get; set; } = false;
        protected readonly IDictionary<string, object> CardAttributes = new Dictionary<string, object>();
        #endregion

        #region Data source
        private GridCustomDataSource DataSource { get; set; }

        protected GridCustomDataSource GetGridDevExtremeDataSource()
        {
            return new CustomGridDevExtremeNewDataSource<C>(ExecuteDataSourceHttpRequestPage_,
                () => page_,
                RefreshGrid_,
                GetAndUpdateGridStatus,
                !UsingManualyData ? null : () => ManualyData,
                SummaryDataSourceHttpRequestPage_
                );
        }


        #endregion


        #region internal propeties
        protected bool PageSizeSelector { get; set; } = true;
        protected bool ShowPager { get; set; } = true;
        protected int PagerVisibleNumericButtonCount { get; set; } = 10;
        protected int PagerSwitchToInputBoxButtonCount { get; set; } = 20;        
        protected bool NewButtonVisible { get; set; } = false;        
        protected bool ClearFilterButtonVisible { get; set; } = false;
        protected bool DeleteButtonVisible { get; set; } = false;
        protected bool DeleteAllButtonVisible { get; set; } = true;
        protected bool CanRefresh { get; set; } = true;
        protected bool CanCreate { get; set; } = true;
        protected bool EditButtonVisible { get; set; } = false;
        protected bool ShowsAll_ { get; set; } = false;
        protected bool ShowAllRows { get; set; } = false;
        protected bool AllowSort { get; set; } = true;
        protected bool AllowSelectRowByClick { get; set; } = true;
        protected bool displayItem => EditButtonVisible || NewButtonVisible || DeleteButtonVisible;
        protected bool displayItem_ => EditButtonVisible || NewButtonVisible;
        protected bool AllowAllRow { get; set; } = false;
        protected bool AllowRowCounting { get; set; } = false;
        #endregion

        

        #region other properties
        public string EditorRoute { get; set; }        
        protected GridSelectionMode SelectionMode { get; set; } = GridSelectionMode.Multiple;
        protected GridColumnResizeMode ColumnResizeMode { get; set; } = GridColumnResizeMode.ColumnsContainer;
        protected GridEditMode CurrentEditMode { get; set; } = GridEditMode.EditForm;
        protected DxGrid DxGridRef { get; set; }        
        public CardComponent CardComponentRef { get; set; }
        protected DxGridSelectionColumn DxGridSelectionColumnRef { get; set; }
        protected List<AbstractNewGridDataItem> ColumnsDatas { get; set; }
        protected List<SummaryDataItem> ColumnsSummaryDatas { get; set; }
        bool canAddSummary { get; set; }
        public IReadOnlyList<object> SelectedDataItems { get; set; } = new List<object>();
        protected object SelectedDataItem { get; set; }

        protected void SelectedDataItemsChanged(IReadOnlyList<object> selectedDataItems)
        {
            SelectedDataItems = selectedDataItems;
            if(SelectedDataItems.Count > 0)
            {
                OnSelectedDataItemChanged(SelectedDataItems[SelectedDataItems.Count - 1]);
            }
            else
            {
                OnSelectedDataItemChanged(null);
            }
            if (CanCallBackSelectionChangeHandler && StateReadyFirst_)
            {
                CanCallBackSelectionChangeHandler = false;
                OnSelectionChangeCallBack();
            }
        }

        protected virtual void OnSelectedDataItemChanged(object selectedDataItem)
        {
            SelectedDataItem = selectedDataItem;
        }

        private bool CanCallBackSelectionChangeHandler { get; set; } = true;
        private async void OnSelectionChangeCallBack()
        {
            if (GridSelectionMode.Multiple.Equals(SelectionMode))
            {
                Task tas = Task.Delay(TimeSpan.FromSeconds(1.5));
                tas = tas.ContinueWith(t => OnSelectionChangeHandlerCallBack());
                await tas;
            }
            else
            {
                OnSelectionChangeHandlerCallBack();
            }
        }

        protected async void OnSelectionChangeHandlerCallBack()
        {
            if (OnSelectionChangeHandler_ != null)
            {
                Task tas = OnSelectionChangeHandler_.Invoke(GetSelectionDataItemsIds());
                tas = tas.ContinueWith(
                    t => { 
                        CanCallBackSelectionChangeHandler = true;
                    });
                await tas;
            }
        }

        protected bool ShowFilterRow { get; set; } = true;
        private int page_CurrentPage;
        protected int page_PageSize = 25;
        protected readonly BrowserDataFilter filter = new BrowserDataFilter();
        protected readonly IReadOnlyList<int> PageSize = new List<int>() { 10, 25, 50, 100, 200, 500, 1000, 2000/*, 5000, 10000, 20000*/ };
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
        protected virtual bool CanShowSelectionColumn { get; set; } = true;
        protected string DeleteContext { get; set; } = "";
        protected virtual string DeletionTitle { get; set; }
        protected string DeleteMessage { get; set; } = "";

        #endregion

        #region abstract method

        protected abstract int ItemsCount { get; }
        protected virtual int ItemsSummaryCount { get; }
        protected abstract Task<BrowserDataPage<C>> SearchRows(BrowserDataFilter filter);
        protected abstract AbstractNewGridDataItem GetGridDataItem(int Position);
        protected virtual SummaryDataItem GetSummaryItem(int Position){ return null; }
        protected abstract object GetFieldValue(C item, int grilleColumnPosition);
        protected abstract Task OnRowRemoving(C dataItem);
        protected abstract Task OnRowRemoving(IReadOnlyList<object> dataItem);
        protected abstract Task OnRowUpdating(C dataItem, C editModel);
        protected abstract Task OnRowInserting(C newValues);
        protected abstract string KeyFieldName();
        protected abstract object KeyFieldValue(C item);
        protected abstract string NavLinkURI();

        #endregion

        #region Virtual
        

        protected virtual string ColumnChoserWidth { get { return ItemsCount == 0 ? "auto" : ItemsCount < 3 ? "2.5%" : ItemsCount < 7 ? "3%" : "35px"; } }
        protected virtual int ColumnMinWidth { get { return ItemsCount == 0 ? 150 : ItemsCount < 3 ? 130 : ItemsCount < 20 ? 75 : 30; } }
        protected virtual string ColumnWidth(int Position) {  return ItemsCount == 0 ? "auto" : ItemsCount < 10 ? "auto" : "200px";  }
        protected virtual string getCommandColumnWidth()
        {
            return "5%";
        }
        protected virtual Task BuildFilter(BrowserDataFilter filter)
        {
            filter.ColumnFilters = null;
            filter.Criteria = null;
            ColumnFilter columnFilter2 = new ColumnFilter();
            foreach (var abstractNewGridDataItem in ColumnsDatas)
            {
                if (!string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Operation))
                {
                    bool isNull = string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) && abstractNewGridDataItem.ColumnFilters.Operation.Equals("IsNullOrEmpty");
                    bool isNotNull = string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) && abstractNewGridDataItem.ColumnFilters.Operation.Equals("NotIsNullOrEmpty");
                    bool isNullORisNotNull = string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) && (isNull || isNotNull);
                    if (!string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) || isNullORisNotNull)
                    {
                        columnFilter2.Items.Add(abstractNewGridDataItem.ColumnFilters);
                        abstractNewGridDataItem.ColumnFilters.Name = abstractNewGridDataItem.ColumnName;
                    }
                }
            }
            if (columnFilter2.Items.Count > 1)
            {
                filter.ColumnFilters = new();
                filter.ColumnFilters.Link = " And ";
                filter.ColumnFilters.Operation = " And ";
                filter.ColumnFilters.Grouped = true;
                filter.ColumnFilters.Items.AddRange(columnFilter2.Items);
            }
            else
            {
                if(columnFilter2.Items.Count == 1)
                {
                    filter.ColumnFilters = columnFilter2.Items[0];
                }
                
            }
            return Task.CompletedTask;
        }
        protected virtual string getPopupEditFormHeaderTextLabel()
        {
            return "EDIT.FORM";
        }

        protected virtual void CheckedChanged(C item, int position, bool value)
        {

        }
        protected virtual bool CkeckDeleteCondition()
        {
            return SelectedDataItem != null && DeleteContext.Equals(AppState["Delete"]);
        }

        protected virtual void AfterDelete()
        {

        }
        protected virtual void SetFieldValue(C item, int grilleColumnPosition, object value) {
        }
        protected virtual void Duplicate(List<long> selectedItems)
        {

        }

        #endregion

        #region new Item
        protected virtual C NewItem()
        {
            return default;
        }

        protected virtual C NewItem(C item)
        {
            C item_ = NewItem();
            if (item_ != null)
            {
                if (ColumnsDatas != null)
                {
                    ColumnsDatas.ForEach(CItem =>
                    {
                        var value = GetFieldValue(item, CItem.Position);
                        SetFieldValue(item_, CItem.Position, value);
                    });
                }
            }
            return item_;
        }

        private C NewEmptyItem()
        {
            C item_ = NewItem();
            if (item_ != null)
            {
                item_._IsInNewEditMode = true;
            }
            return item_;
        }

        #endregion
        

        #region edition
        
        protected void EditGridCell(C item, int position, object value)
        {
            SetFieldValue(item, position, value);
        }

        protected virtual void RowDoubleClick(GridRowClickEventArgs args)
        {
            C item = (C)args.Grid.GetDataItem(args.VisibleIndex);
            if (displayItem_ && item != null)
            {
                try
                {
                    if (((C)item)._IsInNewEditMode)
                    {
                        SetNewEdite(item);
                    }
                    else
                    {
                        SetEdite(item);
                    }
                    ((C)item).CanEditRefreshGridStatus = true;
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
            }
        }

        protected async Task EnterHandleValidSubmit(KeyboardEventArgs args, C DataItem)
        {
            try { 
            if (args != null && !string.IsNullOrWhiteSpace(args.Code) && args.Code.Contains("Enter"))
            {
                await HandleValidSubmit(DataItem);
                DataItem.IsInEditMode = false;
                DataItem._IsInNewEditMode = false;
            }
            else
            if (args != null && !string.IsNullOrWhiteSpace(args.Code) && args.Code.Equals("Escape"))
            {
                await OnCancelButtonClick(DataItem);
            }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                if (args != null && !string.IsNullOrWhiteSpace(args.Code) && (args.Code.Contains("Enter") || args.Code.Equals("Escape")))
                {
                    DataItem.CanEditRefreshGridStatus = false;
                    if (CanRefreshAfterEdit)
                    {
                        await RefreshGrid_();
                    }
                }
            }         
        }

        protected bool CanRefreshAfterEdit  = false;

        protected virtual Task HandleValidSubmit(C DataItem)
        {
            if (DataItem._IsInNewEditMode)
            {
                return OnRowInserting_(EditedValues);
            }
            else
            {
                return OnRowUpdating_(DataItem, EditedValues);
            }
        }

        [Parameter]
        public bool CanRefreshGridStatus { get; set; } = true;

        public bool CanEditRefreshGridStatus { get; set; } = false;

        protected override bool ShouldRender()
        {
            return CanRefreshGridStatus || CanEditRefreshGridStatus;
        }

        protected void UpdateCanRfreshGridStatus(bool CanRefreshGridStatus)
        {
            this.CanRefreshGridStatus = CanRefreshGridStatus;
        }

        protected bool GetCanRfreshGridStatus() => this.CanRefreshGridStatus;

        private  bool GetAndUpdateGridStatus(bool newStatus)
        {
            bool stat = this.CanRefreshGridStatus;
            UpdateCanRfreshGridStatus(newStatus);
            return stat;
        }

        protected Task RefreshGrid_()
        {
            UpdateCanRfreshGridStatus(true);
            if(DxGridRef != null)
            {
                DxGridRef.Reload();
            }
            return Task.CompletedTask;
        }
        protected async Task AfterRefreshGrid_()
        {
            if (DxGridRef != null && DxGridRef.ShowAllRows != AllowAllRow)
            {
                var dico = new Dictionary<string, object>() {
                        { "PageIndex", AllowAllRow ? 0 : page_CurrentPage },
                        { "ShowAllRows", AllowAllRow },
                        { "PageSizeSelectorAllRowsItemVisible", AllowAllRow },
                    };
                await DxGridRef.SetParametersAsync(ParameterView.FromDictionary(dico));
            }
        }

        protected async  Task StateHasChangedAsync()
        {
            await this.InvokeAsync(StateHasChanged);
        }

        #endregion

       

        #region override method


        private void ShowPagerWidgetsCheckbox()
        {
            RenderFormContentRef.Refresh(BuildChecBoxTotalPage);
        }

        #endregion

        #region protected method Get value
        protected virtual string GetEditTextFieldName(C DataItem, AbstractNewGridDataItem GridDataItem)
        {
            object value_ = GetFieldValue(DataItem, GridDataItem.Position);
            string value = null;
            if (value_ != null)
            {
                value = value_.ToString();
            }
            return value;
        }

        protected virtual decimal? GetEditDecimalFieldName(C DataItem, AbstractNewGridDataItem GridDataItem)
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
                return value__;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            return null;
        }

        protected virtual RenderFragment<Action> GetEditData(GridDataColumnCellDisplayTemplateContext context, AbstractNewGridDataItem GridDataItem)
        {
            return null;
        }

        protected virtual DateTime? GetEditDateTimeFieldName(C DataItem, AbstractNewGridDataItem GridDataItem)
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
                return date__;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            return null;
        }

        protected BrowserDataPage<C> page_ { get; set; } = new();

        #region CallBack JS


        [JSInvokable("RefreshGirdFromJSRow")]
        public void RefreshGirdFromJSRow(bool value)
        {
            if (AllowAllRow != value)
            {
                AllowAllRow = value;
                if (!AllowAllRow)
                {
                    page_PageSize = 25;
                }
                else
                {
                    page_PageSize = -1;
                    page_CurrentPage = 0;
                }
                InvokeAsync(RefreshGrid_);
            }
        }

        [JSInvokable("RefreshGirdFromJS")]
        public void RefreshGirdFromJS(bool value)
        {
            AllowRowCounting = value;
            InvokeAsync(RefreshGrid_);
        }

        protected bool StateReadyFirst_ { get; set; } = false;

        [JSInvokable("StateReadyFirst")]
        public void StateReadyFirst(bool value)
        {
            StateReadyFirst_ = value;
        }
        private DotNetObjectReference<AbstractNewGridComponent<P, C>> DotNetObjectReference_ { get; set; }

        #endregion

        private RenderFormContent RenderFormContentRef { get; set; }
        private RenderFormContent RenderFormContentRefDialog { get; set; }
       

        protected virtual async Task ExecuteDataSourceHttpRequestPage_(int page, int pageSize)
        {
            filter.Page = page + 1;
            filter.PageSize = pageSize;
            if (!AllowAllRow || UsingManualyData)
            {
                await ExecuteDataSourceHttpRequestPage(AfterRefreshGrid_);
            }
            else
            {
                await LoadAllData(AfterRefreshGrid_);
            }
        }

        protected virtual  dynamic GetSummaryData(string[] columnName, C item)
        {
            return null;
        }

        Task<System.Collections.IList> SummaryDataSourceHttpRequestPage_(GridCustomDataSourceSummaryOptions options)
        {
            string[] columnName = options.SummaryInfo.Select(item => item.FieldName).ToArray();
            var data = page_.Items.Select(ite => GetSummaryData(columnName, ite)).ToObservableCollection();
            return Task.FromResult(data as System.Collections.IList);
        }


      async  Task<BrowserDataPage<C>> ExecuteDataSourceHttpRequestPageAll_(bool showAll, int page, int pageSize)
        {
            filter.Page = page + 1;
            filter.PageSize = pageSize;
            filter.ShowAll = showAll;
            page_PageSize = -1;
            await BuildFilter(filter);
            return await SearchRows_();
        }

        public async Task LoadAllData(Func<Task> afterRefreshGrid)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();
            if (!UsingManualyData)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    page_ = new BrowserDataPage<C>();
                    bool canLoad = true;
                    bool isRefresh = false;
                    int current_page = 0;
                    int MaxPageSize = 2000;
                    int MaxTotalItems = 6000;
                    while (canLoad)
                    {
                        BrowserDataPage<C> pag_ = await ExecuteDataSourceHttpRequestPageAll_(false, current_page++, MaxPageSize);
                        if (pag_ != null)
                        {
                            if (pag_.Items != null && pag_.Items.Count() > 0)
                            {
                                foreach (var item in pag_.Items)
                                {
                                    page_.Items.Add(item);
                                }
                            }
                            if (pag_.PageSize != pag_.Items.Count() || page_.Items.Count > MaxTotalItems)
                            {
                                canLoad = false;
                            }
                            if (afterRefreshGrid != null && !isRefresh)
                            {
                                isRefresh = true;
                                //await afterRefreshGrid();
                                //await Task.Delay(TimeSpan.FromSeconds(30));
                            }
                            else
                            {
                                //await Task.Delay(TimeSpan.FromSeconds(30));
                            }
                        }
                       
                    }
                    page_.TotalItemCount = page_.Items.Count;
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    if (DotNetObjectReference_ != null && AllowToDisplayCustomPager)
                    {
                        ShowPagerWidgetsCheckbox();
                        await JSRuntime.InvokeVoidAsync("setRowsCountOfGrid2", GridId, CheckBoxTotalPageId, CheckBoxTotalPageControl, DotNetObjectReference_).AsTask();

                        await Task.Delay(TimeSpan.FromSeconds(1.2))
                                    .ContinueWith(t =>
                                    {
                                        JSRuntime.InvokeVoidAsync("setRowsCountOfGrid2", GridId, CheckBoxTotalPageId, CheckBoxTotalPageControl, DotNetObjectReference_).AsTask();
                                    });
                    }
                    sw.Stop();
                    long time = sw.ElapsedTicks / (Stopwatch.Frequency / (1000L * 1000L));
                    Console.WriteLine("Total Time to request from Server : {0}ms, {1}s", sw.Elapsed.TotalMilliseconds, sw.Elapsed.TotalSeconds);
                }
            }
            else
            {
                page_.Items = GetManualyDatas();
                page_.CurrentPage = 1;
                page_.TotalItemCount = page_.Items.Count();
            }
            UpdateCanRfreshGridStatus(false);
            if (afterRefreshGrid != null)
            {
                await afterRefreshGrid();
            }
        }

       protected async Task ExecuteDataSourceHttpRequestPage(Func<Task> afterRefreshGrid)
        {
            Stopwatch sw = new Stopwatch(); 
            sw.Start();
            if (!UsingManualyData)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    filter.ShowAll = AllowAllRow;
                    filter.AllowRowCounting = AllowRowCounting;
                    await BuildFilter(filter);
                    page_ = await SearchRows_();
                    int currentotal = (page_.CurrentPage * page_.PageSize);
                    if (!AllowRowCounting)
                    {
                        if (AllowAllRow)
                        {
                            page_.TotalItemCount = page_.Items.Count;
                        }
                        else
                        {
                            if (page_.Items.Count == page_.PageSize)
                            {
                                page_.TotalItemCount = currentotal + page_.PageSize;
                            }
                            else
                        if (page_.Items.Count < page_.PageSize)
                            {
                                if (page_.Items.Count == 0)
                                {
                                    page_.TotalItemCount = currentotal - page_.PageSize;
                                    if(page_.TotalItemCount < 0)
                                    {
                                        page_.TotalItemCount = 0;
                                    }
                                }
                                else
                                {
                                    page_.TotalItemCount = currentotal - page_.PageSize + page_.Items.Count;
                                    if (page_.TotalItemCount < 0)
                                    {
                                        page_.TotalItemCount = 0;
                                    }
                                }
                            }
                        }
                    }

                    if (NewButtonVisible)
                    {
                        page_.TotalItemCount++;
                        EditedValues = NewEmptyItem();
                        page_.Items.Add(EditedValues);
                    }
                    if (DotNetObjectReference_ != null && !StateReadyFirst_)
                    {
                        await JSRuntime.InvokeVoidAsync("setStateReadyFirst", GridId, DotNetObjectReference_,"StateReadyFirst").AsTask();
                    }
                    AppState.HideLoadingStatus();
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    if (DotNetObjectReference_ != null && AllowToDisplayCustomPager)
                    {
                        ShowPagerWidgetsCheckbox();
                        await JSRuntime.InvokeVoidAsync("setRowsCountOfGrid2", GridId, CheckBoxTotalPageId, CheckBoxTotalPageControl, DotNetObjectReference_).AsTask();

                        await Task.Delay(TimeSpan.FromSeconds(1.2))
                                    .ContinueWith(t => {
                                        JSRuntime.InvokeVoidAsync("setRowsCountOfGrid2", GridId, CheckBoxTotalPageId, CheckBoxTotalPageControl, DotNetObjectReference_).AsTask();
                                    });
                    }
                    sw.Stop();
                    long time = sw.ElapsedTicks / (Stopwatch.Frequency / (1000L * 1000L));
                    Console.WriteLine("Total Time to request from Server : {0}ms, {1}s", sw.Elapsed.TotalMilliseconds, sw.Elapsed.TotalSeconds);
                }
            }
            else
            {
                page_.Items = GetManualyDatas();
                page_.CurrentPage = 1;
                page_.TotalItemCount = page_.Items.Count();
            }
             UpdateCanRfreshGridStatus(false);
            if(afterRefreshGrid != null)
            {
              await  afterRefreshGrid();
            }
        }
        public virtual ObservableCollection<C> GetManualyDatas()
        {
            return ManualyData;
        }
        #endregion

        #region private method
        protected async virtual void NavigateTo(object id, int? position = null)
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

        protected virtual string GetOpenTabLink(object id, int? position = null)
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

        protected Task<BrowserDataPage<C>> SearchRows_()
        {
            if (ItemsCount > 0)
            {
                return SearchRows(filter);
            }
            return Task.FromResult(new BrowserDataPage<C>());
        }

        private async void CancelDeletion()
        {
            DeleteConfirmationPopup = false;
            await InvokeAsync(RenderFormContentRefDialog.StateHasChanged_);
        }

        

        protected async Task DeleteAll()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (DxGridRef != null && page_.Items.Count > 0)
                {
                    await OnRowRemoving_((IReadOnlyList<object>)page_.Items);
                }
                AppState.HideLoadingStatus();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
        protected async void DeleteAction_()
        {
            await DeleteAction();
        }
        protected async virtual Task DeleteAction()
        {
            if (CkeckDeleteCondition())
            {
                AppState.ShowLoadingStatus();
                if (SelectedDataItems != null && SelectedDataItems.Count > 0)
                {
                    await OnRowRemoving_(SelectedDataItems).ContinueWith(t => RefreshGrid_());
                }
                else
                if (SelectedDataItem != null)
                {
                    await OnRowRemoving_(new List<C>() { (C)SelectedDataItem }).ContinueWith(t => RefreshGrid_());
                }
                AppState.HideLoadingStatus();
            }
            else if (DeleteContext.Equals(AppState["DeleteAll"]))
            {
              await DeleteAll().ContinueWith(t => RefreshGrid_());
            }
            AfterDelete();
            DeleteConfirmationPopup = false;
        }
        private async Task OnRowUpdating_(C dataItem, C newValues)
        {
            try
            {
                await OnRowUpdating(dataItem, newValues);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        private async Task OnRowRemoving_(C dataItem)
        {
            try
            {
                await OnRowRemoving(dataItem);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        private async Task OnRowRemoving_(IReadOnlyList<object> dataItems)
        {
            try
            {
                await OnRowRemoving(dataItems);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        private async Task OnRowInserting_(C newValues)
        {
            try
            {
                await OnRowInserting(newValues);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
        protected async Task onContextMenu(MouseEventArgs e, C item)
        {
            OnSelectedDataItemChanged(item);
            await ContextMenuRef.ShowAsync(e);
        }
        protected override async Task OnInitializedAsync()
        {
            if (AllowToDisplayCustomPager)
            {
                DotNetObjectReference_ = DotNetObjectReference.Create(this);
            }
            if (ItemsSummaryCount > 0 && !attributes.ContainsKey("TotalSummary"))
            {
                attributes.Add("TotalSummary", TotalSummary);
                attributes.Add("CustomizeSummaryDisplayText", (Action<GridCustomizeSummaryDisplayTextEventArgs>)Grid_CustomizeSummaryDisplayText);
                attributes.Add("UnboundColumnData", (Action<GridUnboundColumnDataEventArgs>)Grid_CustomUnboundColumnData);                
            }
            if (EditButtonVisible)
            {
                //attributes.Add("onclick=", (Func<MouseEventArgs, Task>)onClick_);
            }
            if (CustomHeaderRenderHandler != null)
            {
                CanDisplayHeader = true;
                CardAttributes.Add("Header", CardHeader);
            }
            DataSource = GetGridDevExtremeDataSource();
            await base.OnInitializedAsync();
        }
        #endregion

        #region init and dispose

        public override Task SetParametersAsync(ParameterView parameters)
        {
            attributes.Remove("grid-bcephal-id");
            attributes.Add("grid-bcephal-id", GridId);
            return base.SetParametersAsync(parameters);
        }
        protected override  Task OnAfterRenderAsync(bool firstRender)
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
                if (CardComponentRef != null && CardComponentRef.CanDisplayHeader)
                {
                    CardComponentRef.RefreshHeader();
                }
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        private async void RefreshGird()
        {
          await  RefreshGrid_();
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
            if (CanRefresh && !AppState.IsDashboard)
            {
                AppState.CanRefresh = false;
                AppState.RefreshHander -= RefreshGird;
            }

            if (CanCreate && !AppState.IsDashboard)
            {
                AppState.CanCreate = false;
                AppState.CreateHander -= Create;
            }
            return ValueTask.CompletedTask;
        }
        #endregion

        #region text decoration

        protected virtual void OnCustomHtmlElementDataCellDecoration(GridCustomizeElementEventArgs e)
        {
           
        }

        protected virtual Task onFocusLost(FocusEventArgs ee, GridCustomizeElementEventArgs e)
        {
            return Task.CompletedTask;
        }

        protected virtual Task onClick_(MouseEventArgs ee)
        {
            return Task.CompletedTask;
        }

        async Task onFocusLost_(FocusEventArgs ee)
        {
            await Task.Yield();
            await onFocusLost(ee, null);
        }

        protected virtual void Grid_CustomUnboundColumnData(GridUnboundColumnDataEventArgs e)
        {
           
        }

        protected virtual void Grid_CustomizeSummaryDisplayText(GridCustomizeSummaryDisplayTextEventArgs e)
        {
           
        }

        private void OnHtmlElementDataCellDecoration(GridCustomizeElementEventArgs e)
        {
            if (GridElementType.DataRow.Equals(e.ElementType))
            {
                e.Attributes["oncontextmenu"] = EventCallback.Factory.Create(this, (Func<MouseEventArgs, Task>)onContextMenu_);
                async Task onContextMenu_(MouseEventArgs ee)
                {
                    await onContextMenu(ee, (C)e.Grid.GetDataItem(e.VisibleIndex));
                }
                //e.Attributes["onfocusout"] = EventCallback.Factory.Create(this, (Func<FocusEventArgs, Task>) (async (ee) => await onFocusLost(ee, e)));
                //async Task onFocusLost_(FocusEventArgs ee)
                //{
                //    await Task.Yield();
                //    await onFocusLost(ee, e);
                //}

                //e.Attributes["onclick"] = EventCallback.Factory.Create(this, (Func<MouseEventArgs, Task>)onClick_);
                //Task onClick_(MouseEventArgs ee)
                //{
                //    if (e.Grid.SelectedDataItems.Contains(e.Grid.GetDataItem(e.VisibleIndex)))
                //    {
                //       // e.Style += "background-color: #e4ecfa;";
                //    }
                //    return Task.CompletedTask;
                //}
            }
            if (GridElementType.DataRow.Equals(e.ElementType) && (e.VisibleIndex % 2) == 1)
            {
                e.Style += "background-color: #e4ecfa;";
            }

            if (GridElementType.DataCell.Equals(e.ElementType))
            {
                OnCustomHtmlElementDataCellDecoration(e);
            }

            //if (e.ElementType == GridElementType.DetailCell)
            //{
            //}
            //if (e.ElementType == GridElementType.SelectionCell)
            //{
            //}

            //if (e.ElementType == GridElementType.CommandCell)
            //{
            //}

            //OnCustomHtmlElementDataCellDecoration(e);

            //if (e.ElementType == GridElementType.DataRow && (System.Decimal)e.Grid.GetRowValue(e.VisibleIndex, "Total") > 1000)
            //{
            //    e.CssClass = "highlighted-item";
            //}
            //if (e.ElementType == GridElementType.DataCell && e.Column.Name == "Total")
            //{
            //    e.Style = "font-weight: 800";
            //}
            //if (e.ElementType == GridElementType.GroupRow && e.Column.Name == "Country")
            //{
            //    var summaryItems = e.Grid.GetGroupSummaryItems().Select(i => e.Grid.GetGroupSummaryDisplayText(i, e.VisibleIndex));
            //    e.Attributes["title"] = string.Join(", ", summaryItems);
            //}
        }
        protected virtual void OnHtmlDataCellDecoration(GridCustomizeCellDisplayTextEventArgs eventArgs)
        {
            //if (eventArgs.FieldName == nameof(Models.Loaders.FileLoaderColumn.FileColumn))
            //{
            //    if (eventArgs.DataItem != null && !((Models.Loaders.FileLoaderColumn)eventArgs.DataItem).DimensionId.HasValue)
            //    {
            //        //eventArgs.Style += " color: #dc3545;";
            //    }
            //}
        }
        #endregion

        #region header template


        protected virtual string GridCssClass()
        {
            return "custom-grid custom_-grid";
        }
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
                catch { }
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
                string value =  obj_.ToString(format);
                if (value == null || value.Contains("/0001"))
                {
                    DateTime dt = DateTime.ParseExact(obj.ToString(), format, CultureInfo.InvariantCulture);
                    return dt.ToString(format);
                }
                return value;
            }
            catch { }
            return obj.ToString();
        }

        #endregion

        #region context menu to link

        DxContextMenu ContextMenuOpenLinkRef;

        private string currentUrl { get; set; }
        private string currentId { get; set; }

        private void OpenLing(MouseEventArgs args, C item, string url, string id)
        {
            SelectedDataItem = item;
            currentUrl = url;
            currentId = id;
            ContextMenuOpenLinkRef.ShowAsync(args);
        }
        private Task OnItemClickOpenLinkOnNewTab(ContextMenuItemClickEventArgs args)
        {
            SelectedDataItem = args.ItemInfo.DataItem;
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

        protected virtual void SetEdite(C DataItem)
        {
            if (LastEditeItem != null && LastEditeItem.IsInEditMode)
            {
                LastEditeItem.IsInEditMode = false;
            }
            EditedValues = NewItem((C)DataItem);
            ((C)DataItem).IsInEditMode = true;
            LastEditeItem = DataItem;
        }

        
        protected virtual void SetNewEdite(C DataItem)
        {
            if (LastEditeItem != null && LastEditeItem.IsInEditMode)
            {
                LastEditeItem.IsInEditMode = false;
            }
            EditedValues = NewItem((C)DataItem);
            ((C)DataItem).IsInEditMode = true;
            LastEditeItem = DataItem;
        }

        private DxContextMenu ContextMenuRef { get; set; }
        protected C EditedValues { get; set; }

        private C EditedValues_() => EditedValues;
        protected C LastEditeItem { get; set; }
        protected bool UsingDefaultCustomTemplate { get; set; } = true;

        protected virtual RenderFragment CustomContextMenuRender { get; }

        public List<long> GetSelectionDataItemsIds()
        {
            List<long> ids = new();
            if (SelectedDataItems != null)
            {
                SelectedDataItems.ToList().ForEach(item =>
                {
                    object obje = KeyFieldValue((C)item);
                    if (obje != null)
                    {
                        long.TryParse(obje.ToString(), out long id);
                        ids.Add(id);
                    }
                });
            }
            return ids;
        }

        public ObservableCollection<long?> GetSelectionDataItemsIds(Func<C, bool> condition)
        {
            List<long?> ids = new();
            if (SelectedDataItems != null)
            {
                SelectedDataItems.ToList().ForEach(item =>
                {
                    if (condition.Invoke((C)item))
                    {
                        object obje = KeyFieldValue((C)item);
                        if (obje != null)
                        {
                            long.TryParse(obje.ToString(), out long id);
                            ids.Add(id);
                        }
                    }
                });
            }
            return new(ids);
        }

        public ObservableCollection<long> GetSelectionDataItemsIds_(Func<C, bool> condition)
        {
            List<long> ids = new();
            if (SelectedDataItems != null)
            {
                SelectedDataItems.ToList().ForEach(item =>
                {
                    if (condition.Invoke((C)item))
                    {
                        object obje = KeyFieldValue((C)item);
                        if (obje != null)
                        {
                            long.TryParse(obje.ToString(), out long id);
                            ids.Add(id);
                        }
                    }
                });
            }
            return new(ids);
        }
        protected async virtual Task OnItemClick(ContextMenuItemClickEventArgs args)
        {
            string text = args.ItemInfo.Text;
            DeleteContext = text;
            if (string.IsNullOrWhiteSpace(DeletionTitle))
            {
                DeletionTitle = AppState["DeletionTitle"];
            }

            if (text.Equals(AppState["New"]))
            {
                SetNewEdite((C)SelectedDataItem);
                ((C)SelectedDataItem).CanEditRefreshGridStatus = true;
            }
            else
           if (text.Equals(AppState["Edit"]))
            {
                SetEdite((C)SelectedDataItem);
                ((C)SelectedDataItem).CanEditRefreshGridStatus = true;
            }
            if (text.Equals(AppState["Duplicate"]))
            {
                Duplicate(GetSelectionDataItemsIds());
            }
            else
            if (text.Equals(AppState["Delete"]))
            {
                DeleteMessage = AppState["SureToDeleteSelectedItem", GetFieldValue((C)SelectedDataItem, 0)];
                DeleteConfirmationPopup = true;
                await InvokeAsync(RenderFormContentRefDialog.StateHasChanged_);
            }
            else if (text.Equals(AppState["DeleteAll"]))
            {
                DeleteMessage = AppState["SureToDeleteAllItem"];
                DeleteConfirmationPopup = true;
                await InvokeAsync(RenderFormContentRefDialog.StateHasChanged_);
            }
            
        }

        #endregion



        #region edit formcontext

        protected CustomNewCellEditContext CustomCellEditContext_ { get; set; } = null;
        protected class CustomNewCellEditContext
        {
            public Error Error { get; set; }
            public CustomNewCellEditContext(C EditedValues, Func<C, int, object> func, Action<C, int, object> setFieldValueHandler, Error Error)
            {
                this.EditedValues = EditedValues;
                GetFieldValueFunc = func;
                SetFieldValueHandler = setFieldValueHandler;
                this.Error = Error;
                Items = new();
            }

            protected Func<C, int, object> GetFieldValueFunc;
            protected Action<C, int, object> SetFieldValueHandler;
            public C DataItem { get; set; }
            public List<dynamic> Items { get; set; }
            public C EditedValues { get; set; }
            public Action StateHasChanged { get; set; }


            public string GetEditTextFieldName(AbstractNewGridDataItem item)
            {
                object value_ = GetFieldValueFunc?.Invoke(DataItem, item.Position);
                string value = null;
                if (value_ != null)
                {
                    value = value_.ToString();
                }
                object els = GetFieldValueFunc?.Invoke(EditedValues, item.Position);
                if (els != null)
                {
                    value = els.ToString();
                }
                return value;
            }

            public virtual decimal? GetEditDecimalFieldName(AbstractNewGridDataItem item)
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
                    object els = GetFieldValueFunc?.Invoke(EditedValues, item.Position);
                    if (els != null)
                    {
                        decimal.TryParse(els.ToString(), out decimal value);
                        value__ = value;
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

            public virtual DateTime? GetEditDateTimeFieldName(AbstractNewGridDataItem item)
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
                    object els = GetFieldValueFunc?.Invoke(EditedValues, item.Position);
                    if (els != null)
                    {
                        DateTime.TryParse(els.ToString(), out DateTime date);
                        date__ = date;
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

            public void EditGridCell(AbstractNewGridDataItem item, object value)
            {
                SetFieldValueHandler?.Invoke(EditedValues, item.Position, value);
            }
        }

        protected virtual CustomNewCellEditContext GetCustomCellEditContext(object EditedValues)
        {
            return new CustomNewCellEditContext((C)EditedValues, GetFieldValue,SetFieldValue, Error);
        }

        protected virtual Task OnCancelButtonClick(C item)
        {
            item.IsInEditMode = false;
            return Task.CompletedTask;
        }

        protected virtual async Task OnCancelButtonClick()
        {
            await DxGridRef.CancelEditAsync();
            CustomCellEditContext_ = null;
        }
      
        #endregion

    }

    public abstract class AbstractNewGridDataItem
    {
        public int Position { get; set; }

        private object Value { get; set; }
        public ColumnFilter ColumnFilters { get; set; }

        public string ColumnStyle { get; set; } = "";

        public Action<AbstractNewGridDataItem, object> EditAction { get; set; }

        public AbstractNewGridDataItem(int position)
        {
            this.Position = position;
            ColumnFilters = new ColumnFilter();
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
        public virtual bool IsNavLink
        {
            get
            {
                return false;
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
            get
            {
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

    public class NewGridDataItem : AbstractNewGridDataItem
    {
        object GridColumns;

        public NewGridDataItem(object GridColumns, int position) : base(position)
        {
            this.GridColumns = GridColumns;
        }
        


        
    public override string CaptionName
        {
            get
            {
                if (GridColumns == null)
                {
                    return null;
                }
                return (string)GridColumns.GetPropertyValue("CaptionName");
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
                return (string)GridColumns.GetPropertyValue("ColumnName");
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
                return (Type)GridColumns.GetPropertyValue("ColumnType");
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
                return (string)GridColumns.GetPropertyValue("ColumnWidth");
            }
        }

        public override bool IsNavLink
        {
            get
            {
                if (GridColumns == null)
                {
                    return base.IsNavLink;
                }
                object ob = GridColumns.GetPropertyValue("IsNavLink");
                if (ob == null)
                {
                    return base.IsNavLink;
                }
                return (bool)ob;
            }
        }

        //public override string ColumnFormat
        //{
        //    get

        //    {
        //        if (ColumnFormat == null)
        //        {
        //            return null;
        //        }
        //        object val = GridColumns.GetPropertyValue("ColumnFormat");
        //        if(val != null)
        //        {
        //            return (string)val;
        //        }
        //        return null;
        //    }
        //}

    }

    public class SummaryDataItem
    {

        object SummaryColumns;

        public SummaryDataItem(object SummaryColumns, int position)
        {
            this.SummaryColumns = SummaryColumns;
        }
       
        public GridSummaryItemType SummaryType
        {
            get
            {
                return (GridSummaryItemType)SummaryColumns.GetPropertyValue("SummaryType");
            }
        }
        public string FieldName
        {
            get
            {
                return (string)SummaryColumns.GetPropertyValue("FieldName");
            }
        }
        public string ValueDisplayFormat
        {
            get
            {
                return (string)SummaryColumns.GetPropertyValue("ValueDisplayFormat");
            }
        }
        public string Name
        {
            get
            {
                return (string)SummaryColumns.GetPropertyValue("Name");
            }
        }
    }


    public static class ExtensionMethods
    {
        public static int RemoveAll<T>(this ObservableCollection<T> coll, Func<T, bool> condition)
        {
            var itemsToRemove = coll.Where(condition).ToList();
            foreach (var itemToRemove in itemsToRemove)
            {
                coll.Remove(itemToRemove);
            }
            return itemsToRemove.Count;
        }

        public static object GetPropertyValue(this object ob,string propName)
        {
            PropertyInfo pi = ob.GetType().GetProperties().Where(x => propName.Equals(x.Name)).FirstOrDefault();
            if (pi != null)
            {
                return pi.GetValue(ob, null);
            }
            return null;
        }
    }
}
