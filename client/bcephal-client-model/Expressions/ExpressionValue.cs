using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionValue
    {

		public ExpressionValueType? Type { get; set; }

		public decimal? DecimalValue { get; set; }

		public PeriodValue PeriodValue { get; set; }

		public string StringValue { get; set; }

		public long? IdValue { get; set; }

		public long? DimensionId { get; set; }

		public DimensionType? DimensionType { get; set; }



        [JsonIgnore]
        public  bool IsColumn { get { return Type.HasValue && Type.Value.IsColumn(); } }

        [JsonIgnore]
        public bool IsParameter { get { return Type.HasValue && Type.Value.IsParameter(); } }

        [JsonIgnore]
        public bool IsSpot { get { return Type.HasValue && Type.Value.IsSpot(); } }

        [JsonIgnore]
        public bool IsLoop { get { return Type.HasValue && Type.Value.IsLoop(); } }

        [JsonIgnore]
        public bool IsNull { get { return Type.HasValue && Type.Value.IsNull(); } }

        [JsonIgnore]
        public bool IsNo { get { return Type.HasValue && Type.Value.IsNo(); } }



        [JsonIgnore]
        public bool IsAttribute { get { return DimensionType.HasValue && DimensionType.Value.IsAttribute(); } }

        [JsonIgnore]
        public bool IsMeasure { get { return DimensionType.HasValue && DimensionType.Value.IsMeasure(); } }

        [JsonIgnore]
        public bool IsPeriod { get { return DimensionType.HasValue && DimensionType.Value.IsPeriod(); } }


    }
}
