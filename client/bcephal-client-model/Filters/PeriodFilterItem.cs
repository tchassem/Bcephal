using Bcephal.Models.Archives;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class PeriodFilterItem : FilterItem
    {

        public PeriodOperator Operator { get; set; }

        public string Comparator { get; set; }

        public DateTime? Value { get; set; }

        public string Sign { get; set; }

        public int? Number { get; set; }

        public PeriodGranularity Granularity { get; set; }

        public PeriodFilterItemCalendar Calendar { get; set; }

        public PeriodFilterItem() : base()
        {
            this.DimensionType = DimensionType.PERIOD;
            this.Comparator = MeasureOperator.EQUALS;
            this.Sign = "+";
            this.Operator = PeriodOperator.SPECIFIC;
            this.Granularity = PeriodGranularity.DAY;
        }

        public void Synchronize(PeriodFilterItem filterItem, string formula)
        {
            this.Operator = filterItem.Operator;
            this.Comparator = filterItem.Comparator;
            this.Value = filterItem.Value;
            this.Sign = filterItem.Sign;
            this.Number = filterItem.Number;
            this.Granularity = filterItem.Granularity;
            this.Calendar = filterItem.Calendar;
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
