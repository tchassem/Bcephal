using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Filters
{
    public class SpotFilterItem : FilterItem
    {

        public string Operator { get; set; }

        public decimal? Value { get; set; }


        public SpotFilterItem() : base()
        {
            this.DimensionType = DimensionType.SPOT;
            this.Operator = MeasureOperator.NOT_EQUALS;
        }


        public void Synchronize(SpotFilterItem filterItem, string formula)
        {
            this.Operator = filterItem.Operator;
            this.Value = filterItem.Value;
            this.DimensionType = filterItem.DimensionType;
            this.DimensionId = filterItem.DimensionId;
            this.DimensionName = filterItem.DimensionName;
            this.FilterVerb = filterItem.FilterVerb;
            this.OpenBrackets = filterItem.OpenBrackets;
            this.CloseBrackets = filterItem.CloseBrackets;
            this.Position = filterItem.Position;
            this.Formula = formula;
        }

        public SpotFilterItem Copy()
        {
            SpotFilterItem copy = new SpotFilterItem();
            copy.Operator = this.Operator;
            copy.Value = this.Value;
            copy.DimensionType = this.DimensionType;
            copy.DimensionId = this.DimensionId;
            copy.DimensionName = this.DimensionName;
            copy.FilterVerb = this.FilterVerb;
            copy.OpenBrackets = this.OpenBrackets;
            copy.CloseBrackets = this.CloseBrackets;
            copy.Position = this.Position;
            copy.Formula = this.Formula;
            return copy;
        }

    }
}
