using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Conditions;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Expression
{
    public partial class ConditionItem
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool IsAdded { get; set; } = false;

        [Parameter]
        public ConditionalExpressionItem CondExpItem { get; set; }

        [Parameter]
        public EventCallback<ConditionalExpressionItem> AddCondExpItemrCallback { get; set; }

        [Parameter]
        public EventCallback<ConditionalExpressionItem> DelCondExpItemrCallback { get; set; }

        [Parameter]
        public EventCallback<ConditionalExpressionItem> UpdCondExpItemrCallback { get; set; }

        [Parameter]
        public IEnumerable<Nameable> SpotData { get; set; }

        public bool IsSmallScreen { get; set; }

        public ObservableCollection<DimensionType> Dimensions { get; set; }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            if (CondExpItem == null)
            {
                CondExpItem = new();
            }
            Dimensions = DimensionTypeExtensionMethods.GetAll().Where(d => d.Equals(DimensionType.SPOT) || d.Equals(DimensionType.FREE)).Reverse().ToObservableCollection();
            if (!Enum.IsDefined(typeof(DimensionType), CondExpItem.Value2Type) || string.IsNullOrEmpty(CondExpItem.Operator))
            {
                CondExpItem.Value2Type = DimensionType.FREE;
                CondExpItem.Operator = MeasureOperator.NOT_EQUALS;
            }
        }

        FilterVerb filterVerbOperator => FilterVerb.AND;
        private ObservableCollection<string> FilterVerbOperatorCollection_ { get => filterVerbOperator.GetAll(text => AppState[text]); }

        public string CurrentVerb
        {
            get
            {
                FilterVerb? verb = FilterVerb.AND.Parse(CondExpItem.FilterVerb);
                if (verb.HasValue)
                {
                    return verb.Value.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                CondExpItem.FilterVerb = FilterVerb.AND.GetFilterVerb(value, text => AppState[text]).ToString();
                UpdateExprItem(CondExpItem);
            }
        }

        public ConditionalOpLeft OpenBrackets
        {
            get
            {
                if (!string.IsNullOrEmpty(CondExpItem.OpenBrackets))
                {
                    return ConditionalOpLeft.GetByCode(CondExpItem.OpenBrackets);
                }
                return null;
            }
            set
            {
                CondExpItem.OpenBrackets = value.code;
                UpdateExprItem(CondExpItem);
            }
        }

        private Nameable SpotId1
        {
            get
            {
                if (CondExpItem.SpotId1 != null)
                {
                    return SpotData.Where(s => CondExpItem.SpotId1 == s.Id).FirstOrDefault();
                }
                return null;
            }
            set
            {
                CondExpItem.SpotId1 = value.Id;
                if (!IsAdded)
                {
                    AddCondExpItem();
                } else
                {
                    UpdateExprItem(CondExpItem);
                }
            }
        }

        public string Operator
        {
            get
            {
                if (!string.IsNullOrEmpty(CondExpItem.Operator))
                {
                    return CondExpItem.Operator;
                }
                return null;
            }
            set
            {
                CondExpItem.Operator = value;
                UpdateExprItem(CondExpItem);
            }
        }

        public DimensionType Value2Type
        {
            get
            {
                return CondExpItem.Value2Type;
                // return (DimensionType) Enum.Parse(typeof(DimensionType), CondExpItem.Value2Type.ToString());
            }
            set
            {
                CondExpItem.Value2Type = value;
                UpdateExprItem(CondExpItem);
            }
        }

        public decimal? CurrentValue
        {
            get
            {
                return CondExpItem.Value;
            }
            set
            {
                CondExpItem.Value = value;
                UpdateExprItem(CondExpItem);
            }
        }

        private Nameable SpotId2
        {
            get
            {
                if (CondExpItem.SpotId2 != null)
                {
                    return SpotData.Where(s => CondExpItem.SpotId2 == s.Id).FirstOrDefault();
                }
                return null;
            }
            set
            {
                CondExpItem.SpotId2 = value.Id;
                UpdateExprItem(CondExpItem);
            }
        }

        public ConditionalOpRight CloseBrackets
        {
            get
            {
                if (!string.IsNullOrEmpty(CondExpItem.CloseBrackets))
                {
                    return ConditionalOpRight.GetByCode(CondExpItem.CloseBrackets);
                }
                return null;
            }
            set
            {
                CondExpItem.CloseBrackets = value.code;
                UpdateExprItem(CondExpItem);
            }
        }

        private bool IsFilled()
        {
            if (CondExpItem.SpotId1 != null)
            {
                return true;
            }
            return false;
        }

        private async void AddCondExpItem()
        {
            if (IsFilled())
            {
                await AddCondExpItemrCallback.InvokeAsync(CondExpItem);
                resetFields();
                StateHasChanged();
            }
        }

        private async void RemoveItem(ConditionalExpressionItem Item)
        {
            await DelCondExpItemrCallback.InvokeAsync(Item);
        }

        private async void UpdateExprItem(ConditionalExpressionItem Item)
        {
            if (Item.IsPersistent)
            {
                await UpdCondExpItemrCallback.InvokeAsync(Item);
            }
        }

        private void resetFields()
        {
            CondExpItem = new();
        }
    }
}
