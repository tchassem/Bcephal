using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Joins;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    public class JoinConditionItem : Persistent
    {
        public JoinConditionItemType Type { get; set; }
        public DimensionType DimensionType { get; set; }
        public  long? GridId { get; set; }
        public long? ColumnId { get; set; }
        public long? DimensionId { get; set; }
        public string DimensionName { get; set; }
        public string StringValue { get; set; }
        public decimal? DecimalValue { get; set; }
        public PeriodValue PeriodValue { get; set; }

    }
}
