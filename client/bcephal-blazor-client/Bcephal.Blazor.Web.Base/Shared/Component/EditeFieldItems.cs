using Bcephal.Blazor.Web.Shared.Component;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Rendering;
using Microsoft.JSInterop;
using System;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public class EditeFieldItems<T, S, C> : ComponentBase, IDisposable where T : Nameable where S : Persistent where C : ComponentBase
    {
        [Parameter]
        public TargetMode TargetMode { get; set; } = TargetMode.SELECT;
        [Parameter]
        public EventCallback<T> SelectTargetCallback { get; set; }
        [Parameter]
        public EventCallback<T> addCallback { get; set; }
        [Parameter]
        public EventCallback<T> updateCallback { get; set; }
        [Parameter]
        public EventCallback<T> deleteCallback { get; set; }
        [Parameter]
        public RenderFragment DataTemplate { get; set; }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        public C RefC { get; set; }
        public Action<C> RefCChanged { get; set; }

        private void initActionHandler(C refC)
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
            if (genericTDest.GetGenericArguments().Length == 0)
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

        public void AddOrUpdateTarget(object target_)
        {
            T target = target_ as T;
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
                if (SelectTargetCallback.HasDelegate)
                {
                    SelectTargetCallback.InvokeAsync(target);
                }
            }
        }

        protected override void BuildRenderTree(RenderTreeBuilder __builder)
        {
            base.BuildRenderTree(__builder);
            __builder.OpenElement(0,"div");
            __builder.AddContent(1, DataTemplate);
            __builder.CloseElement();
        }

        }
}
