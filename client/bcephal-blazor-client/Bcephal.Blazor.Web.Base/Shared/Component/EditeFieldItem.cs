using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Grids;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.CompilerServices;
using Microsoft.AspNetCore.Components.Rendering;
using Microsoft.AspNetCore.Components.Routing;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Shared.Component
{
    public class EditeFieldItem<T, S, C> : ComponentBase,IDisposable where T : Nameable where S : Persistent where C : ComponentBase
    {
        [Parameter]
        public string HeaderTitle { get; set; }
        [Parameter]
        public string NullText { get; set; }
        [Parameter]
        public TargetMode TargetMode { get; set; } = TargetMode.SELECT;
        [Parameter]
        public T Target { get; set; }
        [Parameter]
        public EventCallback<T> TargetChanged { get; set; }
        [Parameter]
        public EventCallback<T> SelectTargetCallback { get; set; }
        [Parameter]
        public ObservableCollection<T> ItemsTargets { get; set; }
        [Parameter]
        public EventCallback<ObservableCollection<T>> ItemsTargetsChanged { get; set; }
        [Parameter]
        public ObservableCollection<S> ItemsSources { get; set; }
        [Parameter]
        public EventCallback<ObservableCollection<S>> ItemsSourcesChanged { get; set; }

        [Parameter]
        public EventCallback<IEnumerable<S>> SelectItemsSourcesCallback { get; set; }

        EditeFieldBase<T> EditeField;

        [Parameter]
        public bool IsUpdate { get; set; } = false;
        [Parameter]
        public EventCallback<bool> IsUpdateChanged { get; set; }
        [Parameter]
        public EventCallback<T> addCallback { get; set; }
        [Parameter]
        public EventCallback<T> updateCallback { get; set; }
        [Parameter]
        public EventCallback<T> deleteCallback { get; set; }
        [Parameter]
        public IDictionary<string, object> Csettings { get; set; }
        [Parameter]
        public bool DisplaySingleField { get; set; } = true;
        [Parameter]
        public ObservableCollection<S> Values { get; set; }
        [Parameter]
        public EventCallback<ObservableCollection<S>> ValuesChanged { get; set; }

        public ObservableCollection<S> ValuesBinding
        {
            get { return Values; }
            set
            {
                Values = value;
                ValuesChanged.InvokeAsync(Values);
                Console.WriteLine( " select Item 0 " + Values.ToArray().ToString());
            }
        }


        public ObservableCollection<T> ItemsTargetsBinding
        {
            get { return ItemsTargets; }
            set
            {
                ItemsTargets = value;
                ItemsTargetsChanged.InvokeAsync(ItemsTargets);
            }
        }

        public ObservableCollection<S> ItemsSourcesBinding
        {
            get { return ItemsSources; }
            set
            {
                ItemsSources = value;
                ItemsSourcesChanged.InvokeAsync(ItemsSources);
            }
        }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        public C RefC { get; set; }
        public Action<C> RefCChanged { get; set; }

        protected override Task OnInitializedAsync()
        {
            if (Csettings == null)
            {
                Csettings = new Dictionary<string, object>();
            }
            if (ItemsSources == null)
            {
                ItemsSources = new ObservableCollection<S>();
            }
            if (ItemsTargets == null)
            {
                ItemsTargets = new ObservableCollection<T>();
            }
            RefCChanged += initActionHandler;
            return base.OnInitializedAsync();
        }

        private void  initActionHandler(C refC)
        {
            RefC = refC;
            if (isDimension() && refC != null)
            {
                ((ViewHandler)refC).setHander(AddOrUpdateTarget);
            }
        }


        private bool isDimension()
        {
            Type genericTDest = typeof(CustomDxTreeView<HierarchicalData>);
            Type genericT = typeof(C);
            if(genericTDest.GetGenericArguments().Length == 0)
            {
                return false;
            }
            if (genericT.GetGenericArguments().Length == 0)
            {
                return false;
            }
            if (genericT.GetGenericArguments().Length == genericTDest.GetGenericArguments().Length && genericTDest.GetGenericArguments().Length > 1)
            {
                return false;
            }
            Type genericTDestParam = genericTDest.GetGenericArguments()[0];
            Type genericParam = genericT.GetGenericArguments()[0];
            return genericTDestParam.IsAssignableFrom(genericParam);
        }
        public void Dispose()
        {
            
            RefCChanged -= initActionHandler;
        }

        public void EndEditionFocusOut(FocusEventArgs args)
        {
            Console.WriteLine("FocusEventArgs :   " + args);
        }


        public void AddOrUpdateTarget(object target_)
        {
            T target = target_ as T;
            Console.WriteLine("TargetMode  :   " + TargetMode);
            if (target != null && TargetMode.ADD.Equals(TargetMode))
            {
                if (!target.Id.HasValue)
                {
                    if (addCallback.HasDelegate)
                    {
                        addCallback.InvokeAsync(target);
                    }
                }
                else
                {
                    if (updateCallback.HasDelegate)
                    {
                        updateCallback.InvokeAsync(target);
                    }
                }
            }
            else
            {
                if (target_ is T)
                {
                    if (SelectTargetCallback.HasDelegate)
                    {
                        SelectTargetCallback.InvokeAsync(target);
                    }
                }
                else
                {
                    if (SelectItemsSourcesCallback.HasDelegate)
                    {
                        SelectItemsSourcesCallback.InvokeAsync((IEnumerable<S>)target);
                    }
                }
            }
        }

        public void DeleteTarget(T target)
        {
            Console.WriteLine("call DeleteTarget  :   " + target);
            if (target != null && deleteCallback.HasDelegate)
            {
                deleteCallback.InvokeAsync(target);
            }
        }

        RenderFragment RenderCParams()
        {
            RenderFragment RenderWidgetB = __builder =>
            {
                __builder.OpenComponent<C>(0);
                foreach (KeyValuePair<string, object> entry in Csettings)
                    {
                    if (!string.IsNullOrWhiteSpace(entry.Key) && entry.Key.StartsWith("Csettings-"))
                    {
                        string key = entry.Key.Substring("Csettings-".Length);
                        if (entry.Value is string)
                        {
                            if (string.Equals(entry.Value, "ItemsSources"))
                            {
                                __builder.AddAttribute(1, key, ItemsSources);
                            }
                            else
                               if (string.Equals(entry.Value, "ItemsTargets"))
                            {
                                __builder.AddAttribute(1, key, ItemsTargets);
                            }
                            else
                            if (string.Equals(entry.Value, "Values"))
                            {
                                __builder.AddAttribute(1, key, Values);
                            }
                            else
                            {
                                __builder.AddAttribute(1, key, entry.Value);
                            }
                        }
                        else
                        {
                            __builder.AddAttribute(1, key, entry.Value);
                        }
                    }
                    else
                if (!string.IsNullOrWhiteSpace(entry.Key) && entry.Key.StartsWith("Cevents-"))
                    {
                        string key = entry.Key.Substring("Cevents-".Length);
                        var ItemEventCallback = EventCallback.Factory.Create<S>(this, (EventCallback)entry.Value);
                         __builder.AddAttribute<S>(1, key, ItemEventCallback);
                    }
                    else
                if (!string.IsNullOrWhiteSpace(entry.Key) && entry.Key.StartsWith("Cbinding-"))
                    {
                        object BindingValue = entry.Value;
                        string key = entry.Key.Substring("Cbinding-".Length);
                        if (entry.Value is string)
                        {
                            if (string.Equals(entry.Value, "ItemsSources"))
                            {
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<ObservableCollection<S>>>(EventCallback.Factory.Create<ObservableCollection<S>>(this, EventCallback.Factory.CreateInferred(this, __value => ItemsSourcesBinding = __value, ItemsSourcesBinding)));
                                __builder.AddAttribute(1, key, ItemsSourcesBinding);
                                __builder.AddAttribute<ObservableCollection<S>>(1, key + "Changed", KeyChanged2);
                            }
                            else
                               if (string.Equals(entry.Value, "ItemsTargets"))
                            {
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<ObservableCollection<T>>>(EventCallback.Factory.Create<ObservableCollection<T>>(this, EventCallback.Factory.CreateInferred(this, __value => ItemsTargetsBinding = __value, ItemsTargetsBinding)));
                                __builder.AddAttribute(1, key, ItemsTargetsBinding);
                                __builder.AddAttribute<ObservableCollection<T>>(1, key + "Changed", KeyChanged2);
                            }
                            else
                               if (string.Equals(entry.Value, "Values"))
                            {
                                IEnumerable<S> SourceGrilleColumns = (IEnumerable<S>)ValuesBinding;
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<IEnumerable<S>>>(EventCallback.Factory.Create<IEnumerable<S>>(this, EventCallback.Factory.CreateInferred(this, __value => SourceGrilleColumns = __value, SourceGrilleColumns)));
                                //var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<IEnumerable<S>>>(EventCallback.Factory.Create<IEnumerable<S>>(this, RuntimeHelpers.CreateInferredEventCallback(this, __value => SourceGrilleColumns = __value, SourceGrilleColumns)));
                                __builder.AddAttribute(1, key, SourceGrilleColumns);
                                __builder.AddAttribute<IEnumerable<S>>(1, key + "Changed", KeyChanged2);
                            }
                            else
                            {
                                string SourceGrilleColumns = ValuesBinding as string;
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<string>>(EventCallback.Factory.Create<string>(this, EventCallback.Factory.CreateInferred(this, __value => SourceGrilleColumns = __value, SourceGrilleColumns)));
                                __builder.AddAttribute(1, key, SourceGrilleColumns);
                                __builder.AddAttribute<string>(1, key + "Changed", KeyChanged2);
                            }
                        }
                        else
                        {
                            var KeyChanged = RuntimeHelpers.TypeCheck<EventCallback<object>>(EventCallback.Factory.Create<object>(this, EventCallback.Factory.CreateInferred(this, __value => BindingValue = __value, BindingValue)));
                            if (entry.Value is List<S>)
                            {
                                var BindingValue2 = BindingValue as List<S>;
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<List<S>>>(EventCallback.Factory.Create<List<S>>(this, EventCallback.Factory.CreateInferred(this, __value => BindingValue2 = __value, BindingValue2)));
                                __builder.AddAttribute(1, key, BindingValue2);
                                __builder.AddAttribute<List<S>>(1, key + "Changed", KeyChanged2);
                            }
                            else
                            if (entry.Value is S)
                            {
                                var BindingValue2 = BindingValue as S;
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<S>>(EventCallback.Factory.Create<S>(this, EventCallback.Factory.CreateInferred(this, __value => BindingValue2 = __value, BindingValue2)));
                                __builder.AddAttribute(1, key, BindingValue2);
                                __builder.AddAttribute<S>(1, key + "Changed", KeyChanged2);
                            }
                            else
                            if (entry.Value is List<T>)
                            {
                                var BindingValue2 = BindingValue as List<T>;
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<List<T>>>(EventCallback.Factory.Create<List<T>>(this, EventCallback.Factory.CreateInferred(this, __value => BindingValue2 = __value, BindingValue2)));
                                __builder.AddAttribute(1, key, BindingValue2);
                                __builder.AddAttribute<List<T>>(1, key + "Changed", KeyChanged2);
                            }
                            else
                            if (entry.Value is T)
                            {
                                var BindingValue2 = BindingValue as T;
                                var KeyChanged2 = RuntimeHelpers.TypeCheck<EventCallback<T>>(EventCallback.Factory.Create<T>(this, EventCallback.Factory.CreateInferred(this, __value => BindingValue2 = __value, BindingValue2)));
                                __builder.AddAttribute(1, key, BindingValue2);
                                __builder.AddAttribute<T>(1, key + "Changed", KeyChanged2);
                            }
                            else
                            {
                                __builder.AddAttribute(1, key, BindingValue);
                                __builder.AddAttribute<object>(1, key + "Changed", KeyChanged);
                            }
                        }
                    }
                    }
                __builder.AddComponentReferenceCapture(1, capturedRef => { RefCChanged?.Invoke((C)capturedRef); });
                __builder.CloseComponent();
                __builder.AddMarkupContent(1, "\r\n");
                };
                return RenderWidgetB;
        }


        RenderFragment RenderWidgetEditeField(T itemsTarget)
        {
            RenderFragment RenderWidgetB = __builder =>
            {
                
                //var ItemChanged = EventCallback.Factory.Create<T>(this, EventCallback.Factory.CreateInferred(this, __value => itemsTarget = __value, itemsTarget));
                var ItemChanged = EventCallback.Factory.Create<T>(this, RuntimeHelpers.CreateInferredEventCallback(this, __value => itemsTarget = __value, itemsTarget));
                __builder.OpenComponent<EditeFieldBase<T>>(0);
                __builder.AddAttribute(1, "NullText", NullText);
                __builder.AddAttribute(1, "Item", itemsTarget);
                __builder.AddAttribute(1, "ItemChanged", ItemChanged);
                __builder.AddComponentReferenceCapture(1, evt => { EditeField = (EditeFieldBase<T>)evt; });
                __builder.CloseComponent();
                __builder.AddMarkupContent(1, "\r\n");
            };
            return RenderWidgetB;
        }



        protected override void BuildRenderTree(RenderTreeBuilder __builder)
        {
            base.BuildRenderTree(__builder);
            __builder.OpenComponent<DxGridLayout>(0);
            __builder.AddAttribute(1, "CssClass","target-overflow");
            __builder.AddAttribute(2, "Rows", (RenderFragment)((__builder2) =>
            {
                __builder2.AddMarkupContent(3, "\r\n");
                __builder2.OpenComponent<DxGridLayoutRow>(4);
                __builder2.AddAttribute(5, "Height", "50px");
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(6, "\r\n");
            }
            ));
            __builder.AddAttribute(7, "Rows", (RenderFragment)((__builder2) =>
            {
                __builder2.AddMarkupContent(8, "\r\n");
                __builder2.OpenComponent<DxGridLayoutRow>(9);
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(10, "\r\n");
            }
            ));
            __builder.AddAttribute(11, "Columns", (RenderFragment)((__builder2) =>
            {
                __builder2.AddMarkupContent(12, "\r\n");
                __builder2.OpenComponent<DxGridLayoutColumn>(13);
                __builder2.AddAttribute(14, "Width", "90%");
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(15, "\r\n");
            }
            ));
            __builder.AddAttribute(16, "Items", (RenderFragment)((__builder2) =>
            {
                __builder2.AddMarkupContent(17, "\r\n");
                __builder2.OpenComponent<DxGridLayoutItem>(18);
                __builder2.AddAttribute(19, "Row", RuntimeHelpers.TypeCheck<System.Int32>(0));
                __builder2.AddAttribute(20, "Column", RuntimeHelpers.TypeCheck<System.Int32>(0));
                __builder2.AddAttribute(21, "Template", (RenderFragment)((__builder3) =>
                {
                    __builder3.AddMarkupContent(22, "\r\n");
                    __builder3.OpenElement(23, "div");
                    __builder3.AddAttribute(24, "style", "background-color:dimgray;color:white;font-size:medium;");
                    __builder3.AddMarkupContent(25, "\r\n");
                    __builder3.AddContent(26, HeaderTitle);
                    __builder3.AddMarkupContent(27, "\r\n");
                    __builder3.CloseElement();
                    __builder3.AddMarkupContent(28, "\r\n");
                }
                ));
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(29, "\r\n\r\n");
                __builder2.AddContent(30, "   ");
                __builder2.OpenComponent<DxGridLayoutItem>(31);
                __builder2.AddAttribute(32, "Row", RuntimeHelpers.TypeCheck<System.Int32>(1));
                __builder2.AddAttribute(33, "Column", RuntimeHelpers.TypeCheck<System.Int32>(0));
                __builder2.AddAttribute(34, "Template", (RenderFragment)((__builder3) =>
                {
                    __builder3.AddMarkupContent(35, "\r\n");
                    __builder3.OpenElement(36, "div");
                    __builder3.AddAttribute(37, "class", "target-overflow");
                    __builder3.AddMarkupContent(38, "\r\n");
                    if (ItemsTargets != null && ItemsTargets.Count > 0)
                    {
                        int index2 = 0;
                        while (index2 < ItemsTargets.Count) 
                        {
                            __builder3.OpenRegion(39);
                            __builder3.AddContent(0, RenderWidgetEditeField(ItemsTargets[index2]));
                            __builder3.CloseRegion();
                            __builder3.AddMarkupContent(40, "\r\n");
                            index2++;
                        }
                    }
                    __builder3.AddMarkupContent(41, "\r\n");
                    __builder3.OpenElement(42, "div");
                    __builder3.AddAttribute(43, "class", "target-overflow");
                    __builder3.AddMarkupContent(44, "\r\n");
                    __builder3.AddContent(45, RenderCParams());
                    // __builder3.AddContent(46, RenderWidgetTreeView());
                    __builder3.AddMarkupContent(47, "\r\n");
                    __builder3.CloseElement();
                    __builder3.AddMarkupContent(48, "\r\n");
                    __builder3.OpenElement(49, "div");
                    __builder3.AddAttribute(50, "class", "target-overflow");
                    __builder3.AddMarkupContent(51, "\r\n");
                    if (DisplaySingleField)
                    {
                        __builder3.OpenRegion(52);
                        __builder3.AddContent(0, RenderWidgetEditeField(Target));
                        __builder3.CloseRegion();
                    }
                    __builder3.AddMarkupContent(53, "\r\n");
                    __builder3.CloseElement();
                    __builder3.CloseElement();
                    __builder3.AddMarkupContent(54, "\r\n");
                }));
                __builder2.CloseComponent();
                __builder2.AddMarkupContent(55, "\r\n");
            }));
            __builder.CloseComponent();
        }

       
    }

    public enum TargetMode{
        SELECT,ADD
    }
    public interface ViewHandler
    {
        void setHander(Action<object> ActionHandler);
    }
    public class CustomDxTreeView<P> : DxTreeView, ViewHandler where P : HierarchicalData
    {
        public Action<object> ActionHandler { get; set; }
        public string TreeViewTextExpression(object itemsTarget)
        {
            if (itemsTarget is Models.Dimensions.Measure)
            {
                Models.Dimensions.Measure item = itemsTarget as Models.Dimensions.Measure;
                return item.FieldId;
            }
            if (itemsTarget is Period)
            {
                Period item = itemsTarget as Period;
                return item.FieldId;
            }
            if (itemsTarget is Entity)
            {
                Entity item = itemsTarget as Entity;
                return item.FieldId;
            }
            if (itemsTarget is Models.Dimensions.Attribute)
            {
                Models.Dimensions.Attribute item = itemsTarget as Models.Dimensions.Attribute;
                return item.FieldId;
            }
            return "";
        }
        public string TreeViewNameExpression(object itemsTarget)
        {
            if (itemsTarget is HierarchicalData)
            {
                HierarchicalData item = itemsTarget as HierarchicalData;
                return item.Name;
            }
            return "";
        }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }
        public IEnumerable TreeViewChildrenExpression(object itemsTarget)
        {
            if (itemsTarget is Models.Dimensions.Measure)
            {
                Models.Dimensions.Measure item = itemsTarget as Models.Dimensions.Measure;
                return item.Descendents as IEnumerable;
            }
            if (itemsTarget is Period)
            {
                Period item = itemsTarget as Period;
                return item.Descendents as IEnumerable;
            }
            if (itemsTarget is Entity)
            {
                Entity item = itemsTarget as Entity;
                return item.Descendents as IEnumerable;
            }
            if (itemsTarget is Models.Dimensions.Attribute)
            {
                Models.Dimensions.Attribute item = itemsTarget as Models.Dimensions.Attribute;
                return item.Descendents as IEnumerable;
            }
            return new List<P>() as IEnumerable;
        }
        protected void SelectionChanged__(TreeViewNodeEventArgs e)
        {
            ActionHandler?.Invoke(e.NodeInfo.DataItem);
            InvokeAsync(StateHasChanged);
        }
        protected override void OnInitialized()
        {
            TextExpression = (item) => TreeViewTextExpression(item);
            NameExpression = (item) => TreeViewNameExpression(item);
            ChildrenExpression = (item) => TreeViewChildrenExpression(item);
            SelectionChanged = SelectionChanged__;
            base.OnInitialized();
        }

        public void setHander(Action<object> ActionHandler)
        {
            this.ActionHandler = ActionHandler;
        }
    }

    public class CustomDxListBox<P,L> : DxListBox<P,L>, ViewHandler where P : Persistent where L : Persistent
    {
        public Action<object> ActionHandler { get; set; }

        protected override void OnInitialized()
        {
            SelectedItemsChanged = SelectionChanged;
            base.OnInitialized();
        }

        protected void SelectionChanged(IEnumerable<P> e)
        {
            ActionHandler?.Invoke(e);
            InvokeAsync(StateHasChanged);
        }

        public void setHander(Action<object> ActionHandler)
        {
            this.ActionHandler = ActionHandler;
        }
    }

    public class CustomDxComboBox<P, L> : DxComboBox<P, L>, ViewHandler where P : Persistent where L : Persistent
    {
        public Action<object> ActionHandler { get; set; }

        protected override void OnInitialized()
        {
            SelectedItemChanged = SelectionChanged;
            base.OnInitialized();
        }

        protected void SelectionChanged(P e)
        {
            ActionHandler?.Invoke(e);
            InvokeAsync(StateHasChanged);
        }

        public void setHander(Action<object> ActionHandler)
        {
            this.ActionHandler = ActionHandler;
        }
    }
}
