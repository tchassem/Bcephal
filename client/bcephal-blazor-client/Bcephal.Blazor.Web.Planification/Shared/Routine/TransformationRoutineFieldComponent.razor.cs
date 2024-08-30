using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Routines;
using Bcephal.Models.Utils;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Planification.Shared.Routine
{
    public partial class TransformationRoutineFieldComponent : ComponentBase
    {
        [Inject] public AppState AppState { get; set; }

        [Parameter] public TransformationRoutineField SourceField { get; set; }
        [Parameter] public EventCallback<TransformationRoutineField> SourceFieldChanged { get; set; }
        [Parameter] public DimensionType Type { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Dimensions { get; set; }
        [Parameter] public IEnumerable<TransformationRoutineSourceType> SourceTypes { get; set; }
        [Parameter] public bool ShowItemLabels { get; set; } = true;
        [Parameter] public string ItemCssClass { get; set; }
        [Parameter] public string ItemSpacing { get; set; } = "4px";
        [Parameter] public string SourceTypeSize { get; set; } = "25%";
        [Parameter] public string TextLength { get; set; } = "0.8fr";
        [Parameter] public RenderFragment Fragment { get; set; }

        private TransformationRoutineSourceType RoutineSourceType
        {
            get
            {
                return SourceField.RoutineSourceType;
            }
            set
            {
                SourceField.RoutineSourceType = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }
        private string StringValue
        {
            get
            {
                return SourceField.StringValue;
            }
            set
            {
                SourceField.StringValue = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }
        private decimal? DecimalValue
        {
            get
            {
                return SourceField.DecimalValue.HasValue ? DecimalUtils.Trim(SourceField.DecimalValue) : 0M;
            }
            set
            {
                SourceField.DecimalValue = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }
        private PeriodValue PeriodValue
        {
            get
            {
                return SourceField.PeriodValue;
            }
            set
            {
                SourceField.PeriodValue = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }
        private TransformationRoutineMapping Mapping
        {
            get
            {
                if (SourceField.Mapping == null)
                {
                    SourceField.Mapping = new();
                }
                return SourceField.Mapping;
            }
            set
            {
                SourceField.Mapping = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }
        private int? PositionStart
        {
            get
            {
                return SourceField.PositionStart;
            }
            set
            {
                SourceField.PositionStart = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }

        private int? PositionEnd
        {
            get
            {
                return SourceField.PositionEnd;
            }
            set
            {
                SourceField.PositionEnd = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }

        private HierarchicalData dimension;
        private HierarchicalData Dimension
        {
            get
            {
                if (Dimensions != null && Dimensions.Count > 0 && SourceField.DimensionId.HasValue)
                {
                    if (dimension != null && dimension.Id.Value == SourceField.DimensionId.Value)
                    {
                        return dimension;
                    }
                    else
                    {
                        if (Type.IsAttribute())
                        {
                            foreach (Models.Dimensions.Entity entity in Dimensions)
                            {
                                dimension = entity.Attributes.FirstOrDefault(d => d.Id == SourceField.DimensionId.Value);
                                if (dimension != null && dimension.Id.HasValue)
                                {
                                    return dimension;
                                }
                            }
                        }
                        else if (Type.IsPeriod() || Type.IsMeasure())
                        {
                            dimension = Dimensions.FirstOrDefault(d => d.Id == SourceField.DimensionId.Value);
                            if (dimension != null && dimension.Id.HasValue)
                            {
                                return dimension;
                            }
                        }
                    }
                }
                return null;
            }
            set
            {
                SourceField.DimensionId = value.Id;
                dimension = value;
                SourceFieldChanged.InvokeAsync(SourceField);
            }
        }

        private string freeTextLenght = "0.72fr";
        private string dimensionTextLenght = "0.8fr";

        protected override void OnInitialized()
        {
            base.OnInitialized();

            if (SourceField.PeriodValue == null)
            {
                SourceField.PeriodValue = new PeriodValue();
            }

        }

        protected override void OnParametersSet()
        {
            if(Type == DimensionType.ATTRIBUTE)
            {
                freeTextLenght = "0.72fr";
                dimensionTextLenght = "0.8fr";
            }
            else if (Type == DimensionType.MEASURE)
            {
                freeTextLenght = "0.715fr";
                dimensionTextLenght = "0.8fr";
            }
            else if (Type == DimensionType.PERIOD)
            {
                freeTextLenght = "0.72fr";
                dimensionTextLenght = "0.75fr";
            }
        }

        public void OnDimensionChanged(HierarchicalData hierarchicalData)
        {
            Dimension = hierarchicalData;
            SourceField.DimensionId = hierarchicalData.Id;

            SourceFieldChanged.InvokeAsync(SourceField);
        }

    }
}
