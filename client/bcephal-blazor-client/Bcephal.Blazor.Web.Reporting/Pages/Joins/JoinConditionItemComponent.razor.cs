using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component.Expression;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinConditionItemComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public JoinEditorData JoinEditorData { get; set; }

        [Parameter]
        public JoinCondition JoinCondition { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool IsAdded { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        [Parameter]
        public EventCallback<JoinCondition> AddConditionCallback { get; set; }

        [Parameter]
        public EventCallback<JoinCondition> UpdateConditionCallback { get; set; }

        [Parameter]
        public EventCallback<JoinCondition> DelConditionCallback { get; set; }

        [Parameter]
        public List<Models.Dimensions.Attribute> ModelsAttributes { get; set; }

        private List<Nameable> SpotData { get; set; }

        public PeriodFilterItem periodFilterItem { get; set; } = new();

        public IEnumerable<JoinGrid> JoinGrids { get; set; } = new List<JoinGrid> { };

        FilterVerb filterVerbOperator => FilterVerb.AND;
        private ObservableCollection<string> FilterVerbOperatorCollection_ { get => filterVerbOperator.GetAll(text => AppState[text]); }

        AttributeOperator? attributeOperator => AttributeOperator.NOT_NULL;
        private ObservableCollection<string> AttributeOperatorCollection_ { get => attributeOperator.GetAll(text => AppState[text]); }

        JoinConditionItemType? conditionItemType => JoinConditionItemType.PARAMETER;
        private ObservableCollection<string> ConditionItemTypeCollection_ { get => conditionItemType.GetAll(text => AppState[text]); }

        IEnumerable<string> PeriodGranularities { get; set; }

        protected override async Task OnInitializedAsync()
        {
            if (JoinCondition == null)
            {
                JoinCondition = new JoinCondition();
            }
            
            if (JoinCondition.Item1 == null)
            {
                JoinCondition.Item1 = new(); 
            }
            if (JoinCondition.Item2 == null)
            {
                JoinCondition.Item2 = new();
            }
            if(JoinCondition.Verb == null)
            {
                JoinCondition.Verb = FilterVerb.AND.ToString();
            }
            SpotData = JoinEditorData.Spots.ToList();
            PeriodGranularities = periodFilterItem.Granularity.GetAll(text => AppState[text]);
            await base.OnInitializedAsync();
        }

        public string CurrentVerb
        {
            get
            {
                if (JoinCondition != null)
                {
                    FilterVerb? verb = FilterVerb.AND.Parse(JoinCondition.Verb);
                    if (verb.HasValue)
                    {
                        return verb.Value.GetText(text => AppState[text]);
                    }
                }
                return null;
            }
            set
            {
                JoinCondition.Verb = FilterVerb.AND.GetFilterVerb(value, text => AppState[text]).ToString();
                AddOrUpdateCondition();
            }
        }

        public ConditionalOpLeft OpenBrackets
        {
            get
            {
                if (JoinCondition != null && !string.IsNullOrEmpty(JoinCondition.OpeningBracket))
                {
                    return ConditionalOpLeft.GetByCode(JoinCondition.OpeningBracket);
                }
                return null;
            }
            set
            {
                if (JoinCondition == null)
                {
                    JoinCondition = new JoinCondition();
                }
                JoinCondition.OpeningBracket = value.code;
                AddOrUpdateCondition();
            }
        }

        public string OperatorPeriode
        {
            get
            {
                if (JoinCondition != null && !string.IsNullOrEmpty(JoinCondition.Comparator) && (JoinCondition.Item1.DimensionType.IsPeriod() || JoinCondition.Item1.DimensionType.IsMeasure()))
                {
                    MeasureOperator measureOperator = MeasureOperator.GetByCode(JoinCondition.Comparator);
                    if(measureOperator != null)
                    {
                        return measureOperator.GetText(Text => AppState[Text]);
                    }
                    
                }
                return null;
            }
            set
            {
                if (JoinCondition == null)
                {
                    JoinCondition = new JoinCondition();
                }
                JoinCondition.Comparator = MeasureOperator.EQUALS_.GetMeasureOperator(value, Text => AppState[Text]).code;
                AddOrUpdateCondition();
            }
        }

        public string Operator
        {
            get
            {
                if (JoinCondition != null && !string.IsNullOrEmpty(JoinCondition.Comparator) && JoinCondition.Item1.DimensionType.IsAttribute())
                {
                    AttributeOperator? Operator_ = AttributeOperator.CONTAINS.Parse(JoinCondition.Comparator);
                    if (Operator_.HasValue)
                    {
                        return Operator_.GetText(text => AppState[text]);
                    }
                }
                return null;
            }
            set
            {
                if (JoinCondition == null)
                {
                    JoinCondition = new JoinCondition();
                }
                JoinCondition.Comparator = attributeOperator.GetAttributeOperator(value, text => AppState[text]).ToString();
                AddOrUpdateCondition();
            }
        }

        public string ItemType
        {
            get
            {
                if (JoinCondition != null && JoinCondition.Item2 != null)
                {
                    return JoinCondition.Item2.Type.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                if (JoinCondition == null)
                {
                    JoinCondition = new JoinCondition();
                }
                JoinCondition.Item2.Type = JoinConditionItemType.PARAMETER.GetJoinCoditionItemType(value, text => AppState[text]);
                if(JoinCondition.Item2.Type.Equals(JoinConditionItemType.PARAMETER))
                {
                    JoinCondition.Item2.DimensionType = JoinCondition.Item1.DimensionType;
                }
                AddOrUpdateCondition();
            }

        }

        private long? SpotId
        {
            get
            {
                if (JoinCondition != null && JoinCondition.Item2 != null && JoinCondition.Item2.ColumnId != null)
                {
                    return JoinCondition.Item2.ColumnId;
                }
                return 0;
            }
            set
            {
                JoinCondition.Item2.ColumnId = value;
                AddOrUpdateCondition();
            }
        }

        private JoinConditionItemType GetJoinConditionItemType()
        {
            return JoinCondition.Item2.Type;
        }

        public ConditionalOpRight CloseBrackets
        {
            get
            {
                if (JoinCondition != null && !string.IsNullOrEmpty(JoinCondition.ClosingBracket))
                {
                    return ConditionalOpRight.GetByCode(JoinCondition.ClosingBracket);
                }
                return null;
            }
            set
            {
                if (JoinCondition == null)
                {
                    JoinCondition = new JoinCondition();
                }
                JoinCondition.ClosingBracket = value.code;
                AddOrUpdateCondition();
            }
        }

        private async void RemoveCondition(JoinCondition Condition)
        {
            await DelConditionCallback.InvokeAsync(Condition);
        }

        private MeasureFilterItem GetMeasureFilterItem(JoinConditionItem item)
        {

            if (item.DecimalValue == null)
            {
                item.DecimalValue = 0;
                return new MeasureFilterItem() { Value = 0 };
            }
            else
            {
                return new MeasureFilterItem()
                {
                    Value = item.DecimalValue
                };
            }
        }

        private void JoinConditionUpdateMeasureHandler(JoinConditionItem item, MeasureFilterItem Item)
        {
            JoinCondition.Item2.DecimalValue = Item.Value;
            AddOrUpdateCondition();
        }

        private PeriodFilterItem GetPeriodFilterItem(JoinConditionItem item)
        {

            if (item.PeriodValue == null)
            {
                return new PeriodFilterItem()
                {
                    Number = 0
                };
            }
            else
            {
                return new PeriodFilterItem()
                {
                    Value = item.PeriodValue.DateValue,
                    Operator = item.PeriodValue.DateOperator

                };
            }
        }

        private void JoinConditionUpdatePeriodHandler(JoinConditionItem item, PeriodFilterItem Item)
        {
            JoinCondition.Item2.PeriodValue = new PeriodValue()
            {
                DateOperator = Item.Operator,
                DateValue = Item.Value
            };
            AddOrUpdateCondition();
        }

        private AttributeFilterItem GetAttributeFilterItem(JoinConditionItem item)
        {
            if (JoinCondition.Item2.StringValue != null)
            {
                return new AttributeFilterItem()
                {
                    Value = JoinCondition.Item2.StringValue
                };
            }
            else
            {
                return new AttributeFilterItem()
                {
                    Value = item.StringValue,
                    DimensionName = item.DimensionName,
                    DimensionId = item.DimensionId
                };
            }
        }

        private void JoinConditionUpdateAttributeHandler(JoinConditionItem item, AttributeFilterItem Item)
        {
            JoinCondition.Item2.StringValue = Item.Value;
            JoinCondition.Item2.DimensionId = item.DimensionId;
            AddOrUpdateCondition();
        }

        public string Sign
        {
            get
            {
                if (JoinCondition != null && JoinCondition.Item2 != null && JoinCondition.Item2.PeriodValue != null)
                {
                    return JoinCondition.Item2.PeriodValue.DateSign;
                }
                return null;
            }
            set
            {
                if (JoinCondition.Item2.PeriodValue == null)
                {
                    JoinCondition.Item2.PeriodValue = new();
                }
                JoinCondition.Item2.PeriodValue.DateSign = value;
                AddOrUpdateCondition();
            }
        }

        public string Granularity
        {
            get
            {
                if (JoinCondition != null && JoinCondition.Item2 != null && JoinCondition.Item2.PeriodValue != null)
                {
                    return JoinCondition.Item2.PeriodValue.DateGranularity.GetText(key => AppState[key]);
                }
                return null;

                
            }
            set
            {
                if (JoinCondition.Item2.PeriodValue == null)
                {
                    JoinCondition.Item2.PeriodValue = new();
                }
                if (!string.IsNullOrWhiteSpace(value))
                {
                    JoinCondition.Item2.PeriodValue.DateGranularity = periodFilterItem.Granularity.GetPeriodGranularity(value, text => AppState[text]);
                    AddOrUpdateCondition();
                }
            }

        }

        public int Number
        {
            get 
            {
                if (JoinCondition != null && JoinCondition.Item2 != null && JoinCondition.Item2.PeriodValue != null)
                {
                    return JoinCondition.Item2.PeriodValue.DateNumber;
                }
                return 0;
                 
            }
            set
            {
                if (JoinCondition.Item2.PeriodValue == null)
                {
                    JoinCondition.Item2.PeriodValue = new();
                }
                JoinCondition.Item2.PeriodValue.DateNumber = value;
                AddOrUpdateCondition();
            }
        }

        private bool IsFilled()
        {
            if (JoinCondition.Item1.GridId != null && JoinCondition.Item1.ColumnId != null)
            {
                return true;
            }
            return false;
        }

        private  void AddOrUpdateCondition()
        {
            if (!IsAdded)
            {
                AddCondition();
            }
            else
            {
                UpdateCondition();
            }
        }

        private async void AddCondition()
        {
            if (IsFilled())
            {
                await AddConditionCallback.InvokeAsync(JoinCondition);
            }
        }

        private void UpdateCondition()
        {
            UpdateConditionCallback.InvokeAsync(JoinCondition);
        }

        private void SetConditionItem1Callback(JoinConditionItem item)
        {
            JoinCondition.Item1 = item;
            if (!IsAdded)
            {
                AddCondition();
            }
            else
            {
                UpdateCondition();
            }
        }

        private void SetConditionItem2Callback(JoinConditionItem item)
        {
            JoinCondition.Item2 = item;
            UpdateCondition();
        }

  
    }
}
