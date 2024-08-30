using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class AttributeFilterItem : FilterItem
    {

        public AttributeOperator? Operator { get; set; }

        public string Value { get; set; }

        public bool UseLink { get; set; }

        public AttributeFilterItem() : base()
        {
            this.DimensionType = DimensionType.ATTRIBUTE;
        }

        public void Synchronize(AttributeFilterItem filterItem, string formula)
        {
            this.Operator = filterItem.Operator;
            this.Value = filterItem.Value;
            this.UseLink = filterItem.UseLink;
            this.DimensionType = filterItem.DimensionType;
            this.DimensionId = filterItem.DimensionId;
            this.DimensionName = filterItem.DimensionName;
            this.FilterVerb = filterItem.FilterVerb;
            this.OpenBrackets = filterItem.OpenBrackets;
            this.CloseBrackets = filterItem.CloseBrackets;
            this.Position = filterItem.Position;
            this.Formula = formula;
        }

    }
}
