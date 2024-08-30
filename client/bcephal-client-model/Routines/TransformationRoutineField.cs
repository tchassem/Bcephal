using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutineField : Persistent
    {

		public string SourceType { get; set; }
		[JsonIgnore]
		public TransformationRoutineSourceType RoutineSourceType
		{
			get { return TransformationRoutineSourceType.GetByCode(this.SourceType); }
			set { this.SourceType = value != null ? value.code : null; }
		}

		public string StringValue { get; set; }

		public decimal? DecimalValue { get; set; }

		public PeriodValue PeriodValue { get; set; }

		public long? DimensionId { get; set; }

		public int? PositionStart { get; set; }

		public int? PositionEnd { get; set; }

		public TransformationRoutineMapping Mapping { get; set; }

	}
}
