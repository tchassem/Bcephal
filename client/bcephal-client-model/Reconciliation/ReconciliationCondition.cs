using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationCondition : Persistent
	{

		public DimensionType DimensionType { get; set; }

		public string Side1 { get; set; }
		[JsonIgnore]
		public ReconciliationModelSide ModelSide1
		{
			get { return string.IsNullOrEmpty(Side1) ? null : ReconciliationModelSide.GetByCode(Side1); }
			set { this.Side1 = value != null ? value.getCode() : null; }
		}

		public string Side2 { get; set; }
		[JsonIgnore]
		public ReconciliationModelSide ModelSide2
		{
			get { return string.IsNullOrEmpty(Side2) ? null : ReconciliationModelSide.GetByCode(Side2); }
			set { this.Side2 = value != null ? value.getCode() : null; }
		}

		public long? ColumnId1 { get; set; }

		public long? ColumnId2 { get; set; }

		public string Operator { get; set; }

		public string Verb { get; set; }

		public string OpeningBracket { get; set; }

		public string ClosingBracket { get; set; }

		public int Position { get; set; }

		public string PeriodCondition { get; set; }
		[JsonIgnore]
		public AutoRecoPeriodCondition PeriodConditions
		{
			get
			{
				return string.IsNullOrEmpty(PeriodCondition) ? null : AutoRecoPeriodCondition.GetByCode(PeriodCondition);
			}
			set
			{
				this.PeriodCondition = value != null ? value.getCode() : null;
			}
		}

		public string DateSign { get; set; }

		public int DateNumber { get; set; }

		public PeriodGranularity DateGranularity { get; set; }

		public bool EcludeNullAndEmptyValue { get; set; }


		public ReconciliationCondition()
		{
			
		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is ReconciliationCondition)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((ReconciliationCondition)obj).Id)) return 0;
			if (this.Position.Equals(((ReconciliationCondition)obj).Position))
			{
				//return this.LeftDimensionId.CompareTo(((ReconciliationCondition)obj).LeftDimensionId);
			}
			return this.Position.CompareTo(((ReconciliationCondition)obj).Position);
		}

	}
}
