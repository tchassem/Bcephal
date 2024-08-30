using Bcephal.Models.Sheets;
using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class MeasureFilterItem : FilterItem
    {

        public string Operator { get; set; }

        public decimal? Value { get; set; }


        public MeasureFilterItem() : base()
        {
            this.DimensionType = DimensionType.MEASURE;
            this.Operator = MeasureOperator.NOT_EQUALS;
        }


        public void Synchronize(MeasureFilterItem cellMeasure, string formula)
        {
            this.Operator = cellMeasure.Operator;
            this.Value = cellMeasure.Value;
            this.DimensionType = cellMeasure.DimensionType;
            this.DimensionId = cellMeasure.DimensionId;
            this.DimensionName = cellMeasure.DimensionName;
            this.FilterVerb = cellMeasure.FilterVerb;
            this.OpenBrackets = cellMeasure.OpenBrackets;
            this.CloseBrackets = cellMeasure.CloseBrackets;
            this.Position = cellMeasure.Position;
            this.Formula = formula;
        }

        public MeasureFilterItem Copy()
        {
            MeasureFilterItem copy = new MeasureFilterItem();
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
