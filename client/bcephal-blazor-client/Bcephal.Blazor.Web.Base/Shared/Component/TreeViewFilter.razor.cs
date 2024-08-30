using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared.Component.Splitter;
using Microsoft.AspNetCore.Components.Web;
using Bcephal.Blazor.Web.Base.Shared.Utils;
using Bcephal.Models.Filters;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class TreeViewFilter<C> : ComponentBase, IDisposable where C : HierarchicalData
    {
        CreateDimensionComponent<C> CreateDimComponent { get; set; }
        private DxTreeView DxTreeViewRef { get; set; }
        [Parameter]
        public bool buttonHide { get; set; } = true;
        public bool TextBox { get; set; } = false;
        public bool ShowExpandButton { get; set; } = true;
        [Parameter]
        public string SelectedItem { get; set; }

        [Parameter]
        public bool CanSelectEntity { get; set; } = false;

        [Parameter]
        public bool Contenteditable { get; set; } = false;        

        [Parameter]
        public bool CanSelectModel { get; set; } = false;

        [Inject]
        private IJSRuntime JSRuntime { get; set; }

        [Inject]
        private AppState AppState { get; set; }
        [Parameter]
        public ObservableCollection<C> ItemsFilter { get; set; } = new ObservableCollection<C>();

        [Parameter]
        public EventCallback<C> SelectFilterItemCallback { get; set; }

        [Parameter]
        public EventCallback<KeyboardEventArgs> KeyPressCallback { get; set; }

        private string ViewIdButton { get; set; }

        private string PositionTarget { get; set; }

        private string CurrentSelectedItem { get; set; } = null;

        private DimensionType DimensionType_ { get; set; }

        public string Btn_Text { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        public async void OnSelectFilterItemChanged(TreeViewNodeEventArgs e)
        {
            if (e.NodeInfo != null)
            {
                bool ItemEntity = (C)e.NodeInfo.DataItem is Entity;
                bool ItemModel = (C)e.NodeInfo.DataItem is Model;
                if ((ItemEntity == false && ItemModel == false) || (CanSelectEntity && ItemEntity) ||
                    (CanSelectModel && ItemModel))
                {
                    SelectedItem = ((C)e.NodeInfo.DataItem).Name;
                    CurrentSelectedItem = SelectedItem;
                    await SelectFilterItemCallback.InvokeAsync((C)e.NodeInfo.DataItem);
                }
                IsOpen = false;
                TextBox = false;
            }
        }

        public async void SelectedItemChanged(C value)
        {
            await SelectFilterItemCallback.InvokeAsync(value);
            SelectedItem = ((C)value).Name;
            CurrentSelectedItem = SelectedItem;
            TextBox = false;
        }

        public void Dispose()
        {
            if (CreateDimComponent != null)
            {
                GC.SuppressFinalize(CreateDimComponent);
                GC.SuppressFinalize(this);
            }
        }
        private ObservableCollection<C> ItemsFilters { get {
                if (TextBox)
                {
                    if(ItemsFilter.Count > 0)
                    {
                        var first = ItemsFilter.FirstOrDefault();
                        if(first is Models.Dimensions.Measure)
                        {
                            return GetMeasures();
                        }
                        else
                        if (first is Models.Dimensions.Period)
                        {
                            return GetPeriods();
                        }
                        else
                        {
                            return GetAttributes();
                        }
                    }
                    return new ObservableCollection<C>();
                }
                else
                {
                    return ItemsFilter;
                }
            }
        }

        private ObservableCollection<C> Attributes { get; set; }
        private ObservableCollection<C> Measures { get; set; }
        private ObservableCollection<C> Periods { get; set; }
        private ObservableCollection<C> GetAttributes()
        {
            if(Attributes != null)
            {
                return Attributes;
            }
            else
            {
                Attributes = new();
                foreach (var item in ItemsFilter)
                {
                    if(item is Entity)
                    {
                        Entity entities = item as Entity;
                        foreach (var attrib in entities.Descendents)
                        {
                            Attributes.Add(attrib as C);
                        }
                    }
                }
                return Attributes;
            }
        }

        private ObservableCollection<C> GetPeriods()
        {
            if (Periods != null)
            {
                return Periods;
            }
            else
            {
                Periods = new();
                foreach (var item in ItemsFilter)
                {
                    if (item is Models.Dimensions.Period)
                    {
                        Models.Dimensions.Period periods_ = item as Models.Dimensions.Period;
                        foreach (var period in periods_.Descendents)
                        {
                            Periods.Add(period as C);
                        }
                        Periods.Add(periods_ as C);
                    }
                }
                return Periods;
            }
        }

        private ObservableCollection<C> GetMeasures()
        {
            if (Measures != null)
            {
                return Measures;
            }
            else
            {
                Measures = new();
                foreach (var item in ItemsFilter)
                {
                    if (item is Models.Dimensions.Measure)
                    {
                        Models.Dimensions.Measure measures_ = item as Models.Dimensions.Measure;
                        foreach (var measure in measures_.Descendents)
                        {
                            Measures.Add(measure as C);
                        }
                        Measures.Add(measures_ as C);
                    }
                }
                return Measures;
            }
        }

        private bool isOpen_ { get; set; } = false;
        public bool IsOpen { get => isOpen_ && Editable; 
            set {
                isOpen_ = value;
                if (!value)
                {
                    Display = DisplayNone;
                    if (string.IsNullOrWhiteSpace(SelectedItem))
                    {
                        SelectedItem = CurrentSelectedItem;
                    }
                }
            } 
        }
                
        private string DisplayNone => "display:none;";

        private string display_ { get; set; } = "display:none;";
        private string Display { get => display_;
            set {
                display_ = value;
            }
        }

        DxButton DxButtonRef { get; set; }
        DxDropDown DxDropDownRef;
        ElementReference DxButtonRef_ { get; set; }

        [JSInvokable("openjs")]
        public Task openEventJs(string targetId)
        {
            PositionTarget = $"#{targetId}";
            ViewIdButton = targetId;
            IsOpen = true;
            return Task.CompletedTask;
        }

        private ElementReference DivRef { get; set; }
        string DivRefId { get; set; }
        async  void clickEvent(MouseEventArgs args)
        {
            if (!IsOpen)
            {
                CurrentSelectedItem = SelectedItem;
                SelectedItem = null;
                await SplitterCJsInterop.GetId(JSRuntime, DivRef, DotNetObjectReference.Create(this), "openjs");
                //await SplitterCJsInterop.GetId(JSRuntime, DivRefId, DotNetObjectReference.Create(this), "openjs");     
            }
            else
            {
                IsOpen = !IsOpen;
            }
        }        

        public IEnumerable<C> TreeViewChildrenExpression(object itemsTarget)
        {

            if (itemsTarget is Entity)
            {
                Entity item = itemsTarget as Entity;
                if(item.Attributes != null)
                {
                    IEnumerable<Models.Dimensions.Attribute> attribs = item.Attributes.OrderBy(x => x.Name);
                    foreach (Models.Dimensions.Attribute ite in attribs)
                    {
                        if (!string.IsNullOrWhiteSpace(ite.ParentId) && ite.ParentId.Contains("ATTRIBUTE"))
                        {
                           attribs = attribs.Where(x => x.ParentId != ite.ParentId); 
                        }
                    }
                    return (IEnumerable<C>)attribs;
                }

            }

            if (itemsTarget is Models.Dimensions.Measure)
            {
                Models.Dimensions.Measure item = itemsTarget as Models.Dimensions.Measure;
                Btn_Text = AppState["New_Measure"];
                DimensionType_ = DimensionType.MEASURE;
             
                if (item.Children != null )
                {
                    IEnumerable<Models.Dimensions.Measure> measures = item.Children.OrderBy(x => x.Name);
                    return (IEnumerable<C>)measures;
                }
                
            }

            if (itemsTarget is Models.Dimensions.Period)
            {
                Models.Dimensions.Period item = itemsTarget as Models.Dimensions.Period;
                Btn_Text = AppState["New_Period"];
                DimensionType_ = DimensionType.PERIOD;
                if (item.Children != null)
                {
                   return (IEnumerable<C>)item.Children.OrderBy(x => x.Name);
                }                
            }

            if (itemsTarget is Models.Dimensions.Attribute)
            {
               Models.Dimensions.Attribute item = itemsTarget as Models.Dimensions.Attribute;
                Btn_Text = AppState["New_Attribute"];
                DimensionType_ = DimensionType.ATTRIBUTE;
                if (item.Children != null)
                {   
                    return (IEnumerable<C>)item.Children.OrderBy(x => x.Name);
                }                
            }
            return new List<C>();
        }

        string InputId { get; set; }
        private void OpenSearchItem()
        {
            CurrentSelectedItem = null;
            IsSetFocus = false;
            if (string.IsNullOrWhiteSpace(InputId))
            {
                InputId = Guid.NewGuid().ToString("d");
            }
            TextBox = true;
        }
        private DxComboBox<C,C> DxComboBoxRef { get; set; }
        private bool IsSetFocus { get; set; } = false;
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ItemsFilter = SortItemFilters(ItemsFilter);
            if (TextBox && !IsSetFocus && DxComboBoxRef != null && !string.IsNullOrWhiteSpace(DxComboBoxRef.InputId))
            {
                await JsInterop.SetFocusById(JSRuntime, DxComboBoxRef.InputId);
                await JsInterop.SetEventJs(JSRuntime, DxComboBoxRef.InputId, DotNetObjectReference.Create(this), "DxComboBoxEnter", null, "onkeyup");
                await JsInterop.SetEventJsArgv(JSRuntime, DxComboBoxRef.InputId, DotNetObjectReference.Create(this), "FocusOut", "focusout",true);
                IsSetFocus = true;
            }
        }

        protected override Task OnInitializedAsync()
        {
                 if (typeof(C).Equals(typeof(Models.Dimensions.Measure)))
                {
                    Btn_Text = AppState["New_Measure"];
                    DimensionType_ = DimensionType.MEASURE;
                }
                else if (typeof(C).Equals(typeof(Models.Dimensions.Period)))
                {
                    Btn_Text = AppState["New_Period"];
                    DimensionType_ = DimensionType.PERIOD;
                }
                else
                {
                    Btn_Text = AppState["New_Attribute"];
                    DimensionType_ = DimensionType.ATTRIBUTE;
                }
            return base.OnInitializedAsync();
        }

        [JSInvokable("DxComboBoxEnter")]
        public async Task DxComboBoxEnter(KeyboardEventArgs args, string value)
        {
            await Task.Yield();
            if (args != null && "Enter".Equals(args.Code))
            {
                StateHasChanged();
            }
        }

        [JSInvokable("FocusOut")]
        public async Task FocusOut(FocusEventArgs args)
        {
            await Task.Yield();
            if (TextBox && !DxComboBoxRef.DropDownVisible)
            {
                IsSetFocus = false;
                TextBox = false;
                StateHasChanged();
            }
        }

        private bool CanShowAddPopup { get; set; } = false;
        private void ShowPopup()
        {
            IsOpen = false;
            CanShowAddPopup = true;
        }

        private ObservableCollection<C> SortItemFilters(ObservableCollection<C> items)
        {
            if (items != null)
            {
                IEnumerable<C> filters = items.ToList();
                filters = filters.OrderBy(x => x.Name);
                return new ObservableCollection<C>(filters);
            }

            return new ObservableCollection<C>();
        }

        protected override Task OnParametersSetAsync()
        {
            BuildTreeItemsFilter();
            return base.OnParametersSetAsync();
        }

        private void BuildTreeItemsFilter()
        {
            ObservableCollection<C> buildedItemsFilter = new();
            if (ItemsFilter != null && ItemsFilter.Any())
            {
                var ite = ItemsFilter.FirstOrDefault();
                if(ite is Models.Dimensions.Measure)
                {
                    foreach (C item in ItemsFilter)
                    {
                        Models.Dimensions.Measure item_ = item as Models.Dimensions.Measure;
                        if(string.IsNullOrWhiteSpace(item_.ParentId))
                        {
                            buildedItemsFilter.Add(item_ as C);
                        }
                    }
                    ItemsFilter = buildedItemsFilter;
                }
                else if(ite is Models.Dimensions.Period)
                {
                    foreach (C item in ItemsFilter)
                    {
                        Models.Dimensions.Period item_ = item as Models.Dimensions.Period;
                        if (string.IsNullOrWhiteSpace(item_.ParentId))
                        {
                            buildedItemsFilter.Add(item_ as C);
                        }
                    }
                    ItemsFilter = buildedItemsFilter;
                }
            }
        }

        private void CanRebuildTreeItemsFilter(bool val)
        {
            if (val)
            {
                BuildTreeItemsFilter();
                StateHasChanged();
            }
        }


        private async void KeyHandler(KeyboardEventArgs e)
        {
            await Task.Yield();
            if (e != null && !string.IsNullOrWhiteSpace(e.Code) && e.Code.Contains("Enter"))
            {
                await KeyPressCallback.InvokeAsync(e);
            }
            else
            if (e != null && !string.IsNullOrWhiteSpace(e.Code) && e.Code.Equals("Escape"))
            {
                await KeyPressCallback.InvokeAsync(e);
            }
        }
    }
}
