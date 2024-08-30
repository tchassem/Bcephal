using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    public class JoinColumnField : Persistent
	{ 
		public string Type { get; set; }

		[JsonIgnore]
		public JoinColumnType JoinColumnType
	    {
			get { return JoinColumnType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}
		public DimensionType DimensionType { get; set; }
		public long? GridId { get; set; }
		public long? ColumnId { get; set; }
		public long? DimensionId { get; set; }
		public string DimensionName { get; set; }
		public int StartPosition { get; set; }
		public int EndPosition { get; set; }
		public string StringValue { get; set; }
		public decimal? DecimalValue { get; set; }

		public PeriodValue DateValue { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is JoinColumnField)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((JoinColumnField)obj).Id)) return 0;
			return this.DimensionName.CompareTo(((JoinColumnField)obj).DimensionName);
		}
	}
}
