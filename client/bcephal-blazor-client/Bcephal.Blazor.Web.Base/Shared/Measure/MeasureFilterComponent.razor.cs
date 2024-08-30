
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Measure
{
    public partial class MeasureFilterComponent : ComponentBase
    {
        [Parameter]
        public string HeaderStyle { get; set; } = "d-flex flex-row bc-header-3 p-0 bc-header-height bc-text-align";

        [Inject]
        private AppState AppState { get; set; }


        [Parameter]
        public MeasureFilter measureFilter { get; set; }


        [Parameter]
        public EventCallback<MeasureFilter> measureFilterChanged { get; set; }

        [Parameter]
        public ObservableCollection<Bcephal.Models.Dimensions.Measure> MeasureItems { get; set; }

        private IEnumerable<Bcephal.Models.Dimensions.Measure> Measures { get { return (IEnumerable<Bcephal.Models.Dimensions.Measure>)MeasureItems; } }

        [Parameter]
        public bool ShouldRender_ { get; set; } = true;

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            await base.OnAfterRenderAsync(firstRender);
        }

        [Parameter]
        public string FilterName { get; set; } = "MeasureFilterComponent";

        [Parameter]
        public bool Editable { get; set; } = true;

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
            return ShouldRender_; // base.ShouldRender();
        }

        public ObservableCollection<HierarchicalData> MeasureItems_
        {
            get
            {
                ObservableCollection<HierarchicalData> obs = new ObservableCollection<HierarchicalData>();
                Measures.ToList().ForEach(x => obs.Add((HierarchicalData)x));
                return obs;
            }
            set
            {
            }
        }

        private ObservableCollection<MeasureFilterItem> Items
        {
            get
            {
                if (measureFilter != null)
                {
                    return measureFilter.ItemListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }
        }

        public bool IsItemsFilterEmpty { get; set; } = false;


        public bool IsExpand { get; set; } = false;

        [Parameter]
        public Action RefreshGrid { get; set; }

        private void RemoveMFilterItem(MeasureFilterItem item)
        {
            ShouldRender_ = true;
            measureFilter.DeleteItem(item);
            measureFilterChanged.InvokeAsync(measureFilter);
        }

        public void ShowOtherFieldFilters()
        {
            ShouldRender_ = true;
            if (measureFilter != null && measureFilter.ItemListChangeHandler.GetItems().Count > 0)
            {
                IsExpand = !IsExpand;
            }
            else
            {
                IsExpand = false;
            }
            StateHasChanged();
        }

        private void TargetFilter(MeasureFilterItem item)
        {
            ShouldRender_ = true;
            if (!Items.Contains(item))
            {
                measureFilter.AddItem(item);
            }
            else
            {
                measureFilter.UpdateItem(item);
            }
            measureFilterChanged.InvokeAsync(measureFilter);
        }

    }
}
