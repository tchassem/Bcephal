
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Period
{
    public partial class PeriodFilterComponent : ComponentBase
    {

        [Parameter]
        public string HeaderStyle { get; set; } = "d-flex flex-row bc-header-3 p-0 bc-header-height bc-text-align";

        [Inject]
        private AppState AppState{ get; set; }

        [Parameter]
        public PeriodFilter _PeriodFilters { get; set; }

        [Parameter]
        public EventCallback<PeriodFilter> _PeriodFiltersChanged { get; set; }

        [Parameter]
        public ObservableCollection<Bcephal.Models.Dimensions.Period> PeriodsItems { get; set; }

        public ObservableCollection<HierarchicalData> PeriodsItems_
        {
            get
            {
                ObservableCollection<HierarchicalData> obs = new ObservableCollection<HierarchicalData>();
                PeriodsItems.ToList().ForEach(x => obs.Add((HierarchicalData)x));
                return obs;
            }
            set
            {
            }
        }

        private IEnumerable<Bcephal.Models.Dimensions.Period> Periods { get { return (IEnumerable<Bcephal.Models.Dimensions.Period>)PeriodsItems; } }


        private ObservableCollection<PeriodFilterItem> Items
        {
            get
            {
                if (_PeriodFilters != null)
                {
                    return _PeriodFilters.ItemListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }
        }


        public bool IsExpand { get; set; } = false;

        public bool LastItem { get; set; } = false;

        [Parameter]
        public Action RefreshGrid { get; set; }

        [Parameter]
        public string FilterName { get; set; } = "PeriodFilterComponent";

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


        private void RemovePeriodFilter(PeriodFilterItem item)
        {
            ShouldRender_ = true;
            _PeriodFilters.DeleteItem(item);
            _PeriodFiltersChanged.InvokeAsync(_PeriodFilters);
        }

        private void TargetFilter(PeriodFilterItem item)
        {
            ShouldRender_ = true;
            if (!Items.Contains(item))
            {
                _PeriodFilters.AddItem(item);
            }
            else
            {
                _PeriodFilters.UpdateItem(item);
            }
            _PeriodFiltersChanged.InvokeAsync(_PeriodFilters);
        }

        [Parameter]
        public bool ShouldRender_ { get; set; } = true;

        public void ShowOtherFieldFilters()
        {
            ShouldRender_ = true;
            if (_PeriodFilters.ItemListChangeHandler.GetItems().Count > 0)
            {
                IsExpand = !IsExpand;
            }
            else
            {
                IsExpand = false;
            }
            InvokeAsync(StateHasChanged);
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            await base.OnAfterRenderAsync(firstRender);
        }

        protected override bool ShouldRender()
        {
            return ShouldRender_; // base.ShouldRender();
        }
    }
}
