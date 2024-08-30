
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Period
{
    public partial class PeriodFilterItemComponent : ComponentBase
    {
        private string display => (removeButton == true && ExpandField == true) || (!DisplayFirstItem && !DisplaySecondItem) ? "block" :  "none";
        private string display2 => removeButton == true ? "block" : "none";

        DxComboBox<string, string> DxComboBoxRefSign { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        public string Period { get; set; } = "";

        public int ParamaterAdd { get; set; } = 1;

        [Parameter]
        public bool DisplayFirstItem { get; set; } = true;


        [Parameter]
        public bool DisplaySecondItem { get; set; } = true;

        [Parameter]
        public bool DisplayDeleteButton { get; set; } = true;

        [Parameter]
        public bool DisplayGroupItemJoin { get; set; } = true;

        [Parameter]
        public PeriodFilterItem periodFilterItem { get; set; }

        [Parameter]
        public EventCallback<PeriodFilterItem> ItemCallback { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Periods_ { get; set; }

        [Parameter]
        public EventCallback<PeriodFilterItem> OnClickRemove { get; set; }

        [Parameter]
        public bool removeButton { get; set; }

        [Parameter]
        public bool AddPaddingTop { get; set; } = true;

        public bool IsSpecific { get; set; }

        [Parameter]
        public bool ExpandField { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        IEnumerable<string> PeriodGranularities { get; set; }
        IEnumerable<string> PeriodOperators { get; set; }
        IEnumerable<string> MeasureOperators { get; set; }

        public string Comparator
        {
            get { return periodFilterItem.Comparator; }
            set
            {
                ShouldRender_ = true;
                periodFilterItem.Comparator = value;
                ItemCallback.InvokeAsync(periodFilterItem);
            }
        }

        [Parameter]
        public bool ShouldRender_ { get; set; } = true;

        protected override bool ShouldRender()
        {
            return ShouldRender_; // base.ShouldRender();
        }

        public string Operator
        {
            get
            {
                return periodFilterItem.Operator.GetText(text => AppState[text]); ;
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    ShouldRender_ = true;
                    periodFilterItem.Operator = periodFilterItem.Operator.GetPeriodOperator(value, text => AppState[text]);
                    ItemCallback.InvokeAsync(periodFilterItem);
                }
            }
        }


        public DateTime? Value
        {
            get
            {
                if (!periodFilterItem.Value.HasValue)
                {
                    periodFilterItem.Value = DateTime.Now;
                }
                return periodFilterItem.Value;
            }
            set
            {
                ShouldRender_ = true;
                periodFilterItem.Value = value;
                ItemCallback.InvokeAsync(periodFilterItem);
            }
        }


        public string Sign
        {
            get
            {
                if (periodFilterItem != null)
                {
                    return periodFilterItem.Sign;
                }
                else
                {
                    return null;
                }
            }
            set
            {
                ShouldRender_ = true;
                periodFilterItem.Sign = value;
                ItemCallback.InvokeAsync(periodFilterItem);
            }
        }


        public int? Number
        {
            get { return periodFilterItem.Number; }
            set
            {
                ShouldRender_ = true;
                periodFilterItem.Number = value.Value;
                ItemCallback.InvokeAsync(periodFilterItem);
            }
        }


        public string Granularity
        {
            get
            {
                return periodFilterItem.Granularity.GetText(text => AppState[text]);
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    ShouldRender_ = true;
                    periodFilterItem.Granularity = periodFilterItem.Granularity.GetPeriodGranularity(value, text => AppState[text]);
                    ItemCallback.InvokeAsync(periodFilterItem);
                }
            }

        }


        protected override void OnInitialized()
        {
            if (periodFilterItem.Operator == PeriodOperator.SPECIFIC)
            {
                IsSpecific = true;
            }
            PeriodGranularities = periodFilterItem.Granularity.GetAll(text => AppState[text]);
            PeriodOperators = periodFilterItem.Operator.GetAll(text => AppState[text]);
        }

        public void PeriodChanged(HierarchicalData Periode)
        {
            ShouldRender_ = true;
            periodFilterItem.DimensionName = Periode.Name;
            periodFilterItem.DimensionId = Periode.Id;
            ItemCallback.InvokeAsync(periodFilterItem);
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            await base.OnAfterRenderAsync(firstRender);
        }
    }
}
