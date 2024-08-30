using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Entities
{
    public partial class AttributeFilterComponent : ComponentBase
    {
        [Parameter]
        public string HeaderStyle { get; set; } = "d-flex flex-row bc-header-3 p-0 bc-header-height bc-text-align";

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public AttributeFilter attributeFilter { get; set; }

        [Parameter]
        public EventCallback<AttributeFilter> attributeFilterChanged { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> EntityItems { get; set; }

        [Parameter]
        public EventCallback<ObservableCollection<HierarchicalData>> EntityItemsChanged { get; set; }

        [Parameter]
        public Action RefreshGrid { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        private IEnumerable<HierarchicalData> Entities
        {
            get
            {
                return
                (IEnumerable<HierarchicalData>)EntityItems.Where((HierarchicalData h) => (h.ParentId.Contains("MODEL") == true));
            }
        }

        private ObservableCollection<AttributeFilterItem> Items
        {
            get
            {
                if (attributeFilter != null)
                {
                    return attributeFilter.ItemListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }
        }

        public bool IsExpand { get; set; } = false;

        [Parameter]
        public bool ShouldRender_ { get; set; } = true;

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            await base.OnAfterRenderAsync(firstRender);
        }

        [Parameter]
        public string FilterName { get; set; } = "AttributeFilterComponent";

        private string InfosKey = "InfosKey";

        public override async Task SetParametersAsync(ParameterView parameters)
        {
            await base.SetParametersAsync(parameters);
            if (InfosKey.Equals("InfosKey"))
            {
                InfosKey = FilterName + InfosKey;
            }
        }


        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }

        public ObservableCollection<string> AttributesValues { get; set; } = new ObservableCollection<string>();

        SizeMode SizeMode { get; set; }

        private void TargetFilter(AttributeFilterItem item)
        {

            ShouldRender_ = true;
            if (!Items.Contains(item))
            {
                attributeFilter.AddItem(item);
            }
            else
            {
                attributeFilter.UpdateItem(item);
            }
            attributeFilterChanged.InvokeAsync(attributeFilter);
        }

        public void ShowOtherFieldFilters()
        {
            ShouldRender_ = true;
            if (attributeFilter != null && attributeFilter.ItemListChangeHandler.GetItems().Count > 0)
            {
                IsExpand = !IsExpand;
            }
            else
            {
                IsExpand = false;
            }
            InvokeAsync(StateHasChanged);
        }

        private void RemoveAttributeFilter(AttributeFilterItem item)
        {
            ShouldRender_ = true;
            attributeFilter.DeleteItem(item);
            attributeFilterChanged.InvokeAsync(attributeFilter);
            //if (RefreshGrid != null)
            //{
            //    RefreshGrid?.Invoke();
            //}
            //else
            //{
            //    StateHasChanged();
            //}

        }

    }
}
