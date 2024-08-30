using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class FilterComponent : ComponentBase
    {
        #region Parameters
        [Parameter] public UniverseFilter UniverseFilter { get; set; }
        [Parameter] public ObservableCollection<Bcephal.Models.Dimensions.Measure> Measures { get; set; }
        [Parameter] public ObservableCollection<Bcephal.Models.Dimensions.Period> Periods { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Attributes { get; set; }
        [Parameter] public EventCallback<UniverseFilter> UniverseFilterChanged { get; set; }
        [Parameter] public string Filterstyle { get; set; } = "grid-bc-filtercontent w-100";
        [Parameter] public string AttributeHeaderStyle { get; set; } = "d-flex flex-row bc-header-3 p-0 bc-header-height bc-text-align pl-1";
        [Parameter] public string PeriodHeaderStyle { get; set; } = "d-flex flex-row bc-header-3 p-0 bc-header-height bc-text-align pl-1";
        [Parameter] public string MeasureHeaderStyle { get; set; } = "d-flex flex-row bc-header-3 p-0 bc-header-height bc-text-align pl-1";
        [Parameter] public string FilterName { get; set; } = "FilterComponent";
        [Parameter] public bool CanRefreshGrid { get; set; } = true;
        [Parameter] public bool Editable { get; set; } = true;
        [Inject] private AppState AppState { get; set; }

        #endregion

        public bool ShouldRender_ { get; set; } = true;

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            //Console.WriteLine("OnAfterRenderAsync FilterComponent");
            return base.OnAfterRenderAsync(firstRender);
        }

        protected override bool ShouldRender()
        {
            return ShouldRender_; 
        }

        private string InfosKey = "Infos_Key";

        public override async Task SetParametersAsync(ParameterView parameters)
        {
            await base.SetParametersAsync(parameters);
            if (InfosKey.Equals("Infos_Key"))
            {
                InfosKey = FilterName + InfosKey;
            }
        }

        private void RefreshGrid()
        {
            if (CanRefreshGrid)
            {
                AppState.Refresh();
            }
        }


        public PeriodFilter PeriodFilter { get; set; }
        public AttributeFilter AttributeFilter { get; set; }
        public MeasureFilter MeasureFilter { get; set; }
        public PeriodFilter PeriodFilterBinding
        {
            get { return UniverseFilter.PeriodFilter; }
            set
            {
                UniverseFilter.PeriodFilter = value;
                UniverseFilterChanged.InvokeAsync(UniverseFilter);
                RefreshGrid();
            }
        }
        public AttributeFilter AttributeFilterBinding
        {
            get { return UniverseFilter.AttributeFilter; }
            set
            {
                UniverseFilter.AttributeFilter = value;
                UniverseFilterChanged.InvokeAsync(UniverseFilter);
                RefreshGrid();
            }
        }
        public MeasureFilter MeasureFilterBinding
        {
            get { return UniverseFilter.MeasureFilter; }
            set
            {
                UniverseFilter.MeasureFilter = value;
                UniverseFilterChanged.InvokeAsync(UniverseFilter);
                RefreshGrid();
            }
        }

    }
}
