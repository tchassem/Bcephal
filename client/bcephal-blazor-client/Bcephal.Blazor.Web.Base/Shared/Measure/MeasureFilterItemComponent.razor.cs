
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Measure
{
    public partial class MeasureFilterItemComponent : ComponentBase
    {
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public MeasureFilterItem modelmeasure { get; set; }

        [Parameter]
        public EventCallback<MeasureFilterItem> ItemCallback { get; set; }

        [Parameter]
        public bool removeButton { get; set; }


        [Parameter]
        public EventCallback<MeasureFilterItem> OnClickRemove { get; set; }

        [Parameter]
        public bool ExpandField { get; set; }

        [Parameter]
        public bool DisplayOpenBrackets { get; set; } = true;

        [Parameter]
        public bool DisplayFilterVerb_ { get; set; } = true;

        [Parameter]
        public bool DisplayOperator { get; set; } = true;

        [Parameter]
        public bool DisplayDeleteButton { get; set; } = true;

        [Parameter]
        public bool DisplaySpinnerMeasure { get; set; } = true;

        [Parameter]
        public bool DisplayTreeViewFilter { get; set; } = true;

        [Parameter]
        public bool DisplayCloseBrackets { get; set; } = true;


        [Parameter]
        public EventCallback<bool> ExpandFieldChanged { get; set; }

        [Parameter]
        public ObservableCollection<string> MeasuresName { get; set; }

        [Parameter]
        public IEnumerable<Bcephal.Models.Dimensions.Measure> MeasureItems { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Measures_ { get; set; }

        private string display => (removeButton == true && ExpandField == true) ? "block" : "none";
        private string display2 => removeButton == true ? "block" : "none";
        IEnumerable<string> FilterVerbs { get; set; }


        [Parameter]
        public bool ShouldRender_ { get; set; } = true;

        [Parameter]
        public bool Editable { get; set; } = true;

        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            await base.OnAfterRenderAsync(firstRender);
        }

        public string CloseBrackets
        {
            get { return modelmeasure.CloseBrackets; }
            set
            {
                ShouldRender_ = true;
                modelmeasure.CloseBrackets = value;
                ItemCallback.InvokeAsync(modelmeasure);
            }
        }

        public decimal? Value
        {
            get { return modelmeasure.Value; }
            set
            {
                ShouldRender_ = true;
                modelmeasure.Value = value;
                ItemCallback.InvokeAsync(modelmeasure);
            }
        }

        public string Operator
        {
            get { return modelmeasure.Operator; }
            set
            {
                ShouldRender_ = true;
                modelmeasure.Operator = value;
                ItemCallback.InvokeAsync(modelmeasure);
            }
        }

        public string OpenBrackets
        {
            get { return modelmeasure.OpenBrackets; }
            set
            {
                ShouldRender_ = true;
                modelmeasure.OpenBrackets = value;
                ItemCallback.InvokeAsync(modelmeasure);
            }
        }

        public void modelmeasureChanged(HierarchicalData measure)
        {
            ShouldRender_ = true;
            modelmeasure.DimensionName = measure.Name;
            modelmeasure.DimensionId = measure.Id;
            ItemCallback.InvokeAsync(modelmeasure);
        }


        public string FilterVerb_
        {
            get
            {
                return modelmeasure.FilterVerb.GetText(text => AppState[text]);
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    ShouldRender_ = true;
                    modelmeasure.FilterVerb = modelmeasure.FilterVerb.GetFilterVerb(value, text => AppState[text]);
                    ItemCallback.InvokeAsync(modelmeasure);
                }
            }
        }

        protected override void OnInitialized()
        {
            FilterVerbs = modelmeasure.FilterVerb.GetAll(text => AppState[text]);
        }
    }
}
